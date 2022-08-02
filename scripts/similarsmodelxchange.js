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
        db.datasets.dataset.update({"accession":data.accession, "database":data.database},{ "$addToSet":{"additional.omics_type":"RecuratedModel"}});
	var id = db.datasets.dataset.findOne({"accession":datasets.accession, "database":datasets.database})._id;
        print(id);
     
    }}
    );

db.datasets.dataset.find({"database":{"$in":["FAIRDOMHub", "Physiome Model Repository",
...                         "BioModels", "Cell Collective"]},"crossReferences.pubmed":{"$exists":true}}).forEach(function(data){ 
...     
...     //var mo = db.datasets.similars.find({"similars.similarDataset":})
...     
...     var pubmedids = data.crossReferences.pubmed;
...     
...     //print(pubmedids + "accession is " + data.accession);
...     
...     var datasets = db.datasets.dataset.findOne({ "crossReferences.pubmed" : {"$exists":true}, 
...     "crossReferences.pubmed":{"$in":pubmedids}, "accession":{"$ne":data.accession},"additional.omics_type":"Models","database":{"$ne":data.database}});
...     
...     if(datasets != null){
...         print(pubmedids + " accession is " + data.accession);
...         print("redundant model is " + datasets.accession);
...         print("redundant model database is " + datasets.database);
...         db.datasets.dataset.update({"accession":data.accession, "database":data.database},{ "$addToSet":{"additional.omics_type":"RecuratedModel"}});
... var id = db.datasets.dataset.findOne({"accession":datasets.accession, "database":datasets.database})._id;
... db.datasets.similars.update({"accession":data.accession,"database":data.database,"_class":"uk.ac.ebi.ddi.service.db.model.dataset.DatasetSimilars"},{ 
...             $addToSet:{"similars":{"relationType":"Other Omics Data in:","similarDataset":{"$ref" : "datasets.dataset","$id" : id}}}},true, true);   
...     }
...     })


short description of what achieved, what you did, why failed, take it down, 
wednesday 2 pm bycovid meeting

   
     var pubmedids = 26468131;
     
     //print(pubmedids + "accession is " + data.accession);
     
     var datasets = db.datasets.dataset.find({ "crossReferences.pubmed" : {"$exists":true}, 
     "crossReferences.pubmed":{"$in":pubmedids}, "accession":{"$ne":data.accession},""additional.omics_type":{"$in":["Models"],"database":{"$ne":data.database}});
     
     if(datasets != null){
	 datasets.forEach(function(similarDataset){
         print(pubmedids + " accession is " + data.accession);
         print("redundant model is " + similarDataset.accession);
         print("redundant model database is " + similarDataset.database);
         db.datasets.dataset.update({"accession":data.accession, "database":data.database},{ "$addToSet":{"additional.omics_type":"RecuratedModel"}});
 var id = db.datasets.dataset.findOne({"accession":similarDataset.accession, "database":similarDataset.database})._id;
 db.datasets.similars.update({"accession":data.accession,"database":data.database,"_class":"uk.ac.ebi.ddi.service.db.model.dataset.DatasetSimilars"},{ 
             $addToSet:{"similars":{"relationType":"Other Omics Data in:","similarDataset":{"$ref" : "datasets.dataset","$id" : id}}}},true, true);   
     });}
    

db.datasets.dataset.update({"additional.omics_type":{"$in":["RecurratedModel"]}},{"$pull":{"additional.omics_type":"RecurratedModel"}},false,true)

db.datasets.dataset.find({"database":{"$in":["FAIRDOMHub", "Physiome Model Repository",
                         "BioModels", "Cell Collective"]},"crossReferences.pubmed":{"$exists":true}}).forEach(function(data){ 
     
     //var mo = db.datasets.similars.find({"similars.similarDataset":})
     
     var pubmedids = data.crossReferences.pubmed;
     
     //print(pubmedids + "accession is " + data.accession);
     
     var datasets = db.datasets.dataset.find({ "crossReferences.pubmed" : {"$exists":true}, 
     "crossReferences.pubmed":{"$in":pubmedids}, "accession":{"$ne":data.accession},"additional.omics_type":{"$in":["Models"],"database":{"$ne":data.database}});
     
     if(datasets != null){
	 datasets.forEach(function(similarDataset)){
         print(pubmedids + " accession is " + data.accession);
         print("redundant model is " + datasets.accession);
         print("redundant model database is " + datasets.database);
         db.datasets.dataset.update({"accession":data.accession, "database":data.database},{ "$addToSet":{"additional.omics_type":"RecuratedModel"}});
 var id = db.datasets.dataset.findOne({"accession":similarDataset.accession, "database":similarDataset.database})._id;
 db.datasets.similars.update({"accession":data.accession,"database":data.database,"_class":"uk.ac.ebi.ddi.service.db.model.dataset.DatasetSimilars"},{ 
             $addToSet:{"similars":{"relationType":"Other Omics Data in:","similarDataset":{"$ref" : "datasets.dataset","$id" : id}}}},true, true);   
     }
     })


db.datasets.dataset.find({"database":{"$in":["FAIRDOMHub", "Physiome Model Repository",
                         "BioModels", "Cell Collective"]},"crossReferences.pubmed":{"$exists":true}}).forEach(function(data){ 
     
     //var mo = db.datasets.similars.find({"similars.similarDataset":})
     
     var pubmedids = data.crossReferences.pubmed;
     
     //print(pubmedids + "accession is " + data.accession);
     
     var datasets = db.datasets.dataset.find({ "crossReferences.pubmed" : {"$exists":true}, 
     "crossReferences.pubmed":{"$in":pubmedids}, "accession":{"$ne":data.accession},
     "additional.omics_type":{"$in":["Models"]}});
     
      if(datasets != null){
	 datasets.forEach(function(similarDataset){
         print(pubmedids + " accession is " + data.accession);
         print("redundant model is " + similarDataset.accession);
         print("redundant model database is " + similarDataset.database);
         db.datasets.dataset.update({"accession":data.accession, "database":data.database},{ "$addToSet":{"additional.omics_type":"RecuratedModel"}});
 var id = db.datasets.dataset.findOne({"accession":similarDataset.accession, "database":similarDataset.database})._id;
 db.datasets.similars.update({"accession":data.accession,"database":data.database,"_class":"uk.ac.ebi.ddi.service.db.model.dataset.DatasetSimilars"},{ 
             $addToSet:{"similars":{"relationType":"Other Omics Data in:","similarDataset":{"$ref" : "datasets.dataset","$id" : id}}}},true, true);   
     })
       
     }})
