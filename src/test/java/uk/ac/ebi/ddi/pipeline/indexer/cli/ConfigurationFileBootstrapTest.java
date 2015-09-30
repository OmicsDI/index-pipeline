package uk.ac.ebi.ddi.pipeline.indexer.cli;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.ddi.pipeline.indexer.model.DataSource;

import java.util.List;

import static org.junit.Assert.*;

public class ConfigurationFileBootstrapTest {

    private XMLConfiguration config;

    @Before
    public void setUp() throws Exception {
        config = ConfigurationFileBootstrap.getBootstrapSettings();
    }

    @Test
    public void testGetBootstrapSettings() throws Exception {
        if(config != null)
            System.out.println(config.toString());
        HierarchicalConfiguration sub = config.configurationAt("data-sources");
        List<Object> fieldNames = sub.getList("data-sources.data-source.name");
        for(int i = 0; i < fieldNames.size(); i++)
            if(fieldNames.get(i) != null)
                System.out.println(fieldNames.toString());
    }

    @Test
    public void testGetDataSources() throws Exception {
        List<DataSource> sources = ConfigurationFileBootstrap.getDataSources(config);
        for(DataSource source: sources)
            System.out.println(source.toString());

    }
}