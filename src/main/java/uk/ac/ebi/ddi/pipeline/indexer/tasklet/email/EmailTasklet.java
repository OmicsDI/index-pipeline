package uk.ac.ebi.ddi.pipeline.indexer.tasklet.email;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.UnexpectedJobExecutionException;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.pipeline.indexer.email.EmailContentGenerator;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


/**
 * Tasklet to send an email
 *
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 29/09/15
 */
@Getter
@Setter
public class EmailTasklet extends AbstractTasklet {
    public static final Logger LOGGER = LoggerFactory.getLogger(EmailTasklet.class);


    private JavaMailSender mailSender;
    private String mailTo;
    private String mailFrom;
    private String mailBcc;
    private String mailCc;
    private String mailSubject;
    private EmailContentGenerator emailContentGenerator;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        LOGGER.info("Generating submission summary email");

        // update message body template
        String emailBody = emailContentGenerator.generate();
        if (emailBody == null) {
            String msg = "Failed to generate the body for submission email";
            LOGGER.error(msg);
            throw new UnexpectedJobExecutionException(msg);
        }

        // send email to submitter
        sendEmail(emailBody);

        return RepeatStatus.FINISHED;
    }

    private void sendEmail(String emailBody) throws MessagingException {
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        message.setTo(mailTo);
        message.setFrom(mailFrom);
        message.setBcc(mailBcc);
        if (mailCc != null && !mailTo.equals(mailCc)) {
            message.setCc(mailCc);
        }
        message.setSubject(mailSubject);
        message.setText(emailBody, true);

        try {
            this.mailSender.send(mimeMessage);
        } catch (MailException ex) {
            String msg = "Failed to send submission email";
            LOGGER.error(msg, ex);
            throw new UnexpectedJobExecutionException(msg, ex);
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(mailSender, "Mail sender can not be null");
        Assert.notNull(mailTo, "Mail to cannot be null");
        Assert.notNull(mailFrom, "Mail from cannot be null");
        Assert.notNull(mailSubject, "Mail subject cannot be null");
        Assert.notNull(emailContentGenerator, "Email context generator cannot be null");
    }
}

