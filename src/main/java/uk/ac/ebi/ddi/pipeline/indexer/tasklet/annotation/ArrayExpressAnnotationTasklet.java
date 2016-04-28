package uk.ac.ebi.ddi.pipeline.indexer.tasklet.annotation;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import uk.ac.ebi.ddi.annotation.model.EnrichedDataset;
import uk.ac.ebi.ddi.annotation.service.CrossReferencesProteinDatabasesService;
import uk.ac.ebi.ddi.pipeline.indexer.annotation.DatasetAnnotationEnrichmentService;
import uk.ac.ebi.ddi.pipeline.indexer.annotation.DatasetAnnotationFieldsService;
import uk.ac.ebi.ddi.pipeline.indexer.io.DDIFile;
import uk.ac.ebi.ddi.xml.validator.parser.OmicsXMLFile;
import uk.ac.ebi.ddi.xml.validator.parser.model.Entry;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 27/04/2016
 */
public class ArrayExpressAnnotationTasklet extends AnnotationXMLTasklet{

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        List<Entry> listToPrint = new ArrayList<>();
        int counterFiles = 1;

        if(inputDirectory != null && inputDirectory.getFile() != null && inputDirectory.getFile().isDirectory()){
            for(File file: inputDirectory.getFile().listFiles()){
                    final OmicsXMLFile reader = new OmicsXMLFile(file);

                    reader.setDatabaseName("ArrayExpress");

                    reader.getEntryIds().parallelStream().forEach( x -> {
                        try{
                            logger.info("The ID: " + x + " will be enriched!!");
                            Entry dataset = reader.getEntryById(x);

                            dataset.addAdditionalField("repository", "ArrayExpress");
                            dataset.addAdditionalField("omics_type", "Transcriptomics");
                            dataset.addAdditionalField("full_dataset_link", "https://www.ebi.ac.uk/arrayexpress/experiments/" + dataset.getId());
                            dataset = DatasetAnnotationFieldsService.replaceMEDLINEPubmed(dataset);
                            dataset = DatasetAnnotationFieldsService.replaceAuthorField(dataset);
                            dataset = DatasetAnnotationFieldsService.replaceKeywords(dataset);
                            dataset = DatasetAnnotationFieldsService.addPublicationDateFromSubmission(dataset);
                            dataset = DatasetAnnotationEnrichmentService.updatePubMedIds(publicationService, dataset);

                            EnrichedDataset enrichedDataset = DatasetAnnotationEnrichmentService.enrichment(annotationService, dataset);
                            dataset = DatasetAnnotationEnrichmentService.addEnrichedFields(dataset, enrichedDataset);

                            logger.debug(enrichedDataset.getEnrichedTitle());
                            logger.debug(enrichedDataset.getEnrichedAbstractDescription());
                            logger.debug(enrichedDataset.getEnrichedSampleProtocol());
                            logger.debug(enrichedDataset.getEnrichedDataProtocol());

                            listToPrint.add(dataset);
                            DDIFile.writeList(reader, listToPrint, prefixFile, x, outputDirectory.getFile());

                        }catch (Exception e){
                            logger.info("Error Reading file: " + e.getMessage());
                        }

                    });



            }
        }
        return RepeatStatus.FINISHED;
    }
}
