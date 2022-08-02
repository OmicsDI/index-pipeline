db.datasets.dataset.update({"database":"GEO"},{"$set":{"additional.omics_type":["Genomics"]}},false, true)
db.datasets.dataset.update({"database":"BioModels"},{"$set":{"additional.omics_type":["Models"]}},false, true)
db.datasets.dataset.update({"additional.repository":"GEO","additional.omics_type":["Other"]},{"$set":{"additional.omics_type":["Genomics"]}},false, true)
db.datasets.dataset.update({"additional.repository":"ArrayExpress","additional.omics_type":["Other"]},{"$set":{"additional.omics_type":["Genomics"]}},false, true)
db.datasets.dataset.update({"database":"ExpressionAtlas","additional.omics_type":["Other"]},{"$set":{"additional.omics_type":["Transcriptomics"]}},false, true)
db.datasets.dataset.find({"database":"ArrayExpress","additional.omics_type":["Other"]})
db.datasets.dataset.find({"accession":/E-GEOD/,"additional.omics_type":["Other"]}).count()

