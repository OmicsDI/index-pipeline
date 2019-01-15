package uk.ac.ebi.ddi.pipeline.indexer.tasklet.annotation;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.web.client.RestClientException;
import uk.ac.ebi.ddi.annotation.service.crossreferences.CrossReferencesProteinDatabasesService;
import uk.ac.ebi.ddi.annotation.service.dataset.DatasetAnnotationEnrichmentService;
import uk.ac.ebi.ddi.pipeline.indexer.utils.DatasetAnnotationFieldsUtils;
import uk.ac.ebi.ddi.service.db.model.dataset.Dataset;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 03/12/2015
 */
public class GPMDBAnnotationTasklet extends AnnotationXMLTasklet {

    private List<String> databases;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        List<Dataset> datasets = datasetAnnotationService.getAllDatasetsByDatabase(databaseName);

        datasets.parallelStream().forEach(dataset -> {
            Dataset existing = datasetAnnotationService.getDataset(dataset.getAccession(), dataset.getDatabase());
            DatasetAnnotationFieldsUtils.addpublicationDate(existing);
            existing = DatasetAnnotationEnrichmentService.updatePubMedIds(publicationService, existing);
            existing = DatasetAnnotationFieldsUtils.cleanDescription(existing);
            existing = DatasetAnnotationFieldsUtils.addCrossReferenceAnnotation(existing);
            existing = DatasetAnnotationFieldsUtils.replaceTextCase(existing);
            try {
                CrossReferencesProteinDatabasesService.annotatePXCrossReferences(datasetAnnotationService, existing);
                Map<String, Set<String>> similars = DatasetAnnotationFieldsUtils.getCrossSimilars(existing, databases);
                if (!similars.isEmpty()) {
                    datasetAnnotationService.addDatasetReanalysisSimilars(existing, similars);
                }
            } catch (RestClientException ex) {
                LOGGER.error("Exception occurred when processing dataset {}, ", dataset.getAccession(), ex);
            }
            datasetAnnotationService.updateDataset(existing);


        });
        return RepeatStatus.FINISHED;
    }

    public List<String> getDatabases() {
        return databases;
    }

    public void setDatabases(List<String> databases) {
        this.databases = databases;
    }
}
