db.datasets.dataset.update({"database":"Pride", "additional.omics_type":{"$exists":false}},{"$set":{"additional.omics_type":["Proteomics"]}}, false, true)