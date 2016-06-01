package uk.ac.ebi.ddi.pipeline.indexer.tasklet.io;

import org.apache.commons.io.FileUtils;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 26/10/15
 */
public class CopyFilesWithPatternFromSourceTasklet extends AbstractTasklet{

    Resource inputDirectory;

    Resource outputDirectory;

    String pattern;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(inputDirectory, "The input Directory can't be null!");
        Assert.notNull(outputDirectory," The output Directory can't be null!");
        Assert.notNull(pattern, "The pattern can be null!!!");
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        List<File> sourceFiles = new ArrayList<>();
        if(inputDirectory.exists() && inputDirectory.getFile().isDirectory()){
            sourceFiles = Arrays.asList(inputDirectory.getFile().listFiles());
        }

        if (sourceFiles == null || sourceFiles.isEmpty()) {
            logger.warn("Skipping file copy, since there are no files listed!");
        } else {
            // there are files to copy, so let's try to get on with the job
            File target = outputDirectory.getFile();

            for (File sourceFile : sourceFiles) {
                if(sourceFile.isFile() && sourceFile.getName().contains(pattern)){
                    Assert.state(sourceFile.isFile() && sourceFile.exists(), "Source must be an existing file: " + sourceFile.getAbsolutePath());
                    logger.info("Copying file " + sourceFile.getAbsolutePath() + " to " + target.getAbsolutePath());
                    FileUtils.copyFileToDirectory(sourceFile, target);
                }
            }
        }

        return RepeatStatus.FINISHED;
    }

    public Resource getInputDirectory() {
        return inputDirectory;
    }

    public void setInputDirectory(Resource inputDirectory) {
        this.inputDirectory = inputDirectory;
    }

    public Resource getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(Resource outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
}
