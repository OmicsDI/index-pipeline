package uk.ac.ebi.ddi.pipeline.indexer.tasklet.generation;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.Assert;
/*import uk.ac.ebi.ddi.arrayexpress.reader.ExperimentReader;
import uk.ac.ebi.ddi.arrayexpress.reader.GenerateArrayExpressFile;
import uk.ac.ebi.ddi.arrayexpress.reader.ProtocolReader;
import uk.ac.ebi.ddi.arrayexpress.reader.model.experiments.Experiments;
import uk.ac.ebi.ddi.arrayexpress.reader.model.protocols.Protocols;*/
import uk.ac.ebi.ddi.arrayexpress.reader.ExperimentReader;
import uk.ac.ebi.ddi.arrayexpress.reader.GenerateArrayExpressFile;
import uk.ac.ebi.ddi.arrayexpress.reader.ProtocolReader;
import uk.ac.ebi.ddi.arrayexpress.reader.model.experiments.Experiments;
import uk.ac.ebi.ddi.arrayexpress.reader.model.protocols.Protocols;
import uk.ac.ebi.ddi.arrayexpress.reader.utils.Constants;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;

import java.io.File;


/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 03/05/2016
 */
@Getter
@Setter
public class GenerationArrayExpressXML extends AbstractTasklet {

    public static final Logger LOGGER = LoggerFactory.getLogger(GenerationArrayExpressXML.class);

    private String outputFile;

    private String experimentFileName;

    private String protocolFileName;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        File omicsDIFile = new File(outputFile);
        Experiments experiments = new ExperimentReader(new File(experimentFileName)).getExperiments();
        Protocols protocols = new ProtocolReader(new File(protocolFileName)).getProtocols();
        GenerateArrayExpressFile.generate(experiments, protocols, omicsDIFile);

        return RepeatStatus.FINISHED;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(outputFile, "Output directory cannot be null.");
        Assert.notNull(experimentFileName, "experiment prefix can't be null.");
        Assert.notNull(protocolFileName,   "protocolPrefix can't be null.");

    }
}
