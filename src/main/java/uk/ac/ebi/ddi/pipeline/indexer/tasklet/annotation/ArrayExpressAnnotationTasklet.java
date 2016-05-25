package uk.ac.ebi.ddi.pipeline.indexer.tasklet.annotation;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import uk.ac.ebi.ddi.annotation.service.taxonomy.NCBITaxonomyService;
import uk.ac.ebi.ddi.annotation.service.dataset.DatasetAnnotationEnrichmentService;
import uk.ac.ebi.ddi.pipeline.indexer.annotation.DatasetAnnotationFieldsService;
import uk.ac.ebi.ddi.service.db.model.dataset.Dataset;

import java.util.List;

/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 27/04/2016
 */
public class ArrayExpressAnnotationTasklet extends AnnotationXMLTasklet{

    NCBITaxonomyService taxonomyService = NCBITaxonomyService.getInstance();

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        if(databaseName != null){
            List<Dataset> datasets = datasetAnnotationService.getAllDatasetsByDatabase(databaseName);
            datasets.parallelStream().forEach( dataset -> {
                Dataset exitingDataset = datasetAnnotationService.getDataset(dataset.getAccession(), dataset.getDatabase());
                exitingDataset = DatasetAnnotationFieldsService.addpublicationDate(exitingDataset);
                exitingDataset = DatasetAnnotationEnrichmentService.updatePubMedIds(publicationService, exitingDataset);
                exitingDataset = taxonomyService.annotateSpecies(exitingDataset);
                datasetAnnotationService.annotateDataset(exitingDataset);
            });
        }
        return RepeatStatus.FINISHED;
    }
}
