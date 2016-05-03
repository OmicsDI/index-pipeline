package uk.ac.ebi.ddi.pipeline.indexer.tasklet.generation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.arrayexpress.reader.ExperimentReader;
import uk.ac.ebi.ddi.arrayexpress.reader.ProtocolReader;
import uk.ac.ebi.ddi.arrayexpress.reader.model.experiments.Experiments;
import uk.ac.ebi.ddi.arrayexpress.reader.model.protocols.Protocols;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;


import java.io.File;


/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 03/05/2016
 */
public class GenerationArrayExpressXML extends AbstractTasklet {

    public static final Logger logger = LoggerFactory.getLogger(GenerationArrayExpressXML.class);

    private String outputFile;

    private String experimentFileName;

    private String protocolFileName;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        File omicsDIFile = new File (outputFile);
        Experiments experiments = new ExperimentReader(new File (experimentFileName)).getExperiments();
        Protocols protocols = new ProtocolReader(new File (protocolFileName)).getProtocols();
        uk.ac.ebi.ddi.arrayexpress.reader.generateArrayExpressFile.generate(experiments, protocols, omicsDIFile);

        return RepeatStatus.FINISHED;

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(outputFile, "Output directory cannot be null.");

        Assert.notNull(experimentFileName, "experiment prefix can't be null.");
        Assert.notNull(protocolFileName,   "protocolPrefix can't be null.");

    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public String getExperimentFileName() {
        return experimentFileName;
    }

    public void setExperimentFileName(String experimentFileName) {
        this.experimentFileName = experimentFileName;
    }

    public String getProtocolFileName() {
        return protocolFileName;
    }

    public void setProtocolFileName(String protocolFileName) {
        this.protocolFileName = protocolFileName;
    }
}
