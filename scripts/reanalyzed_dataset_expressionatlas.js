db.datasets.similars.aggregate([{"$match":{"similars.relationType":"Reanalyzed by"}},{"$unwind":"$similars"},
{"$match":{"similars.similarDataset":{"$exists":true},"similars.relationType":"Reanalyzed by" }}],{allowDiskUse:true}).forEach(function(input_data){
    var dataset_output = {}; 
    print(input_data.accession);
    print(input_data.database);
    var parent_dataset = db.datasets.dataset.findOne({"accession":input_data.accession,"database":input_data.database});
    if(parent_dataset != null){
        if(parent_dataset.dates != null && parent_dataset.dates.publication != null){
            dataset_output.parent_pub_date = parent_dataset.dates.publication;
        }
    var id = input_data.similars.similarDataset.$id;
    print(id)
    var reanalyzed_dataset = db.datasets.dataset.findOne({"_id":id});
    if(reanalyzed_dataset != null){
    dataset_output.accession = input_data.accession;
    dataset_output.database = input_data.database;
    if(reanalyzed_dataset.dates != null && reanalyzed_dataset.dates.publication != null){
            dataset_output.publication_date = reanalyzed_dataset.dates.publication;
        }
    print("reanalyzed accession " + reanalyzed_dataset.accession)    
    print("reanalyzed database" + reanalyzed_dataset.database)    
    dataset_output.acc =   reanalyzed_dataset.accession;
    dataset_output.db = reanalyzed_dataset.database;    
    db.reanlysed_data_mar29.save(dataset_output);    
    }
}
    })
