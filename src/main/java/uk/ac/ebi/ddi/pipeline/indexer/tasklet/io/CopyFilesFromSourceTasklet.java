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
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Copy file list of files to directory
 *
 * @author Yasset Perez-Riverol
 * @version $Id$
 */

@Getter
@Setter
public class CopyFilesFromSourceTasklet extends AbstractTasklet {
    public static final Logger LOGGER = LoggerFactory.getLogger(CopyFilesFromSourceTasklet.class);

    private Resource sourceDirectory;
    private Resource targetDirectory;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        List<File> sourceFiles = new ArrayList<>();
        if (sourceDirectory.exists() && sourceDirectory.getFile().isDirectory()) {
            File[] files = sourceDirectory.getFile().listFiles();
            if (files != null) {
                sourceFiles = Arrays.asList(files);
            }
        }

        if (sourceFiles.isEmpty()) {
            LOGGER.warn("Skipping file copy, since there are no files listed!");
        } else {
            // there are files to copy, so let's try to get on with the job
            File target = targetDirectory.getFile();

            for (File sourceFile : sourceFiles) {
                if (sourceFile.isFile()) {
                    Assert.state(sourceFile.exists(),
                            "Source must be an existing file: " + sourceFile.getAbsolutePath());
                    LOGGER.info("Copying file " + sourceFile.getAbsolutePath() + " to " + target.getAbsolutePath());
                    FileUtils.copyFileToDirectory(sourceFile, target);
                }
            }
        }
        return RepeatStatus.FINISHED;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(targetDirectory, "Target directory can not be null");
    }
}
