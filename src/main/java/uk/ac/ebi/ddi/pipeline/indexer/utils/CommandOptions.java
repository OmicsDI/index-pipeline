package uk.ac.ebi.ddi.pipeline.indexer.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 30/09/15
 */
public enum CommandOptions {

    UPDATE("update"),
    DOWNLOAD("download"),
    ENRICHMENT("enrichment"),
    VALIDATE("validate"),
    RESTART("restart");

    private final String name;

    CommandOptions(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static List<String> getValuesName() {
        List<String> values = new ArrayList<>();
        for (CommandOptions command : values()) {
            values.add(command.getName());
        }
        return values;
    }
}
