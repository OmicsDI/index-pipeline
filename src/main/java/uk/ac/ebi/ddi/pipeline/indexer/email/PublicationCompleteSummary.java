package uk.ac.ebi.ddi.pipeline.indexer.email;

import lombok.Getter;

/**
 * Summary of the publication details, including FTP URL etc
 *
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 18/08/2015
 */
@Getter
public class PublicationCompleteSummary {
    private final String projectAccession;
    private final String projectTitle;
    private final String projectUrl;
    private final String ftpUrl;
    private final String submitterName;
    private final String pubmedId;
    private final String bioSamplesId;

    public PublicationCompleteSummary(String pubmedId,
                                      String submitterName,
                                      String ftpUrl,
                                      String projectUrl,
                                      String projectTitle,
                                      String projectAccession,
                                      String bioSamplesId) {
        this.pubmedId = pubmedId;
        this.submitterName = submitterName;
        this.ftpUrl = ftpUrl;
        this.projectUrl = projectUrl;
        this.projectTitle = projectTitle;
        this.projectAccession = projectAccession;
        this.bioSamplesId = bioSamplesId;
    }
}
