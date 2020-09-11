package uk.ac.ebi.ddi.pipeline.indexer.utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONTokener;
import uk.ac.ebi.ddi.pipeline.indexer.model.BioStudies;
import uk.ac.ebi.ddi.pipeline.indexer.model.Submissions;
import com.fasterxml.jackson.databind.DeserializationFeature;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class StudiesParser {

    public static final String ID = "id";
    public static final String ACCNO = "accno";
    public static final String SECKEY = "seckey";
    public static final String RTIME = "rtime";
    public static final String CTIME = "ctime";
    public static final String MTIME = "mtime";
    public static final String ATTRIBUTES = "attributes";
    public static final String SUBMISSIONS = "submissions";

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
            // Check the first token
            /*if (jsonParser.nextToken() != JsonToken.START_ARRAY) {
                throw new IllegalStateException("Expected content to be an array");
            }*/
            while(jsonToken != JsonToken.END_OBJECT) {
                System.out.println(jsonParser.getCurrentName());
                if (jsonToken == JsonToken.FIELD_NAME && SUBMISSIONS.equals(jsonParser.getCurrentName())) {

                    System.out.println("\nYour are in submissions ");

                    jsonToken = jsonParser.nextToken();

                    if(jsonToken == JsonToken.START_ARRAY){
                        jsonToken = jsonParser.nextToken();
                    }
                    // Iterate over the tokens until the end of the array
                    while (jsonToken != JsonToken.END_ARRAY) {
                        // Read a contact instance using ObjectMapper and do something with it
                        Submissions submissions = mapper.readValue(jsonParser, Submissions.class);
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



    public void parseStudiesJson() throws IOException {



       /* File jsonFile = new File("/media/gaur/Elements/teststudies.json");
        JsonFactory jsonfactory = new JsonFactory();*/

        /*ObjectMapper objectMapper = new ObjectMapper();
        String json = "{ \"color\" : \"Black\", \"type\" : \"BMW\" }";
        BioStudies car = objectMapper.readValue(json, BioStudies.class);*/
    }

    public static void main(String[] args) throws IOException {
        File initialFile = new File("/media/gaur/Elements/biostudies/publicOnlyStudies.json");
        InputStream targetStream = new FileInputStream(initialFile);

        StudiesParser studiesParser = new StudiesParser();
        //studiesParser.process("/media/gaur/Elements/teststudies.json");
        /*StudiesParser studiesParser = new StudiesParser();
        studiesParser.parseStudiesJson();*/
        studiesParser.parseJson(targetStream);
    }

   /* public void mapperProcessor() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        //"/media/gaur/Elements/teststudies.json"
        BioStudies bioStudies = mapper.readValue(new File("/media/gaur/Elements/biostudies/publicOnlyStudies.json"), BioStudies.class);
        System.out.println(bioStudies.toString());

        File initialFile = new File("src/main/resources/sample.txt");
        InputStream targetStream = new FileInputStream(initialFile);
    }*/
    public void process(String jsonFilePath){
        File jsonFile = new File(jsonFilePath);
        JsonFactory jsonfactory = new JsonFactory(); //init factory
        List<Submissions> submissionsList = new ArrayList<Submissions>();

        try {
            int numberOfRecords = 0;
            JsonParser jsonParser = jsonfactory.createJsonParser(jsonFile); //create JSON parser
            BioStudies bioStudies = new BioStudies();
            bioStudies.setSubmissions(submissionsList);
            Submissions submissions = new Submissions();
            JsonToken jsonToken = jsonParser.nextToken();
            while (jsonToken!= JsonToken.END_ARRAY){ //Iterate all elements of array
                String fieldname = jsonParser.getCurrentName(); //get current name of token
                if (jsonToken == JsonToken.FIELD_NAME && SUBMISSIONS.equals(jsonParser.getCurrentName())) {

                    System.out.println("\nYour are in submissions ");

                    jsonToken = jsonParser.nextToken(); // // Read left bracket i.e. [
                    // Loop to print array elements until right bracket i.e ]
                    while (jsonToken != JsonToken.END_ARRAY) {

                            jsonToken = jsonParser.nextToken();

                            fieldname = jsonParser.getCurrentName();

                            if (ID.equals(fieldname)) {
                                jsonToken = jsonParser.nextToken();
                                submissions.setId(jsonParser.getText());
                            }

                            if (MTIME.equals(fieldname)) {
                                jsonToken = jsonParser.nextToken();
                                submissions.setMtime(jsonParser.getText());
                            }
                            if (CTIME.equals(fieldname)) {
                                jsonToken = jsonParser.nextToken();
                                submissions.setCtime(jsonParser.getText());
                            }
                            if (SECKEY.equals(fieldname)) {
                                jsonToken = jsonParser.nextToken();
                                submissions.setSeckey(jsonParser.getText());
                            }
                            if (RTIME.equals(fieldname)) {
                                jsonToken = jsonParser.nextToken();
                                submissions.setRtime(jsonParser.getText());
                            }
                            if (ACCNO.equals(fieldname)) {
                                jsonToken = jsonParser.nextToken();
                                submissions.setAccno(jsonParser.getText());
                            }
                        if(jsonToken == JsonToken.END_OBJECT){
                            bioStudies.getSubmissions().add(submissions);
                        }

                    }
                    System.out.println();
                }
                jsonToken = jsonParser.nextToken();
            }
            System.out.println("Total Records Found : " + bioStudies.getSubmissions().size());
            System.out.println("Total Records Found : " +  numberOfRecords);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
/* if(fieldname != null) {

                    if (SUBMISSIONS.equals(fieldname)) {
                        jsonToken = jsonParser.nextToken(); //read next token
                        //bioStudies.setSubmissions();

                        if (DOCUMENT_ID.equals(fieldname)) {
                            jsonToken = jsonParser.nextToken(); //read next token
                            //jsonParser.
                            //document.setAttributes(jsonParser.getText());
                        }
                        if (DOC_TYPE.equals(fieldname)) {
                            jsonToken = jsonParser.nextToken();
                            document.setMtime(jsonParser.getText());
                        }
                        if (DOC_AUTHOR.equals(fieldname)) {
                            jsonToken = jsonParser.nextToken();
                            document.setCtime(jsonParser.getText());
                        }
                        if (DOC_TITLE.equals(fieldname)) {
                            jsonToken = jsonParser.nextToken();
                            document.setSeckey(jsonParser.getText());
                        }
                        if (IS_PARENT.equals(fieldname)) {
                            jsonToken = jsonParser.nextToken();
                            document.setRtime(jsonParser.getText());
                        }
                        if (PARENT_DOC_ID.equals(fieldname)) {
                            jsonToken = jsonParser.nextToken();
                            document.setAccno(jsonParser.getText());
                        }
                        if (DOC_LANGUAGE.equals(fieldname)) {  //array type field
                            jsonToken = jsonParser.nextToken();
                        *//*List<String> docLangs = new ArrayList<>(); //read all elements and store into list
                        while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                            docLangs.add(jsonParser.getText());
                        }*//*
                        document.setId(jsonParser.getText());
                    }
                *//*if (DOC_LANGUAGE.equals(fieldname)) {  //array type field
                    jsonToken = jsonParser.nextToken();
                    List<String> docLangs = new ArrayList<>(); //read all elements and store into list
                    while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                        docLangs.add(jsonParser.getText());
                    }
                    document.setDocLanguage(docLangs);
                }*//*
                }
                if(jsonToken==JsonToken.END_OBJECT){
                    submissionsList.add(document);
                    //do some processing, Indexing, saving in DB etc..
                    document = new Submissions();
                    numberOfRecords++;
                }
                jsonToken = jsonParser.nextToken();*/
