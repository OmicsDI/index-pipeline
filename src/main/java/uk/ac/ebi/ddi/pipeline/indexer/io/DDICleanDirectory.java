package uk.ac.ebi.ddi.pipeline.indexer.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * This code is licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0

 *  ==Overview==
 *
 *  This class
 *
 * Created by ypriverol (ypriverol@gmail.com) on 17/07/2016.
 */
public class DDICleanDirectory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DDICleanDirectory.class);

    public static void cleanDirectory(String directoryName) {
        File inputDirectory = new File(directoryName);
        if (inputDirectory.isDirectory()) {
            File[] files = inputDirectory.listFiles();
            if (files == null) {
                return;
            }
            for (File file : files) {
                try {
                    Files.deleteIfExists(file.toPath());
                } catch (IOException e) {
                    LOGGER.error("An error occurred when delete file {}, ", file.getAbsolutePath(), e);
                }
            }
        }
    }

    public static void cleanDirectory(Resource inputDirectory) throws IOException {
        if (inputDirectory != null && inputDirectory.getFile().isDirectory()) {
            File[] files = inputDirectory.getFile().listFiles();
            if (files == null) {
                return;
            }
            for (File file : files) {
                try {
                    Files.deleteIfExists(file.toPath());
                } catch (IOException e) {
                    LOGGER.error("An error occurred when delete file {}, ", file.getAbsolutePath(), e);
                }
            }
        }
    }
}
