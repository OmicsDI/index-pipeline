db.datasets.dataset.find({"additional":{"$exists":true}}).forEach(function(input_dataset){
    
    print(input_dataset.accession)
    
    if(input_dataset.additional.name_synonyms != null && input_dataset.additional.name_synonyms.length > 1){
        var name_synonyms = input_dataset.additional.name_synonyms;
        print(name_synonyms.slice(name_synonyms.length -2, name_synonyms.length -1));
        input_dataset.additional.name_synonyms = name_synonyms.slice(name_synonyms.length -2, name_synonyms.length -1);
    }
    if(input_dataset.additional.description_synonyms != null && input_dataset.additional.description_synonyms.length > 1){
        var description_synonyms = input_dataset.additional.description_synonyms;
        print(description_synonyms.slice(description_synonyms.length -2, description_synonyms.length -1));
        input_dataset.additional.description_synonyms = description_synonyms.slice(description_synonyms.length -2, description_synonyms.length -1);
    }
    if(input_dataset.additional.sample_synonyms != null && input_dataset.additional.sample_synonyms.length > 1){
        var sample_synonyms = input_dataset.additional.sample_synonyms;
        print(sample_synonyms.slice(sample_synonyms.length -2, sample_synonyms.length -1));
        input_dataset.additional.sample_synonyms = sample_synonyms.slice(sample_synonyms.length -2, sample_synonyms.length -1);
    }
    if(input_dataset.additional.data_synonyms != null && input_dataset.additional.data_synonyms.length > 1){
        var data_synonyms = input_dataset.additional.data_synonyms;
        print(data_synonyms.slice(data_synonyms.length -2, data_synonyms.length -1));
        input_dataset.additional.data_synonyms = data_synonyms.slice(data_synonyms.length -2, data_synonyms.length -1);
    }
    if(input_dataset.additional.pubmed_title_synonyms != null && input_dataset.additional.pubmed_title_synonyms.length > 1){
        var pubmed_title_synonyms = input_dataset.additional.pubmed_title_synonyms;
        print(pubmed_title_synonyms.slice(pubmed_title_synonyms.length -2, pubmed_title_synonyms.length -1));        
        input_dataset.additional.pubmed_title_synonyms = pubmed_title_synonyms.slice(pubmed_title_synonyms.length -2, pubmed_title_synonyms.length -1);
    }
    if(input_dataset.additional.pubmed_abstract_synonyms != null && input_dataset.additional.pubmed_abstract_synonyms.length > 1){
        var pubmed_abstract_synonyms = input_dataset.additional.pubmed_abstract_synonyms;
        print(pubmed_abstract_synonyms.slice(pubmed_abstract_synonyms.length -2, pubmed_abstract_synonyms.length -1));
        input_dataset.additional.pubmed_abstract_synonyms = pubmed_abstract_synonyms.slice(pubmed_abstract_synonyms.length -2, pubmed_abstract_synonyms.length -1);
    }
    //print(input_dataset.additional.name_synonyms)
    
  
    db.datasets.dataset.save(input_dataset);
    
    })