

db.datasets.dataset.find({"database":"ExpressionAtlas"}).forEach(function(x){
    print(x)
    url_link = "https://www.ebi.ac.uk/gxa/experiments/" + x.accession;
    db.datasets.dataset.update({"accession":x.accession,"database":x.database},{"$set":{"additional.full_dataset_link":[url_link]}});
})