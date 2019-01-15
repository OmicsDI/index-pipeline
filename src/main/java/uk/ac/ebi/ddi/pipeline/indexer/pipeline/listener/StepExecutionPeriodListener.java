package uk.ac.ebi.ddi.pipeline.indexer.pipeline.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

import java.sql.Timestamp;


public class StepExecutionPeriodListener implements StepExecutionListener {
    public static final Logger LOGGER = LoggerFactory.getLogger(StepExecutionPeriodListener.class);

    @Override
    public void beforeStep(StepExecution stepExecution) {
        LOGGER.info("StepExecution - " + stepExecution.getStepName() + " begins at: "
                + new Timestamp(System.currentTimeMillis()));
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        LOGGER.info("StepExecution - " + stepExecution.getStepName() + " ends at: "
                + new Timestamp(System.currentTimeMillis()));
        return null;
    }
}
