db.datasets.dataset.find({"database":"iProX"}).forEach(function(x){
    print(x)
    url_link = "http://proteomecentral.proteomexchange.org/cgi/GetDataset?ID=" + x.accession;
    db.datasets.dataset.update({"accession":x.accession,"database":x.database},{"$set":{"additional.full_dataset_link":[url_link]}});
})