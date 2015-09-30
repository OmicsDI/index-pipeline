package uk.ac.ebi.ddi.pipeline.indexer.io;

import java.io.File;
import java.io.IOException;

/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 29/09/15
 */
public class SubmissionDirectoryFinder {

    public static File findSubmissionDirectory(File startingDirectory, String accession) throws IOException {
        if (isSubmissionDirectory(startingDirectory, accession)) {
            return startingDirectory;
        } else {
            File result = null;
            if (startingDirectory.isDirectory()) {
                for (File file : startingDirectory.listFiles()) {
                    result = findSubmissionDirectory(file, accession);
                    if (result != null) {
                        break;
                    }
                }
            }
            return result;
        }
    }

    private static boolean isSubmissionDirectory(File file, String accession) throws IOException {
        return (file.isDirectory() && file.getName().equals(accession));
    }

}
