package uk.ac.ebi.ddi.pipeline.indexer.tasklet.ebepx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.reader.GeneratePxEbeFiles;

/**
 * Generate all the files from pX submission by crawling the ProteomeXchange Page and parsing the XML files
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


    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        GeneratePxEbeFiles.searchFilesWeb(Integer.valueOf(loopGap),Integer.valueOf(endPoint),pxPrefix,pxURL,outputDirectory);

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
}
