package uk.ac.ebi.ddi.pipeline.indexer.tasklet.annotation;

import lombok.Getter;
import lombok.Setter;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import uk.ac.ebi.ddi.annotation.service.crossreferences.CrossReferencesProteinDatabasesService;
import uk.ac.ebi.ddi.annotation.service.dataset.DatasetAnnotationEnrichmentService;
import uk.ac.ebi.ddi.pipeline.indexer.utils.DatasetAnnotationFieldsUtils;
import uk.ac.ebi.ddi.service.db.model.dataset.Dataset;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
 * Created by ypriverol (ypriverol@gmail.com) on 19/06/2016.
 */
@Getter
@Setter
public class PeptideAtlasAnnotation extends AnnotationXMLTasklet {

    private List<String> databases;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        List<Dataset> datasets = datasetAnnotationService.getAllDatasetsByDatabase(databaseName);

        datasets.parallelStream().forEach(dataset -> {
            Dataset existing = datasetAnnotationService.getDataset(dataset.getAccession(), dataset.getDatabase());
            existing = DatasetAnnotationEnrichmentService.updatePubMedIds(publicationService, existing);
            existing = DatasetAnnotationFieldsUtils.cleanRepository(existing, databaseName);
            existing = DatasetAnnotationFieldsUtils.addCrossReferenceAnnotation(existing);
            existing = DatasetAnnotationFieldsUtils.replaceTextCase(existing);
            try {
                existing = CrossReferencesProteinDatabasesService.annotatePXCrossReferences(
                        datasetAnnotationService, existing);
                Map<String, Set<String>> similars = DatasetAnnotationFieldsUtils.getCrossSimilars(existing, databases);
                if (!similars.isEmpty()) {
                    datasetAnnotationService.addDatasetReanalysisSimilars(existing, similars);
                }
                datasetAnnotationService.updateDataset(existing);
            } catch (Exception ex) {
                LOGGER.error("Exception occurred when processing dataset {}, ", dataset.getAccession(), ex);
            }
        });
        return RepeatStatus.FINISHED;
    }
}
