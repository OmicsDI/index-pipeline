//db.datasets.similars.find()

db.datasets.dataset.find({"database":{"$in":["FAIRDOMHub", "Physiome Model Repository",
                        "BioModels", "Cell Collective"]},"crossReferences.pubmed":{"$exists":true}}).forEach(function(data){ 
    
    //var mo = db.datasets.similars.find({"similars.similarDataset":})
    
    var pubmedids = data.crossReferences.pubmed;
    
    //print(pubmedids + "accession is " + data.accession);
    
    var datasets = db.datasets.dataset.findOne({ "crossReferences.pubmed" : {"$exists":true}, 
    "crossReferences.pubmed":{"$in":pubmedids}, "accession":{"$ne":data.accession},"additional.omics_type":"Models","database":{"$ne":data.database}});
    
    if(datasets != null){
        print(pubmedids + " accession is " + data.accession);
        print("redundant model is " + datasets.accession);
        print("redundant model database is " + datasets.database);
        db.datasets.dataset.update({"accession":data.accession, "database":data.database},{ "$addToSet":{"additional.omics_type":"RecuratedModel"}})
        
    }
    })