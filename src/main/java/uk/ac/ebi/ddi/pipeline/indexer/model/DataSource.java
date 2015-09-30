package uk.ac.ebi.ddi.pipeline.indexer.model;

/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 29/09/15
 */
public class DataSource {

    private String name = null;

    private String originalURL = null;

    private String dataURL = null;

    private String pattern = null;

    private boolean singleFile = false;

    private String typeConnection = null;

    public DataSource(String name, String originalURL, String dataURL, String pattern, boolean singleFile, String typeConnection) {
        this.name = name;
        this.originalURL = originalURL;
        this.dataURL = dataURL;
        this.pattern = pattern;
        this.singleFile = singleFile;
        this.typeConnection = typeConnection;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOriginalURL() {
        return originalURL;
    }

    public void setOriginalURL(String originalURL) {
        this.originalURL = originalURL;
    }

    public String getDataURL() {
        return dataURL;
    }

    public void setDataURL(String dataURL) {
        this.dataURL = dataURL;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public boolean isSingleFile() {
        return singleFile;
    }

    public void setSingleFile(boolean singleFile) {
        this.singleFile = singleFile;
    }

    public String getTypeConnection() {
        return typeConnection;
    }

    public void setTypeConnection(String typeConnection) {
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
