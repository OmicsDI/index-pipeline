package uk.ac.ebi.ddi.pipeline.indexer.tasklet.enrichment;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.Resource;
import uk.ac.ebi.ddi.annotation.model.EnrichedDataset;
import uk.ac.ebi.ddi.annotation.service.DDIAnnotationService;
import uk.ac.ebi.ddi.annotation.utils.DataType;
import uk.ac.ebi.ddi.pipeline.indexer.annotation.DatasetAnnotationEnrichmentService;
import uk.ac.ebi.ddi.pipeline.indexer.io.DDIFile;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.ddi.xml.validator.parser.OmicsXMLFile;
import uk.ac.ebi.ddi.xml.validator.parser.model.Entry;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.Assert;

/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 09/12/2015
 */
public class EnrichmentXMLTasklet extends AbstractTasklet{

    public static final Logger logger = LoggerFactory.getLogger(EnrichmentXMLTasklet.class);

    Resource inputDirectory;

    DDIAnnotationService annotationService;

    DataType dataType;


    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        List<Entry> listToPrint = new ArrayList<>();
        if(inputDirectory != null && inputDirectory.getFile() != null && inputDirectory.getFile().isDirectory()){
            for(File file: inputDirectory.getFile().listFiles()){
                try{
                    OmicsXMLFile reader = new OmicsXMLFile(file);

                    for(String id: reader.getEntryIds()){

                        logger.info("The ID: " + id + " will be enriched!!");
                        Entry dataset = reader.getEntryById(id);

                        EnrichedDataset enrichedDataset = DatasetAnnotationEnrichmentService.enrichment(annotationService, dataset);
                        dataset = DatasetAnnotationEnrichmentService.addEnrichedFields(dataset, enrichedDataset);

                        logger.debug(enrichedDataset.getEnrichedTitle());
                        logger.debug(enrichedDataset.getEnrichedAbstractDescription());
                        logger.debug(enrichedDataset.getEnrichedSampleProtocol());
                        logger.debug(enrichedDataset.getEnrichedDataProtocol());

                        listToPrint.add(dataset);
                    }
                    if(!listToPrint.isEmpty()){
                        // This must be printed before leave because it contains the end members of the list.
                        DDIFile.writeList(reader, listToPrint, file);
                        listToPrint.clear();
                    }
                }catch (Exception e){
                    logger.info("Error Reading file: " + e.getMessage());
                }
            }
        }
        return RepeatStatus.FINISHED;

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(inputDirectory, "Input Directory can not be null");
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

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }
}
