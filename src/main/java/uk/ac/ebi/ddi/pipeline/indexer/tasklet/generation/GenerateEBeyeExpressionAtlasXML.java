package uk.ac.ebi.ddi.pipeline.indexer.tasklet.generation;

import lombok.Getter;
import lombok.Setter;
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
 * This code is licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0

 *  ==Overview==
 *
 *  This class
 *
 * Created by ypriverol (ypriverol@gmail.com) on 26/06/2016.
 */
@Getter
@Setter
public class GenerateEBeyeExpressionAtlasXML extends AbstractTasklet {

    public static final Logger LOGGER = LoggerFactory.getLogger(GenerationArrayExpressXML.class);

    private String outputFile;

    private String experimentFileName;

    private String geneFileName;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        File omicsDIFile = new File(outputFile);
        OmicsXMLFile experiments = new OmicsXMLFile(new File(experimentFileName));
        LOGGER.info("Total entries: {}", experiments.getAllEntries().size());
        List<Entry> genes = FastOmicsDIReader.getInstance().read(new File(geneFileName));
        LOGGER.info("Total genes: {}", genes.size());
        GenerateExpressionAtlasFile.generate(experiments, genes, omicsDIFile);
        return RepeatStatus.FINISHED;

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(outputFile, "Output directory cannot be null.");
        Assert.notNull(experimentFileName, "experiment prefix can't be null.");
        Assert.notNull(geneFileName,   "protocolPrefix can't be null.");
    }
}
