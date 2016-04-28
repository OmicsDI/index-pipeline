package uk.ac.ebi.ddi.pipeline.indexer.io;


import uk.ac.ebi.ddi.xml.validator.parser.OmicsXMLFile;
import uk.ac.ebi.ddi.xml.validator.parser.marshaller.OmicsDataMarshaller;
import uk.ac.ebi.ddi.xml.validator.parser.model.Database;
import uk.ac.ebi.ddi.xml.validator.parser.model.Entries;
import uk.ac.ebi.ddi.xml.validator.parser.model.Entry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 20/10/15
 */
public class DDIFile {

    public static void writeList(OmicsXMLFile originalReader, List<Entry> listToPrint, String prefixFile, int postfix, File folder) throws FileNotFoundException {

        if(folder != null && folder.isDirectory()){
            OutputStream outputFile = new FileOutputStream(folder.getAbsolutePath() + "/" + prefixFile + "_" + postfix + ".xml");
            OmicsDataMarshaller outputXMLFile = new OmicsDataMarshaller();
            Database database = new Database();
            database.setDescription(originalReader.getDescription());
            database.setName(originalReader.getName());
            database.setRelease(originalReader.getRelease());
            database.setReleaseDate(originalReader.getReleaseDate());
            database.setEntryCount(listToPrint.size());
            database.setEntries(new Entries(listToPrint));
            outputXMLFile.marshall(database, outputFile);
        }


    }

    public static void writeList(OmicsXMLFile originalReader, List<Entry> listToPrint, String prefixFile, String postfix, File folder) throws FileNotFoundException {

        if(folder != null && folder.isDirectory()){
            OutputStream outputFile = new FileOutputStream(folder.getAbsolutePath() + "/" + prefixFile + "_" + postfix + ".xml");
            OmicsDataMarshaller outputXMLFile = new OmicsDataMarshaller();
            Database database = new Database();
            database.setDescription(originalReader.getDescription());
            database.setName(originalReader.getName());
            database.setRelease(originalReader.getRelease());
            database.setReleaseDate(originalReader.getReleaseDate());
            database.setEntryCount(listToPrint.size());
            database.setEntries(new Entries(listToPrint));
            outputXMLFile.marshall(database, outputFile);
        }


    }

    public static void writeList(OmicsXMLFile originalReader, List<Entry> listToPrint, File file) throws FileNotFoundException {

        OutputStream outputFile = new FileOutputStream(file);
        OmicsDataMarshaller outputXMLFile = new OmicsDataMarshaller();
        Database database = new Database();
        database.setDescription(originalReader.getDescription());
        database.setName(originalReader.getName());
        database.setRelease(originalReader.getRelease());
        database.setReleaseDate(originalReader.getReleaseDate());
        database.setEntryCount(listToPrint.size());
        database.setEntries(new Entries(listToPrint));
        outputXMLFile.marshall(database, outputFile);
    }
}
