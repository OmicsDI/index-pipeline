db.datasets.similars.find({"similars.relationType":"Reanalyzed by"}).limit(10).forEach(function(input_data){
     var parent_dataset = db.datasets.dataset.findOne({"accession":input_data.accession,"database":input_data.database},
     {"accession":1,"database":1,"additional.omics_type":1})
     input_data.similars.forEach(function(similars){
         var reanalysed_dataset = db.datasets.dataset.findOne({"_id":similars.similarDataset.$id},{"accession":1,"database":1,"dates.publication":1}) ;   
         print(reanalysed_dataset);
         
     });
     })
