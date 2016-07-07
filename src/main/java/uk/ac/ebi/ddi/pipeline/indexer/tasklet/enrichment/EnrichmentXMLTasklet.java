package uk.ac.ebi.ddi.pipeline.indexer.tasklet.enrichment;

import org.json.JSONException;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.web.client.RestClientException;
import uk.ac.ebi.ddi.annotation.model.EnrichedDataset;
import uk.ac.ebi.ddi.annotation.service.dataset.DDIDatasetAnnotationService;
import uk.ac.ebi.ddi.annotation.service.synonyms.DDIAnnotationService;
import uk.ac.ebi.ddi.annotation.utils.DataType;
import uk.ac.ebi.ddi.annotation.service.dataset.DatasetAnnotationEnrichmentService;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.ddi.service.db.model.dataset.Dataset;
import uk.ac.ebi.ddi.service.db.utils.DatasetCategory;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.Assert;
import uk.ac.ebi.ddi.xml.validator.exception.DDIException;

/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 09/12/2015
 */
public class EnrichmentXMLTasklet extends AbstractTasklet{

    public static final Logger logger = LoggerFactory.getLogger(EnrichmentXMLTasklet.class);

    String databaseName;

    DDIAnnotationService annotationService;

    DDIDatasetAnnotationService datasetAnnotationService;

    DataType dataType;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        List<Dataset> datasets = datasetAnnotationService.getAllDatasetsByDatabase(databaseName);
        datasets = datasets.parallelStream()
                .filter(x -> x.getCurrentStatus().equalsIgnoreCase(DatasetCategory.INSERTED.getType()) ||
                        x.getCurrentStatus().equalsIgnoreCase(DatasetCategory.UPDATED.getType()))
                .collect(Collectors.toList());

        datasets.parallelStream().forEach( dataset -> {
            Dataset existingDataset = datasetAnnotationService.getDataset(dataset.getAccession(), dataset.getDatabase());
            EnrichedDataset enrichedDataset = null;
            try {
                enrichedDataset = DatasetAnnotationEnrichmentService.enrichment(annotationService, existingDataset, false);
                dataset = DatasetAnnotationEnrichmentService.addEnrichedFields(existingDataset, enrichedDataset);
                logger.debug(enrichedDataset.getEnrichedAttributes().toString());
                datasetAnnotationService.enrichedDataset(existingDataset);
                } catch (DDIException | RestClientException | UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }
        });

        return RepeatStatus.FINISHED;

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(databaseName, "Input databaseName can not be null");
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public DDIAnnotationService getAnnotationService() {
        return annotationService;
    }

    public void setAnnotationService(DDIAnnotationService annotationService) {
        this.annotationService = annotationService;
    }

    public void setDatasetAnnotationService(DDIDatasetAnnotationService datasetAnnotationService) {
        this.datasetAnnotationService = datasetAnnotationService;
    }

    public DDIDatasetAnnotationService getDatasetAnnotationService() {
        return datasetAnnotationService;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }
}
