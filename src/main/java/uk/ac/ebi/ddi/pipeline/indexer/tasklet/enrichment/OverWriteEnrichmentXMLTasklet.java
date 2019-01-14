package uk.ac.ebi.ddi.pipeline.indexer.tasklet.enrichment;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.annotation.model.EnrichedDataset;
import uk.ac.ebi.ddi.annotation.service.database.DDIDatabaseAnnotationService;
import uk.ac.ebi.ddi.annotation.service.dataset.DDIDatasetAnnotationService;
import uk.ac.ebi.ddi.annotation.service.dataset.DatasetAnnotationEnrichmentService;
import uk.ac.ebi.ddi.annotation.service.synonyms.DDIAnnotationService;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.service.db.model.dataset.Database;
import uk.ac.ebi.ddi.service.db.model.dataset.Dataset;

import java.util.List;
import java.util.concurrent.ForkJoinPool;

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
 * Created by ypriverol (ypriverol@gmail.com) on 07/07/2016.
 */
@Getter
@Setter
public class OverWriteEnrichmentXMLTasklet extends AbstractTasklet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OverWriteEnrichmentXMLTasklet.class);

    DDIDatabaseAnnotationService databaseAnnotationService;

    DDIAnnotationService annotationService;

    DDIDatasetAnnotationService datasetAnnotationService;

    private static final int PARALLEL = Math.min(6, Runtime.getRuntime().availableProcessors());

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        List<Database> databases = databaseAnnotationService.getDatabases();
        for (Database database : databases) {
            List<Dataset> datasets = datasetAnnotationService.getAllDatasetsByDatabase(database.getName());
            ForkJoinPool customThreadPool = new ForkJoinPool(PARALLEL);
            customThreadPool.submit(() -> datasets.parallelStream().forEach(this::process)).get();
        }
        return RepeatStatus.FINISHED;
    }

    private void process(Dataset dataset) {
        Dataset existingDataset = datasetAnnotationService.getDataset(dataset.getAccession(), dataset.getDatabase());
        try {
            EnrichedDataset enrichedDataset = DatasetAnnotationEnrichmentService.enrichment(
                    annotationService, existingDataset, true);
            DatasetAnnotationEnrichmentService.addEnrichedFields(existingDataset, enrichedDataset);
            datasetAnnotationService.enrichedDataset(existingDataset);
        } catch (Exception e) {
            LOGGER.error("Exception occurred when processing dataset {}", dataset.getAccession(), e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(databaseAnnotationService, "Input databaseService can not be null");
        Assert.notNull(annotationService, "annotation Service can not be null");
        Assert.notNull(datasetAnnotationService, "dataset Service can not be null");
    }
}
