package uk.ac.ebi.ddi.pipeline.indexer.tasklet.statistics;


import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.similarityCalculator.SimilarityCounts;

public class DatasetDownloadCountTasklet extends AbstractTasklet {


    SimilarityCounts similarityCount;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        try {
            similarityCount.addDatasetDownloadCount();
        }
        catch(Exception ex)
        {
            logger.debug(ex.getMessage());
        }
        return RepeatStatus.FINISHED;
    }

    public SimilarityCounts getSimilarityCount() {
        return similarityCount;
    }

    public void setSimilarityCount(SimilarityCounts similarityCount) {
        this.similarityCount = similarityCount;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(similarityCount, "The similarity counts object can't be null");
    }
}
