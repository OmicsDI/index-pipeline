

db.datasets.dataset.updateMany({"database":"LINCS","additional.omics_type":"Unknown"},{"$set":{"additional.omics_type":["Transcriptomics"]}})

db.datasets.dataset.updateMany({"accession":{"$regex":"E-GEOD-*"},"additional.omics_type":"Unknown","database":"ArrayExpress"},{"$set": {"additional.omics_type":["Genomics"]}})
db.datasets.dataset.updateMany({"accession":{"$regex":"E-MTAB-*"},"additional.omics_type":"Unknown","database":"ArrayExpress"},{"$set": {"additional.omics_type":["Transcriptomics"]}})
db.datasets.dataset.updateMany({"accession":{"$regex":"E-MEXP-*"},"additional.omics_type":"Unknown","database":"ArrayExpress"},{"$set": {"additional.omics_type":["Transcriptomics"]}})
db.datasets.dataset.updateMany({"accession":{"$regex":"E-BASE-*"},"additional.omics_type":"Unknown","database":"ArrayExpress"},{"$set": {"additional.omics_type":["Transcriptomics"]}})
db.datasets.dataset.updateMany({"accession":{"$regex":"E-DORD-*"},"additional.omics_type":"Unknown","database":"ArrayExpress"},{"$set": {"additional.omics_type":["Transcriptomics"]}})
db.datasets.dataset.updateMany({"accession":{"$regex":"E-T*"},"additional.omics_type":"Unknown","database":"ArrayExpress"},{"$set": {"additional.omics_type":["Transcriptomics"]}})
db.datasets.dataset.updateMany({"additional.repository":"GEO", "additional.omics_type":"Unknown", "database":"ArrayExpress"},{"$set": {"additional.omics_type":["Genomics"]}});

db.datasets.dataset.updateMany({"accession":{"$regex":"E-PROT-*"},"additional.omics_type":"Unknown","database":"ExpressionAtlas"},{"$set": {"additional.omics_type":["Proteomics"]}})
db.datasets.dataset.updateMany({"accession":{"$regex":"E-GEOD-*"},"additional.omics_type":"Unknown","database":"ExpressionAtlas"},{"$set": {"additional.omics_type":["Genomics"]}})
db.datasets.dataset.updateMany({"accession":{"$regex":"E-MTAB-*"},"additional.omics_type":"Unknown","database":"ExpressionAtlas"},{"$set": {"additional.omics_type":["Transcriptomics"]}})
db.datasets.dataset.updateMany({"accession":{"$regex":"E-MEXP-*"},"additional.omics_type":"Unknown","database":"ExpressionAtlas"},{"$set": {"additional.omics_type":["Transcriptomics"]}})
db.datasets.dataset.updateMany({"accession":{"$regex":"E-TABM-*"},"additional.omics_type":"Unknown","database":"ExpressionAtlas"},{"$set": {"additional.omics_type":["Transcriptomics"]}})
db.datasets.dataset.update({"database":"ExpressionAtlas", "additional.omics_type":"Unknown"},{"$set":{"additional.omics_type":["Transcriptomics"]}},false, true)