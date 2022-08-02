package uk.ac.ebi.ddi.pipeline.indexer.tasklet.database;

//import javafx.util.Pair;
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
import uk.ac.ebi.ddi.ddidomaindb.dataset.DSField;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.pipeline.indexer.utils.Constants;
import uk.ac.ebi.ddi.pipeline.indexer.utils.Utils;
import uk.ac.ebi.ddi.service.db.model.dataset.Dataset;
import uk.ac.ebi.ddi.xml.validator.parser.OmicsXMLFile;
import uk.ac.ebi.ddi.xml.validator.parser.model.AdditionalFields;
import uk.ac.ebi.ddi.xml.validator.parser.model.Entry;
import uk.ac.ebi.ddi.xml.validator.parser.model.Field;

import java.io.File;
import java.io.IOException;
import java.util.*;
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
        CopyOnWriteArrayList<Map.Entry<String, String>> threadSafeList = new CopyOnWriteArrayList<>();

        LOGGER.info("DataSet import, inputDirectory: {} ", inputDirectory.getURI());

        File[] files = inputDirectory.getFile().listFiles();
        if (files == null) {
            LOGGER.warn("Input directory is empty, {}", inputDirectory.getFile().getAbsolutePath());
            return RepeatStatus.FINISHED;
        }

        Arrays.asList(files).parallelStream().forEach(file -> {
            try {
                LOGGER.info("processing file:" + file);

                OmicsXMLFile omicsXMLFile = new OmicsXMLFile(file);

                List<Entry> entries = omicsXMLFile.getAllEntries();
                for (Entry dataEntry : entries) {
                    String db = omicsXMLFile.getDatabaseName() != null ? omicsXMLFile.getDatabaseName() : "NA";
                    if ("".equals(db)) {
                        db = dataEntry.getRepository() != null ? dataEntry.getRepository() : "";
                    }
                    if(db.equals("INSDC Project")){
                        db = "ENA";
                        dataEntry.addAdditionalField(DSField.Additional.REPOSITORY.key(),"ENA");
                        dataEntry.addAdditionalField(DSField.Additional.OMICS.key(),"Genomics");
                        dataEntry.addAdditionalField(DSField.Additional.LINK.key(),"https://www.ncbi.nlm.nih.gov/bioproject/?term=" + dataEntry.getId());
                    }
                    long submitterCount = dataEntry.getAdditionalFields() != null ?
                            dataEntry.getAdditionalFields().getField().parallelStream().
                            filter(fld -> fld.getName()
                                    .equals(DSField.Additional.SUBMITTER_KEYWORDS.key())).count() : 0;
                    if (submitterCount > 0) {
                        List<String> keywordSet = dataEntry
                                .getAdditionalFieldValues(DSField.Additional.SUBMITTER_KEYWORDS.key());
                        if (keywordSet != null) {
                            Entry finalDataEntry = dataEntry;
                            keywordSet.parallelStream().flatMap(dt -> {
                                    if (dt.contains(Constants.SEMI_COLON_TOKEN)) {
                                        String[] newKeywords = dt.split(Constants.SEMI_COLON_TOKEN);
                                        return Arrays.stream(newKeywords);
                                    } else {
                                        return Stream.of(dt);
                                    }
                                }
                        ).distinct().forEach(tr -> finalDataEntry
                                    .addAdditionalField(DSField.Additional.SUBMITTER_KEYWORDS.key(), tr));
                        }
                    }
                    LOGGER.debug("inserting: " + dataEntry.getId() + " " + db + "");
                    LOGGER.info("before update of " + dataEntry.getId() + " omicstype is " + dataEntry.getAdditionalFieldValue("omics_type") + "");
                    dataEntry = updateOmicsType(dataEntry);
                    LOGGER.info("after update of " + dataEntry.getId() + " omicstype is " + dataEntry.getAdditionalFieldValue("omics_type") + "");
                    datasetAnnotationService.insertDataset(dataEntry, db);
                    threadSafeList.add(new AbstractMap.SimpleEntry<>(dataEntry.getId(), db));
                    LOGGER.info("Dataset: " + dataEntry.getId() + " " + db + "has been added");
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

        Set<String> databases = threadSafeList.parallelStream().map(Map.Entry::getValue).collect(Collectors.toSet());
        CopyOnWriteArrayList<Map.Entry<List<Dataset>, String>> datasets = new CopyOnWriteArrayList<>();
        databases.parallelStream().forEach(database -> datasets.add(
                new AbstractMap.SimpleEntry<>(datasetAnnotationService.getAllDatasetsByDatabase(database), database)));

        CopyOnWriteArrayList<Dataset> removed = new CopyOnWriteArrayList<>();
        datasets.parallelStream().forEach(x -> x.getKey().parallelStream().forEach(dataset -> {
            Map.Entry<String, String> pair = new AbstractMap.SimpleEntry<>(dataset.getAccession(), dataset.getDatabase());
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

    public Entry updateOmicsType(Entry dataEntry ) throws IOException {
        HashMap<String, String> omicsVocab = Utils.readCsvHashMap();
        List<String> omicsValues = dataEntry.getAdditionalFieldValues(DSField.Additional.OMICS.key());

        if ( omicsValues != null && omicsValues.size() >= 1) {
            for (String omicsValue : omicsValues) {
                List omicstype = omicsVocab.entrySet().stream().map(r ->
                        Utils.processOmics(r, omicsValue)).filter(r -> r != "").collect(Collectors.toList());
                if (omicstype.size() == 0 && !omicsVocab.values().contains(omicsValue)) {
                    Field omicsField = dataEntry.getAdditionalFields().getField().stream()
                            .filter(r -> r.getValue().equals(omicsValue)).findFirst().get();
                    LOGGER.info("Updating dataset id with " + dataEntry.getId() + " with current omicstype " + omicsValue + "with Other");
                    dataEntry.getAdditionalFields().getField().remove(omicsField);
                    dataEntry.addAdditionalField(DSField.Additional.OMICS.getName(), "Other");
                    //dataEntry.addAdditionalField(DSField.Additional.OMICS.getName(), "Other");
                } else if (omicstype.size() > 0 && !omicstype.get(0).equals("")) {
                    LOGGER.info("dataset id with " + dataEntry.getId() + " is matched with " + omicstype.get(0) + "with current omics " + dataEntry.getAdditionalFieldValue("omics_type"));
                    if(!omicsValue.equals(omicstype.get(0))) {
                        Field omicsField = dataEntry.getAdditionalFields().getField().stream()
                                .filter(r -> r.getValue().equals(omicsValue)).findFirst().get();
                        dataEntry.getAdditionalFields().getField().remove(omicsField);
                        dataEntry.addAdditionalField(DSField.Additional.OMICS.getName(), omicstype.get(0).toString());
                    }
                }

            }
        }
        else{
                dataEntry.addAdditionalField(DSField.Additional.OMICS.getName(), "Unknown");
            }

        return dataEntry;
    }
}
