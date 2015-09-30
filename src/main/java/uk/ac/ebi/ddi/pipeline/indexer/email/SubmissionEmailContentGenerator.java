package uk.ac.ebi.ddi.pipeline.indexer.email;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


/**
 * Generate email content for submission complete email
 *
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 18/08/2015
 */
public class SubmissionEmailContentGenerator implements EmailContentGenerator {
    private final SubmissionCompleteSummary submissionCompleteSummary;
    private final String submissionEmailTemplate;
    private final TemplateEngine templateEngine;

    public SubmissionEmailContentGenerator(TemplateEngine templateEngine,
                                           String submissionEmailTemplate,
                                           SubmissionCompleteSummary submissionCompleteSummary) {
        this.templateEngine = templateEngine;
        this.submissionEmailTemplate = submissionEmailTemplate;
        this.submissionCompleteSummary = submissionCompleteSummary;
    }

    @Override
    public String generate() {
        final Context context = new Context();
        final String projectAccession = submissionCompleteSummary.getProjectAccession();
        final String doi = submissionCompleteSummary.getDoi();

        context.setVariable("projectAccession", projectAccession);
        if (doi != null) {
            context.setVariable("doi", doi);
        }
        context.setVariable("projectAccessionPlusDoi", projectAccession);
        context.setVariable("projectTitle", submissionCompleteSummary.getProjectName());
        context.setVariable("submitterName", submissionCompleteSummary.getSubmitterName());
        context.setVariable("reviewerAccount", submissionCompleteSummary.getReviewerUserName());
        context.setVariable("reviewerPassword", submissionCompleteSummary.getReviewerPassword());
        context.setVariable("bioSamplesId", submissionCompleteSummary.getBioSamplesId());

        return templateEngine.process(submissionEmailTemplate, context);
    }
}
