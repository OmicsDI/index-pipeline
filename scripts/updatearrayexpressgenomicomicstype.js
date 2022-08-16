db.datasets.dataset.update({"accession":/E-GEO*/,"database":"ArrayExpress", "additional.omics_type":{"$exists":false}},
{"$set":{"additional.omics_type":["Genomics"]}},false, true)

db.datasets.dataset.update({"accession":/E-MTAB*/,"database":"ArrayExpress", "additional.omics_type":{"$exists":false}},
{"$set":{"additional.omics_type":["Transcriptomics"]}},false, true)

db.datasets.dataset.update({"accession":/E-TAB*/,"database":"ArrayExpress", "additional.omics_type":{"$exists":false}},
{"$set":{"additional.omics_type":["Transcriptomics"]}},false, true)

db.datasets.dataset.update({"accession":/E-MEXP*/,"database":"ArrayExpress", "additional.omics_type":{"$exists":false}},
{"$set":{"additional.omics_type":["Transcriptomics"]}},false, true)

db.datasets.dataset.update({"accession":/E-TOX*/,"database":"ArrayExpress", "additional.omics_type":{"$exists":false}},
{"$set":{"additional.omics_type":["Transcriptomics"]}},false, true)