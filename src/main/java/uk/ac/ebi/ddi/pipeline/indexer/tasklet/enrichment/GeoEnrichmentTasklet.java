package uk.ac.ebi.ddi.pipeline.indexer.tasklet.enrichment;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponentsBuilder;
import uk.ac.ebi.ddi.annotation.service.dataset.DDIDatasetAnnotationService;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.pipeline.indexer.utils.FileUtil;
import uk.ac.ebi.ddi.service.db.model.dataset.Dataset;
import uk.ac.ebi.ddi.service.db.model.publication.PublicationDataset;
import uk.ac.ebi.ddi.service.db.service.dataset.IDatasetService;
import uk.ac.ebi.ddi.service.db.utils.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Getter
@Setter
public class GeoEnrichmentTasklet extends AbstractTasklet {

    private DDIDatasetAnnotationService datasetAnnotationService;

    private IDatasetService datasetService;

    private static final String DATASET_NAME = "GEO";

    private static final String NCBI_ENDPOINT = "https://www.ncbi.nlm.nih.gov/geo/query/acc.cgi";

    private File processedFile = new File(System.getProperty("user.home"), "GeoEnrichmentTasklet.processed");

    @Value("${ddi.common.storage.path}")
    private String downloadPath;

    private static final String TASK_WORKING_DIR = "geo-raw-dataset";

    private static final int PARALLEL = Math.min(12, Runtime.getRuntime().availableProcessors());

    private File downloadDir;

    private static final Logger LOGGER = LoggerFactory.getLogger(GeoEnrichmentTasklet.class);

    private HashMap<String, String> processedDatasets = new HashMap<>();

    private static final int RETRIES = 5;
    private RetryTemplate template = new RetryTemplate();

    private Pattern seriesSamplePattern = Pattern.compile("\\s*!Series_sample_id\\s*=\\s*(\\w*)");
    private Pattern sampleSeriesPattern = Pattern.compile("\\s*!Sample_series_id\\s*=\\s*(\\w*)");
    private Pattern reanalysedPattern = Pattern.compile("\\s*Reanalyzed by:\\s*(\\w*)");

    public GeoEnrichmentTasklet() {
        SimpleRetryPolicy policy =
                new SimpleRetryPolicy(RETRIES, Collections.singletonMap(Exception.class, true));
        template.setRetryPolicy(policy);
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(2000);
        backOffPolicy.setMultiplier(1.6);
        template.setBackOffPolicy(backOffPolicy);
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        LOGGER.info("Starting");
        downloadDir = new File(downloadPath, TASK_WORKING_DIR);
        if (!downloadDir.exists()) {
            downloadDir.mkdirs();
        }
        if (processedFile.exists()) {
            processedDatasets = FileUtil.loadObjectFromFile(processedFile);
        }
        List<Dataset> datasets = datasetService.readDatasetHashCode(DATASET_NAME);
        AtomicInteger counter = new AtomicInteger(0);
        ForkJoinPool customThreadPool = new ForkJoinPool(PARALLEL);
        customThreadPool.submit(() -> datasets.stream().parallel().forEach(dataset -> {
            LOGGER.info("Processing dataset " + dataset.getAccession() + ", {}/{}", counter.getAndIncrement(),
                    datasets.size());
            process(dataset);
        })).get();
        processedFile.delete();
        LOGGER.info("Finished");
        return RepeatStatus.FINISHED;
    }

    private Set<PublicationDataset> reanalysedDatasetCorrection(Set<PublicationDataset> allPubDatasets, Dataset dataset) {
        Set<PublicationDataset> result = new HashSet<>();
        for (PublicationDataset publicationDataset : allPubDatasets) {
            if (publicationDataset.getDatasetID().equals(dataset.getAccession())) {
                continue;
            }
            Dataset refDataset = datasetService.read(publicationDataset.getDatasetID(),
                    publicationDataset.getDatabase());
            if (refDataset != null) {
                result.add(publicationDataset);
            } else {
                List<Dataset> secondaries =
                        datasetService.getBySecondaryAccession(publicationDataset.getDatasetID());
                if (secondaries.isEmpty()) {
                    LOGGER.info("Adding related datasets to {} with type Reanalyzed by, " +
                            "but none of them were in our database, {}", dataset.getAccession(),
                            allPubDatasets.stream().map(PublicationDataset::getDatasetID).collect(Collectors.toList()));
                }
                for (Dataset secondary : secondaries) {
                    PublicationDataset pub = new PublicationDataset();
                    pub.setDatabaseID(secondary.getDatabase());
                    pub.setDatasetID(secondary.getAccession());
                    result.add(pub);
                }
            }
        }
        return result;
    }

    private void process(Dataset dataset) {
        try {
            if (isDatasetProcessed(dataset.getAccession())) {
                LOGGER.info("Dataset {} processed, ignoring...", dataset.getAccession());
                return;
            }
            Set<PublicationDataset> allPubDatasets = new HashSet<>();
            List<String> sampleIds = getSampleIds(dataset.getAccession());
            for (String sampleId : sampleIds) {
                allPubDatasets.addAll(getReanalysisDataset(sampleId));
            }
            allPubDatasets = reanalysedDatasetCorrection(allPubDatasets, dataset);
            if (allPubDatasets.size() > 0) {
                datasetAnnotationService.addGEODatasetSimilars(dataset, allPubDatasets, Constants.REANALYZED_TYPE);
                for (PublicationDataset publicationDataset : allPubDatasets) {
                    Dataset refDataset = new Dataset(publicationDataset.getDatasetID(), publicationDataset.getDatabase());
                    PublicationDataset pub = new PublicationDataset();
                    pub.setDatabaseID(dataset.getDatabase());
                    pub.setDatasetID(dataset.getAccession());
                    datasetAnnotationService.addGEODatasetSimilars(refDataset, Collections.singleton(pub),
                            Constants.REANALYSIS_TYPE);
                }
            }
            datasetProcessed(dataset.getAccession());
        } catch (Exception e) {
            LOGGER.error("Exception occurred when processing dataset {}", dataset, e);
        }
    }

    private synchronized boolean isDatasetProcessed(String accession) {
        return processedDatasets.containsKey(accession);
    }

    private synchronized void datasetProcessed(String accession) throws IOException {
        processedDatasets.put(accession, accession);
        if (processedDatasets.size() % 100 == 0) {
            FileUtil.writeObjectToFile(processedFile, processedDatasets, true);
        }
    }

    List<String> getSampleIds(String accession) throws IOException {
        List<String> result = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(getDatasetFile(accession)))) {
            for(String line; (line = br.readLine()) != null; ) {
                Matcher matcher = seriesSamplePattern.matcher(line);
                if (matcher.find()) {
                    result.add(matcher.group(1));
                }
            }
        }
        return result;
    }

    private List<String> getDatasetFromSample(String accession) throws IOException {
        List<String> result = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new FileReader(getDatasetFile(accession)))) {
            for(String line; (line = br.readLine()) != null; ) {
                Matcher matcher = sampleSeriesPattern.matcher(line);
                if (matcher.find()) {
                    result.add(matcher.group(1));
                }
            }
        }
        return result;
    }

    Set<PublicationDataset> getReanalysisDataset(String sampleId) throws IOException {
        Set<PublicationDataset> dataset = new HashSet<>();
        Set<String> datasetIds = new HashSet<>();
        try(BufferedReader br = new BufferedReader(new FileReader(getDatasetFile(sampleId)))) {
            for(String line; (line = br.readLine()) != null; ) {
                Matcher matcher = reanalysedPattern.matcher(line);
                if (matcher.find()) {
                    if (matcher.group(1).contains("GSE")) {
                        // In case the sample is reanalysed by a dataset
                        datasetIds.add(matcher.group(1));
                    } else if (matcher.group(1).contains("GSM")) {
                        // In case the sample is reanalysed by another sample
                        List<String> accessions = getDatasetFromSample(matcher.group(1));
                        for (String accession : accessions) {
                            if (accession.contains("GSE")) {
                                datasetIds.add(accession);
                            }
                        }
                    }
                }
            }
        }
        for (String accession : datasetIds) {
            PublicationDataset publicationDataset = new PublicationDataset();
            publicationDataset.setDatabaseID(DATASET_NAME);
            publicationDataset.setDatasetID(accession);
            dataset.add(publicationDataset);
        }
        return dataset;
    }

    private File getDatasetFile(String accessionId) throws IOException {
        File downloadedFile = new File(downloadDir, accessionId);
        if (downloadedFile.exists()) {
            return downloadedFile;
        }
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(NCBI_ENDPOINT)
                .queryParam("acc", accessionId)
                .queryParam("targ", "self")
                .queryParam("form", "text");
        return template.execute(context -> {
            FileUtils.copyURLToFile(builder.build().toUri().toURL(), downloadedFile);
            return downloadedFile;
        });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(datasetAnnotationService, "The dataset annotation object can't be null");
        Assert.notNull(datasetService, "The dataset service can't be null");
    }
}
