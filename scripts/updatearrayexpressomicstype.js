db.datasets.dataset.update({"database":"ArrayExpress","additional.omics_type":{"$exists":false}},
{"$set":{"additional.omics_type":["Transcriptomics"]}},false,true)