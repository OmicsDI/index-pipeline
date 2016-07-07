package uk.ac.ebi.ddi.pipeline.indexer.tasklet.generation;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.expressionatlas.GenerateExpressionAtlasFile;
import uk.ac.ebi.ddi.expressionatlas.utils.FastOmicsDIReader;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.ddi.xml.validator.parser.OmicsXMLFile;
import uk.ac.ebi.ddi.xml.validator.parser.model.Entry;

import java.io.File;
import java.util.List;

/**
 * Created by yperez on 26/06/2016.
 */
public class GenerateEBeyeExpressionAtlasXML extends AbstractTasklet {

    public static final Logger logger = LoggerFactory.getLogger(GenerationArrayExpressXML.class);

    private String outputFile;

    private String experimentFileName;

    private String geneFileName;


    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        File omicsDIFile = new File (outputFile);
        OmicsXMLFile experiments = new OmicsXMLFile(new File (experimentFileName));
        System.out.println(experiments.getAllEntries().size());
        List<Entry> genes = FastOmicsDIReader.getInstance().read(new File(geneFileName));
        System.out.println(genes.size());
        GenerateExpressionAtlasFile.generate(experiments, genes, omicsDIFile);
        return RepeatStatus.FINISHED;

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(outputFile, "Output directory cannot be null.");

        Assert.notNull(experimentFileName, "experiment prefix can't be null.");
        Assert.notNull(geneFileName,   "protocolPrefix can't be null.");

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

    public String getGeneFileName() {
        return geneFileName;
    }

    public void setGeneFileName(String geneFileName) {
        this.geneFileName = geneFileName;
    }
}
