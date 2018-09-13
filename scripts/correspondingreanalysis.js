var relationType = "Reanalysis of";
var collectionName = "datasets.dataset";
var className = "uk.ac.ebi.ddi.service.db.model.dataset.DatasetSimilars";
var reanalyzedRelation = "Reanalyzed by";

db.datasets.similars.find({"accession":"PXD000561"}).forEach(function(input_data){
        input_data.similars.forEach(function(similars_data){
                //print(similars_data.relationType);
                if(similars_data.relationType == reanalyzedRelation){
                    //print("relation found ");
                    //print(similars_data.similarDataset);
                    var id = similars_data.similarDataset.$id;
                    print(id);
                    var dataset = db.datasets.dataset.findOne({"_id":id});
                    var targetAccession = dataset.accession;
                    var targetDatabase = dataset.database;
                    var sim_data = {
                        accession :targetAccession,
                        database : targetDatabase,
                        "_class" : className,
                        similars : []
                    };
                    var similar_datasets = {
                        relationType : relationType,
                        similarDataset : { 
                            "$ref" : collectionName,
                            "$id" : db.datasets.dataset.findOne({"accession":input_data.accession,"database":input_data.database})._id
                            } 
                    };
                    print(similar_datasets);
                    sim_data.similars.push(similar_datasets);
                    print(sim_data);
                    //db.datasets.similars.update({"accession":sim_data.accession,"database":sim_data.database,"_class":sim_data._class},
                    //{$addToSet:{"similars":similar_datasets}},true,true);

            }});
    });