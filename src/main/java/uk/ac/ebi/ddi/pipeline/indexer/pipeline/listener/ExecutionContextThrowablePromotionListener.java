package uk.ac.ebi.ddi.pipeline.indexer.pipeline.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;

import java.util.List;

/**
 * Listener promotes the exceptions from step level to the job level
 *
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 29/09/15
 */
public class ExecutionContextThrowablePromotionListener extends StepExecutionListenerSupport {

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        JobExecution jobExecution = stepExecution.getJobExecution();

        List<Throwable> exceptions = stepExecution.getFailureExceptions();
        if (!exceptions.isEmpty()) {
            exceptions.forEach(jobExecution::addFailureException);
        }

        return ExitStatus.COMPLETED;
    }
}
