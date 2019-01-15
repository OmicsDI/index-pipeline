package uk.ac.ebi.ddi.pipeline.indexer.tasklet.statistics;


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

@Setter
@Getter
public class DatasetDownloadCountTasklet extends AbstractTasklet {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatasetDownloadCountTasklet.class);

    SimilarityCounts similarityCount;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        try {
            similarityCount.addDatasetDownloadCount();
        } catch (Exception ex) {
            LOGGER.error("Exception occurred, {}", ex);
        }
        return RepeatStatus.FINISHED;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(similarityCount, "The similarity counts object can't be null");
    }
}
