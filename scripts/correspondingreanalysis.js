db.datasets.similars.find({"accession":"PXD000561"}).forEach(function(input_data){
        input_data.similars.forEach(function(similars_data){
                //print(similars_data.relationType);
                if(similars_data.relationType == "Reanalyzed by"){
                    //print("relation found ");
                    //print(similars_data.similarDataset);
                    var id = similars_data.similarDataset.$id.str;
                    print(id);
                    var dataset = db.datasets.dataset.find({"_id":id});
                    var targetAcccession = dataset.accession;
                    var targetDatabase = dataset.database;
                    

            });
    });