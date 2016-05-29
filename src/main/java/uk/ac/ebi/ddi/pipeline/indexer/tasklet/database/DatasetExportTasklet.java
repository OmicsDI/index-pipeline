package uk.ac.ebi.ddi.pipeline.indexer.tasklet.database;

import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.annotation.service.database.DDIDatabaseAnnotationService;
import uk.ac.ebi.ddi.annotation.service.dataset.DDIDatasetAnnotationService;
import uk.ac.ebi.ddi.annotation.utils.DatasetUtils;
import uk.ac.ebi.ddi.pipeline.indexer.io.DDIFile;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.annotation.AnnotationXMLTasklet;
import uk.ac.ebi.ddi.service.db.model.dataset.Database;
import uk.ac.ebi.ddi.service.db.model.dataset.Dataset;
import uk.ac.ebi.ddi.service.db.utils.DatasetCategory;
import uk.ac.ebi.ddi.xml.validator.parser.OmicsXMLFile;
import uk.ac.ebi.ddi.xml.validator.parser.model.Entry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Created by yperez on 26/05/2016.
 */
public class DatasetExportTasklet extends AbstractTasklet{

    public static final Logger logger = LoggerFactory.getLogger(AnnotationXMLTasklet.class);

    Resource outputDirectory;

    String databaseName;

    private String filePrefix;

    private int numberEntries;

    DDIDatasetAnnotationService datasetAnnotationService;

    DDIDatabaseAnnotationService databaseService;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        List<Entry> listToPrint = new ArrayList<>();
        final int[] counterFiles = {1};
        List<Dataset> datasets = datasetAnnotationService.getAllDatasetsByDatabase(databaseName);
        Database database = databaseService.getDatabaseInfo(databaseName);
        datasets.stream().forEach( dataset -> {
            if(dataset.getCurrentStatus().equalsIgnoreCase(DatasetCategory.UPDATED.getType()) ||
                    dataset.getCurrentStatus().equalsIgnoreCase(DatasetCategory.ENRICHED.getType())){
                Dataset existingDataset = datasetAnnotationService.getDataset(dataset.getAccession(), dataset.getDatabase());
                Entry entry = DatasetUtils.tansformDatasetToEntry(existingDataset);
                listToPrint.add(entry);

                if(listToPrint.size() == numberEntries){
                    try {
                        DDIFile.writeList(listToPrint, filePrefix, counterFiles[0], outputDirectory.getFile(), database.getDescription(), databaseName, database.getReleaseTag());
                        listToPrint.clear();
                        counterFiles[0]++;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        // This must be printed before leave because it contains the end members of the list.
        if(!listToPrint.isEmpty()){
            DDIFile.writeList(listToPrint, filePrefix, counterFiles[0], outputDirectory.getFile(), database.getDescription(), databaseName, database.getReleaseTag());
        }
        return RepeatStatus.FINISHED;
    }

    public void setInputDirectory(Resource outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public void setDatasetAnnotationService(DDIDatasetAnnotationService datasetAnnotationService) {
        this.datasetAnnotationService = datasetAnnotationService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(outputDirectory, "Input Directory can not be null");
        Assert.notNull(datasetAnnotationService, "Annotation Service can't be null");

    }

    public Resource getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(Resource outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public DDIDatabaseAnnotationService getDatabaseService() {
        return databaseService;
    }

    public void setDatabaseService(DDIDatabaseAnnotationService databaseService) {
        this.databaseService = databaseService;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getFilePrefix() {
        return filePrefix;
    }

    public void setFilePrefix(String filePrefix) {
        this.filePrefix = filePrefix;
    }

    public int getNumberEntries() {
        return numberEntries;
    }

    public void setNumberEntries(int numberEntries) {
        this.numberEntries = numberEntries;
    }

    public DDIDatasetAnnotationService getDatasetAnnotationService() {
        return datasetAnnotationService;
    }
}
