package uk.ac.ebi.ddi.pipeline.indexer.email;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring3.SpringTemplateEngine;

/**
 * Generate email content for publication complete email
 *
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 18/08/2015
 */
public class PublicationEmailContentGenerator implements EmailContentGenerator {
    private final PublicationCompleteSummary publicationCompleteSummary;
    private final String publicationEmailTemplate;
    private final TemplateEngine templateEngine;

    public PublicationEmailContentGenerator(SpringTemplateEngine templateEngine,
                                            String publicationEmailTemplate,
                                            PublicationCompleteSummary publicationCompleteSummary) {
        this.publicationCompleteSummary = publicationCompleteSummary;
        this.publicationEmailTemplate = publicationEmailTemplate;
        this.templateEngine = templateEngine;
    }

    @Override
    public String generate() {
        final Context context = new Context();
        final String projectAccession = publicationCompleteSummary.getProjectAccession();

        context.setVariable("projectAccession", projectAccession);
        context.setVariable("projectTitle", publicationCompleteSummary.getProjectTitle());
        context.setVariable("submitterName", publicationCompleteSummary.getSubmitterName());
        final String pubmedId = publicationCompleteSummary.getPubmedId();
        if (pubmedId != null) {
            context.setVariable("pubmedId", pubmedId);
        }
        context.setVariable("projectUrl", publicationCompleteSummary.getProjectUrl());
        context.setVariable("ftpUrl", publicationCompleteSummary.getFtpUrl());
        context.setVariable("bioSamplesID", publicationCompleteSummary.getBioSamplesId());

        return templateEngine.process(publicationEmailTemplate, context);
    }
}
