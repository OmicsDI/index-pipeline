package uk.ac.ebi.ddi.pipeline.indexer.tasklet.validation;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.annotation.service.dataset.DDIDatasetAnnotationService;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.pipeline.indexer.utils.Constants;

public class PrivateUpdateDatasetsTasklet extends AbstractTasklet {
    DDIDatasetAnnotationService datasetAnnotationService;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        try {
            datasetAnnotationService.updatePrivateDataset(Constants.BIOMODELS_DATABASE);
        }
        catch(Exception ex)
        {
            logger.debug(ex.getMessage());
        }
        return RepeatStatus.FINISHED;
    }

    public DDIDatasetAnnotationService getDatasetAnnotationService() {
        return datasetAnnotationService;
    }

    public void setDatasetAnnotationService(DDIDatasetAnnotationService datasetAnnotationService) {
        this.datasetAnnotationService = datasetAnnotationService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(datasetAnnotationService, "The dataset annotation object can't be null");
    }
}
