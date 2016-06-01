package uk.ac.ebi.ddi.pipeline.indexer.tasklet.similarity;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.annotation.service.dataset.DDIDatasetAnnotationService;
import uk.ac.ebi.ddi.annotation.service.synonyms.DDIExpDataImportService;
import uk.ac.ebi.ddi.annotation.utils.DataType;
import uk.ac.ebi.ddi.annotation.service.dataset.DatasetAnnotationEnrichmentService;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.service.db.model.dataset.Dataset;
import uk.ac.ebi.ddi.service.db.utils.DatasetCategory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 25/11/2015
 */
public class UpdateTermsTasklet extends AbstractTasklet {

    String databaseName;

    DataType dataType;

    DDIExpDataImportService ddiExpDataImportService;

    DDIDatasetAnnotationService datasetAnnotationService;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(databaseName, "Input Directory can not be null");
        Assert.notNull(ddiExpDataImportService, "This Service can't be null");
        Assert.notNull(dataType, "The datatype can't be null");
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        List<Dataset> datasets = datasetAnnotationService.getAllDatasetsByDatabase(databaseName);
        datasets = datasets.parallelStream()
                .filter(x -> x.getCurrentStatus().equalsIgnoreCase(DatasetCategory.INSERTED.getType()) ||
                        x.getCurrentStatus().equalsIgnoreCase(DatasetCategory.UPDATED.getType()) ||
                        x.getCurrentStatus().equalsIgnoreCase(DatasetCategory.ENRICHED.getType())
                )
                .collect(Collectors.toList());
        datasets.parallelStream().forEach(dataset -> {
            dataset = datasetAnnotationService.getDataset(dataset.getAccession(), dataset.getDatabase());
            DatasetAnnotationEnrichmentService.importTermsToDatabase(dataset, dataType,ddiExpDataImportService);
        });
        return RepeatStatus.FINISHED;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public DDIDatasetAnnotationService getDatasetAnnotationService() {
        return datasetAnnotationService;
    }

    public void setDatasetAnnotationService(DDIDatasetAnnotationService datasetAnnotationService) {
        this.datasetAnnotationService = datasetAnnotationService;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public DDIExpDataImportService getDdiExpDataImportService() {
        return ddiExpDataImportService;
    }

    public void setDdiExpDataImportService(DDIExpDataImportService ddiExpDataImportService) {
        this.ddiExpDataImportService = ddiExpDataImportService;
    }

    public DataType getDataType() {
        return dataType;
    }
}
