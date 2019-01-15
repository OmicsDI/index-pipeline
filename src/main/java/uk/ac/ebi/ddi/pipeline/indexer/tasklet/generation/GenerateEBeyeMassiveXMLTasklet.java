package uk.ac.ebi.ddi.pipeline.indexer.tasklet.generation;

import lombok.Getter;
import lombok.Setter;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.api.readers.massive.ws.client.MassiveWsConfigProd;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;

/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 10/11/15
 */
@Getter
@Setter
public class GenerateEBeyeMassiveXMLTasklet extends AbstractTasklet {

    private String outputDirectory;

    private MassiveWsConfigProd massiveConfig;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(outputDirectory, "Output directory cannot be null.");
        Assert.notNull(massiveConfig,   "The massive Config can't be null");
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        //Todo: What is this pipeline?
        //IGenerator generator = GenerateMassiveEbeFiles.generateMWXMLfiles(massiveConfig, outputDirectory);
        return RepeatStatus.FINISHED;
    }
}
