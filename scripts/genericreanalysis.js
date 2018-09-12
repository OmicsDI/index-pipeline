var reanalysisRelationType = "Reanalysis of";
var dbValue = "GEO";
var classValue = "uk.ac.ebi.ddi.service.db.model.dataset.DatasetSimilars";
var reanalyzedRelationType = "Reanalyzed by";


var reanalysed = db.datasets.similars.aggregate([{"$match":{"similars.relationType":reanalyzedRelationType,"database":dbValue}},
{"$unwind":"$similars"},{"$group":{"_id":"$similars.similarDataset","data":{"$addToSet":{"acc":"$accession","db":"$database"}} }}]);

print(reanalysed);

reanalysed.forEach(function(input_data){
    print(input_data._id.$ref);
    var dt = db.datasets.dataset.findOne({"_id":input_data._id.$id});
    print(dt.accession);

    input_data.data.forEach(function(redata){
    var sim_data = {
        accession :dt.accession,
        database : dt.database,
        "_class" : classValue,
        similars : []
    };
    var similar_datasets = {
        relationType : reanalysisRelationType,
        similarDataset : { 
            "$ref" : "datasets.dataset",
            "$id" : db.datasets.dataset.findOne({"accession":redata.acc,"database":redata.db})._id} 
        };
    print(similar_datasets);
    sim_data.similars.push(similar_datasets);
    print(sim_data.accession);
    print(sim_data);
    db.datasets.similars.update({"accession":sim_data.accession,"database":sim_data.database,"_class":sim_data._class},
            {$addToSet:{"similars":similar_datasets}},true,true);
     });
}
);