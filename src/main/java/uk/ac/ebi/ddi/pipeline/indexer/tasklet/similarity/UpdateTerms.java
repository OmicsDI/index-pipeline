package uk.ac.ebi.ddi.pipeline.indexer.tasklet.similarity;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.annotation.service.synonyms.DDIExpDataImportService;
import uk.ac.ebi.ddi.annotation.utils.DataType;
import uk.ac.ebi.ddi.annotation.service.dataset.DatasetAnnotationEnrichmentService;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.xml.validator.parser.OmicsXMLFile;
import uk.ac.ebi.ddi.xml.validator.parser.model.Entry;

import java.io.File;

/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 25/11/2015
 */
public class UpdateTerms extends AbstractTasklet {

    Resource inputDirectory;

    DataType dataType;

    DDIExpDataImportService ddiExpDataImportService;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(inputDirectory, "Input Directory can not be null");
        Assert.notNull(ddiExpDataImportService, "This Service can't be null");
        Assert.notNull(dataType, "The datatype can't be null");
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

         if(inputDirectory != null && inputDirectory.getFile() != null && inputDirectory.getFile().isDirectory()){
            for(File file: inputDirectory.getFile().listFiles()){
                try{
                    OmicsXMLFile reader = new OmicsXMLFile(file);
                    for(String id: reader.getEntryIds()){

                        logger.info("The ID: " + id + " will be imported!!");
                        Entry dataset = reader.getEntryById(id);

                        DatasetAnnotationEnrichmentService.importTermsToDatabase(dataset, dataType,ddiExpDataImportService);

                    }
                }catch (Exception e){
                    logger.info("Error Reading file: " + e.getMessage());
                }

            }
        }
        return RepeatStatus.FINISHED;
    }


    public void setInputDirectory(Resource inputDirectory) {
        this.inputDirectory = inputDirectory;
    }


    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public DDIExpDataImportService getDdiExpDataImportService() {
        return ddiExpDataImportService;
    }

    public void setDdiExpDataImportService(DDIExpDataImportService ddiExpDataImportService) {
        this.ddiExpDataImportService = ddiExpDataImportService;
    }
}
