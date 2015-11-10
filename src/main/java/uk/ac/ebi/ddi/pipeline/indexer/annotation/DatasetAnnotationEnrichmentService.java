package uk.ac.ebi.ddi.pipeline.indexer.annotation;

import org.json.JSONException;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.ddi.annotation.model.DatasetTobeEnriched;
import uk.ac.ebi.ddi.annotation.model.EnrichedDataset;
import uk.ac.ebi.ddi.annotation.service.DDIAnnotationService;
import uk.ac.ebi.ddi.annotation.service.DDIExpDataImportService;
import uk.ac.ebi.ddi.annotation.service.DDIPublicationAnnotationService;
import uk.ac.ebi.ddi.annotation.utils.DataType;
import uk.ac.ebi.ddi.xml.validator.exception.DDIException;
import uk.ac.ebi.ddi.xml.validator.parser.model.Entry;
import uk.ac.ebi.ddi.xml.validator.parser.model.Reference;
import uk.ac.ebi.ddi.xml.validator.utils.Field;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains a set of methods that hels the enrichment and annotation of different datasets
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 04/11/15
 */
public class DatasetAnnotationEnrichmentService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DatasetAnnotationEnrichmentService.class);


    /**
     * This function provides a way of doing the enrichment of an specific dataset using the enrichment service
     * @param service DDIAnnotationService that enrich a correponding dataset
     * @param dataset Entry to be enriched
     * @return
     * @throws DDIException
     * @throws UnsupportedEncodingException
     * @throws JSONException
     */
    public static EnrichedDataset enrichment(DDIAnnotationService service, Entry dataset) throws DDIException, UnsupportedEncodingException, JSONException {

        DatasetTobeEnriched datasetTobeEnriched = new DatasetTobeEnriched(dataset.getId(),dataset.getAdditionalFieldValue(Field.REPOSITORY.getName()),
                dataset.getName().getValue(), dataset.getDescription(), dataset.getAdditionalFieldValue(Field.SAMPLE.getName()),
                dataset.getAdditionalFieldValue(Field.DATA.getName()));

        EnrichedDataset enrichedDataset = service.enrichment(datasetTobeEnriched);

        return enrichedDataset;
    }

    /**
     * This function import all the biological entities into the MongoDB database and to compute the similarity scores.
     * @param dataset Entry dataset
     * @param dataType Data type to be index Metabolomics, proteomics, etc
     * @param ddiExpDataImportService The import service
     */
    public static void importTermsToDatabase(Entry dataset, DataType dataType, DDIExpDataImportService ddiExpDataImportService){
        String entryId = dataset.getId();
        List<Reference> refs = dataset.getCrossReferences().getRef();
        ddiExpDataImportService.importDatasetTerms(dataType.getName(), entryId, dataset.getAdditionalFieldValue(Field.REPOSITORY.getName()), refs);
    }

    /**
     * Add the enrichment fields to the entry to be use during indexing process
     * @param dataset Entry the dataset to add the new fields
     * @param enrichedDataset The new fields to be added to the dataset
     * @return Entry a new entry with all the fields
     */
    public static Entry addEnrichedFields(Entry dataset, EnrichedDataset enrichedDataset){
        dataset.addAdditionalField(Field.ENRICH_SYNONYMS.getName(), enrichedDataset.getEnrichedTitle());
        dataset.addAdditionalField(Field.ENRICH_SYNONYMS.getName(), enrichedDataset.getEnrichedAbstractDescription());
        dataset.addAdditionalField(Field.ENRICH_SYNONYMS.getName(), enrichedDataset.getEnrichedSampleProtocol());
        dataset.addAdditionalField(Field.ENRICH_SYNONYMS.getName(), enrichedDataset.getEnrichedDataProtocol());
        return dataset;
    }

    /**
     * This function takes a dataset check if contains pubmed articles in the cross-references. If the pubmed ids are not provided
     * as cross-references, the currect function looks in all the fields of a dataset for dois information and retrive the pubmed
     * id and annotated them.
     *
     * @param service DDIPublicationAnnotationService
     * @param dataset dataset to be updated
     * @return Entry the new dataset with the corresponding information
     */
    public static Entry updatePubMedIds(DDIPublicationAnnotationService service, Entry dataset){
        // check if the dataset contains pubmed references
        if(dataset.getCrossReferences() == null || dataset.getCrossReferenceFieldValue(Field.PUBMED.getName()).isEmpty()){
            List<String> datasetText = new ArrayList<>();
            datasetText.add(dataset.toString());
            List<String> dois = service.getDOIListFromText(datasetText);
            if(dois != null && !dois.isEmpty()){
                List<String> ids = service.getPubMedIDsFromDOIList(dois);
                if(ids != null && ids.size() >0){
                    for(String pubmedID: ids)
                        dataset.addCrossReferenceValue(Field.PUBMED.getName(), pubmedID);
                }
            }

        }
        return dataset;
    }


}
