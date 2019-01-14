package uk.ac.ebi.ddi.pipeline.indexer.tasklet.enrichment;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.annotation.service.dataset.DDIDatasetAnnotationService;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.service.db.model.publication.PublicationDataset;

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
 * Created by ypriverol (ypriverol@gmail.com) on 13/06/2016.
 */
public class SimilarsEnrichmentTasklet extends AbstractTasklet {

    DDIDatasetAnnotationService datasetAnnotationService;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        List<PublicationDataset> datasetList = datasetAnnotationService.getPublicationDatasets().parallelStream()
                .filter(x -> x.getOmicsType() != null && !x.getOmicsType().isEmpty())
                .collect(Collectors.toList());
        Map<String, Set<PublicationDataset>> publicationMap = datasetList.parallelStream()
                .collect(Collectors.groupingBy(PublicationDataset::getPubmedId, Collectors.toSet()));
        publicationMap.entrySet().parallelStream().forEach(publication -> publication.getValue().forEach(x -> {
            List<PublicationDataset> similars = publication.getValue().stream()
                     .filter(dat -> !dat.getDatasetID().equals(x.getDatasetID()))
                     .collect(Collectors.toList());
            Map<String, Set<String>> similarMap = similars.parallelStream()
                     .collect(Collectors.groupingBy(
                             PublicationDataset::getDatabase,
                             Collectors.mapping(PublicationDataset::getDatasetID, Collectors.toSet())));
            //Todo: why commented this
//            datasetAnnotationService.updateDatasetSimilars(x.getDatasetID(), x.getDatabase(), similarMap);
        }));
        return RepeatStatus.FINISHED;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(datasetAnnotationService, "The object can't be null");
    }

    public DDIDatasetAnnotationService getDatasetAnnotationService() {
        return datasetAnnotationService;
    }

    public void setDatasetAnnotationService(DDIDatasetAnnotationService datasetAnnotationService) {
        this.datasetAnnotationService = datasetAnnotationService;
    }
}
