package uk.ac.ebi.ddi.pipeline.indexer.tasklet.generation;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.api.readers.mw.ws.client.MWWsConfigProd;


/**
 * Generate all the files from pX submission by crawling the ProteomeXchange Page
 * and parsing the XML files. For every INSERTED a file is created in the defined folder.
 *
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 29/09/15
 */
@Getter
@Setter
public class GenerateEBeyeMwXMLTasklet extends AbstractTasklet {

    public static final Logger LOGGER = LoggerFactory.getLogger(GenerateEBeyeMwXMLTasklet.class);

    private String outputDirectory;

    private MWWsConfigProd configProd;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        //Todo: why comment?
        //GenerateMWEbeFiles.generateMWXMLfiles(configProd, taxWsConfigProd, outputDirectory);

        return RepeatStatus.FINISHED;

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(outputDirectory, "Output directory cannot be null.");
        Assert.notNull(configProd, "configProd can't be null.");
    }
}
