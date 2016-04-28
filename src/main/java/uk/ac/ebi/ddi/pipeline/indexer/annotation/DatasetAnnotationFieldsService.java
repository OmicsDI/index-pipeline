package uk.ac.ebi.ddi.pipeline.indexer.annotation;

import uk.ac.ebi.ddi.xml.validator.parser.model.Date;
import uk.ac.ebi.ddi.xml.validator.parser.model.Entry;
import uk.ac.ebi.ddi.xml.validator.parser.model.Reference;
import uk.ac.ebi.ddi.xml.validator.utils.Field;

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

    public static Entry addPublicationDateFromSubmission(Entry dataset){
        if(dataset.getDates() != null && !dataset.getDates().isEmpty() && !dataset.getDates().containsPublicationDate()){
            Date date = dataset.getDates().getDateByKey(Field.SUBMISSION_DATE.getName());
            if(date != null){
                dataset.addDate(new Date(Field.PUBLICATION.getName(), date.getValue()));
            }
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

    public static Entry replaceMEDLINEPubmed(Entry dataset){

        if(dataset.getCrossReferences() != null && !dataset.getCrossReferenceFieldValue(Field.MEDLINE.getName()).isEmpty()){
            for(String value: dataset.getCrossReferenceFieldValue(Field.MEDLINE.getName())){
                if(value != null && !value.isEmpty())
                    dataset.addCrossReferenceValue(Field.PUBMED.getName(), value);
            }
            dataset.removeCrossReferences(Field.MEDLINE.getName());
        }
        return dataset;
    }

    public static Entry replaceAuthorField(Entry dataset){
        if(dataset.getAuthors() != null && !dataset.getAuthors().isEmpty()){
            dataset.addAdditionalField(Field.SUBMITTER.getName(), dataset.getAuthors());
            dataset.setAuthors(null);
        }
        return dataset;
    }

    public static Entry replaceKeywords(Entry dataset) {
        if(dataset.getKeywords() != null && !dataset.getKeywords().isEmpty()){
            dataset.addAdditionalField(Field.SUBMITTER_KEYWORDS.getName(), dataset.getKeywords());
            dataset.setKeywords(null);
        }
        return dataset;
    }
}
