package uk.ac.ebi.ddi.pipeline.indexer.tasklet.io;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.pipeline.indexer.io.DDICleanDirectory;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;

import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 07/10/15
 */
@Getter
@Setter
public class DownloadFilesFromFTPTasklet extends AbstractTasklet {

    public static final Logger LOGGER = LoggerFactory.getLogger(DownloadFilesFromFTPTasklet.class);

    // File to be download
    private String sourceDirectory;

    //Folder to copy the results file
    private Resource targetDirectory;

    // User of the ftp
    private String user;

    //Passowrd for the FTP
    private String password;

    // Server of the ftp
    private String server;

    // Port of the FTP the default port fort most fo the services is 21
    private int port = 21;

    //Pattern of files to be download
    private String pattern;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(server, "Server can't be null!");
        Assert.notNull(password, "Password can't be null!");
        Assert.notNull(user, "User can't be null!");
        Assert.notNull(sourceDirectory, "Source directory in the ftp can't be null!");
        Assert.notNull(targetDirectory, "The target directory in the local machine can't be null!");
        Assert.notNull(pattern, "The pattern can't be null!");
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        DDICleanDirectory.cleanDirectory(targetDirectory);

        //new ftp client
        FTPClient ftp = new FTPClient();
        //try to connect
        ftp.connect(server, port);
        //login to server
        if (!ftp.login(user, password)) {
            ftp.logout();
        }
        int reply = ftp.getReplyCode();
        //FTPReply stores a set of constants for FTP reply codes.
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
        }

        //enter passive mode
        ftp.enterLocalPassiveMode();
        //get system name
        LOGGER.info("Remote system is {}", ftp.getSystemType());
        //change current directory
        ftp.changeWorkingDirectory(sourceDirectory);

        LOGGER.info("Current directory is {}", ftp.printWorkingDirectory());

        LOGGER.info("pattern is {}", pattern);

        //get list of filenames
        FTPFile[] ftpFiles = ftp.listFiles();

        LOGGER.info("number of files are " + ftpFiles.length);
        LOGGER.info("file names are {}", ftpFiles);
        for (FTPFile file : ftpFiles) {
            LOGGER.info("file name is " + file.getName());
            LOGGER.info("does file has pattern {}", file.getName().contains(pattern));
            if ((file.isFile() || file.isSymbolicLink())
                    && (pattern != null && !pattern.isEmpty() && file.getName().contains(pattern))) {
                LOGGER.info("File is " + file.getName());
                try (OutputStream output = new FileOutputStream(
                        targetDirectory.getFile() + "/" + file.getName())) {
                    ftp.retrieveFile(file.getName(), output);
                }
            }
        }
        ftp.logout();
        ftp.disconnect();

        return RepeatStatus.FINISHED;
    }
}
