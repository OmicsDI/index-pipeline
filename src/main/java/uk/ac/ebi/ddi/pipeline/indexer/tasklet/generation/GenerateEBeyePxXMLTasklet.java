package uk.ac.ebi.ddi.pipeline.indexer.tasklet.generation;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.api.readers.model.IGenerator;
import uk.ac.ebi.ddi.pipeline.indexer.io.DDICleanDirectory;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.api.readers.px.GeneratePxOmicsXML;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Generate all the files from pX submission by crawling the ProteomeXchange Page
 * and parsing the XML files. For every INSERTED a file is created in the defined folder.
 *
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 29/09/15
 */

@Getter
@Setter
public class GenerateEBeyePxXMLTasklet extends AbstractTasklet {

    public static final Logger LOGGER = LoggerFactory.getLogger(GenerateEBeyePxXMLTasklet.class);

    private String pxURL;

    private String pxPrefix;

    private int endPoint;

    private int loopGap;

    private String outputDirectory;

    private List<String> databases;


    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate localDate = LocalDate.now();

        DDICleanDirectory.cleanDirectory(outputDirectory);
        IGenerator generator = new GeneratePxOmicsXML(loopGap, endPoint, pxPrefix, pxURL, outputDirectory, databases,
                dtf.format(localDate));
        generator.generate();
        return RepeatStatus.FINISHED;

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(outputDirectory, "Output directory cannot be null.");
        Assert.notNull(pxURL, "pxURL can't be null.");
        Assert.notNull(pxPrefix, "pxPrefix can't be null.");
    }
}
