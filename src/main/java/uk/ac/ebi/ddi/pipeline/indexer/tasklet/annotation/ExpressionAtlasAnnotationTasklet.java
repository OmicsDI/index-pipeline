package uk.ac.ebi.ddi.pipeline.indexer.tasklet.annotation;

import lombok.Getter;
import lombok.Setter;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.web.client.RestClientException;
import uk.ac.ebi.ddi.annotation.service.taxonomy.NCBITaxonomyService;
import uk.ac.ebi.ddi.pipeline.indexer.utils.DatasetAnnotationFieldsUtils;
import uk.ac.ebi.ddi.service.db.model.dataset.Dataset;
import uk.ac.ebi.ddi.service.db.utils.DatasetCategory;

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
 * Created by ypriverol (ypriverol@gmail.com) on 26/06/2016.
 */
@Getter
@Setter
public class ExpressionAtlasAnnotationTasklet extends AnnotationXMLTasklet {

    NCBITaxonomyService taxonomyService = NCBITaxonomyService.getInstance();

    private String originalDatabase;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        //int i = 0;
        if (databaseName != null) {
            List<Dataset> datasets = datasetAnnotationService.getAllDatasetsByDatabase(databaseName);
            datasets.parallelStream().forEach(dataset -> {
                int i = 0;
                try {
                    //to run annotations for all datasets comment inserted to include all
                    if (dataset.getCurrentStatus().equalsIgnoreCase(DatasetCategory.INSERTED.getType())) {
                        Dataset exitingDataset = datasetAnnotationService.getDataset(
                                dataset.getAccession(), dataset.getDatabase());
                        Dataset originalDataset = datasetAnnotationService.getDataset(
                                dataset.getAccession(), originalDatabase);
                        exitingDataset = DatasetAnnotationFieldsUtils.refineDates(exitingDataset);
                        exitingDataset = taxonomyService.annotateSpecies(exitingDataset);
                        if (originalDataset != null) {
                            i++;
                            DatasetAnnotationFieldsUtils.addInformationFromOriginal(originalDataset, exitingDataset);
                            Map<String, Set<String>> similars = new HashMap<>();
                            Set<String> values = new HashSet<>();
                            values.add(originalDataset.getAccession());
                            similars.put(originalDatabase, values);
                            datasetAnnotationService.addDatasetReanalysisSimilars(exitingDataset, similars);
                        }
                        datasetAnnotationService.annotateDataset(exitingDataset);
                        LOGGER.info("Counts is {}", i);
                    }
                } catch (RestClientException e) {
                    LOGGER.error("Exception occurred when processing dataset {}, ", dataset.getAccession(), e);
                }
            });
        }
        return RepeatStatus.FINISHED;
    }
}
