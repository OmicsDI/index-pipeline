package uk.ac.ebi.ddi.pipeline.indexer.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.ddi.pipeline.indexer.model.Attributes;
import uk.ac.ebi.ddi.pipeline.indexer.model.Submissions;
import uk.ac.ebi.ddi.service.db.model.dataset.Dataset;
import uk.ac.ebi.ddi.service.db.service.dataset.DatasetService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class StudiesParser {

    public static final String SUBMISSIONS = "submissions";
    public static final String TITLE = "Title";
    public static final String ABSTRACT = "Abstract";
    public static final String EXPERIMENTTYPE = "Experiment type";
    public static final String ORGANISM = "Organism";
    public static final String PUBLICATION = "ReleaseDate";
    //public static final String

    DatasetService datasetService;

    private void parseJson(InputStream is) throws IOException {

        List<Submissions> submissionsList = new LinkedList<Submissions>();
        // Create and configure an ObjectMapper instance
        ObjectMapper mapper = new ObjectMapper();
        //mapper.registerModule(new JavaTimeModule());
        mapper.findAndRegisterModules();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        // Create a JsonParser instance
        try (JsonParser jsonParser = mapper.getFactory().createParser(is)) {
            JsonToken jsonToken = jsonParser.nextToken();

            while (jsonToken != JsonToken.END_OBJECT) {
                System.out.println(jsonParser.getCurrentName());
                if (jsonToken == JsonToken.FIELD_NAME && SUBMISSIONS.equals(jsonParser.getCurrentName())) {

                    System.out.println("\nYour are in submissions ");

                    jsonToken = jsonParser.nextToken();

                    if (jsonToken == JsonToken.START_ARRAY) {
                        jsonToken = jsonParser.nextToken();
                    }
                    // Iterate over the tokens until the end of the array
                    while (jsonToken != JsonToken.END_ARRAY) {
                        jsonToken = jsonParser.nextToken();
                        // Read a contact instance using ObjectMapper and do something with it
                        Submissions submissions = mapper.readValue(jsonParser, Submissions.class);
                        Dataset dataset = tranformSubmissionDataset(submissions);
                        //datasetService.save(dataset);
                        submissions.toString();
                        System.out.println("accession number is " + submissions.getAccno());
                        submissionsList.add(submissions);
                        System.out.println("list count is " + submissionsList.size());
                        jsonToken = jsonParser.nextToken();
                    }
                }
                jsonToken = jsonParser.nextToken();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        File initialFile = new File("/media/gaur/Elements/biostudies/publicOnlyStudies.json");
        InputStream targetStream = new FileInputStream(initialFile);

        StudiesParser studiesParser = new StudiesParser();
        studiesParser.parseJson(targetStream);
    }

    public Dataset tranformSubmissionDataset(Submissions submissions){
        Dataset dataset = new Dataset();
        dataset.setAccession(submissions.getAccno());
        Map<String, String> sectionMap = submissions.getSection().getAttributes().stream()
                .collect(Collectors.toMap(Attributes::getName, Attributes::getValue));
        Map<String, String> attributesMap = submissions.getAttributes().stream()
                .collect(Collectors.toMap(Attributes::getName, Attributes::getValue));
        dataset.setDescription(sectionMap.get(ABSTRACT.toString()));
        dataset.setName(sectionMap.get(TITLE.toString()));
        Map<String, Set<String>> dates = new HashMap<String, Set<String>>();
        HashSet<String> setData = new HashSet<String>();
        setData.add(attributesMap.get(PUBLICATION));
        dates.put("publication",setData);
        dataset.setDates(dates);
        setData.clear();
        if(sectionMap.containsKey(ORGANISM.toString())) {
            setData.add(sectionMap.get(ORGANISM.toString()));
            dataset.getAdditional().put(ORGANISM, setData);
        }

        return dataset;
    }

}
