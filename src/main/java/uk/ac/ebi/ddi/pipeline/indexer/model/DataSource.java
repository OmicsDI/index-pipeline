package uk.ac.ebi.ddi.pipeline.indexer.model;

import lombok.Data;

/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 29/09/15
 */
@Data
public class DataSource {

    private String name;

    private String originalURL;

    private String dataURL;

    private String pattern;

    private boolean singleFile;

    private String typeConnection;

    public DataSource(String name, String originalURL, String dataURL, String pattern, boolean singleFile,
                      String typeConnection) {
        this.name = name;
        this.originalURL = originalURL;
        this.dataURL = dataURL;
        this.pattern = pattern;
        this.singleFile = singleFile;
        this.typeConnection = typeConnection;
    }
}
