package uk.ac.ebi.ddi.pipeline.indexer.cli;


import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.ddi.pipeline.indexer.exception.DDIException;
import uk.ac.ebi.ddi.pipeline.indexer.model.DataSource;


import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 29/09/15
 */
public class ConfigurationFileBootstrap {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ConfigurationFileBootstrap.class);

    /**
     * Read bootstrap settings from config/config.props file.
     *
     * @return Properties   bootstrap settings.
     */
    public static XMLConfiguration getBootstrapSettings() {
        // load properties
        XMLConfiguration config = null;
        try
        {
            URL pathURL = getFullPath(ConfigurationFileBootstrap.class, "config/config.xml");
            config = new XMLConfiguration(pathURL);
        }
        catch(ConfigurationException cex){
            new DDIException("The config file was removed or was not provided please check the folder config/config.xml", cex);
        }
        return config;
    }

    /**
     * This function provides a way to retrieve the config file from the config folder for the project
     *
     * @param cs
     * @param subPath
     * @return URL
     */
    private static URL getFullPath(Class cs, String subPath) {

        if ( cs == null) {
            throw new IllegalArgumentException("Input class cannot be NULL");
        }

        URL fullPath = null;

        CodeSource src = cs.getProtectionDomain().getCodeSource();
        if (src != null) {
            if (subPath == null) {
                fullPath = src.getLocation();
            } else {
                try {
                    fullPath = new URL(src.getLocation(), subPath);
                } catch (MalformedURLException e) {
                    logger.error("Failed to create a new URL based on: " + subPath);
                }
            }
        }

        return fullPath;
    }

    public static List<DataSource> getDataSources(XMLConfiguration config){
        List<DataSource> dataSources = new ArrayList<DataSource>();
        if(config != null){
            Object prop = config.getProperty("sources.source.name");
            if(prop instanceof Collection){
                int sourcesNumber  = ((Collection<?>) prop).size();
                for(int i = 0; i < sourcesNumber; i++){
                    String name = config.getString("sources.source(" + i + ").name");
                    String originalURL = config.getString("sources.source(" + i + ").original-url");
                    String dataURL = config.getString("sources.source(" + i + ").ddi-data");
                    String typeConnection = config.getString("sources.source(" + i + ").type-connection");
                    String singleFile = config.getString("sources.source(" + i + ").single-file");
                    String pattern = config.getString("sources.source(" + i + ").file-pattern");
                    dataSources.add(new DataSource(name, originalURL,dataURL,pattern,parseSingleFile(singleFile), typeConnection));
                }
            }
            System.out.println(prop.toString());
        }
        return dataSources;
    }

    private static boolean parseSingleFile(String singleFile) {
        boolean single = false;
        if(singleFile != null && (singleFile.equalsIgnoreCase("yes") || Boolean.parseBoolean(singleFile)))
            single = true;
        return single;

    }


}
