package uk.ac.ebi.ddi.pipeline.indexer.tasklet.ebepx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.reader.GeneratePxEbeFiles;
import uk.ac.ebi.ddi.reader.utils.ReadProperties;


import java.io.File;


/**
 * Generate all the files from pX submission by crawling the ProteomeXchange Page and parsing the XML files
 *
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 29/09/15
 */

public class GenerateEBeyePxXMLTasklet extends AbstractTasklet{

    public static final Logger logger = LoggerFactory.getLogger(GenerateEBeyePxXMLTasklet.class);

    String pxURL;

    private String outputDirectory;


    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        String pxPrefix = ReadProperties.getInstance().getProperty("pxPrefix");

        Integer endPoint   = Integer.valueOf(ReadProperties.getInstance().getProperty("pxEnd"));

        Integer loopGap = Integer.valueOf(ReadProperties.getInstance().getProperty("loopGap"));

        GeneratePxEbeFiles.searchFilesWeb(loopGap,endPoint,pxPrefix,pxURL,outputDirectory);

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(submissionFile, "Summary PX file cannot be null.");
        Assert.notNull(projectAccession, "PX accession cannot be null.");
        Assert.notNull(projectRepository, "Proj repo cannot be null.");
        Assert.notNull(outputDirectory, "Output directory cannot be null.");
    }
}
