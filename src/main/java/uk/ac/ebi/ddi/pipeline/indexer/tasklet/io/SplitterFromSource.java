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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
        Assert.isTrue(inputDirectory.getFile().isDirectory(), "inputDirectory must be a directory");
        Assert.notNull(outputDirectory, "outputDirectory can't be null!");
        Assert.notNull(filePrefix, "File Prefix can't be null!");
        Assert.notNull(originalPrefix, "originalPrefix can't be null!");
    }

    private void process(File file, AtomicInteger index, int total, AtomicInteger counterOutFiles,
                         List<Entry> entries) {
        try {
            index.getAndIncrement();
            if (!file.getName().contains(originalPrefix)) {
                LOGGER.info("filename {} is not valid ", file.getName());
                return;
            }
            LOGGER.info("reading file with name {} ", file.getName());
            OmicsXMLFile reader = new OmicsXMLFile(file);
            for (String id: reader.getEntryIds()) {

                //LOGGER.info("The ID: {} will be enriched!!", id);
                Entry dataset = reader.getEntryById(id);

                entries.add(dataset);

                if (entries.size() == numberEntries) {
                    DDIFile.writeList(reader, entries, filePrefix, counterOutFiles.get(), outputDirectory.getFile());
                    entries.clear();
                    counterOutFiles.getAndIncrement();
                }
            }
            //if (index.get() == total && !entries.isEmpty())
            if (!entries.isEmpty()) {
                //LOGGER.info("writing last file {}", index.get());
                DDIFile.writeList(reader, entries, filePrefix, counterOutFiles.get(), outputDirectory.getFile());
                entries.clear();
                counterOutFiles.getAndIncrement();
            }
            reader.close();
        } catch (Exception e) {
            LOGGER.error("Error Reading file: {}, ", file.getAbsolutePath(), e);
        }
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        List<Entry> listToPrint = new ArrayList<>();
        DDICleanDirectory.cleanDirectory(outputDirectory);
        File[] files = inputDirectory.getFile().listFiles();
        if (files == null) {
            LOGGER.warn("Source input is empty {}", inputDirectory.getFile().getAbsolutePath());
            return RepeatStatus.FINISHED;
        }

        AtomicInteger count = new AtomicInteger(0);
        AtomicInteger counterOutFiles = new AtomicInteger(1);
        Arrays.asList(files).forEach(file -> process(file, count, files.length, counterOutFiles, listToPrint));
        return RepeatStatus.FINISHED;
    }
}
