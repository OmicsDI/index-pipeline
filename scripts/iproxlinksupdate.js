db.datasets.dataset.find({"database":"iProX"}).forEach(function(x){
var url = "http://proteomecentral.proteomexchange.org/cgi/GetDataset?ID=" + x.accession;
db.datasets.dataset.update({"database":x.database,"accession":x.accession},{"$set":{"additional.full_dataset_link":[url]}});
});