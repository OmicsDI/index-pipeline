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
import java.util.concurrent.ForkJoinPool;

/**
 * @author Andrey Zorin (ypriverol@gmail.com)
 * @date 01/12/2017
 */
public class BioprojectsAnnotationTasklet extends AnnotationXMLTasklet{

    NCBITaxonomyService taxonomyService = NCBITaxonomyService.getInstance();

    private static final int PARALLEL = Math.min(3, Runtime.getRuntime().availableProcessors());

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        if(databaseName != null){
            List<Dataset> datasets = datasetAnnotationService.getAllDatasetsByDatabase(databaseName);
            ForkJoinPool customThreadPool = new ForkJoinPool(PARALLEL);
            customThreadPool.submit(() -> datasets.stream().parallel().forEach(this::process)).get();
        }
        return RepeatStatus.FINISHED;
    }

    private void process(Dataset dataset) {
        if (!dataset.getCurrentStatus().equalsIgnoreCase(DatasetCategory.INSERTED.getType())){
            return;
        }
        try {
            Dataset existingDataset = datasetAnnotationService.getDataset(dataset.getAccession(), dataset.getDatabase());
            existingDataset = DatasetAnnotationFieldsService.addpublicationDate(existingDataset);
            existingDataset = DatasetAnnotationEnrichmentService.updatePubMedIds(publicationService, existingDataset);
            existingDataset = taxonomyService.annotateSpecies(existingDataset);
            datasetAnnotationService.annotateDataset(existingDataset);
        } catch (RestClientException e){
            logger.error(e.getMessage());
        }
    }
}
