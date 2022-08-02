package uk.ac.ebi.ddi.pipeline.indexer.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 19/08/2015
 */
public class Utils {

    private Utils() {
    }

    public static String toTitleCase(String input) {
        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;
        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }
            titleCase.append(c);
        }
        return titleCase.toString();
    }

    public static HashMap<String, String> readCsvHashMap() throws IOException {
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        String currDir = System.getProperty("user.dir");
        String path = "/nfs/pride/prod/ddi/pipeline/final/testgeo/omicsdivocab1.csv";
        if(!currDir.contains("nfs")){
            path = currDir+"/src/main/resources/prop/omicsdivocab1.csv";
        }
        BufferedReader br = new BufferedReader(
                new FileReader(path));
                //new FileReader(currDir+"/src/main/resources/prop/omicsdivocab1.csv"));

        String line = null;
        HashMap<String, String> map = new HashMap<String, String>();

        while ((line = br.readLine()) != null) {
            String str[] = line.split(",");
            map.put(str[0], str[1]);
            /*for (int i = 0; i < str.length; i++) {
                String arr[] = str[i].split(",");
                map.put(arr[0], arr[1]);
            }*/
        }
        System.out.println(map);
        return map;
    }

    public static String processOmics(Map.Entry<String, String> r , String updatedOmics){
        Boolean isMatched = updatedOmics.matches("(?i)(.*)" + r.getKey() + "(.*)");
        String updated = "";
        if (isMatched){
            updated = r.getValue();
        }
        return updated;
    }
}
