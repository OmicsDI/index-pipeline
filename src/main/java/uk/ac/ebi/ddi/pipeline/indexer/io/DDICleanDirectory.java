package uk.ac.ebi.ddi.pipeline.indexer.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import org.springframework.core.io.Resource;

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

    public static void cleanDirectory(String directoryName){
        File inputDirectory = new File(directoryName);
        if(inputDirectory != null && inputDirectory.isDirectory() && inputDirectory.listFiles().length > 0){
            Arrays.asList(inputDirectory.listFiles()).stream().forEach(file -> {
                try {
                    Files.deleteIfExists(file.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void cleanDirectory(Resource inputDirectory) throws IOException {
        if(inputDirectory != null && inputDirectory.getFile().isDirectory() && inputDirectory.getFile().listFiles().length > 0){
            Arrays.asList(inputDirectory.getFile().listFiles()).stream().forEach(file -> {
                try {
                    Files.deleteIfExists(file.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
