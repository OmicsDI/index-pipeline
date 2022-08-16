db.datasets.dataset.find({"accession":"PXD005141"})

db.datasets.dataset.update({"database":"Expression Atlas ", "additional.omics_type":{"$exists":false}},
{"$set":{"additional.omics_type":["Transcriptomics"]}},false, true)



db.datasets.dataset.update({"database":"INSDC Project", "additional.omics_type":{"$exists":false}},
{"$set":{"additional.omics_type":["Genomics"]}},false, true)

db.datasets.dataset.update({"database":"Pride Archive", "additional.omics_type":{"$exists":false}},
{"$set":{"additional.omics_type":["Proteomics"]}},false, true)

db.datasets.dataset.update({"accession":/E-GEO*/,"database":"ArrayExpress", "additional.omics_type":{"$exists":false}},
{"$set":{"additional.omics_type":["Genomics"]}},false, true)


db.datasets.dataset.update({"accession":/E-PRO*/,"database":"ArrayExpress", "additional.omics_type":{"$exists":false}},
{"$set":{"additional.omics_type":["Proteomics"]}},false, true)


db.datasets.dataset.update({"accession":/E-PRO*/,"database":"ExpressionAtlas", "additional.omics_type":{"$exists":false}},
{"$set":{"additional.omics_type":["Proteomics"]}},false, true)


db.datasets.dataset.update({"accession":/E-GEO*/,"database":"ExpressionAtlas", "additional.omics_type":{"$exists":false}},
{"$set":{"additional.omics_type":["Genomics"]}},false, true)


db.datasets.dataset.update({"accession":/E-MTAB*/,"database":"ExpressionAtlas", "additional.omics_type":{"$exists":false}},
{"$set":{"additional.omics_type":["Transcriptomics"]}},false, true)