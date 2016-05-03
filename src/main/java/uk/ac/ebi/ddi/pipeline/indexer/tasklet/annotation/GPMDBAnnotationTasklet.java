package uk.ac.ebi.ddi.pipeline.indexer.tasklet.annotation;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.Resource;
import uk.ac.ebi.ddi.annotation.model.EnrichedDataset;
import uk.ac.ebi.ddi.annotation.service.CrossReferencesProteinDatabasesService;
import uk.ac.ebi.ddi.annotation.service.DDIAnnotationService;
import uk.ac.ebi.ddi.annotation.service.DDIPublicationAnnotationService;
import uk.ac.ebi.ddi.pipeline.indexer.annotation.DatasetAnnotationEnrichmentService;
import uk.ac.ebi.ddi.pipeline.indexer.annotation.DatasetAnnotationFieldsService;
import uk.ac.ebi.ddi.pipeline.indexer.io.DDIFile;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.xml.validator.parser.OmicsXMLFile;
import uk.ac.ebi.ddi.xml.validator.parser.model.Entry;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 03/12/2015
 */
public class GPMDBAnnotationTasklet extends AnnotationXMLTasklet {

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        List<Entry> listToPrint = new ArrayList<>();
        int counterFiles = 1;

        if(inputDirectory != null && inputDirectory.getFile() != null && inputDirectory.getFile().isDirectory()){
            for(File file: inputDirectory.getFile().listFiles()){
                try{
                    OmicsXMLFile reader = new OmicsXMLFile(file);
                    reader.setDatabaseName("GPMDB");
                    for(String id: reader.getEntryIds()){

                        logger.info("The ID: " + id + " will be enriched!!");
                        Entry dataset = reader.getEntryById(id);

                        DatasetAnnotationFieldsService.addpublicationDate(dataset);

                        dataset = DatasetAnnotationEnrichmentService.updatePubMedIds(publicationService, dataset);

                        dataset = DatasetAnnotationFieldsService.cleanDescription(dataset);

                        dataset = CrossReferencesProteinDatabasesService.annotateGPMDBProteins(dataset);

                        dataset = CrossReferencesProteinDatabasesService.annotateCrossReferences(dataset);

                        listToPrint.add(dataset);

                        if(listToPrint.size() == numberEntries){
                            DDIFile.writeList(reader, listToPrint, prefixFile, counterFiles, outputDirectory.getFile());
                            listToPrint.clear();
                            counterFiles++;
                        }
                    }
                    // This must be printed before leave because it contains the end members of the list.
                    if(!listToPrint.isEmpty()){
                        DDIFile.writeList(reader, listToPrint, prefixFile, counterFiles, outputDirectory.getFile());
                        listToPrint.clear();
                        counterFiles++;
                    }
                }catch (Exception e){
                    logger.info("Error Reading file: " + e.getMessage());
                }

            }
        }
        return RepeatStatus.FINISHED;
    }
}
