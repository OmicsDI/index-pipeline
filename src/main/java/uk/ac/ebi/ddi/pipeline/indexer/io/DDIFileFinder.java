package uk.ac.ebi.ddi.pipeline.indexer.io;

import uk.ac.ebi.ddi.pipeline.indexer.utils.FileUtil;

import java.io.File;
import java.io.IOException;

/**
 * File finder for PRIDE 3 file system
 *
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 29/09/15
 */
public class DDIFileFinder implements FileFinder {
    private final File rootPath;

    public DDIFileFinder(File rootPath) {
        if (!rootPath.isDirectory()) {
            throw new IllegalArgumentException("Root path must be a valid path: " + rootPath.getAbsolutePath());
        }

        this.rootPath = rootPath;
    }

    @Override
    public File find(File file) throws IOException {
        if (FileUtil.isGzipped(file)) {
            String decompressedFileName = FileUtil.getDecompressedFileName(file);
            File foundFile = findFileByName(decompressedFileName);
            if (foundFile != null) {
                return foundFile;
            }
        }

        return findFileByName(file.getName());
    }

    private File findFileByName(String fileName) {
        File[] files = rootPath.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    File[] nestedFiles = file.listFiles();
                    if (nestedFiles != null) {
                        for (File nestedFile : nestedFiles) {
                            if (nestedFile.getName().equals(fileName)) {
                                return nestedFile;
                            }
                        }
                    }
                } else {
                    if (file.getName().equals(fileName)) {
                        return file;
                    }
                }
            }
        }

        return null;
    }
}
