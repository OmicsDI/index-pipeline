package uk.ac.ebi.ddi.pipeline.indexer.tasklet.similarity;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.similarityCalculator.SimilarityCounts;

/**
 * Created by gaur on 02/08/17.
 */
@Getter
@Setter
public class EBISearchPubmedTasklet extends AbstractTasklet {

    SimilarityCounts similarityCounts;

    private static final Logger LOGGER = LoggerFactory.getLogger(EBISearchPubmedTasklet.class);


    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        try {
            similarityCounts.saveSearchcounts();
        } catch (Exception ex) {
            LOGGER.debug("Exception occurred, ", ex);
        }
        return RepeatStatus.FINISHED;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(similarityCounts, "The similarity count object can't be null");
    }
}
