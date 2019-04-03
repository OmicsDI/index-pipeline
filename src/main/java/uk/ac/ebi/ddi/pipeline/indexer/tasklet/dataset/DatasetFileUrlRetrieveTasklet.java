package uk.ac.ebi.ddi.pipeline.indexer.tasklet.dataset;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.pipeline.indexer.exception.DatabaseNotFoundException;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.retriever.DefaultDatasetFileUrlRetriever;
import uk.ac.ebi.ddi.retriever.IDatasetFileUrlRetriever;
import uk.ac.ebi.ddi.retriever.providers.*;
import uk.ac.ebi.ddi.service.db.model.database.DatabaseDetail;
import uk.ac.ebi.ddi.service.db.model.dataset.Dataset;
import uk.ac.ebi.ddi.service.db.service.database.DatabaseDetailService;
import uk.ac.ebi.ddi.service.db.service.dataset.IDatasetService;
import uk.ac.ebi.ddi.xml.validator.utils.Field;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Getter
@Setter
public class DatasetFileUrlRetrieveTasklet extends AbstractTasklet {

    private IDatasetService datasetService;

    private DatabaseDetailService databaseDetailService;

    private IDatasetFileUrlRetriever retriever = new DefaultDatasetFileUrlRetriever();

    private List<DatabaseDetail> databaseDetails;

    private static final Logger LOGGER = LoggerFactory.getLogger(DatasetFileUrlRetrieveTasklet.class);

    private List<String> processed = new ArrayList<>();

    public DatasetFileUrlRetrieveTasklet() {

        // Initializing retrievers
        retriever = new ArrayExpressFileUrlRetriever(retriever);
        retriever = new GEOFileUrlRetriever(retriever);
        retriever = new BioModelsFileUrlRetriever(retriever);
        retriever = new ExpressionAtlasFileUrlRetriever(retriever);
        retriever = new DbGapFileUrlRetriever(retriever);
        retriever = new GNPSFileUrlRetriever(retriever);
        retriever = new JPostFileUrlRetriever(retriever);
        retriever = new MassIVEFileUrlRetriever(retriever);
        retriever = new LincsFileUrlRetriever(retriever);
        retriever = new PeptideAtlasFileUrlRetriever(retriever);
    }

    private DatabaseDetail getDatabase(String accession) {
        for (DatabaseDetail databaseDetail : databaseDetails) {
            if (databaseDetail.getAccessionPrefix() == null) {
                continue;
            }
            for (String prefix : databaseDetail.getAccessionPrefix()) {
                if (accession.startsWith(prefix)) {
                    return databaseDetail;
                }
            }
        }
        LOGGER.error("Database for dataset {} is not found", accession);
        throw new DatabaseNotFoundException();
    }

    private synchronized void calculatePercentFinished(String accession) {
        processed.add(accession);
        if (processed.size() % 500 == 0) {
            LOGGER.info("Processed {}", processed.size());
        }
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        LOGGER.info("Starting....");
        databaseDetails = databaseDetailService.getDatabaseList();
        databaseDetails.stream().parallel().forEach(x -> {
            datasetService.readDatasetHashCode(x.getDatabaseName()).forEach(this::process);
        });
        return RepeatStatus.FINISHED;
    }

    private void process(Dataset ds) {
        LOGGER.debug("Processing dataset {}", ds.getAccession());
        Dataset dataset = datasetService.read(ds.getAccession(), ds.getDatabase());
        try {
            Set<String> urls = retriever.getDatasetFiles(dataset.getAccession(), dataset.getDatabase());
            boolean hasChange = false;
            if (!urls.isEmpty()) {
                Set<String> originalUrls = dataset.getAdditional().get(Field.DATASET_FILE.getName());
                originalUrls = originalUrls == null ? new HashSet<>() : originalUrls;
                urls.addAll(originalUrls);
                dataset.getFiles().put(dataset.getAccession(), urls);
                hasChange = true;
            }
            for (String secondaryAccession : dataset.getAllSecondaryAccessions()) {
                if (secondaryAccession.contains("~")) {
                    secondaryAccession = secondaryAccession.split("~")[0];
                }
                String database = getDatabase(secondaryAccession).getDatabaseName();
                if (database.equals(dataset.getDatabase())) {
                    // Subseries
                    continue;
                }
                Set<String> mirrors = retriever.getDatasetFiles(secondaryAccession, database);
                if (!mirrors.isEmpty()) {
                    dataset.getFiles().put(secondaryAccession, mirrors);
                    hasChange = true;
                }
            }
            if (hasChange) {
                datasetService.update(dataset.getId(), dataset);
            }
        } catch (Exception e) {
            String identity = dataset.getAccession() + " - " + dataset.getDatabase();
            LOGGER.error("Exception occurred with dataset {}, ", identity, e);
        } finally {
            calculatePercentFinished(dataset.getAccession());
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(databaseDetailService, "The dataset details service can't be null");
        Assert.notNull(datasetService, "The dataset service can't be null");
    }
}
