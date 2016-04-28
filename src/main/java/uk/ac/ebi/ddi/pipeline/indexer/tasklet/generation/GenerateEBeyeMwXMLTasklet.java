package uk.ac.ebi.ddi.pipeline.indexer.tasklet.generation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.mw.GenerateMWEbeFiles;
import uk.ac.ebi.ddi.mw.extws.entrez.config.TaxWsConfigProd;
import uk.ac.ebi.ddi.mw.extws.mw.config.MWWsConfigProd;

/**
 * Generate all the files from pX submission by crawling the ProteomeXchange Page
 * and parsing the XML files. For every Dataset a file is created in the defined folder.
 *
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 29/09/15
 */

public class GenerateEBeyeMwXMLTasklet extends AbstractTasklet{

    public static final Logger logger = LoggerFactory.getLogger(GenerateEBeyeMwXMLTasklet.class);

    private String outputDirectory;

    private MWWsConfigProd configProd;

    private TaxWsConfigProd taxWsConfigProd;


    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {


        GenerateMWEbeFiles.generateMWXMLfiles(configProd, taxWsConfigProd, outputDirectory);

        return RepeatStatus.FINISHED;

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(outputDirectory, "Output directory cannot be null.");
        Assert.notNull(taxWsConfigProd, "The the Ws Config can't be null.");
        Assert.notNull(configProd, "configProd can't be null.");
    }

    public MWWsConfigProd getConfigProd() {
        return configProd;
    }

    public void setConfigProd(MWWsConfigProd configProd) {
        this.configProd = configProd;
    }

    public TaxWsConfigProd getTaxWsConfigProd() {
        return taxWsConfigProd;
    }

    public void setTaxWsConfigProd(TaxWsConfigProd taxWsConfigProd) {
        this.taxWsConfigProd = taxWsConfigProd;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }
}
