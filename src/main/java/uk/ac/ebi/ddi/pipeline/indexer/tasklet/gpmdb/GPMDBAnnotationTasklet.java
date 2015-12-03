package uk.ac.ebi.ddi.pipeline.indexer.tasklet.gpmdb;

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
public class GPMDBAnnotationTasklet extends AbstractTasklet {

    Resource inputDirectory;

    private DDIAnnotationService annotationService;

    private int numberEntries;

    private String prefixFile;

    private Resource outputDirectory;

    private DDIPublicationAnnotationService publicationService = DDIPublicationAnnotationService.getInstance();

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        List<Entry> listToPrint = new ArrayList<>();
        int counterFiles = 1;

        if(inputDirectory != null && inputDirectory.getFile() != null && inputDirectory.getFile().isDirectory()){
            for(File file: inputDirectory.getFile().listFiles()){
                try{
                    OmicsXMLFile reader = new OmicsXMLFile(file);
                    for(String id: reader.getEntryIds()){

                        logger.info("The ID: " + id + " will be enriched!!");
                        Entry dataset = reader.getEntryById(id);

                        DatasetAnnotationFieldsService.addpublicationDate(dataset);

                        dataset = DatasetAnnotationEnrichmentService.updatePubMedIds(publicationService, dataset);

                        dataset = DatasetAnnotationFieldsService.cleanDescription(dataset);

                        dataset = CrossReferencesProteinDatabasesService.annotateCrossReferences(dataset);

                        EnrichedDataset enrichedDataset = DatasetAnnotationEnrichmentService.enrichment(annotationService, dataset);

                        dataset = DatasetAnnotationEnrichmentService.addEnrichedFields(dataset, enrichedDataset);

                        logger.debug(enrichedDataset.getEnrichedTitle());
                        logger.debug(enrichedDataset.getEnrichedAbstractDescription());
                        logger.debug(enrichedDataset.getEnrichedSampleProtocol());
                        logger.debug(enrichedDataset.getEnrichedDataProtocol());

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

    public void setInputDirectory(Resource inputDirectory) {
        this.inputDirectory = inputDirectory;
    }

    public void setAnnotationService(DDIAnnotationService annotationService) {
        this.annotationService = annotationService;
    }

    public void setNumberEntries(int numberEntries) {
        this.numberEntries = numberEntries;
    }

    public void setPrefixFile(String prefixFile) {
        this.prefixFile = prefixFile;
    }

    public void setOutputDirectory(Resource outputDirectory) {
        this.outputDirectory = outputDirectory;
    }


}
