package uk.ac.ebi.ddi.pipeline.indexer.tasklet.enrichment;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.ac.ebi.ddi.annotation.service.dataset.DDIDatasetAnnotationService;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.pipeline.indexer.utils.FileUtil;
import uk.ac.ebi.ddi.service.db.model.dataset.Dataset;
import uk.ac.ebi.ddi.service.db.model.publication.PublicationDataset;
import uk.ac.ebi.ddi.service.db.service.dataset.IDatasetService;
import uk.ac.ebi.ddi.service.db.utils.Constants;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private File downloadDir;

    private static final Logger LOGGER = LoggerFactory.getLogger(GeoEnrichmentTasklet.class);

    private RestTemplate restTemplate = new RestTemplate();

    private HashMap<String, String> processedDatasets = new HashMap<>();

    private static final int RETRIES = 5;
    private RetryTemplate template = new RetryTemplate();

    public GeoEnrichmentTasklet() {
        SimpleRetryPolicy policy =
                new SimpleRetryPolicy(RETRIES, Collections.singletonMap(HttpClientErrorException.class, true));
        template.setRetryPolicy(policy);
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(2000);
        backOffPolicy.setMultiplier(1.6);
        template.setBackOffPolicy(backOffPolicy);
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        LOGGER.info("Starting");
        downloadDir = new File(downloadPath);
        if (!downloadDir.exists()) {
            downloadDir.mkdirs();
        }
        if (processedFile.exists()) {
            processedDatasets = FileUtil.loadObjectFromFile(processedFile);
        }
        List<Dataset> datasets = datasetService.readDatasetHashCode(DATASET_NAME);
        AtomicInteger counter = new AtomicInteger(0);
        datasets.stream().parallel().forEach(dataset -> {
            LOGGER.info("Processing dataset " + dataset.getAccession() + ", {}/{}", counter.getAndIncrement(), datasets.size());
            process(dataset);
        });
        processedFile.delete();
        LOGGER.info("Finished");
        return RepeatStatus.FINISHED;
    }

    void process(Dataset dataset) {
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
            if (allPubDatasets.size() > 0) {
                datasetAnnotationService.addGEODatasetSimilars(dataset, allPubDatasets, Constants.REANALYZED_TYPE);
                for (PublicationDataset publicationDataset : allPubDatasets) {
                    Dataset refDataset = datasetService.read(publicationDataset.getDatasetID(), DATASET_NAME);
                    if (refDataset != null) {
                        PublicationDataset pub = new PublicationDataset();
                        pub.setDatabaseID(DATASET_NAME);
                        pub.setDatasetID(dataset.getAccession());
                        datasetAnnotationService.addGEODatasetSimilars(refDataset, Collections.singleton(pub),
                                Constants.REANALYSIS_TYPE);
                    } else {
                        List<Dataset> secondaries =
                                datasetService.getBySecondaryAccession(publicationDataset.getDatasetID());
                        for (Dataset secondary : secondaries) {
                            PublicationDataset pub = new PublicationDataset();
                            pub.setDatabaseID(DATASET_NAME);
                            pub.setDatasetID(dataset.getAccession());
                            datasetAnnotationService.addGEODatasetSimilars(secondary, Collections.singleton(pub),
                                    Constants.REANALYSIS_TYPE);
                        }
                    }
                }
            }
            datasetProcessed(dataset.getAccession());
        } catch (Exception e) {
            LOGGER.error("Exception occurred when processing dataset {}, {}", dataset, e);
        }
    }

    synchronized boolean isDatasetProcessed(String accession) {
        return processedDatasets.containsKey(accession);
    }

    synchronized void datasetProcessed(String accession) throws IOException {
        processedDatasets.put(accession, accession);
        if (processedDatasets.size() % 100 == 0) {
            FileUtil.writeObjectToFile(processedFile, processedDatasets, true);
        }
    }

    List<String> getSampleIds(String accession) throws IOException, ClassNotFoundException {
        String response = getFileContent(accession);
        List<String> result = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\s*!Series_sample_id\\s*=\\s*(\\w*)");
        try (BufferedReader reader = new BufferedReader(new StringReader(response))) {
            String line = reader.readLine();
            while (line != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    result.add(matcher.group(1));
                }
                line = reader.readLine();
            }
        } catch (IOException exc) {
            LOGGER.error("Exception occurred when reading dataset {}, {}", accession, exc);
        }
        return result;
    }

    List<String> getDatasetFromSample(String accession) {
        List<String> result = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\s*!Sample_series_id\\s*=\\s*(\\w*)");
        try (BufferedReader reader = new BufferedReader(new StringReader(getFileContent(accession)))) {
            String line = reader.readLine();
            while (line != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    result.add(matcher.group(1));
                }
                line = reader.readLine();
            }
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.error("Exception occurred when reading dataset {}, {}", accession, e);
        }
        return result;
    }

    Set<PublicationDataset> getReanalysisDataset(String sampleId) {
        Set<PublicationDataset> dataset = new HashSet<>();
        Set<String> datasets = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new StringReader(getFileContent(sampleId)))) {
            String line = reader.readLine();
            Pattern pattern = Pattern.compile("\\s*Reanalyzed by:\\s*(\\w*)");
            while (line != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    if (matcher.group(1).contains("GSE")) {
                        // In case the sample reanalyse a dataset
                        datasets.add(matcher.group(1));
                    } else if (matcher.group(1).contains("GSM")) {
                        // In case the sample reanalyse another sample
                        List<String> accessions = getDatasetFromSample(matcher.group(1));
                        for (String accession : accessions) {
                            if (accession.contains("GSE")) {
                                datasets.add(accession);
                            }
                        }
                    }
                }
                line = reader.readLine();
            }
        } catch (IOException | ClassNotFoundException exc) {
            LOGGER.error("Exception occurred when reading sample id {}, {}", sampleId, exc);
        }
        for (String accession : datasets) {
            PublicationDataset publicationDataset = new PublicationDataset();
            publicationDataset.setDatabaseID(DATASET_NAME);
            publicationDataset.setDatasetID(accession);
            dataset.add(publicationDataset);
        }
        return dataset;
    }

    private String getFileContent(String accessionId) throws IOException, ClassNotFoundException {
        File downloadedFile = new File(downloadDir, accessionId);
        try {
            if (downloadedFile.exists()) {
                return FileUtil.loadObjectFromFile(downloadedFile);
            }
        } catch (EOFException ignore) {
            LOGGER.info("File {} was downloaded but damaged, re-fetching the file", accessionId);
        }
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(NCBI_ENDPOINT)
                .queryParam("acc", accessionId)
                .queryParam("targ", "self")
                .queryParam("form", "text");
        return template.execute(context -> {
            HttpStatus status;
            try {
                ResponseEntity<String> response = restTemplate.getForEntity(builder.build().toString(), String.class);
                if (response.getStatusCode() == HttpStatus.OK) {
                    FileUtil.writeObjectToFile(downloadedFile, response.getBody(), true);
                    return response.getBody();
                }
                status = response.getStatusCode();
            } catch (HttpStatusCodeException e) {
                if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                    return "";
                }
                status = e.getStatusCode();
            }
            String retryTimes = context.getRetryCount() + 1 + "/" + RETRIES;
            LOGGER.info("Retrying " + retryTimes + " to fetch dataset: {}, status: {}", accessionId, status.value());
            throw new HttpClientErrorException(status);
        });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(datasetAnnotationService, "The dataset annotation object can't be null");
        Assert.notNull(datasetService, "The dataset service can't be null");
    }
}
