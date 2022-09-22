

db.datasets.dataset.count({"additional.omics_type":"Unknown","database":"ExpressionAtlas"},{"accession":1})

db.datasets.dataset.update({"accession":/E-PRO*/,"database":"ExpressionAtlas", "additional.omics_type":"Unknown",
{"$set":{"additional.omics_type":["Proteomics"]}},false, true)


db.datasets.dataset.updateMany({"accession":{"$regex":"E-GEOD-*"},"additional.omics_type":"Unknown","database":"ExpressionAtlas"},{"$set": {"additional.omics_type":["Genomics"]}})
db.datasets.dataset.updateMany({"accession":{"$regex":"E-MTAB-*"},"additional.omics_type":"Unknown","database":"ExpressionAtlas"},{"$set": {"additional.omics_type":["Transcriptomics"]}})
db.datasets.dataset.updateMany({"accession":{"$regex":"E-MEXP-*"},"additional.omics_type":"Unknown","database":"ExpressionAtlas"},{"$set": {"additional.omics_type":["Transcriptomics"]}})
db.datasets.dataset.updateMany({"accession":{"$regex":"E-TABM-*"},"additional.omics_type":"Unknown","database":"ExpressionAtlas"},{"$set": {"additional.omics_type":["Transcriptomics"]}})
db.datasets.dataset.update({"database":"ExpressionAtlas", "additional.omics_type":"Unknown"},
{"$set":{"additional.omics_type":["Transcriptomics"]}},false, true)