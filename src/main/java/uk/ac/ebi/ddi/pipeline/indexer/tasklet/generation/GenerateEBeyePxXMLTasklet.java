package uk.ac.ebi.ddi.pipeline.indexer.tasklet.generation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.pipeline.indexer.io.DDICleanDirectory;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.px.GeneratePxEbeFiles;

import java.util.List;

/**
 * Generate all the files from pX submission by crawling the ProteomeXchange Page
 * and parsing the XML files. For every INSERTED a file is created in the defined folder.
 *
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 29/09/15
 */

public class GenerateEBeyePxXMLTasklet extends AbstractTasklet{

    public static final Logger logger = LoggerFactory.getLogger(GenerateEBeyePxXMLTasklet.class);

    private String pxURL;

    private String pxPrefix;

    private String endPoint;

    private String loopGap;

    private String outputDirectory;

    private List<String> databases;


    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        DDICleanDirectory.cleanDirectory(outputDirectory);

        GeneratePxEbeFiles.searchFilesWeb(Integer.valueOf(loopGap),Integer.valueOf(endPoint),pxPrefix,pxURL,outputDirectory, databases);

        return RepeatStatus.FINISHED;

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(outputDirectory, "Output directory cannot be null.");
        Assert.notNull(pxURL, "pxURL can't be null.");
        Assert.notNull(pxPrefix, "pxPrefix can't be null.");
        Assert.notNull(endPoint, "endPoint can't be null.");
        Assert.notNull(loopGap,"loopGap can't be null.");
    }

    public String getPxURL() {
        return pxURL;
    }

    public void setPxURL(String pxURL) {
        this.pxURL = pxURL;
    }

    public String getPxPrefix() {
        return pxPrefix;
    }

    public void setPxPrefix(String pxPrefix) {
        this.pxPrefix = pxPrefix;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getLoopGap() {
        return loopGap;
    }

    public void setLoopGap(String loopGap) {
        this.loopGap = loopGap;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public List<String> getDatabases() {
        return databases;
    }

    public void setDatabases(List<String> databases) {
        this.databases = databases;
    }
}
