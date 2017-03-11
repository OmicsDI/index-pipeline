package uk.ac.ebi.ddi.pipeline.indexer.tasklet.annotation;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.annotation.service.crossreferences.DDIDatasetSimilarityService;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.service.db.model.similarity.DatasetStatInfo;
import uk.ac.ebi.ddi.service.db.model.similarity.IntersectionInfo;

import java.util.*;

/**
 * This code is licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0

 *  ==Overview==
 *
 *  This class
 *
 * Created by ypriverol (ypriverol@gmail.com) on 26/07/2016.
 */
public class CleanBiologicalSimilarTasklet extends AbstractTasklet{

    DDIDatasetSimilarityService ddiExpDataProcessService;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        List<DatasetStatInfo> datasetSimilars = ddiExpDataProcessService.getBiologicalSimilars();

        if(datasetSimilars != null && !datasetSimilars.isEmpty()){
            for (DatasetStatInfo dataset : datasetSimilars) {
                List<IntersectionInfo> newValues = new ArrayList<>();
                if (dataset.getAccession().equalsIgnoreCase("ST000054"))
                    System.out.println("I'm here");
                if (dataset.getIntersectionInfos() != null) {
                    (dataset.getIntersectionInfos()).sort((IntersectionInfo inter1, IntersectionInfo inter2) -> Double.compare(inter2.getCosineScore(), inter1.getCosineScore()));
                    for (IntersectionInfo currentInfo : dataset.getIntersectionInfos()) {
                        boolean toAdd = true;
                        for (IntersectionInfo newValue : newValues) {
                            if (currentInfo.getRelatedDatasetAcc().equalsIgnoreCase(newValue.getRelatedDatasetAcc()) &&
                                    currentInfo.getRelatedDatasetDatabase().equalsIgnoreCase(newValue.getRelatedDatasetDatabase())) {
                                toAdd = false;
                                break;
                            }
                        }
                        if (toAdd)
                            newValues.add(currentInfo);
                    }
                }
                if (newValues.size() != dataset.getIntersectionInfos().size())
                    System.out.println(newValues.size());
            }
        }
//                    Set<SimilarDataset> toRemove = new HashSet<>();
//                Set<SimilarDataset> newSimilars = new HashSet<>();
//                for(SimilarDataset datasetSimilar: dataset.getSimilars()){
//                    if(datasetSimilar.getSimilarDataset() == null)
//                        toRemove.add(datasetSimilar);
//                    else
//                        newSimilars.add(datasetSimilar);
//                }
//                if(toRemove.size() == dataset.getSimilars().size()){
//                    datasetAnnotationService.removeSimilar(dataset);
//                }else if(!toRemove.isEmpty()){
//                    datasetAnnotationService.updateDatasetSimilars(dataset.getAccession(), dataset.getDatabase(), newSimilars);
//                }
        return RepeatStatus.FINISHED;
    }


    public DDIDatasetSimilarityService getDdiExpDataProcessService() {
        return ddiExpDataProcessService;
    }

    public void setDdiExpDataProcessService(DDIDatasetSimilarityService ddiExpDataProcessService) {
        this.ddiExpDataProcessService = ddiExpDataProcessService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(ddiExpDataProcessService, "The dataset annotation object can't be null");
    }


}
