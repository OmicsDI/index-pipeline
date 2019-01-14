package uk.ac.ebi.ddi.pipeline.indexer.email;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.thymeleaf.spring3.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/**
 * This is a manuel testing class for generating email content
 *
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 18/08/2015
 */
public class EmailContentRunner {

    public static void main(String[] args) throws Exception {
        final ClassLoaderTemplateResolver classLoaderTemplateResolver = new ClassLoaderTemplateResolver();
        classLoaderTemplateResolver.setPrefix("template/");
        classLoaderTemplateResolver.setTemplateMode("HTML5");
        classLoaderTemplateResolver.setCharacterEncoding("UTF-8");
        classLoaderTemplateResolver.setOrder(1);

        final SpringTemplateEngine springTemplateEngine = new SpringTemplateEngine();
        springTemplateEngine.addTemplateResolver(classLoaderTemplateResolver);
        springTemplateEngine.setMessageSource(new ReloadableResourceBundleMessageSource());
        springTemplateEngine.afterPropertiesSet();

        // submission
        final SubmissionCompleteSummary summary = new SubmissionCompleteSummary(
                "PXT000121", null, "Test project name",
                "Yasset Perez-Riverol", "yperez@@ebi.ac.uk",
                "reviewerpassword", "TEST-BIOSD-ID");
        final SubmissionEmailContentGenerator submissionEmailContentGenerator = new SubmissionEmailContentGenerator(
                        springTemplateEngine, "pride-submission-email.html", summary);
        System.out.println(submissionEmailContentGenerator.generate());
    }
}
