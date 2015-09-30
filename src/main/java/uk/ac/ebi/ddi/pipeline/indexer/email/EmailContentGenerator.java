package uk.ac.ebi.ddi.pipeline.indexer.email;

/**
 * Interface for generating email content using template
 *
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 18/08/2015
 */
public interface EmailContentGenerator {

    String generate();
}
