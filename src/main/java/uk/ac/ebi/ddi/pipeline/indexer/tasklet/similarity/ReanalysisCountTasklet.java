package uk.ac.ebi.ddi.pipeline.indexer.tasklet.similarity;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.similarityCalculator.SimilarityCounts;

/**
 * Created by gaur on 02/08/17.
 */
public class ReanalysisCountTasklet extends AbstractTasklet {

    SimilarityCounts similarityCounts;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        try {
            similarityCounts.saveReanalysisCount();
        }
        catch(Exception ex)
        {
            logger.debug(ex.getMessage());
        }
        return RepeatStatus.FINISHED;
    }

    public SimilarityCounts getSimilarityCounts() {
        return similarityCounts;
    }

    public void setSimilarityCounts(SimilarityCounts similarityCounts) {
        this.similarityCounts = similarityCounts;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(similarityCounts, "The similarity count object can't be null");
    }
}


