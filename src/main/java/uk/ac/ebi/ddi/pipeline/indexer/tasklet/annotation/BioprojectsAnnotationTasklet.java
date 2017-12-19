package uk.ac.ebi.ddi.pipeline.indexer.tasklet.annotation;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.web.client.RestClientException;
import uk.ac.ebi.ddi.annotation.service.dataset.DatasetAnnotationEnrichmentService;
import uk.ac.ebi.ddi.annotation.service.taxonomy.NCBITaxonomyService;
import uk.ac.ebi.ddi.pipeline.indexer.annotation.DatasetAnnotationFieldsService;
import uk.ac.ebi.ddi.service.db.model.dataset.Dataset;
import uk.ac.ebi.ddi.service.db.utils.DatasetCategory;

import java.util.List;

/**
 * @author Andrey Zorin (ypriverol@gmail.com)
 * @date 01/12/2017
 */
public class BioprojectsAnnotationTasklet extends AnnotationXMLTasklet{

    NCBITaxonomyService taxonomyService = NCBITaxonomyService.getInstance();

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        if(databaseName != null){
            List<Dataset> datasets = datasetAnnotationService.getAllDatasetsByDatabase(databaseName);
            datasets.parallelStream().forEach( dataset -> {
                try {
                    if(dataset.getCurrentStatus().equalsIgnoreCase(DatasetCategory.INSERTED.getType())){
                        Dataset existingDataset = datasetAnnotationService.getDataset(dataset.getAccession(), dataset.getDatabase());
                        existingDataset = DatasetAnnotationFieldsService.addpublicationDate(existingDataset);
                        existingDataset = DatasetAnnotationEnrichmentService.updatePubMedIds(publicationService, existingDataset);
                        existingDataset = taxonomyService.annotateSpecies(existingDataset);
                        datasetAnnotationService.annotateDataset(existingDataset);
                    }
                }catch (RestClientException e){
                    logger.debug(e.getMessage());
                }

            });
        }
        return RepeatStatus.FINISHED;
    }
}
