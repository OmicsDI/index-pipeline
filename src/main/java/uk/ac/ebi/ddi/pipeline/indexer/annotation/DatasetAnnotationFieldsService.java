package uk.ac.ebi.ddi.pipeline.indexer.annotation;

import uk.ac.ebi.ddi.xml.validator.parser.model.Entry;
import uk.ac.ebi.ddi.xml.validator.parser.model.Reference;

/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 18/11/2015
 */
public class DatasetAnnotationFieldsService {

    public static Entry addpublicationDate(Entry dataset){

        if(dataset.getDates() != null && !dataset.getDates().isEmpty() && !dataset.getDates().containsPublicationDate()){
            dataset.getDates().addDefaultPublicationDate();
        }
        return dataset;
    }

    public static Entry cleanDescription(Entry dataset){
        if(dataset != null && dataset.getDescription() != null){
            String finalDescription = "";
            String[] descriptionArray = dataset.getDescription().toString().split("\\(\\[\\[");
            if(descriptionArray.length > 1){
                String[] descriptionArraySecond = descriptionArray[1].split("\\]\\]\\)");
                if(descriptionArraySecond.length > 1){
                    finalDescription = descriptionArray[0] + " " + descriptionArraySecond[1];
                    dataset.setDescription(finalDescription);
                }
            }
        }
        return dataset;
    }
}
