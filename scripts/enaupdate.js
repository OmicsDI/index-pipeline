db.datasets.dataset.update({"database":/insdc/i},{"$set":{"database":"ENA"}},false,true);

db.datasets.dataset.update({"database":"ENA"},{"$set":{"additional.repository":["ENA"]}},false,true);

db.datasets.dataset.find({"database":"ENA"}).forEach(function(x){
    print(x)
    url_link = "https://www.ncbi.nlm.nih.gov/bioproject/?term=" + x.accession;
    db.datasets.dataset.update({"accession":x.accession,"database":x.database},{"$set":{"additional.full_dataset_link":[url_link],"additional.omics_type":["Genomics"]}});
    })
    
db.datasets.dataset.update({"database":"ENA"},{"$rename":{"crossReferences.taxon":"crossReferences.TAXONOMY"}},false,true);

db.datasets.dataset.update({"database":"ENA"},{"$rename":{"crossReferences.PubMed":"crossReferences.pubmed"}},false,true); 