package uk.ac.ebi.ddi.pipeline.indexer.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.pipeline.indexer.utils.Constants;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

/**
 * Generate sub directory in a target directory based on a given creation date
 * <p/>
 * The target directory will look like this:
 * target/year/month
 *
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 29/09/15
 */
public class DateDirectoryGenerator {
    public static final Logger LOGGER = LoggerFactory.getLogger(DateDirectoryGenerator.class);

    private Resource targetDirectory;

    public DateDirectoryGenerator(Resource targetDirectory) {
        this.targetDirectory = targetDirectory;
    }

    public File generate(long date) throws IOException {
        File targetDir = targetDirectory.getFile();
        LOGGER.info("Generating dated directory in " + targetDir.getAbsolutePath());
        Assert.state(targetDir.isDirectory() && targetDir.exists(), "Target directory must be a valid directory");

        File newDir = generateNewDirectoryName(date, targetDir);
        if (!newDir.exists()) {
            boolean created = newDir.mkdirs();
            if (!created) {
                String msg = "Failed to create directory " + newDir.getAbsolutePath();
                LOGGER.error(msg);
                throw new IOException(msg);
            }
        }

        return newDir;
    }

    private File generateNewDirectoryName(long date, File targetDir) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        int month = calendar.get(Calendar.MONTH) + 1;
        String monthStr = month + "";
        if (month < 10) {
            monthStr = "0" + month;
        }
        String path = targetDir.getAbsolutePath() + Constants.FILE_SEPARATOR + calendar.get(Calendar.YEAR)
                + Constants.FILE_SEPARATOR + monthStr;
        return new File(path);
    }
}
