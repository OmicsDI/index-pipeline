package uk.ac.ebi.ddi.pipeline.indexer.tasklet.enrichment;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.Assert;
import org.w3c.dom.Document;
import uk.ac.ebi.ddi.api.readers.utils.XMLUtils;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.service.db.model.enrichment.Identifier;
import uk.ac.ebi.ddi.service.db.service.enrichment.IEnrichmentInfoService;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * This code is licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0

 *  ==Overview==
 *
 *  This tasklet creates additional identifiers
 *
 * Created by azorin (azorin@ebi.ac.uk) on 22/01/2018.
 */
@Getter
@Setter
public class IdentifierEnrichmentTasklet extends AbstractTasklet {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdentifierEnrichmentTasklet.class);

    IEnrichmentInfoService enrichmentInfoService;

    String filePath;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        Stream<Path> files = Files.find(
                Paths.get(filePath), 100,
                (path, basicAttributes) -> path.getFileName().toString().startsWith("PRJNA"));

        List<Identifier> result = new ArrayList<>();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        files.parallel().forEach(file -> {
            try {
                File f = file.toFile();
                Identifier id = new Identifier();
                id.setAccession(f.getName().replace(".xml", ""));
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(f);
                String secondaryId = XMLUtils.readFirstElement(doc, "dbXREF/ID");
                id.setAdditional_accessions(new ArrayList<>());
                id.getAdditional_accessions().add(secondaryId);
                result.add(id);
            } catch (Exception ex) {
                LOGGER.error("Exception occurred when processing file {}, ", file.toFile().getAbsolutePath(), ex);
            }
        });

        LOGGER.info("Found {} identifiers", result.size());

        enrichmentInfoService.updateIdentifiers(result);

        return RepeatStatus.FINISHED;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(enrichmentInfoService, "The enrichmentInfoService can't be null");
    }
}
