package uk.ac.ebi.ddi.pipeline.indexer.tasklet.enrichment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.annotation.service.dataset.DDIDatasetAnnotationService;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;

/**
 * Created by gaur on 01/06/17.
 */
public class ClaimDatasetTasklet extends AbstractTasklet {

    DDIDatasetAnnotationService datasetAnnotationService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ClaimDatasetTasklet.class);

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        try {
            datasetAnnotationService.updateDatasetClaim();
        } catch (Exception ex) {
            LOGGER.error("Exception occurred, ", ex);
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
