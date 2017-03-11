package uk.ac.ebi.ddi.pipeline.indexer.tasklet.annotation;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.annotation.service.database.DDIDatabaseAnnotationService;
import uk.ac.ebi.ddi.annotation.service.dataset.DDIDatasetAnnotationService;
import uk.ac.ebi.ddi.annotation.service.taxonomy.UniProtTaxonomy;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.service.db.model.dataset.Database;
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
 * Created by yperez (ypriverol@gmail.com) on 20/10/2016.
 */
public class AnnotateStrainTasklet extends AbstractTasklet{


    DDIDatasetAnnotationService datasetAnnotationService;

    DDIDatabaseAnnotationService databaseAnnotationService;

    UniProtTaxonomy taxonomyService = UniProtTaxonomy.getInstance();


    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        List<Database> databases = databaseAnnotationService.getDatabases();

        if(databases != null && !databases.isEmpty()){
            databases.parallelStream().forEach(database ->{
                List<Dataset> datasets = datasetAnnotationService.getAllDatasetsByDatabase(database.getName());
                datasets.stream().forEach( dataset -> {
                    Dataset existingDataset = datasetAnnotationService.getDataset(dataset.getAccession(), dataset.getDatabase());
                    existingDataset = taxonomyService.annotateParentForNonRanSpecies(existingDataset);
                    datasetAnnotationService.updateDataset(existingDataset);
                });
            });
        }
        return RepeatStatus.FINISHED;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(datasetAnnotationService, "The dataset annotation object can't be null");
    }

    public DDIDatasetAnnotationService getDatasetAnnotationService() {
        return datasetAnnotationService;
    }

    public void setDatasetAnnotationService(DDIDatasetAnnotationService datasetAnnotationService) {
        this.datasetAnnotationService = datasetAnnotationService;
    }

    public DDIDatabaseAnnotationService getDatabaseAnnotationService() {
        return databaseAnnotationService;
    }

    public void setDatabaseAnnotationService(DDIDatabaseAnnotationService databaseAnnotationService) {
        this.databaseAnnotationService = databaseAnnotationService;
    }
}
