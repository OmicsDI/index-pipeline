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
