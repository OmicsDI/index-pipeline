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
import java.util.Map;
import java.util.Set;

/**
 * Created by yperez on 19/06/2016.
 */
public class PeptideAtlasAnnotation extends AnnotationXMLTasklet {

    private List<String> databases;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        List<Dataset> datasets = datasetAnnotationService.getAllDatasetsByDatabase(databaseName);

        datasets.parallelStream().forEach( dataset -> {
            Dataset existing = datasetAnnotationService.getDataset(dataset.getAccession(), dataset.getDatabase());
            existing = DatasetAnnotationEnrichmentService.updatePubMedIds(publicationService, existing);
            existing = DatasetAnnotationFieldsService.cleanRepository(existing, databaseName);
            existing = DatasetAnnotationFieldsService.addCrossReferenceAnnotation(existing);
            try{
                existing = CrossReferencesProteinDatabasesService.annotatePXCrossReferences(datasetAnnotationService, existing);
                Map<String, Set<String>> similars = DatasetAnnotationFieldsService.getCrossSimilars(existing, databases);
                if(!similars.isEmpty())
                    datasetAnnotationService.addDatasetReanalisisSimilars(existing, similars);
            }catch(RestClientException ex){
                logger.debug(ex.getMessage());
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