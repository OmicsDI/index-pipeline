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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
public class GeoEnrichmentTasklet extends AbstractTasklet {

    private DDIDatasetAnnotationService datasetAnnotationService;

    private IDatasetService datasetService;

    private static final String DATASET_NAME = "GEO";

    private static final String NCBI_ENDPOINT = "https://www.ncbi.nlm.nih.gov/geo/query/acc.cgi";

    private static final Logger LOGGER = LoggerFactory.getLogger(GeoEnrichmentTasklet.class);

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        List<Dataset> datasets = datasetService.readDatasetHashCode(DATASET_NAME);
        for (Dataset dataset : datasets) {
            List<String> sampleIds = getSampleIds(dataset);
            for (String sampleId : sampleIds) {
                Set<PublicationDataset> publicationDatasets = getReanalysisDataset(sampleId);
                datasetAnnotationService.addDatasetSimilars(dataset, publicationDatasets, Constants.REANALYZED_TYPE);
            }
        }
        return null;
    }

    public List<String> getSampleIds(Dataset dataset) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(NCBI_ENDPOINT)
                .queryParam("acc", dataset.getAccession())
                .queryParam("targ", "self")
                .queryParam("form", "text");
        ResponseEntity<String> response = restTemplate.getForEntity(builder.build().toString(), String.class);
        List<String> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new StringReader(response.getBody()))) {
            String line = reader.readLine();
            while (line != null) {
                String patternString = "\\s*!Series_sample_id\\s*=\\s*(\\w*)";
                Pattern pattern = Pattern.compile(patternString);
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    result.add(matcher.group(1));
                }
                line = reader.readLine();
            }
        } catch (IOException exc) {
            LOGGER.error("Exception occurred when reading dataset {}, {}", dataset, exc);
        }
        return result;
    }

    public Set<PublicationDataset> getReanalysisDataset(String sampleId) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(NCBI_ENDPOINT)
                .queryParam("acc", sampleId)
                .queryParam("targ", "self")
                .queryParam("form", "text");
        ResponseEntity<String> response = restTemplate.getForEntity(builder.build().toString(), String.class);
        Set<PublicationDataset> dataset = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new StringReader(response.getBody()))) {
            String line = reader.readLine();
            while (line != null) {
                String patternString = "\\s*Reanalyzed by:\\s*(\\w*)";
                Pattern pattern = Pattern.compile(patternString);
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    PublicationDataset publicationDataset = new PublicationDataset();
                    publicationDataset.setDatabaseID(DATASET_NAME);
                    publicationDataset.setDatasetID(matcher.group(1));
                    publicationDataset.setOmicsType(Constants.REANALYZED_TYPE);
                    dataset.add(publicationDataset);
                }
                line = reader.readLine();
            }
        } catch (IOException exc) {
            LOGGER.error("Exception occurred when reading sample id {}, {}", sampleId, exc);
        }
        return dataset;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(datasetAnnotationService, "The dataset annotation object can't be null");
        Assert.notNull(datasetService, "The dataset service can't be null");
    }
}
