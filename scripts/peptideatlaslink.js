db.datasets.dataset.find({"database":/peptideatlas/i}).forEach(function(x){
var url = "ftp://ftp.peptideatlas.org/pub/PeptideAtlas/Repository/" + x.accession
db.datasets.dataset.update({"database":/peptideatlas/i},{"$set":{"additional.full_dataset_link":[url]}})
})