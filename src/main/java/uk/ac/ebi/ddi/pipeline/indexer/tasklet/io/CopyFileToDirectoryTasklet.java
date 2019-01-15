package uk.ac.ebi.ddi.pipeline.indexer.tasklet.io;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.pipeline.indexer.io.DDICleanDirectory;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;


import java.io.File;
import java.io.IOException;

/**
 * Copy a single file from one location to another.
 *
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 */

@Getter
@Setter
public class CopyFileToDirectoryTasklet extends AbstractTasklet {

    public static final Logger LOGGER = LoggerFactory.getLogger(CopyFileToDirectoryTasklet.class);

    private File sourceFile;
    private Resource targetDirectory;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws IOException {

        if (sourceFile == null) {
            LOGGER.warn("Skipping file copy, since there are no files listed!");
        } else {
            // there are files to copy, so let's try to get on with the job
            File target = targetDirectory.getFile();

            DDICleanDirectory.cleanDirectory(targetDirectory);

            Assert.state(sourceFile.isFile() && sourceFile.exists(),
                    "Source must be an existing file: " + sourceFile.getAbsolutePath());
            LOGGER.info("Copying file " + sourceFile.getAbsolutePath() + " to " + target.getAbsolutePath());

            FileUtils.copyFileToDirectory(sourceFile, target);

        }

        return RepeatStatus.FINISHED;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(sourceFile, "Source file cannot be null");
        Assert.notNull(targetDirectory, "Target file cannot be null");
    }
}
