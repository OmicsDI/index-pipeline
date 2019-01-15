package uk.ac.ebi.ddi.pipeline.indexer.tasklet.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

/**
 * Created by gaur on 01/06/17.
 */
public class IsClaimDatasetTasklet extends AnnotationXMLTasklet {

    public static final Logger LOGGER = LoggerFactory.getLogger(IsClaimDatasetTasklet.class);

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        try {
            datasetAnnotationService.updateDatasetClaim();
        } catch (Exception ex) {
            LOGGER.debug("Exception occurred, ", ex);
        }
        return RepeatStatus.FINISHED;
    }
}
