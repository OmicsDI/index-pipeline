db.datasets.dataset.update({"database":"BioModels"},{"$set":{"additional.omics_type":["Models"]}},false, true)
db.datasets.dataset.update({"database":"GEO"},{"$set":{"additional.omics_type":["Genomics"]}},false, true)
db.datasets.dataset.updateMany({"accession":{"$regex":"E-GEOD-*"},"database":"ArrayExpress"},{"$set": {"additional.omics_type":["Genomics"]}})
db.datasets.dataset.updateMany({"accession":{"$regex":"E-MTAB-*"},"database":"ArrayExpress"},{"$set": {"additional.omics_type":["Transcriptomics"]}})
db.datasets.dataset.updateMany({"accession":{"$regex":"E-GEOD-*"},"database":"ExpressionAtlas"},{"$set": {"additional.omics_type":["Genomics"]}})
db.datasets.dataset.updateMany({"accession":{"$regex":"E-MTAB-*"},"database":"ExpressionAtlas"},{"$set": {"additional.omics_type":["Transcriptomics"]}})
db.datasets.dataset.updateMany({"accession":{"$regex":"E-MEXP-*"},"database":"ArrayExpress"},{"$set": {"additional.omics_type":["Transcriptomics"]}})


