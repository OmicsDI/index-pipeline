package uk.ac.ebi.ddi.pipeline.indexer.tasklet.database;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import uk.ac.ebi.ddi.annotation.service.database.DDIDatabaseAnnotationService;
import uk.ac.ebi.ddi.annotation.service.dataset.DDIDatasetAnnotationService;
import uk.ac.ebi.ddi.annotation.utils.DatasetUtils;
import uk.ac.ebi.ddi.pipeline.indexer.io.DDIFile;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.annotation.AnnotationXMLTasklet;
import uk.ac.ebi.ddi.service.db.model.dataset.Database;
import uk.ac.ebi.ddi.service.db.model.dataset.Dataset;
import uk.ac.ebi.ddi.service.db.utils.DatasetCategory;
import uk.ac.ebi.ddi.xml.validator.parser.model.Entry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
 * Created by ypriverol (ypriverol@gmail.com) on 26/05/2016.
 */

@Getter
@Setter
public class DatasetExportTasklet extends AbstractTasklet {

    public static final Logger LOGGER = LoggerFactory.getLogger(AnnotationXMLTasklet.class);

    Resource outputDirectory;

    String databaseName;

    private String filePrefix;

    private int numberEntries;

    DDIDatasetAnnotationService datasetAnnotationService;

    DDIDatabaseAnnotationService databaseService;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        List<Entry> listToPrint = new ArrayList<>();
        final int[] counterFiles = {1};
        List<Dataset> datasets = datasetAnnotationService.getAllDatasetsByDatabase(databaseName);
        datasets = datasets.parallelStream()
                .filter(x ->  x.getCurrentStatus().equalsIgnoreCase(DatasetCategory.INSERTED.getType()) ||
                        x.getCurrentStatus().equalsIgnoreCase(DatasetCategory.UPDATED.getType()) ||
                        x.getCurrentStatus().equalsIgnoreCase(DatasetCategory.ENRICHED.getType()) ||
                x.getCurrentStatus().equalsIgnoreCase(DatasetCategory.FILES_FETCHED.getType()))
                .collect(Collectors.toList());

        Database database = databaseService.getDatabaseInfo(databaseName);
        try {
            datasets.forEach(ds -> {
                if (datasetAnnotationService.getMergedDatasetCount(ds.getDatabase(), ds.getAccession()) == 0) {
                    Dataset existingDataset = datasetAnnotationService.getDataset(ds.getAccession(), ds.getDatabase());
                    Entry entry = DatasetUtils.tansformDatasetToEntry(existingDataset);
                    listToPrint.add(entry);

                    //shorten description per EBI Search request
                    String description = entry.getDescription();
                    if (null != description) {
                        //entry.setDescription(description.substring(0, 100) + "...");&& description.length() > 100
                        entry.setDescription(description);
                    }
                }
                if (listToPrint.size() == numberEntries) {
                    try {
                        DDIFile.writeList(listToPrint, filePrefix, counterFiles[0], outputDirectory.getFile(),
                                database.getDescription(), databaseName, database.getReleaseTag());
                        listToPrint.clear();
                        counterFiles[0]++;
                    } catch (IOException e) {
                        LOGGER.error("Exception occurred when processing dataset {}, ", ds.getAccession(), e);
                    }
                }
            });
            // This must be printed before leave because it contains the end members of the list.
            if (!listToPrint.isEmpty()) {
                DDIFile.writeList(listToPrint, filePrefix, counterFiles[0], outputDirectory.getFile(),
                        database.getDescription(), databaseName, database.getReleaseTag());
            }
        } catch (Exception ex) {
            LOGGER.error("Exception occurred, ", ex);
        }
        return RepeatStatus.FINISHED;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(outputDirectory, "Input Directory can not be null");
        Assert.notNull(datasetAnnotationService, "Annotation Service can't be null");
    }
}
