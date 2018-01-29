package uk.ac.ebi.ddi.pipeline.indexer.tasklet.enrichment;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.Assert;
import org.w3c.dom.Document;
import uk.ac.ebi.ddi.annotation.service.dataset.DDIDatasetAnnotationService;
import uk.ac.ebi.ddi.annotation.utils.Constants;
import uk.ac.ebi.ddi.annotation.utils.DatasetUtils;
import uk.ac.ebi.ddi.api.readers.bioprojects.ws.model.BioprojectDataset;
import uk.ac.ebi.ddi.api.readers.utils.XMLUtils;
import uk.ac.ebi.ddi.pipeline.indexer.tasklet.AbstractTasklet;
import uk.ac.ebi.ddi.service.db.model.dataset.Dataset;
import uk.ac.ebi.ddi.service.db.model.enrichment.Identifier;
import uk.ac.ebi.ddi.service.db.model.publication.PublicationDataset;
import uk.ac.ebi.ddi.service.db.service.enrichment.IEnrichmentInfoService;
import uk.ac.ebi.ddi.xml.validator.utils.Field;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
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
public class IdentifierEnrichmentTasklet extends AbstractTasklet{

    IEnrichmentInfoService enrichmentInfoService;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    String filePath;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception{

        Supplier<Stream<Path>> files = () -> {
            try {
                return Files.find(Paths.get(filePath), 100, (path, basicAttributes) -> {
                    return path.getFileName().toString().startsWith("PRJNA");
                });
            } catch (IOException ex) {
                System.out.print(String.format("processing %s files\n", ex.getMessage()));
                return null;
            }
        };

        List<Identifier> result = new ArrayList<Identifier>();

        System.out.print(String.format("processing %d files\n", files.get().count()));

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

        files.get().parallel().forEach(x -> {
            try {
                File f = x.toFile();

                Identifier ID = new Identifier();

                ID.setAccession(f.getName().replace(".xml",""));

                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

                Document doc = dBuilder.parse(f);

                String secondary_id = XMLUtils.readFirstElement(doc, "dbXREF/ID");

                ID.setAdditional_accessions(new ArrayList<String>());

                ID.getAdditional_accessions().add(secondary_id);

                result.add(ID);

                //dBuilder.reset();

            }
            catch (Exception ex){
                System.out.print("cannot process file  "+ x.toString() + ":"+ex.getMessage()+"\n");
                //dBuilder.reset();
            }

        });

         /****
        Identifier ID = new Identifier();
        ID.setAccession("enee");
        ID.setAdditional_accessions(new ArrayList<String>());
        ID.getAdditional_accessions().add("menee");
        result.add(ID);
         ****/

        System.out.print(String.format("found %d identifiers\n", result.size()));

        enrichmentInfoService.updateIdentifiers(result);

        return RepeatStatus.FINISHED;
    }

    public IEnrichmentInfoService getEnrichmentInfoService() {
        return enrichmentInfoService;
    }

    public void setEnrichmentInfoService(IEnrichmentInfoService enrichmentInfoService) {
        this.enrichmentInfoService = enrichmentInfoService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(enrichmentInfoService, "The enrichmentInfoService can't be null");
    }
}
