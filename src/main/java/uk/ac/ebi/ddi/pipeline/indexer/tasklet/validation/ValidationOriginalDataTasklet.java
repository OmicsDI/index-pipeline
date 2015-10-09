package uk.ac.ebi.ddi.pipeline.indexer.tasklet.validation;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.xml.validator.parser.OmicsXMLFile;
import uk.ac.ebi.ddi.xml.validator.utils.Tuple;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This function provides a way validate all the files in a directory and detect possible errors and
 * inconsistencies in the files.
 *
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 01/10/15
 */
public class ValidationOriginalDataTasklet extends AbstractTasklet{

    private String directory;

    private String reportName;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(directory, "The directory can't be null");
        Assert.notNull(reportName, "The report name can't be null");
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        List<File> files = new ArrayList<File>();
        File inFile = new File(directory);
        if(inFile.exists() && inFile.isDirectory()){
            File[] fileList = inFile.listFiles();
            for(File a: fileList){
                if (OmicsXMLFile.hasFileHeader(a)){
                    files.add(a);
                }
            }
            Map<File, List<Tuple>> errors = new HashMap<File, List<Tuple>>();
            for(File file: files){
                List<Tuple> error = OmicsXMLFile.validateSchema(file);
                error.addAll(OmicsXMLFile.validateSemantic(file));
                if(errors.containsKey(file)){
                    error.addAll(errors.get(file));
                }
                errors.put(file, error);
            }
            if(!errors.isEmpty()){
                PrintStream reportFile = new PrintStream(new File(directory + "/" + reportName));
                for(File file: errors.keySet()){
                    for (Tuple error: errors.get(file))
                        reportFile.println(file.getAbsolutePath() + "|" + error.getKey() + "|" + error.getValue());
                }
                reportFile.close();
            }
        }
        return RepeatStatus.FINISHED;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }
}
