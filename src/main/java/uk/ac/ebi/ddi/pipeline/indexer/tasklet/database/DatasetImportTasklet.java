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
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.annotation.AnnotationXMLTasklet;
import uk.ac.ebi.ddi.service.db.model.dataset.Database;
import uk.ac.ebi.ddi.service.db.model.dataset.Dataset;
import uk.ac.ebi.ddi.xml.validator.exception.DDIException;
import uk.ac.ebi.ddi.xml.validator.parser.OmicsXMLFile;
import uk.ac.ebi.ddi.xml.validator.parser.model.Entry;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Created by yperez on 24/05/2016.
 */

/**
 * Some considerations, we would have only one provider by database. This must be considered in the future.
 *
 */
public class DatasetImportTasklet extends AbstractTasklet{

    public static final Logger logger = LoggerFactory.getLogger(AnnotationXMLTasklet.class);

    Resource inputDirectory;

    DDIDatasetAnnotationService datasetAnnotationService;

    DDIDatabaseAnnotationService databaseAnnotationService;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        CopyOnWriteArrayList<javafx.util.Pair<String,String>> threadSafeList = new CopyOnWriteArrayList<Pair<String,String>>();
        Arrays.asList(inputDirectory.getFile().listFiles()).parallelStream().forEach(file ->{
            try{
                List<Entry> entries = (new OmicsXMLFile(file)).getAllEntries();
                entries.parallelStream().forEach(dataEntry -> {

                    datasetAnnotationService.insertDataset(dataEntry);
                    threadSafeList.add(new Pair<String, String>(dataEntry.getId(), dataEntry.getDatabase()));
                    logger.debug("Dataset: " + dataEntry.toString() + "has been added");
                });

            }catch (Exception e){
                logger.info("Error Reading file: " + e.getMessage());
            }
        });

        if(inputDirectory.getFile().listFiles() != null && inputDirectory.getFile().listFiles().length > 0){
            OmicsXMLFile file = new OmicsXMLFile(inputDirectory.getFile().listFiles()[0]);
            databaseAnnotationService.updateDatabase(file.getName(),file.getDescription(), file.getReleaseDate(), file.getRelease(), null,null);
        }

        //Todo: Here we need to be carefully. We need to know when a dataset has been removed or not. For now we will consider a dataset
        //Todo: as removed is they are not included in one of the releases.

        Set<String> databases = threadSafeList.parallelStream().map(x -> x.getValue()).collect(Collectors.toSet());
        CopyOnWriteArrayList<Pair<List<Dataset>, String>> datasets = new CopyOnWriteArrayList<>();
        databases.parallelStream().forEach( database ->{
            datasets.add( new Pair<>(datasetAnnotationService.getAllDatasetsByDatabase(database), database));
        });

        CopyOnWriteArrayList<Dataset> removed = new CopyOnWriteArrayList<>();
        datasets.parallelStream().forEach(x -> {
            x.getKey().parallelStream().forEach( dataset ->{
                Pair<String, String> pair = new Pair<>(dataset.getAccession(), dataset.getDatabase());
                if(!threadSafeList.contains(pair)){
                    removed.add(dataset);
                }
            });
        });

        removed.stream().forEach( x -> {
            datasetAnnotationService.updateDeleteStatus(x);
        });

        return RepeatStatus.FINISHED;
    }

    public void setInputDirectory(Resource inputDirectory) {
        this.inputDirectory = inputDirectory;
    }

    public void setDatasetAnnotationService(DDIDatasetAnnotationService datasetAnnotationService) {
        this.datasetAnnotationService = datasetAnnotationService;
    }

    public DDIDatabaseAnnotationService getDatabaseAnnotationService() {
        return databaseAnnotationService;
    }

    public void setDatabaseAnnotationService(DDIDatabaseAnnotationService databaseAnnotationService) {
        this.databaseAnnotationService = databaseAnnotationService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(inputDirectory, "Input Directory can not be null");
        Assert.notNull(datasetAnnotationService, "Annotation Service can't be null");

    }
}
