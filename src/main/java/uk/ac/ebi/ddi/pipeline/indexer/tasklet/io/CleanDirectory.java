package uk.ac.ebi.ddi.pipeline.indexer.tasklet.io;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 05/05/2016
 */
public class CleanDirectory extends AbstractTasklet{

    Resource inputDirectory;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        if(inputDirectory != null && inputDirectory.getFile().isDirectory() && inputDirectory.getFile().listFiles().length > 0){
            Arrays.asList(inputDirectory.getFile().listFiles()).stream().forEach(file -> {
                try {
                    Files.deleteIfExists(file.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        return RepeatStatus.FINISHED;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(inputDirectory, "Target directory can not be null");
    }

    public Resource getInputDirectory() {
        return inputDirectory;
    }

    public void setInputDirectory(Resource inputDirectory) {
        this.inputDirectory = inputDirectory;
    }
}
