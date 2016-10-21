package uk.ac.ebi.ddi.pipeline.indexer.tasklet.annotation;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.web.client.RestClientException;
import uk.ac.ebi.ddi.pipeline.indexer.annotation.DatasetAnnotationFieldsService;
import uk.ac.ebi.ddi.service.db.model.dataset.Dataset;

import java.util.List;

/**
 * This code is licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * == General Description ==
 * <p>
 * This class Provides a general information or functionalities for
 * <p>
 * ==Overview==
 * <p>
 * How to used
 * <p>
 * Created by yperez (ypriverol@gmail.com) on 21/10/2016.
 */
public class AnnotateDatasetLinkTaskLet extends AnnotationXMLTasklet{

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        if(databaseName != null){
            List<Dataset> datasets = datasetAnnotationService.getAllDatasetsByDatabase(databaseName);
            datasets.parallelStream().forEach( dataset -> {
                try {
                    Dataset exitingDataset = datasetAnnotationService.getDataset(dataset.getAccession(), dataset.getDatabase());
                    exitingDataset = DatasetAnnotationFieldsService.replaceStudyByDatasetLink(exitingDataset);
                    datasetAnnotationService.updateDataset(exitingDataset);
                    System.out.println(exitingDataset.getId());

                }catch (RestClientException e){
                    logger.debug(e.getMessage());
                }

            });
        }
        return RepeatStatus.FINISHED;
    }
}
