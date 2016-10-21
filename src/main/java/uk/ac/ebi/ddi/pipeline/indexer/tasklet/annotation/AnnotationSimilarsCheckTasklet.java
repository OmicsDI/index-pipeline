package uk.ac.ebi.ddi.pipeline.indexer.tasklet.annotation;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.annotation.service.dataset.DDIDatasetAnnotationService;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.service.db.model.dataset.DatasetSimilars;
import uk.ac.ebi.ddi.service.db.model.dataset.SimilarDataset;
import uk.ac.ebi.ddi.service.db.utils.DatasetSimilarsType;

import java.util.List;

/**
 * Created by yperez on 15/07/2016.
 */
public class AnnotationSimilarsCheckTasklet extends AbstractTasklet{

    DDIDatasetAnnotationService datasetAnnotationService;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        List<DatasetSimilars> datasetSimilars = datasetAnnotationService.getDatasetSimilars();
        if(datasetSimilars != null && !datasetSimilars.isEmpty()){
            for(int i = 0; i < datasetSimilars.size(); i++){
                DatasetSimilars dataset = datasetSimilars.get(i);
                for(SimilarDataset datasetSimilar: dataset.getSimilars()){
                    boolean match = false;
                    for(int j = 0; j < datasetSimilars.size(); j++){
                        DatasetSimilars datasetSimilarOld = datasetSimilars.get(j);
                        if( datasetSimilarOld.getAccession().equalsIgnoreCase(datasetSimilar.getSimilarDataset().getAccession()) && datasetSimilarOld.getDatabase().equalsIgnoreCase(datasetSimilar.getSimilarDataset().getDatabase())){
                            match = true;
                        }
                    }
                    if(!match){
                        datasetAnnotationService.addDatasetSimilars(datasetSimilar.getSimilarDataset().getAccession(), datasetSimilar.getSimilarDataset().getDatabase(),new SimilarDataset(datasetAnnotationService.getDataset(dataset.getAccession(), dataset.getDatabase()),DatasetSimilarsType.getReverseRelationType(datasetSimilar.getRelationType())));
                    }
                }

            }
        }
        return RepeatStatus.FINISHED;
    }



    public DDIDatasetAnnotationService getDatasetAnnotationService() {
        return datasetAnnotationService;
    }

    public void setDatasetAnnotationService(DDIDatasetAnnotationService datasetAnnotationService) {
        this.datasetAnnotationService = datasetAnnotationService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(datasetAnnotationService, "The dataset annotation object can't be null");
    }
}
