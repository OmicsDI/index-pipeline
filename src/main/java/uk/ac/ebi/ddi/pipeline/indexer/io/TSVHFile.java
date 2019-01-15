package uk.ac.ebi.ddi.pipeline.indexer.io;

import java.io.*;
import java.util.*;

/**
 * A Tab-Separated, Variable-Header, universal representation and parsing class.
 * <p/>
 * Use Case 1: We want to parse a tab-separated file into memory.
 * -> Create an instance of TSVHFile calling the static factory method:
 *                                  TSVHFile tsvhFile = TSVHFile.parseFile(newReadmeFile);
 * -> Access the header Map<column_name,pos> using tsvhFile.getHeader()
 * -> Access the items using tsvhFile.getItemsIterator()
 * -> Each item has methods for getting the column value using the name of the column
 * <p/>
 * Use Case 2: We create.modify a TSVHFile in memory and after we write to disk.
 * -> Create the file using the default no-argument constructor.
 * -> Built up the header using calls to tsvhFile.addHeaderColumn("HEADER_COLUMN_NAME", pos). One tip here is to use an
 * Enum type with each header column name and pos and iterates through its elements.
 * -> Add items using String[] arrays matching the previous header
 * -> Persist the file to disk calling tsvhFile.save(File).
 * <p/>
 * Notice that File items are just associated with the TSHVFile when parsing and saving, but this association is not
 * kept during the life of the object itself.
 * <p/>
 * One example of using an Enum type for quickly adding the header:
 * <p/>
 * // create the file
 * TSVHFile readmeFile = new TSVHFile();
 * // add the header to the file
 * for (ReadmeFileHeaderEnum header: ReadmeFileHeaderEnum.values()) {
 * readmeFile.addHeaderColumn(header.getText(), header.getPos());
 * }
 * <p/>
 * Se each method doc for details.
 *
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 29/09/15
 */
public class TSVHFile {

    private static final char TAB = '\t';
    private Map<String, Integer> headerByName = new HashMap<>();
    private Map<Integer, String> headerByPos = new HashMap<>();

    private List<String[]> items = new LinkedList<>();

    /**
     * Returns an in memory instance of a tab-separated variable-header file or null if parsing error occurs.
     *
     * @param file
     * @return
     */
    public static TSVHFile parseFile(File file) {
        TSVHFile res = null;

        try {
            // try to open and parse the file
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            res = new TSVHFile();
            String line;

            // parse the header
            int pos = 0;
            line = br.readLine();
            String[] columns = line.split("" + TAB);
            for (String columnName : columns) {
                res.addHeaderColumn(columnName, pos);
                pos++;
            }

            // parse the items
            while ((line = br.readLine()) != null) {   // TODO: read here empty lines or comments?
                columns = line.split("" + TAB);
                res.addItem(columns);
            }

        } catch (IOException e) {
            res = null;
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return res;
    }

    /**
     * Writes the in memory file to disk
     *
     * @param file
     */
    public void save(File file) {

        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));

            // Writes the header, in order
            StringBuilder line = new StringBuilder();
            for (int headerPos : headerByPos.keySet()) {
                line.append(headerByPos.get(headerPos)).append(TAB);
            }
            if (line.length() > 0) { // deletes last TAB
                line.deleteCharAt(line.lastIndexOf("" + TAB));
            }
            pw.println(line.toString());

            // Writes the items, one per line, tab separated elements
            Iterator<TSVHFileItem> iterator = this.getItemsIterator();
            while (iterator.hasNext()) {
                line = new StringBuilder();
                for (String columnValue : iterator.next().getAllColumnValues()) {
                    if (columnValue == null) {
                        columnValue = "-";
                    }
                    line.append(columnValue).append(TAB);
                }
                if (line.length() > 0) { // deletes last TAB
                    line.deleteCharAt(line.lastIndexOf("" + TAB));
                }
                pw.println(line.toString());
            }

            pw.close();

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    /**
     * Adds or replace a new column header. If pos (0-based) is greater than header.size() the new column is added at
     * the end of the header.
     *
     * @param columnName
     */
    public void addHeaderColumn(String columnName, int pos) {
        if (pos > headerByName.size()) { // both header should have the same size...
            headerByName.put(columnName, headerByName.size());
            headerByPos.put(headerByPos.size(), columnName);
        } else {
            headerByName.put(columnName, pos);
            headerByPos.put(pos, columnName);
        }
    }


    /**
     * Returns the header map
     *
     * @return
     */
    public Map<String, Integer> getHeader() {
        return this.headerByName;
    }

    /**
     * Adds a new item at the bottom of the file
     *
     * @param item
     */
    public void addItem(String[] item) {
        this.items.add(item);
    }

    public Iterator<TSVHFileItem> getItemsIterator() {
        return new TSVHFileItemIterator();
    }

    private class TSVHFileItemIterator implements Iterator<TSVHFileItem> {

        Iterator<String[]> iterator = items.iterator();

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public TSVHFileItem next() {
            TSVHFileItem item = new TSVHFileItem(headerByName);
            String[] columns = iterator.next();
            int pos = 0;

            for (String column : columns) {
                item.setColumnValue(headerByPos.get(pos), column);
                pos++;
            }

            return item;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
