package uk.ac.ebi.ddi.pipeline.indexer.tasklet.enrichment;

import lombok.Getter;
import lombok.Setter;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.annotation.service.dataset.DDIDatasetAnnotationService;
import uk.ac.ebi.ddi.annotation.utils.Constants;
import uk.ac.ebi.ddi.annotation.utils.DatasetUtils;
import uk.ac.ebi.ddi.ddidomaindb.dataset.DSField;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.service.db.model.dataset.Dataset;
import uk.ac.ebi.ddi.service.db.model.publication.PublicationDataset;
import uk.ac.ebi.ddi.service.db.utils.DatasetSimilarsType;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
 * Created by gdass (gdass) on 26/05/2016.
 */
@Getter
@Setter
public class SameMultiOmicsEnrichmentTasklet extends AbstractTasklet {

    DDIDatasetAnnotationService datasetAnnotationService;

    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        List<PublicationDataset> datasetList = datasetAnnotationService.getPublicationDatasets().parallelStream()
                .filter(x -> x.getOmicsType() != null && !x.getOmicsType().isEmpty())
                .collect(Collectors.toList());

        Map<String, Set<PublicationDataset>> publicationMap = datasetList.parallelStream()
                .collect(Collectors.groupingBy(PublicationDataset::getPubmedId, Collectors.toSet()));

        publicationMap.entrySet().parallelStream().forEach(publication -> {
            publication.getValue().parallelStream().forEach(x -> {
                Dataset dataset = datasetAnnotationService.getDataset(x.getDatasetID(), x.getDatabase());
                if (dataset != null) {
                    dataset = DatasetUtils.addAdditionalField(
                            dataset, DSField.Additional.OMICS.getName(), Constants.MULTIOMICS_TYPE);
                    datasetAnnotationService.updateDataset(dataset);
                    datasetAnnotationService.addDatasetSimilars(
                            dataset, publication.getValue(), DatasetSimilarsType.OTHER_OMICS_DATA.getType());
                }
            });

        });
        return RepeatStatus.FINISHED;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(datasetAnnotationService, "The dataset annotation object can't be null");
    }
}
