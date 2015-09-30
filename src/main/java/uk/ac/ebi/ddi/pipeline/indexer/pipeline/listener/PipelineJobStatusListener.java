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
    private final static Logger logger = LoggerFactory.getLogger(PipelineJobStatusListener.class);

    @Override
    public void beforeJob(JobExecution jobExecution) {
        logger.info(Constants.OUTPUT_DIVIDER);
        logger.info("About to run " + jobExecution.getJobInstance().getJobName());

        logger.info("Input job parameters are: ");
        JobParameters parameters = jobExecution.getJobParameters();
        Map<String, JobParameter> parameterMap = parameters.getParameters();
        for (String s : parameterMap.keySet()) {
            logger.info(s + " = " + parameterMap.get(s).getValue());
        }
        logger.info(Constants.OUTPUT_DIVIDER);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        logger.info(Constants.OUTPUT_DIVIDER);
        logger.info("Job exit status: " + jobExecution.getExitStatus().getExitCode());
        List<Throwable> exceptions = jobExecution.getFailureExceptions();
        if (!exceptions.isEmpty()) {
            logger.error("Number of exceptions " + exceptions.size());
            for (Throwable exception : exceptions) {
                StackTraceElement[] stackTraceElements = exception.getStackTrace();
                for (StackTraceElement stackTraceElement : stackTraceElements) {
                    logger.error(stackTraceElement.toString());
                }
            }
        }
        logger.info(Constants.OUTPUT_DIVIDER);
    }
}
