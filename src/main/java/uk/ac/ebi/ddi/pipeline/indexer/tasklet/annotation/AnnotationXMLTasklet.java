package uk.ac.ebi.ddi.pipeline.indexer.tasklet.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.annotation.model.DatasetTobeEnriched;
import uk.ac.ebi.ddi.annotation.model.EnrichedDataset;
import uk.ac.ebi.ddi.annotation.service.DDIAnnotationService;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.xml.validator.parser.OmicsXMLFile;
import uk.ac.ebi.ddi.xml.validator.parser.model.Entry;
import uk.ac.ebi.ddi.xml.validator.utils.Field;

import java.io.File;

/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 19/10/15
 */
public class AnnotationXMLTasklet extends AbstractTasklet{

    public static final Logger logger = LoggerFactory.getLogger(AnnotationXMLTasklet.class);

    Resource outputDirectory;

    Resource inputDirectory;

    DDIAnnotationService annotationService;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        if(inputDirectory != null && inputDirectory.getFile() != null && inputDirectory.getFile().isDirectory()){
            for(File file: inputDirectory.getFile().listFiles()){
                OmicsXMLFile reader = new OmicsXMLFile(file);
                for(String id: reader.getEntryIds()){
                    Entry dataset = reader.getEntryById(id);
                    DatasetTobeEnriched datasetTobeEnriched = new DatasetTobeEnriched(dataset.getAcc(),dataset.getAdditionalFieldValue(Field.REPOSITORY.getName()),
                            dataset.getName().getValue(), dataset.getDescription(), dataset.getAdditionalFieldValue(Field.SAMPLE.getName()),
                            dataset.getAdditionalFieldValue(Field.DATA.getName()));
                    EnrichedDataset enrichedDataset1 = annotationService.enrichment(datasetTobeEnriched);

                    logger.info(enrichedDataset1.getEnrichedTitle());
                    logger.info(enrichedDataset1.getEnrichedAbstractDescription());
                    logger.info(enrichedDataset1.getEnrichedSampleProtocol());
                    logger.info(enrichedDataset1.getEnrichedDataProtocol());

                }


            }
        }
        return RepeatStatus.FINISHED;

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(inputDirectory, "Input Directory can not be null");
        Assert.notNull(outputDirectory, "Output Directory cant be null");
    }

    public Resource getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(Resource outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public Resource getInputDirectory() {
        return inputDirectory;
    }

    public void setInputDirectory(Resource inputDirectory) {
        this.inputDirectory = inputDirectory;
    }

    public DDIAnnotationService getAnnotationService() {
        return annotationService;
    }

    public void setAnnotationService(DDIAnnotationService annotationService) {
        this.annotationService = annotationService;
    }
}
