package uk.ac.ebi.ddi.pipeline.indexer.utils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 19/08/2015
 */
public class Utils {

    public static String removeRedundantSynonyms(String synonyms){
        if(synonyms != null){
            Set<String> resultStringSet = new HashSet<>();
            String resultSynonym = "";
            String[] synonymsArr = synonyms.split(";");
            for(String synonym: synonymsArr){
                if(synonym != null && !synonym.isEmpty()){
                    String[] redudantSynonyms = synonym.split(",");
                    for(String redundantSynom: redudantSynonyms)
                        resultStringSet.add(redundantSynom.trim());
                }
            }
            for(String synonym: resultStringSet)
                resultSynonym += synonym + ", ";
            if(!resultSynonym.isEmpty())
                resultSynonym.substring(0, resultSynonym.length()-2);
            return resultSynonym;

        }
        return null;
    }

}
