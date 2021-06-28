package uk.ac.ebi.ddi.pipeline.indexer.tasklet.biostudies;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.biostudies.BioStudiesService;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.similarity.CitationSimilarityTasklet;
import uk.ac.ebi.ddi.similarityCalculator.SimilarityCounts;

import java.io.File;

@Getter
@Setter
public class ImportBioStudiesTasklet extends AbstractTasklet {

    private static final Logger LOGGER = LoggerFactory.getLogger(CitationSimilarityTasklet.class);

    Resource inputDirectory;

    BioStudiesService bioStudiesService;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        try {
            LOGGER.info("DataSet import, inputDirectory: {} ", inputDirectory.getURI());

            File inputFile = inputDirectory.getFile();
            if (inputFile == null) {
                LOGGER.warn("Input directory is empty, {}", inputDirectory.getFile().getAbsolutePath());
                return RepeatStatus.FINISHED;
            }
            bioStudiesService.saveStudies(inputFile.getPath());
        } catch (Exception ex) {
            LOGGER.error("Exception occurred, ", ex);
        }
        return RepeatStatus.FINISHED;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(bioStudiesService, "The biostudies service object can't be null");
    }
}
