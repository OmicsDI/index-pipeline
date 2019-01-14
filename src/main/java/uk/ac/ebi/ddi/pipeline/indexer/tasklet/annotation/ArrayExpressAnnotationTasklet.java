package uk.ac.ebi.ddi.pipeline.indexer.tasklet.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import uk.ac.ebi.ddi.annotation.service.dataset.DatasetAnnotationEnrichmentService;
import uk.ac.ebi.ddi.annotation.service.taxonomy.NCBITaxonomyService;
import uk.ac.ebi.ddi.pipeline.indexer.utils.DatasetAnnotationFieldsUtils;
import uk.ac.ebi.ddi.service.db.model.dataset.Dataset;
import uk.ac.ebi.ddi.service.db.utils.DatasetCategory;

import java.util.List;

/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 27/04/2016
 */
public class ArrayExpressAnnotationTasklet extends AnnotationXMLTasklet {

    public static final Logger LOGGER = LoggerFactory.getLogger(ArrayExpressAnnotationTasklet.class);

    NCBITaxonomyService taxonomyService = NCBITaxonomyService.getInstance();

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        if (databaseName != null) {
            List<Dataset> datasets = datasetAnnotationService.getAllDatasetsByDatabase(databaseName);
            datasets.parallelStream().forEach(dataset -> {
                try {
                    if (dataset.getCurrentStatus().equalsIgnoreCase(DatasetCategory.INSERTED.getType())) {
                        Dataset exitingDataset = datasetAnnotationService.getDataset(
                                dataset.getAccession(), dataset.getDatabase());
                        exitingDataset = DatasetAnnotationFieldsUtils.addpublicationDate(exitingDataset);
                        exitingDataset = DatasetAnnotationEnrichmentService.updatePubMedIds(
                                publicationService, exitingDataset);
                        exitingDataset = taxonomyService.annotateSpecies(exitingDataset);
                        datasetAnnotationService.annotateDataset(exitingDataset);
                    }
                } catch (Exception e) {
                    LOGGER.error("Exception ocurred when processing dataset {}, ", dataset.getAccession(), e);
                }
            });
        }
        return RepeatStatus.FINISHED;
    }
}
