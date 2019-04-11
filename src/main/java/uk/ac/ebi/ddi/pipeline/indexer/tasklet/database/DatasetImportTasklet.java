package uk.ac.ebi.ddi.pipeline.indexer.tasklet.database;

import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.annotation.service.database.DDIDatabaseAnnotationService;
import uk.ac.ebi.ddi.annotation.service.dataset.DDIDatasetAnnotationService;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.pipeline.indexer.utils.Constants;
import uk.ac.ebi.ddi.service.db.model.dataset.Dataset;
import uk.ac.ebi.ddi.xml.validator.parser.OmicsXMLFile;
import uk.ac.ebi.ddi.xml.validator.parser.model.Entry;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Some considerations, we would have only one provider by database. This must be considered in the future.
 *
 */
@Getter
@Setter
public class DatasetImportTasklet extends AbstractTasklet {

    public static final Logger LOGGER = LoggerFactory.getLogger(DatasetImportTasklet.class);

    Resource inputDirectory;

    String databaseName;

    DDIDatasetAnnotationService datasetAnnotationService;

    DDIDatabaseAnnotationService databaseAnnotationService;

    Boolean updateStatus = true;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        CopyOnWriteArrayList<javafx.util.Pair<String, String>> threadSafeList = new CopyOnWriteArrayList<>();

        LOGGER.info("DataSet import, inputDirectory: {} ", inputDirectory.getURI());

        File[] files = inputDirectory.getFile().listFiles();
        if (files == null) {
            LOGGER.warn("Input directory is empty, {}", inputDirectory.getFile().getAbsolutePath());
            return RepeatStatus.FINISHED;
        }

        Arrays.asList(files).parallelStream().forEach(file -> {
            try {
                LOGGER.debug("processing file:" + file);

                OmicsXMLFile omicsXMLFile = new OmicsXMLFile(file);

                List<Entry> entries = omicsXMLFile.getAllEntries();
                for (Entry dataEntry : entries) {
                    String db = omicsXMLFile.getDatabaseName() != null ? omicsXMLFile.getDatabaseName() : "NA";
                    if ("".equals(db)) {
                        db = dataEntry.getRepository() != null ? dataEntry.getRepository() : "";
                    }
                    if(dataEntry.getAdditionalFields().getField().contains(Constants.SUBMITTER_KEYWORDS)){
                        List<String> keywordSet = dataEntry.getAdditionalFieldValues(Constants.SUBMITTER_KEYWORDS);
                        keywordSet.parallelStream().flatMap(dt -> {
                                    if (dt.contains(";")){
                                        String[] newKeywords = dt.split(";");
                                        return Arrays.stream(newKeywords);
                                    }else{
                                        return Stream.of(dt);
                                    }
                                }
                        ).distinct().forEach(tr -> dataEntry.addAdditionalField("submitter_keywords",tr));
                    }
                    LOGGER.debug("inserting: " + dataEntry.getId() + " " + db + "");

                    datasetAnnotationService.insertDataset(dataEntry, db);
                    threadSafeList.add(new Pair<>(dataEntry.getId(), db));
                    LOGGER.debug("Dataset: " + dataEntry.getId() + " " + db + "has been added");
                }
            } catch (Exception e) {
                LOGGER.error("Error Reading file : {}, ", file.getAbsolutePath(), e);
            }
        });


        if (files.length > 0) {
            OmicsXMLFile file = new OmicsXMLFile(files[0]);
            databaseAnnotationService.updateDatabase(
                    databaseName, file.getDescription(), file.getReleaseDate(), file.getRelease(), null, null);
        }

        //Todo: Here we need to be carefully. We need to know when a dataset has been removed or not.
        //                                      For now we will consider a dataset
        //Todo: as removed is they are not included in one of the releases.

        Set<String> databases = threadSafeList.parallelStream().map(Pair::getValue).collect(Collectors.toSet());
        CopyOnWriteArrayList<Pair<List<Dataset>, String>> datasets = new CopyOnWriteArrayList<>();
        databases.parallelStream().forEach(database -> datasets.add(
                new Pair<>(datasetAnnotationService.getAllDatasetsByDatabase(database), database)));

        CopyOnWriteArrayList<Dataset> removed = new CopyOnWriteArrayList<>();
        datasets.parallelStream().forEach(x -> x.getKey().parallelStream().forEach(dataset -> {
            Pair<String, String> pair = new Pair<>(dataset.getAccession(), dataset.getDatabase());
            if (!threadSafeList.contains(pair)) {
                removed.add(dataset);
            }
        }));

        if (!updateStatus) {
            removed.forEach(x -> datasetAnnotationService.updateDeleteStatus(x));
        }

        return RepeatStatus.FINISHED;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(inputDirectory, "Input Directory can not be null");
        Assert.notNull(datasetAnnotationService, "Annotation Service can't be null");
        Assert.notNull(databaseName, "DatabaseName can't be null");
        Assert.notNull(updateStatus, "UpdateStatus can't be null");
    }
}
