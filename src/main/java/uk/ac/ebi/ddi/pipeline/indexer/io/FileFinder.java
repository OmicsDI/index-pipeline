package uk.ac.ebi.ddi.pipeline.indexer.io;

import java.io.File;
import java.io.IOException;

/**
 * Interface find a file using a given file
 * <p/>
 * NOTE: it could be the same file
 *
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 29/09/15
 */
public interface FileFinder {

    File find(File file) throws IOException;
}
