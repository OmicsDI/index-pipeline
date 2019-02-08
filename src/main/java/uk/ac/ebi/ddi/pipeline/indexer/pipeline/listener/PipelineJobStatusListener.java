package uk.ac.ebi.ddi.pipeline.indexer.pipeline.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import uk.ac.ebi.ddi.pipeline.indexer.utils.Constants;


import java.util.List;
import java.util.Map;

/**
 * Listener print out the status of the job, particular the input to the job and any exception output
 *
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 29/09/15
 */
public class PipelineJobStatusListener implements JobExecutionListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(PipelineJobStatusListener.class);

    @Override
    public void beforeJob(JobExecution jobExecution) {
        LOGGER.info(Constants.OUTPUT_DIVIDER);
        LOGGER.info("About to run " + jobExecution.getJobInstance().getJobName());

        LOGGER.info("Input job parameters are: ");
        JobParameters parameters = jobExecution.getJobParameters();
        Map<String, JobParameter> parameterMap = parameters.getParameters();
        for (String s : parameterMap.keySet()) {
            LOGGER.info(s + " = " + parameterMap.get(s).getValue());
        }
        LOGGER.info(Constants.OUTPUT_DIVIDER);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        LOGGER.info(Constants.OUTPUT_DIVIDER);
        LOGGER.info("Job exit status: " + jobExecution.getExitStatus().getExitCode());
        List<Throwable> exceptions = jobExecution.getFailureExceptions();
        if (!exceptions.isEmpty()) {
            LOGGER.error("Number of exceptions " + exceptions.size());
            for (Throwable exception : exceptions) {
                StackTraceElement[] stackTraceElements = exception.getStackTrace();
                for (StackTraceElement stackTraceElement : stackTraceElements) {
                    LOGGER.error(stackTraceElement.toString());
                }
            }
        }
        LOGGER.info(Constants.OUTPUT_DIVIDER);
    }
}
