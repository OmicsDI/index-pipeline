package uk.ac.ebi.ddi.pipeline.indexer.tasklet.io;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@Getter
@Setter
public class SplitterFromSource extends AbstractTasklet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SplitterFromSource.class);

    private Resource inputDirectory;
    private Resource outputDirectory;
    private String filePrefix;

    private int numberEntries;

    private String originalPrefix;


    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(inputDirectory, "inputDirectory can't be null!");
        Assert.notNull(outputDirectory, "outputDirectory can't be null!");
        Assert.notNull(filePrefix, "File Prefix can't be null!");
        Assert.notNull(originalPrefix, "originalPrefix can't be null!");
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        List<Entry> listToPrint = new ArrayList<>();
        int counterFiles = 1;
        OmicsXMLFile reader = null;

        if (inputDirectory != null && inputDirectory.getFile() != null && inputDirectory.getFile().isDirectory()) {

            DDICleanDirectory.cleanDirectory(outputDirectory);
            File[] files = inputDirectory.getFile().listFiles();
            if (files == null) {
                LOGGER.warn("Source input is empty {}", inputDirectory.getFile().getAbsolutePath());
                return RepeatStatus.FINISHED;
            }
            for (File file: files) {
                try {
                    if (file.getAbsolutePath().contains(originalPrefix)) {
                        reader = new OmicsXMLFile(file);
                        for (String id: reader.getEntryIds()) {

                            LOGGER.info("The ID: {} will be enriched!!", id);
                            Entry dataset = reader.getEntryById(id);

                            listToPrint.add(dataset);

                            if (listToPrint.size() == numberEntries) {
                                DDIFile.writeList(reader, listToPrint, filePrefix, counterFiles,
                                        outputDirectory.getFile());
                                listToPrint.clear();
                                counterFiles++;
                            }
                        }
                        reader.close();
                    }
                } catch (Exception e) {
                    LOGGER.error("Error Reading file: {}, ", file.getAbsolutePath(), e);
                }
            }
            // This must be printed before leave because it contains the end members of the list.
            if (!listToPrint.isEmpty()) {
                DDIFile.writeList(reader, listToPrint, filePrefix, counterFiles, outputDirectory.getFile());
                listToPrint.clear();
            }
        }
        return RepeatStatus.FINISHED;
    }
}
