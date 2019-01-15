package uk.ac.ebi.ddi.pipeline.indexer.tasklet.io;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.UnexpectedJobExecutionException;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.pipeline.indexer.utils.FileUtil;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Decompress a list of compressed files
 * <p/>
 * NOTE: only zip and gzip format are supported at the moment
 * <p/>
 *
 * @author Yasset PErez-Riverol
 * @version $Id$
 */
@Getter
@Setter
public class DecompressFilesTasklet extends AbstractTasklet {

    public static final Logger LOGGER = LoggerFactory.getLogger(DecompressFilesTasklet.class);

    public static final String ZIP_FILE_EXTENSION = "zip";
    public static final String GZIP_FILE_EXTENSION = "gz";
    public static final int BUFFER_SIZE = 1024;

    private Resource originalDirectory;
    private Resource targetDirectory;
    private boolean deleteOriginal = false;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        File targetDir = targetDirectory.getFile();
        File originDir = originalDirectory.getFile();

        Assert.state(targetDir.exists() && targetDir.isDirectory(),
                "Target output directory must exists");

        if (originDir.isDirectory()) {
            File[] files = originDir.listFiles();
            if (files == null) {
                return RepeatStatus.FINISHED;
            }
            for (File compressedFile : files) {
                LOGGER.info("Decompressing {}", compressedFile.getAbsolutePath());

                String fileExtension = FileUtil.getFileExtension(compressedFile).toLowerCase();

                switch (fileExtension) {
                    case ZIP_FILE_EXTENSION:
                        unzip(compressedFile, targetDir);
                        break;
                    case GZIP_FILE_EXTENSION:
                        gunzip(compressedFile, targetDir);
                        break;
                    default:
                        break;
                }
                deleteOriginalCompressedFile(compressedFile);
            }
        }
        return RepeatStatus.FINISHED;
    }

    /**
     * decompress zipped file, note: decompress all files, but only returns last one
     * ToDo: find better way to handle multiple files in one archive!
     */
    private File unzip(File file, File targetDir) throws IOException {
        File decompressedFile = null;
        FileInputStream fileInputStream = new FileInputStream(file);
        try (ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(fileInputStream))) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                decompressedFile = new File(targetDir.getAbsolutePath() + File.separator + zipEntry.getName());

                //create directories if required.
                decompressedFile.getParentFile().mkdirs();

                if (!zipEntry.isDirectory()) {
                    int count;
                    byte[] buffer = new byte[BUFFER_SIZE];

//                    decompressedFile = new File(targetDir.getAbsolutePath() + File.separator + zipEntry.getName());
                    FileOutputStream fileOutputStream = new FileOutputStream(decompressedFile);

                    try (BufferedOutputStream stream = new BufferedOutputStream(fileOutputStream, BUFFER_SIZE)) {
                        while ((count = zipInputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
                            stream.write(buffer, 0, count);
                            stream.flush();
                        }
                    }
                }
            }
        }

        return decompressedFile;
    }

    private void deleteOriginalCompressedFile(File compressedFile) {
        if (deleteOriginal && compressedFile.isFile()) {
            boolean deleted = FileUtils.deleteQuietly(compressedFile);
            if (!deleted) {
                String msg = "Failed to delete compressed file " + compressedFile.getAbsolutePath();
                LOGGER.error(msg);
                throw new UnexpectedJobExecutionException(msg);
            }
        }
    }

    /**
     * decompress gzip file
     */
    private File gunzip(File file, File targetDir) throws IOException {
        File decompressedFile;
        FileInputStream fileInputStream = new FileInputStream(file);


        int count;
        byte[] buffer = new byte[BUFFER_SIZE];

        String newFileName = file.getName().substring(0, file.getName().lastIndexOf("."));
        decompressedFile = new File(targetDir.getAbsolutePath() + File.separator + newFileName);
        FileOutputStream fileOutputStream = new FileOutputStream(decompressedFile);


        try (
                GZIPInputStream gzipInputStream = new GZIPInputStream(new BufferedInputStream(fileInputStream));
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream, BUFFER_SIZE)
        ) {
            while ((count = gzipInputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
                bufferedOutputStream.write(buffer, 0, count);
                bufferedOutputStream.flush();
            }
        }

        return decompressedFile;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(originalDirectory, "Original directory with the compress files");
        Assert.notNull(targetDirectory, "Targeted directory for the output cannot be empty");
    }
}
