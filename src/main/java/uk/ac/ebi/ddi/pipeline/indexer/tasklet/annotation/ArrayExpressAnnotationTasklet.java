package uk.ac.ebi.ddi.pipeline.indexer.tasklet.annotation;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import uk.ac.ebi.ddi.annotation.service.NCBITaxonomyService;
import uk.ac.ebi.ddi.pipeline.indexer.annotation.DatasetAnnotationEnrichmentService;
import uk.ac.ebi.ddi.pipeline.indexer.annotation.DatasetAnnotationFieldsService;
import uk.ac.ebi.ddi.pipeline.indexer.io.DDIFile;
import uk.ac.ebi.ddi.xml.validator.parser.OmicsXMLFile;
import uk.ac.ebi.ddi.xml.validator.parser.model.Entry;
import uk.ac.ebi.ddi.xml.validator.utils.Field;

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

        if(inputDirectory != null && inputDirectory.getFile() != null && inputDirectory.getFile().isDirectory()){
            for(File file: inputDirectory.getFile().listFiles()){
                    final OmicsXMLFile reader = new OmicsXMLFile(file);

                    reader.setDatabaseName("ArrayExpress");

                    reader.getEntryIds().parallelStream().forEach( x -> {
                        try{
                            logger.info("The ID: " + x + " will be enriched!!");
                            Entry dataset = reader.getEntryById(x);

                            dataset = DatasetAnnotationFieldsService.addPublicationDateFromSubmission(dataset);
                            dataset = DatasetAnnotationEnrichmentService.updatePubMedIds(publicationService, dataset);
                            if(dataset.getAdditionalFields() != null && dataset.getAdditionalFieldValues(Field.SPECIE_FIELD.getName()) != null){
                                List<String> taxonomies = NCBITaxonomyService.getInstance().getNCBITaxonomy(dataset.getAdditionalFieldValues(Field.SPECIE_FIELD.getName()));
                                if(taxonomies != null && taxonomies.size() > 0)
                                    for(String tax: taxonomies)
                                        dataset.addCrossReferenceValue(Field.TAXONOMY.getName(), tax);
                            }
                            listToPrint.add(dataset);
                            DDIFile.writeList(reader, dataset, prefixFile, x, outputDirectory.getFile());

                        }catch (Exception e){
                            logger.info("Error Reading file: " + e.getMessage());
                        }

                    });



            }
        }
        return RepeatStatus.FINISHED;
    }
}
