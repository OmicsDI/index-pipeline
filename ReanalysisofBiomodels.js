var reanalysis = db.datasets.dataset.aggregate([{"$match":{"database":"BioModels Database",
    "crossReferences.biomodels__db":{"$exists":true}}}]);
    
reanalysis.forEach(function(all_data){
    all_data.crossReferences.biomodels__db.forEach(function(input_data){
    print(input_data);
    var dataset = db.datasets.dataset.findOne({"accession":input_data,"database":"BioModels Database"})
    if(dataset != null){
    print(dataset.accession);
        var sim_data = {accession :all_data.accession,
                        database : all_data.database,
                        "_class" : "uk.ac.ebi.ddi.service.db.model.dataset.DatasetSimilars",
                        similars : []
    };
    var similar_datasets = {relationType : "Reanalysis of",
    similarDataset : { "$ref" : "datasets.dataset",
    "$id" : db.datasets.dataset.findOne({"accession":dataset.accession,"database":"BioModels Database"})._id} };
    print(similar_datasets);
    sim_data.similars.push(similar_datasets);
    print(sim_data);
    db.datasets.similars.update({"accession":sim_data.accession,"database":sim_data.database,"_class":sim_data._class},
    {$addToSet:{"similars":similar_datasets}},true,true);
    }
});
});
