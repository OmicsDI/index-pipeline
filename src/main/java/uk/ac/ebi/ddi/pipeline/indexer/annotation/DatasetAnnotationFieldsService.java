package uk.ac.ebi.ddi.pipeline.indexer.annotation;

import uk.ac.ebi.ddi.xml.validator.parser.model.Entry;

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


}
