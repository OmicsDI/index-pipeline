package uk.ac.ebi.ddi.pipeline.indexer.tasklet.enrichment;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClientException;
import uk.ac.ebi.ddi.annotation.model.EnrichedDataset;
import uk.ac.ebi.ddi.annotation.service.database.DDIDatabaseAnnotationService;
import uk.ac.ebi.ddi.annotation.service.dataset.DDIDatasetAnnotationService;
import uk.ac.ebi.ddi.annotation.service.dataset.DatasetAnnotationEnrichmentService;
import uk.ac.ebi.ddi.annotation.service.synonyms.DDIAnnotationService;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.service.db.model.dataset.Database;
import uk.ac.ebi.ddi.service.db.model.dataset.Dataset;
import uk.ac.ebi.ddi.xml.validator.exception.DDIException;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by yperez on 07/07/2016.
 */
public class OverWriteEnrichmentXMLTasklet extends AbstractTasklet{

    public static final Logger logger = LoggerFactory.getLogger(EnrichmentXMLTasklet.class);

    DDIDatabaseAnnotationService databaseAnnotationService;

    DDIAnnotationService annotationService;

    DDIDatasetAnnotationService datasetAnnotationService;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        List<Database> databases = databaseAnnotationService.getDatabases();
        if(databases != null && !databases.isEmpty()){
            databases.parallelStream().forEach(database ->{
                List<Dataset> datasets = datasetAnnotationService.getAllDatasetsByDatabase(database.getName());
                datasets.parallelStream().forEach( dataset -> {
                    Dataset existingDataset = datasetAnnotationService.getDataset(dataset.getAccession(), dataset.getDatabase());
                    EnrichedDataset enrichedDataset = null;
                    try {
                        enrichedDataset = DatasetAnnotationEnrichmentService.enrichment(annotationService, existingDataset, true);
                        dataset = DatasetAnnotationEnrichmentService.addEnrichedFields(existingDataset, enrichedDataset);
                        logger.debug(enrichedDataset.getEnrichedAttributes().toString());
                        datasetAnnotationService.enrichedDataset(existingDataset);
                    } catch (DDIException | RestClientException | UnsupportedEncodingException | JSONException e) {
                        e.printStackTrace();
                    }
                });
            });
        }
        return RepeatStatus.FINISHED;

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(databaseAnnotationService, "Input databaseService can not be null");
        Assert.notNull(annotationService, "annotation Service can not be null");
        Assert.notNull(datasetAnnotationService, "dataset Service can not be null");

    }

    public DDIDatabaseAnnotationService getDatabaseAnnotationService() {
        return databaseAnnotationService;
    }

    public void setDatabaseAnnotationService(DDIDatabaseAnnotationService databaseAnnotationService) {
        this.databaseAnnotationService = databaseAnnotationService;
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


}
