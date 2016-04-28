package uk.ac.ebi.ddi.pipeline.indexer.tasklet.generation;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.massive.GenerateMassiveEbeFiles;
import uk.ac.ebi.ddi.massive.extws.entrez.config.TaxWsConfigProd;
import uk.ac.ebi.ddi.massive.extws.massive.config.MassiveWsConfigProd;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;

/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 10/11/15
 */
public class GenerateEBeyeMassiveXMLTasklet extends AbstractTasklet {

    private String outputDirectory;

    private TaxWsConfigProd taxonomyConfig;

    private MassiveWsConfigProd massiveConfig;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(outputDirectory, "Output directory cannot be null.");
        Assert.notNull(taxonomyConfig,  "The taxonomy Config can't be null");
        Assert.notNull(massiveConfig,   "The massive Config can't be null");
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        GenerateMassiveEbeFiles.generateMWXMLfiles(massiveConfig, taxonomyConfig, outputDirectory);
        return RepeatStatus.FINISHED;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public TaxWsConfigProd getTaxonomyConfig() {
        return taxonomyConfig;
    }

    public void setTaxonomyConfig(TaxWsConfigProd taxonomyConfig) {
        this.taxonomyConfig = taxonomyConfig;
    }

    public MassiveWsConfigProd getMassiveConfig() {
        return massiveConfig;
    }

    public void setMassiveConfig(MassiveWsConfigProd massiveConfig) {
        this.massiveConfig = massiveConfig;
    }
}
