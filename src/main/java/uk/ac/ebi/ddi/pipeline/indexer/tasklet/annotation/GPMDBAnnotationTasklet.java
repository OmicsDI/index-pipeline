package uk.ac.ebi.ddi.pipeline.indexer.tasklet.annotation;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.web.client.RestClientException;
import uk.ac.ebi.ddi.annotation.service.crossreferences.CrossReferencesProteinDatabasesService;
import uk.ac.ebi.ddi.annotation.service.dataset.DatasetAnnotationEnrichmentService;
import uk.ac.ebi.ddi.pipeline.indexer.annotation.DatasetAnnotationFieldsService;
import uk.ac.ebi.ddi.service.db.model.dataset.Dataset;

import java.util.List;

/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 03/12/2015
 */
public class GPMDBAnnotationTasklet extends AnnotationXMLTasklet {

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        List<Dataset> datasets = datasetAnnotationService.getAllDatasetsByDatabase(databaseName);

        datasets.parallelStream().forEach( dataset -> {
            Dataset existing = datasetAnnotationService.getDataset(dataset.getAccession(), dataset.getDatabase());
            DatasetAnnotationFieldsService.addpublicationDate(existing);
            existing = DatasetAnnotationEnrichmentService.updatePubMedIds(publicationService, existing);
            existing = DatasetAnnotationFieldsService.cleanDescription(existing);
            existing = DatasetAnnotationFieldsService.addCrossReferenceAnnotation(existing);
            try{
                existing = CrossReferencesProteinDatabasesService.annotatePXCrossReferences(datasetAnnotationService, existing);
            }catch(RestClientException ex){
                logger.debug(ex.getMessage());
            }
            datasetAnnotationService.updateDataset(existing);
        });
        return RepeatStatus.FINISHED;
    }
}
