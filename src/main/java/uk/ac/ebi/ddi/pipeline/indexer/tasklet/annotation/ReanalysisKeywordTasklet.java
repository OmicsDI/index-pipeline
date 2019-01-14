package uk.ac.ebi.ddi.pipeline.indexer.tasklet.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.similarityCalculator.SimilarityCounts;

public class ReanalysisKeywordTasklet extends AbstractTasklet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReanalysisKeywordTasklet.class);

    SimilarityCounts similarityCounts;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        try {
            similarityCounts.addReanalysisKeyword();
        } catch (Exception ex) {
            LOGGER.error("Exception occurred when add reanalysis keywords, ", ex);
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

