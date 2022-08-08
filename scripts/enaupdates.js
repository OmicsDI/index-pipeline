db.datasets.dataset.find({"database":"ENA"}).forEach(function(x){
    print(x)
    url_link = "https://www.ncbi.nlm.nih.gov/bioproject/?term=" + x.accession;
    db.datasets.dataset.update({"accession":x.accession,"database":x.database},{"$set":{"additional.full_dataset_link":[url_link]}});
    })