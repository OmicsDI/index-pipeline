package uk.ac.ebi.ddi.pipeline.indexer.tasklet.enrichment;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.annotation.model.DatasetTobeEnriched;
import uk.ac.ebi.ddi.annotation.model.EnrichedDataset;
import uk.ac.ebi.ddi.annotation.service.database.DDIDatabaseAnnotationService;
import uk.ac.ebi.ddi.annotation.service.dataset.DDIDatasetAnnotationService;
import uk.ac.ebi.ddi.annotation.service.dataset.DatasetAnnotationEnrichmentService;
import uk.ac.ebi.ddi.annotation.service.synonyms.DDIAnnotationService;
import uk.ac.ebi.ddi.annotation.utils.DatasetUtils;
import uk.ac.ebi.ddi.ddidomaindb.dataset.DSField;
import uk.ac.ebi.ddi.ddidomaindb.dataset.Field;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.service.db.model.dataset.Database;
import uk.ac.ebi.ddi.service.db.model.dataset.Dataset;
import uk.ac.ebi.ddi.service.db.utils.DatasetCategory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public static final String ALL_DATABASES = "ALL_DATABASES";

    String databaseName;

    DDIAnnotationService annotationService;

    DDIDatasetAnnotationService datasetAnnotationService;

    DDIDatabaseAnnotationService databaseAnnotationService;

    private boolean overwrite = false;

    private static final int PARALLEL = Math.min(6, Runtime.getRuntime().availableProcessors());

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        ForkJoinPool customThreadPool = new ForkJoinPool(PARALLEL);
        if (databaseName.equals(ALL_DATABASES)) {
            List<Database> databases = databaseAnnotationService.getDatabases();
            for (Database database : databases) {
                List<Dataset> datasets = datasetAnnotationService.getAllDatasetsByDatabase(database.getName());
                customThreadPool.submit(() -> datasets.parallelStream().forEach(this::process)).get();
            }
        } else {
            List<Dataset> datasets = datasetAnnotationService.getAllDatasetsByDatabase(databaseName)
                    .parallelStream()
                    .filter(x -> x.getCurrentStatus().equalsIgnoreCase(DatasetCategory.INSERTED.getType()) ||
                            x.getCurrentStatus().equalsIgnoreCase(DatasetCategory.UPDATED.getType()))
                    .collect(Collectors.toList());
            customThreadPool.submit(() -> datasets.parallelStream().forEach(this::process)).get();
        }
        return RepeatStatus.FINISHED;
    }

    private void process(Dataset dataset) {
        Dataset existingDataset = datasetAnnotationService.getDataset(dataset.getAccession(), dataset.getDatabase());
        try {
            Map<String, String> fields = new HashMap<>();
            fields.put(DSField.NAME.getName(), existingDataset.getName());
            fields.put(DSField.DESCRIPTION.getName(), existingDataset.getDescription());
            fields.put(DSField.Additional.DATA.getName(), DatasetUtils.getFirstAdditional(
                    existingDataset, DSField.Additional.DATA.getName()));
            fields.put(DSField.Additional.SAMPLE.getName(), DatasetUtils.getFirstAdditional(
                    existingDataset, DSField.Additional.SAMPLE.getName()));
            fields.put(DSField.Additional.PUBMED_ABSTRACT.getName(), DatasetUtils.getFirstAdditional(
                    existingDataset, DSField.Additional.PUBMED_ABSTRACT.getName()));
            fields.put(DSField.Additional.PUBMED_TITLE.getName(), DatasetUtils.getFirstAdditional(
                    existingDataset, DSField.Additional.PUBMED_TITLE.getName()));
            EnrichedDataset enrichedDataset = annotationService.enrichment(
                    new DatasetTobeEnriched(dataset.getAccession(), dataset.getDatabase(), fields), overwrite);

            Map<Field, String> toBeEnriched = new HashMap<>();
            Map<String, String> enrichedAttributes = enrichedDataset.getEnrichedAttributes();
            toBeEnriched.put(DSField.Additional.ENRICH_TITLE, enrichedAttributes.get(DSField.NAME.getName()));
            toBeEnriched.put(DSField.Additional.ENRICH_ABSTRACT, enrichedAttributes.get(DSField.DESCRIPTION.getName()));
            toBeEnriched.put(DSField.Additional.ENRICH_SAMPLE,
                    enrichedAttributes.get(DSField.Additional.SAMPLE.getName()));
            toBeEnriched.put(DSField.Additional.ENRICH_DATA, enrichedAttributes.get(DSField.Additional.DATA.getName()));
            toBeEnriched.put(DSField.Additional.ENRICHE_PUBMED_TITLE,
                    enrichedAttributes.get(DSField.Additional.PUBMED_TITLE.getName()));
            toBeEnriched.put(DSField.Additional.ENRICH_PUBMED_ABSTRACT,
                    enrichedAttributes.get(DSField.Additional.PUBMED_ABSTRACT.getName()));
            DatasetAnnotationEnrichmentService.addEnrichedFields(existingDataset, toBeEnriched);

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
