package uk.ac.ebi.ddi.pipeline.indexer.tasklet.io;

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
public class DownloadFilesFromFTPTasklet extends AbstractTasklet {

    public static final Logger logger = LoggerFactory.getLogger(DownloadFilesFromFTPTasklet.class);

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
        Assert.notNull(port,   "Port   can't be null!");
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
        logger.debug("Remote system is " + ftp.getSystemType());
        //change current directory
        ftp.changeWorkingDirectory(sourceDirectory);

        logger.debug("Current directory is " + ftp.printWorkingDirectory());

        //get list of filenames
        FTPFile[] ftpFiles = ftp.listFiles();

        if (ftpFiles != null && ftpFiles.length > 0) {
            for (FTPFile file : ftpFiles) {
                if((file.isFile() || file.isSymbolicLink()) && (pattern !=null && !pattern.isEmpty() && file.getName().contains(pattern))){
                    System.out.println("File is " + file.getName());
                    //get output stream
                    OutputStream output;
                    output = new FileOutputStream(targetDirectory.getFile() + "/" + file.getName());
                    //get the file from the remote system
                    ftp.retrieveFile(file.getName(), output);
                    //close output stream
                    output.close();
                }
            }
        }
        ftp.logout();
        ftp.disconnect();

        return RepeatStatus.FINISHED;
    }

    public String getSourceDirectory() {
        return sourceDirectory;
    }

    public void setSourceDirectory(String sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    public Resource getTargetDirectory() {
        return targetDirectory;
    }

    public void setTargetDirectory(Resource targetDirectory) {
        this.targetDirectory = targetDirectory;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
}
