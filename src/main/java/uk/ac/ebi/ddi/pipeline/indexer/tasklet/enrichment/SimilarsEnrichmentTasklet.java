package uk.ac.ebi.ddi.pipeline.indexer.tasklet.enrichment;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.annotation.service.dataset.DDIDatasetAnnotationService;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;

/**
 * Created by yperez on 13/06/2016.
 */
public class SimilarsEnrichmentTasklet extends AbstractTasklet{

    DDIDatasetAnnotationService datasetAnnotationService;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
//        List<PublicationDataset> datasetList = datasetAnnotationService.getPublicationDatasets();
//        datasetList = datasetList.parallelStream().filter(x -> x.getOmicsType() != null && !x.getOmicsType().isEmpty()).collect(Collectors.toList());
//        Map<String, Set<PublicationDataset>> publicationMap = datasetList.parallelStream().collect(Collectors.groupingBy(PublicationDataset::getPubmedId, Collectors.toSet()));
//        publicationMap.entrySet().parallelStream().forEach( publication -> {
//            publication.getValue().parallelStream().forEach( x -> {
//                List<PublicationDataset> similars = publication.getValue().stream().filter(dat -> dat.getDatasetID() != x.getDatasetID()).collect(Collectors.toList());
//                Map<String, Set<String>> similarMap = similars.parallelStream().collect(Collectors.groupingBy(PublicationDataset::getDatabase, Collectors.mapping(PublicationDataset::getDatasetID, Collectors.toSet())));
//                datasetAnnotationService.updateDatasetSimilars(x.getDatasetID(), x.getDatabase(), similarMap);
//                });
//            }
//        );
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
