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
                    var sim_data = {
                        accession :targetAccession,
                        database : targetDatabase,
                        "_class" : "uk.ac.ebi.ddi.service.db.model.dataset.DatasetSimilars",
                        similars : []
                    }
                    var similar_datasets = {
                        relationType : "Reanalysis of",
                        similarDataset : { 
                            "$ref" : "datasets.dataset",
                            "$id" : db.datasets.dataset.findOne({"accession":input_data.accession,"database":input_data.database})._id
                            } 
                    };
                    print(similar_datasets);
                    sim_data.similars.push(similar_datasets);
                    print(sim_data);
                    db.datasets.similars.update({"accession":sim_data.accession,"database":sim_data.database,"_class":sim_data._class},
                    {$addToSet:{"similars":similar_datasets}},true,true);

            });
    });