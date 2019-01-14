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
import uk.ac.ebi.ddi.annotation.service.dataset.DDIDatasetAnnotationService;
import uk.ac.ebi.ddi.annotation.service.dataset.DatasetAnnotationEnrichmentService;
import uk.ac.ebi.ddi.annotation.service.synonyms.DDIAnnotationService;
import uk.ac.ebi.ddi.annotation.utils.DataType;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.service.db.model.dataset.Dataset;
import uk.ac.ebi.ddi.service.db.utils.DatasetCategory;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 09/12/2015
 */
@Getter
@Setter
public class EnrichmentXMLTasklet extends AbstractTasklet {

    public static final Logger LOGGER = LoggerFactory.getLogger(EnrichmentXMLTasklet.class);

    String databaseName;

    DDIAnnotationService annotationService;

    DDIDatasetAnnotationService datasetAnnotationService;

    DataType dataType;

    private static final int PARALLEL = Math.min(6, Runtime.getRuntime().availableProcessors());

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        List<Dataset> datasets = datasetAnnotationService.getAllDatasetsByDatabase(databaseName)
                .parallelStream()
                .filter(x -> x.getCurrentStatus().equalsIgnoreCase(DatasetCategory.INSERTED.getType()) ||
                        x.getCurrentStatus().equalsIgnoreCase(DatasetCategory.UPDATED.getType()))
                .collect(Collectors.toList());
        ForkJoinPool customThreadPool = new ForkJoinPool(PARALLEL);
        customThreadPool.submit(() -> datasets.parallelStream().forEach(this::process)).get();
        return RepeatStatus.FINISHED;
    }

    private void process(Dataset dataset) {
        Dataset existingDataset = datasetAnnotationService.getDataset(dataset.getAccession(), dataset.getDatabase());
        try {
            EnrichedDataset enrichedDataset = DatasetAnnotationEnrichmentService.enrichment(
                    annotationService, existingDataset, false);
            dataset = DatasetAnnotationEnrichmentService.addEnrichedFields(existingDataset, enrichedDataset);
            datasetAnnotationService.enrichedDataset(existingDataset);
        } catch (Exception e) {
            LOGGER.error("Exception occurred when processing dataset {}", dataset.getAccession(), e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(databaseName, "Input databaseName can not be null");
    }
}
