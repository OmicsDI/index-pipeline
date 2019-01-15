package uk.ac.ebi.ddi.pipeline.indexer.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.ddi.annotation.utils.DatasetUtils;
import uk.ac.ebi.ddi.service.db.model.dataset.Dataset;
import uk.ac.ebi.ddi.service.db.utils.DatasetCategory;
import uk.ac.ebi.ddi.xml.validator.parser.model.Date;
import uk.ac.ebi.ddi.xml.validator.parser.model.Entry;
import uk.ac.ebi.ddi.xml.validator.utils.Field;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 18/11/2015
 */
public class DatasetAnnotationFieldsUtils {

    private static Pattern patternMassive = Pattern.compile("(MSV[0-9]+)");
    private static Pattern patternPX = Pattern.compile("(PXD[0-9]+)");

    private static final Logger LOGGER = LoggerFactory.getLogger(DatasetAnnotationFieldsUtils.class);

    public static Entry addpublicationDate(Entry dataset) {
        if (dataset.getDates() != null && !dataset.getDates().containsPublicationDate()) {
            dataset.getDates().addDefaultPublicationDate();
        }
        return dataset;
    }

    public static Dataset addpublicationDate(Dataset dataset) {
        if (dataset.getDates() != null && containsPublicationDate(dataset.getDates())) {
            dataset = addDefaultPublicationDate(dataset);
        }
        return dataset;
    }

    private static Dataset addDefaultPublicationDate(Dataset dataset) {
        Set<String> toAdd = null;
        if (dataset.getDates() != null && !dataset.getDates().isEmpty()) {
            for (String dateField : dataset.getDates().keySet()) {
                if (dateField.equalsIgnoreCase(Field.PUBLICATION_UPDATED.getName())) {
                    toAdd = dataset.getDates().get(dateField);
                }
            }
        }
        if (toAdd != null) {
            dataset.getDates().put(Field.PUBLICATION.getName(), toAdd);
        }
        return dataset;
    }

    private static boolean containsPublicationDate(Map<String, Set<String>> dates) {
        if (dates != null && !dates.isEmpty()) {
            for (String dateField : dates.keySet()) {
                if (dateField.equalsIgnoreCase(Field.PUBLICATION.getName())) {
                    return true;
                }
            }
        }
        return false;
    }


    public static Entry addPublicationDateFromSubmission(Entry dataset) {
        if (dataset.getDates() != null && !dataset.getDates().containsPublicationDate()) {
            Date date = dataset.getDates().getDateByKey(Field.SUBMISSION_DATE.getName());
            if (date != null) {
                dataset.addDate(new Date(Field.PUBLICATION.getName(), date.getValue()));
            }
        }
        return dataset;
    }

    @Deprecated
    public static Entry cleanDescription(Entry dataset) {
        if (dataset != null && dataset.getDescription() != null) {
            String finalDescription;
            String[] descriptionArray = dataset.getDescription().split("\\(\\[\\[");
            if (descriptionArray.length > 1) {
                String[] descriptionArraySecond = descriptionArray[1].split("\\]\\]\\)");
                if (descriptionArraySecond.length > 1) {
                    finalDescription = descriptionArray[0] + " " + descriptionArraySecond[1];
                    dataset.setDescription(finalDescription);
                }
            }
        }
        return dataset;
    }

    /**
     * Remove unecesary scripts from the description.
     * @param dataset
     * @return
     */
    public static Dataset cleanDescription(Dataset dataset) {
        if (dataset != null && dataset.getDescription() != null) {
            String finalDescription;
            String[] descriptionArray = dataset.getDescription().split("\\(\\[\\[");
            if (descriptionArray.length > 1) {
                String[] descriptionArraySecond = descriptionArray[1].split("\\]\\]\\)");
                if (descriptionArraySecond.length > 1) {
                    finalDescription = descriptionArray[0] + " " + descriptionArraySecond[1];
                    dataset.setDescription(finalDescription);
                }
            }
        }
        return dataset;
    }

    public static Entry replaceMEDLINEPubmed(Entry dataset) {
        if (dataset.getCrossReferences() != null
                && !dataset.getCrossReferenceFieldValue(Field.MEDLINE.getName()).isEmpty()) {
            dataset.getCrossReferenceFieldValue(Field.MEDLINE.getName()).stream()
                    .filter(value -> value != null && !value.isEmpty())
                    .forEach(value -> dataset.addCrossReferenceValue(Field.PUBMED.getName(), value));
            dataset.removeCrossReferences(Field.MEDLINE.getName());
        }
        return dataset;
    }

    public static Entry replaceAuthorField(Entry dataset) {
        if (dataset.getAuthors() != null && !dataset.getAuthors().isEmpty()) {
            dataset.addAdditionalField(Field.SUBMITTER.getName(), dataset.getAuthors());
            dataset.setAuthors(null);
        }
        return dataset;
    }

    public static Entry replaceKeywords(Entry dataset) {
        if (dataset.getKeywords() != null && !dataset.getKeywords().isEmpty()) {
            dataset.addAdditionalField(Field.SUBMITTER_KEYWORDS.getName(), dataset.getKeywords());
            dataset.setKeywords(null);
        }
        return dataset;
    }

    public static Dataset addCrossReferenceAnnotation(Dataset existing) {
        Matcher matcher  = patternMassive.matcher(existing.getDescription());
        boolean match = matcher.find();
        if (match) {
            DatasetUtils.addCrossReferenceValue(existing, "Massive", matcher.group(0));
        }

        matcher  = patternPX.matcher(existing.getDescription());
        match = matcher.find();
        if (match) {
            DatasetUtils.addCrossReferenceValue(existing, "ProteomeXChange", matcher.group(0));
        }

        return existing;
    }

    public static Map<String, Set<String>> getCrossSimilars(Dataset dataset, List<String> databases) {
        Map<String, Set<String>> similars = new HashMap<>();
        if (dataset.getCrossReferences() != null && !dataset.getCrossReferences().isEmpty()) {
            dataset.getCrossReferences().entrySet().forEach(cross -> {
                if (databases.contains(cross.getKey())) {
                    similars.put(cross.getKey(), cross.getValue());
                }
            });
        }
        return similars;
    }

    public static Dataset cleanRepository(Dataset dataset, String database) {
        if (dataset.getAdditional().containsKey(Field.REPOSITORY.getName())) {
            Set<String> databaseSet = new HashSet<>();
            databaseSet.add(database);
            dataset.addAdditional(Field.REPOSITORY.getName(), databaseSet);
        }
        return dataset;
    }

    public static Dataset addInformationFromOriginal(Dataset originalDataset, Dataset reanalysisDataset) {
        if (originalDataset.getName() != null && !originalDataset.getName().isEmpty()) {
            reanalysisDataset.setName(originalDataset.getName());
        }
        if (originalDataset.getDescription() != null && !originalDataset.getDescription().isEmpty()) {
            reanalysisDataset.setDescription(originalDataset.getDescription());
        }
        if (originalDataset.getAdditional().containsKey(Field.SAMPLE.getName())) {
            reanalysisDataset.addAdditional(
                    Field.SAMPLE.getName(), originalDataset.getAdditional().get(Field.SAMPLE.getName()));
        }
        if (originalDataset.getCrossReferences() != null) {
            for (Map.Entry entry: originalDataset.getCrossReferences().entrySet()) {
                String key = (String) entry.getKey();
                Set<String> values = (Set<String>) entry.getValue();
                reanalysisDataset.addCrossReferences(key, values);
            }
        }
        return reanalysisDataset;
    }

    public static Dataset refineDates(Dataset dataset) {
        if (dataset.getDates() != null && !dataset.getDates().isEmpty()) {
            Map<String, Set<String>> newDates = new HashMap<>();
            for (String dateField: dataset.getDates().keySet()) {
                Set<String> newValues = new HashSet<>();
                for (String oldDate: dataset.getDates().get(dateField)) {
                    newValues.add(returnDate(oldDate));
                }
                newDates.put(dateField, newValues);
            }
            dataset.setDates(newDates);
        }
        return dataset;
    }

    private static String returnDate(String value) {
        value = returnStringDateRefined(value);
        String[] dateValues = new String[]{"yyyy-MM-dd", "dd-MMM-yyyy HH:mm:ss", "yy-MM-dd"};

        for (String dateStr: dateValues) {
            try {
                java.util.Date date = new SimpleDateFormat(dateStr).parse(value);
                return new SimpleDateFormat("yyyy-MM-dd").format(date);
            } catch (ParseException e) {
                LOGGER.error("Exception occurred when parsing date {}, ", value, e);
            }
        }

        return value;
    }

    private static String returnStringDateRefined(String value) {
        if (value != null && !value.isEmpty()) {
            String[] valueArr = value.split("-");
            if (valueArr.length == 3) {
                String year = correctYear(valueArr[0]);
                return year + "-" + valueArr[1] + "-" + valueArr[2];
            }
        }
        return value;

    }

    private static String correctYear(String s) {
        if (s.length() == 4 && s.startsWith("0")) {
            s = s.replaceFirst("0", "2");
        }
        return s;

    }

    public static Dataset refinePeptideAtlasKeyword(Dataset existingDataset) {
        if (existingDataset == null) {
            return null;
        }
        if (existingDataset.getCurrentStatus().equalsIgnoreCase(DatasetCategory.DELETED.getType())) {
            existingDataset.setCurrentStatus(DatasetCategory.ENRICHED.getType());
        }
        if (existingDataset.getAdditional() != null
                && existingDataset.getAdditional().get(Field.SUBMITTER_KEYWORDS.getName()) != null) {
            boolean isSRM = false;
            for (String keyword: existingDataset.getAdditional().get(Field.SUBMITTER_KEYWORDS.getName())) {
                if (keyword.equalsIgnoreCase(Constants.PEPTIDEATLAS_SRM)) {
                    isSRM = true;
                }
            }
            if (isSRM) {
                existingDataset.getAdditional().get(Field.SUBMITTER_KEYWORDS.getName()).add("PASSEL");
            }
        }
        if (existingDataset.getAdditional() != null
                && existingDataset.getAdditional().get(Field.CURATOR_KEYWORDS.getName()) != null) {
            boolean isSRM = false;
            for (String keyword: existingDataset.getAdditional().get(Field.CURATOR_KEYWORDS.getName())) {
                if (keyword.equalsIgnoreCase(Constants.PEPTIDEATLAS_SRM)) {
                    isSRM = true;
                }
            }
            if (isSRM) {
                existingDataset.getAdditional().get(Field.CURATOR_KEYWORDS.getName()).add("PASSEL");
            }
        }
        return existingDataset;
    }

    /**
     * This is needed because EGA is exporting in a wrong way the data.
     * Todo: EGA should export the data using the correct term.
     * @param existingDataset Dataset to be curated.
     * @return return the curated dataset.
     */
    public static Dataset replaceStudyByDatasetLink(Dataset existingDataset) {
        if (existingDataset.getAdditional() != null
                && existingDataset.getAdditional().containsKey("full_study_link")) {
            Set<String> links = existingDataset.getAdditional().remove("full_study_link");
            existingDataset.getAdditional().put(Field.LINK.getName(), links);
        }
        return existingDataset;

    }

    public static Dataset replaceTextCase(Dataset existingDataset) {
        if (existingDataset.getAdditional().containsKey(Field.DISEASE_FIELD)) {
            Set<String> diseases = existingDataset.getAdditional().get(Field.DISEASE_FIELD);
            Set<String> updatedDisease =  diseases.parallelStream()
                    .map(Utils::toTitleCase).collect(Collectors.toSet());
            existingDataset.addAdditional(Field.DISEASE_FIELD.getName(), updatedDisease);
        }
        if (existingDataset.getAdditional().containsKey(Field.SPECIE_FIELD)) {
            Set<String> diseases = existingDataset.getAdditional().get(Field.SPECIE_FIELD);
            Set<String> updatedSpecies =  diseases.parallelStream()
                    .map(Utils::toTitleCase).collect(Collectors.toSet());
            existingDataset.addAdditional(Field.SPECIE_FIELD.getName(), updatedSpecies);
        }
        if (existingDataset.getAdditional().containsKey(Field.TISSUE_FIELD)) {
            Set<String> diseases = existingDataset.getAdditional().get(Field.TISSUE_FIELD);
            Set<String> updatedTissue =  diseases.parallelStream()
                    .map(Utils::toTitleCase).collect(Collectors.toSet());
            existingDataset.addAdditional(Field.TISSUE_FIELD.getName(), updatedTissue);
        }
        return existingDataset;
    }
}
