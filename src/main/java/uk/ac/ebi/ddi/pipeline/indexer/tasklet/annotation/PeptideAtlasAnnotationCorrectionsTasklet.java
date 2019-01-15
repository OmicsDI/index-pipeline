package uk.ac.ebi.ddi.pipeline.indexer.tasklet.annotation;

import lombok.Getter;
import lombok.Setter;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.annotation.service.dataset.DDIDatasetAnnotationService;
import uk.ac.ebi.ddi.pipeline.indexer.utils.DatasetAnnotationFieldsUtils;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.service.db.model.dataset.Dataset;

import java.util.List;

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
 * Created by ypriverol (ypriverol@gmail.com) on 15/07/2016.
 */
@Getter
@Setter
public class PeptideAtlasAnnotationCorrectionsTasklet extends AbstractTasklet {

    DDIDatasetAnnotationService datasetAnnotationService;

    String databaseName;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        List<Dataset> datasets = datasetAnnotationService.getAllDatasetsByDatabase(databaseName);
        if (datasets != null && !datasets.isEmpty()) {
            datasets.parallelStream().forEach(dataset -> {
                Dataset existingDataset = datasetAnnotationService.getDataset(
                        dataset.getAccession(), dataset.getDatabase());
                existingDataset = DatasetAnnotationFieldsUtils.refinePeptideAtlasKeyword(existingDataset);
                datasetAnnotationService.updateDataset(existingDataset);
            });
        }
        return RepeatStatus.FINISHED;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(datasetAnnotationService, "The dataset annotation object can't be null");
        Assert.notNull(databaseName, "The database name can't be null");
    }

}
