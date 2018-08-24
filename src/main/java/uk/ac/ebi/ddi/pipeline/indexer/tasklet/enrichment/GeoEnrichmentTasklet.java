package uk.ac.ebi.ddi.pipeline.indexer.tasklet.enrichment;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.ac.ebi.ddi.annotation.service.dataset.DDIDatasetAnnotationService;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.service.db.model.dataset.Dataset;
import uk.ac.ebi.ddi.service.db.model.publication.PublicationDataset;
import uk.ac.ebi.ddi.service.db.service.dataset.IDatasetService;
import uk.ac.ebi.ddi.service.db.utils.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
public class GeoEnrichmentTasklet extends AbstractTasklet {

    private DDIDatasetAnnotationService datasetAnnotationService;

    private static final int MAX_PARALLEL = 5;

    private IDatasetService datasetService;

    private static final String DATASET_NAME = "GEO";

    private static final String NCBI_ENDPOINT = "https://www.ncbi.nlm.nih.gov/geo/query/acc.cgi";

    private static final Logger LOGGER = LoggerFactory.getLogger(GeoEnrichmentTasklet.class);

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        LOGGER.info("Starting");
        List<Dataset> datasets = datasetService.readDatasetHashCode(DATASET_NAME);
        ForkJoinPool customThreadPool = new ForkJoinPool(MAX_PARALLEL);
        AtomicInteger counter = new AtomicInteger(0);
        customThreadPool.submit(() -> datasets.parallelStream().forEach(dataset -> {
            LOGGER.info("Processing dataset " + dataset.getAccession() + ", {}/{}", counter.getAndIncrement(), datasets.size());
            process(dataset);
        })).get();
        LOGGER.info("Finished");
        return RepeatStatus.FINISHED;
    }

    void process(Dataset dataset) {
        try {
            Set<PublicationDataset> allPubDatasets = new HashSet<>();
            List<String> sampleIds = getSampleIds(dataset.getAccession());
            for (String sampleId : sampleIds) {
                allPubDatasets.addAll(getReanalysisDataset(sampleId));
            }
            if (allPubDatasets.size() > 0) {
                datasetAnnotationService.addGEODatasetSimilars(dataset, allPubDatasets, Constants.REANALYZED_TYPE);
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred when processing dataset {}", dataset);
        }
    }

    List<String> getSampleIds(String accession) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(NCBI_ENDPOINT)
                .queryParam("acc", accession)
                .queryParam("targ", "self")
                .queryParam("form", "text");
        ResponseEntity<String> response = restTemplate.getForEntity(builder.build().toString(), String.class);
        List<String> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new StringReader(response.getBody()))) {
            String line = reader.readLine();
            while (line != null) {
                Pattern pattern = Pattern.compile("\\s*!Series_sample_id\\s*=\\s*(\\w*)");
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
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(NCBI_ENDPOINT)
                .queryParam("acc", accession)
                .queryParam("targ", "self")
                .queryParam("form", "text");
        ResponseEntity<String> response = restTemplate.getForEntity(builder.build().toString(), String.class);
        List<String> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new StringReader(response.getBody()))) {
            String line = reader.readLine();
            while (line != null) {
                Pattern pattern = Pattern.compile("\\s*!Sample_series_id\\s*=\\s*(\\w*)");
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

    Set<PublicationDataset> getReanalysisDataset(String sampleId) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(NCBI_ENDPOINT)
                .queryParam("acc", sampleId)
                .queryParam("targ", "self")
                .queryParam("form", "text");
        ResponseEntity<String> response = restTemplate.getForEntity(builder.build().toString(), String.class);
        Set<PublicationDataset> dataset = new HashSet<>();
        Set<String> datasets = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new StringReader(response.getBody()))) {
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
        } catch (IOException exc) {
            LOGGER.error("Exception occurred when reading sample id {}, {}", sampleId, exc);
        }
        for (String accession : datasets) {
            PublicationDataset publicationDataset = new PublicationDataset();
            publicationDataset.setDatabaseID(DATASET_NAME);
            publicationDataset.setDatasetID(accession);
            publicationDataset.setOmicsType(Constants.REANALYZED_TYPE);
            dataset.add(publicationDataset);
        }
        return dataset;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(datasetAnnotationService, "The dataset annotation object can't be null");
        Assert.notNull(datasetService, "The dataset service can't be null");
    }
}
