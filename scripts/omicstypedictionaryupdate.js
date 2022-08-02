var omics_expr = { '^Genomics' : "Genomics"
    };
    
for(var key in omics_expr) {
  var omics_value = omics_expr[key];

   print(key)
    
  db.datasets.dataset.find({"additional.omics_type":{"$regex":'/'+key+'/i'}}).forEach(
    function(input_data){
        
        print(input_data.accession);
        print(input_data.database);
        
        var currtime = new Date();

        db.omicsupdate.insert({"accession":input_data.accession,"database":input_data.database,
            "omics":input_data.additional.omics_type,"currenttime":currtime});
            
        //db.datasets.dataset.update({"accession":input_data.accession,"database":input_data.database},
        //{"$set":{"additional.omics_type": omics_value}});
        
        });
  //db.datasets.dataset.update({"additional.omics_type":key},{"$set":{"additional.omics_type":value}}, false, true)
   
  // do something with "key" and "value" variables
} 