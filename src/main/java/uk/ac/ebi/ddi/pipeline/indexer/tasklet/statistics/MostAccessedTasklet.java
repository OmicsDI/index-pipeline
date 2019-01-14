package uk.ac.ebi.ddi.pipeline.indexer.tasklet.statistics;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.annotation.service.dataset.DDIDatasetAnnotationService;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;

/**
 * Created by gaur on 25/06/17.
 */
@Getter
@Setter
public class MostAccessedTasklet extends AbstractTasklet {

    private static final Logger LOGGER = LoggerFactory.getLogger(MostAccessedTasklet.class);

    DDIDatasetAnnotationService datasetAnnotationService;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        try {
            datasetAnnotationService.updateMostAccessed();
        } catch (Exception ex) {
            LOGGER.error("Exception occurred, ", ex);
        }
        return RepeatStatus.FINISHED;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(datasetAnnotationService, "The dataset annotation object can't be null");
    }
}
