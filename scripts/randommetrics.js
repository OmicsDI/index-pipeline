db.datasets.dataset.find().forEach(function(input_obj){
        var view_count_scaled = Math.random();
        var reanalysis_count_scaled = Math.random();
        var citation_count_scaled = Math.random();
    
        print(view_count_scaled);
        print(reanalysis_count_scaled);
        print(citation_count_scaled);
    //db.datasets.dataset.update({"accession":input_obj.accession, "database":input_obj.database},
    //{"$set":{"additional.view_count_scaled":view_count_scaled,"additional.reanalysis_count_scaled":reanalysis_count_scaled,
        //"additional.citation_count_scaled":citation_count_scaled }}
    //);
        
    })