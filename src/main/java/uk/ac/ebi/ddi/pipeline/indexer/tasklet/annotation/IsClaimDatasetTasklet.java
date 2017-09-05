package uk.ac.ebi.ddi.pipeline.indexer.tasklet.annotation;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

/**
 * Created by gaur on 01/06/17.
 */
public class IsClaimDatasetTasklet extends AnnotationXMLTasklet {

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        try {
            datasetAnnotationService.updateDatasetClaim();
        }
        catch(Exception ex)
        {
            logger.debug(ex.getMessage());
        }
        return RepeatStatus.FINISHED;
    }
}
