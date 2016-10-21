package uk.ac.ebi.ddi.pipeline.indexer.tasklet.io;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.pipeline.indexer.io.DDICleanDirectory;
import uk.ac.ebi.ddi.pipeline.indexer.io.DDIFile;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.xml.validator.parser.OmicsXMLFile;
import uk.ac.ebi.ddi.xml.validator.parser.model.Entry;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 05/05/2016
 */
public class SplitterFromSource extends AbstractTasklet{

    private Resource inputDirectory;
    private Resource outputDirectory;
    private String filePrefix;

    private int numberEntries;

    private String originalPrefix;


    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(inputDirectory, "inputDirectory can't be null!");
        Assert.notNull(outputDirectory,"outputDirectory   can't be null!");
        Assert.notNull(filePrefix, "File Prefix can't be null!");
        Assert.notNull(numberEntries, "numberEntries can't be null!");
        Assert.notNull(originalPrefix, "originalPrefix can't be null!");
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        List<Entry> listToPrint = new ArrayList<>();
        int counterFiles = 1;
        OmicsXMLFile reader = null;

        if(inputDirectory != null && inputDirectory.getFile() != null && inputDirectory.getFile().isDirectory()){

            DDICleanDirectory.cleanDirectory(outputDirectory);
            for(File file: inputDirectory.getFile().listFiles()){
                try{
                    if(file.getAbsolutePath().contains(originalPrefix)){
                        reader = new OmicsXMLFile(file);
                        for(String id: reader.getEntryIds()){

                            logger.info("The ID: " + id + " will be enriched!!");
                            Entry dataset = reader.getEntryById(id);

                            listToPrint.add(dataset);

                            if(listToPrint.size() == numberEntries){
                                DDIFile.writeList(reader, listToPrint, filePrefix, counterFiles, outputDirectory.getFile());
                                listToPrint.clear();
                                counterFiles++;
                            }
                        }
                    }
                }catch (Exception e){
                    logger.info("Error Reading file: " + e.getMessage());
                }
            }
            // This must be printed before leave because it contains the end members of the list.
            if(!listToPrint.isEmpty()){
                DDIFile.writeList(reader, listToPrint, filePrefix, counterFiles, outputDirectory.getFile());
                listToPrint.clear();
                counterFiles++;
            }

        }
        return RepeatStatus.FINISHED;
    }

    public Resource getInputDirectory() {
        return inputDirectory;
    }

    public void setInputDirectory(Resource inputDirectory) {
        this.inputDirectory = inputDirectory;
    }

    public Resource getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(Resource outputDirectory) {
        this.outputDirectory = outputDirectory;
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

    public void setOriginalPrefix(String originalPrefix) {
        this.originalPrefix = originalPrefix;
    }

    public String getOriginalPrefix() {
        return originalPrefix;
    }
}
