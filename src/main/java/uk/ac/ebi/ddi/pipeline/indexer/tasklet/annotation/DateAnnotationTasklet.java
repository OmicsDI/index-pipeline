package uk.ac.ebi.ddi.pipeline.indexer.tasklet.annotation;

import org.json.JSONException;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClientException;
import uk.ac.ebi.ddi.annotation.model.EnrichedDataset;
import uk.ac.ebi.ddi.annotation.service.database.DDIDatabaseAnnotationService;
import uk.ac.ebi.ddi.annotation.service.dataset.DDIDatasetAnnotationService;
import uk.ac.ebi.ddi.annotation.service.dataset.DatasetAnnotationEnrichmentService;
import uk.ac.ebi.ddi.annotation.utils.Constants;
import uk.ac.ebi.ddi.annotation.utils.DatasetUtils;
import uk.ac.ebi.ddi.pipeline.indexer.annotation.DatasetAnnotationFieldsService;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.service.db.model.dataset.Database;
import uk.ac.ebi.ddi.service.db.model.dataset.Dataset;
import uk.ac.ebi.ddi.service.db.model.publication.PublicationDataset;
import uk.ac.ebi.ddi.service.db.utils.DatasetSimilarsType;
import uk.ac.ebi.ddi.xml.validator.exception.DDIException;
import uk.ac.ebi.ddi.xml.validator.utils.Field;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by yperez on 13/07/2016.
 */
public class DateAnnotationTasklet extends AbstractTasklet{

    DDIDatasetAnnotationService datasetAnnotationService;

    DDIDatabaseAnnotationService databaseAnnotationService;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        List<Database> databases = databaseAnnotationService.getDatabases();
        if(databases != null && !databases.isEmpty()){
            databases.parallelStream().forEach(database ->{
                List<Dataset> datasets = datasetAnnotationService.getAllDatasetsByDatabase(database.getName());
                datasets.parallelStream().forEach( dataset -> {
                    Dataset existingDataset = datasetAnnotationService.getDataset(dataset.getAccession(), dataset.getDatabase());
                    existingDataset = DatasetAnnotationFieldsService.refineDates(existingDataset);
                    datasetAnnotationService.updateDataset(existingDataset);
                });
            });
        }
        return RepeatStatus.FINISHED;
    }

    public DDIDatasetAnnotationService getDatasetAnnotationService() {
        return datasetAnnotationService;
    }

    public void setDatasetAnnotationService(DDIDatasetAnnotationService datasetAnnotationService) {
        this.datasetAnnotationService = datasetAnnotationService;
    }

    public DDIDatabaseAnnotationService getDatabaseAnnotationService() {
        return databaseAnnotationService;
    }

    public void setDatabaseAnnotationService(DDIDatabaseAnnotationService databaseAnnotationService) {
        this.databaseAnnotationService = databaseAnnotationService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(datasetAnnotationService, "The dataset annotation object can't be null");
        Assert.notNull(databaseAnnotationService, "The database annotation object can't be null");
    }
}
