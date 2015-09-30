package uk.ac.ebi.ddi.pipeline.indexer.io;

import java.util.Map;

/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 29/09/15
 */
public class TSVHFileItem {

    private final Map<String, Integer> headerByName;
    private String[] columnValues;

    public TSVHFileItem(Map<String, Integer> header) {
        this.headerByName = header;
        this.columnValues = new String[header.size()];
    }

    public void setColumnValue(String columnName, String value) {
        this.columnValues[headerByName.get(columnName)] = value;
    }

    public String getColumnValue(String columnName) {
        return this.columnValues[headerByName.get(columnName)];
    }

    public String[] getAllColumnValues() {
        return columnValues;
    }
}
