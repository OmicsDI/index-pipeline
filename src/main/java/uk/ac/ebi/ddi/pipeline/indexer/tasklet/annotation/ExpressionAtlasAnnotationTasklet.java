package uk.ac.ebi.ddi.pipeline.indexer.tasklet.annotation;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.web.client.RestClientException;
import uk.ac.ebi.ddi.annotation.service.taxonomy.NCBITaxonomyService;
import uk.ac.ebi.ddi.pipeline.indexer.annotation.DatasetAnnotationFieldsService;
import uk.ac.ebi.ddi.service.db.model.dataset.Dataset;
import uk.ac.ebi.ddi.service.db.utils.DatasetCategory;

import java.util.*;

/**
 * Created by yperez on 26/06/2016.
 */
public class ExpressionAtlasAnnotationTasklet extends AnnotationXMLTasklet{

    NCBITaxonomyService taxonomyService = NCBITaxonomyService.getInstance();

    private String originalDatabase;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        if(databaseName != null){
            List<Dataset> datasets = datasetAnnotationService.getAllDatasetsByDatabase(databaseName);
            datasets.parallelStream().forEach( dataset -> {
                try {
                    if(dataset.getCurrentStatus().equalsIgnoreCase(DatasetCategory.INSERTED.getType())){
                        Dataset exitingDataset = datasetAnnotationService.getDataset(dataset.getAccession(), dataset.getDatabase());
                        Dataset originalDataset = datasetAnnotationService.getDataset(dataset.getAccession(), originalDatabase);
                        exitingDataset = DatasetAnnotationFieldsService.refineDates(exitingDataset);
                        exitingDataset = taxonomyService.annotateSpecies(exitingDataset);
                        if(originalDataset != null){
                            DatasetAnnotationFieldsService.addInformationFromOriginal(originalDataset, exitingDataset);
                            Map<String, Set<String>> similars = new HashMap<String, Set<String>>();
                            Set<String> values = new HashSet<String>();
                            values.add(originalDataset.getAccession());
                            similars.put(originalDatabase, values);
                            datasetAnnotationService.addDatasetReanalysisSimilars(exitingDataset, similars);
                        }
                        datasetAnnotationService.annotateDataset(exitingDataset);
                    }
                }catch (RestClientException e){
                    logger.debug(e.getMessage());
                }

            });
        }
        return RepeatStatus.FINISHED;
    }

    public String getOriginalDatabase() {
        return originalDatabase;
    }

    public void setOriginalDatabase(String originalDatabase) {
        this.originalDatabase = originalDatabase;
    }
}
