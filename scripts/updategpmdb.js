var gpmdbdata = db.datasets.dataset.find({"database":"GPMDB"});

gpmdbdata.forEach(function(input_datasets){
        print(input_datasets.accession);
        var link = "http://gpmdb.thegpm.org/~/dblist_gpmnum/gpmnum=" + input_datasets.accession; 
        db.datasets.dataset.update({"accession":input_datasets.accession},{"$set":{"additional.full_dataset_link":[link]}});
});
