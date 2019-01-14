package uk.ac.ebi.ddi.pipeline.indexer.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 29/09/15
 */
@Setter
@Getter
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

    @Override
    public String toString() {
        return "DataSource{" +
                "name='" + name + '\'' +
                ", originalURL='" + originalURL + '\'' +
                ", dataURL='" + dataURL + '\'' +
                ", pattern='" + pattern + '\'' +
                ", singleFile=" + singleFile +
                ", typeConnection='" + typeConnection + '\'' +
                '}';
    }
}
