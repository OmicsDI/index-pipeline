//db.datasets.dataset.find({"accession":{"$regex":"E-PROT-*"},"database":"ExpressionAtlas"})

db.datasets.dataset.updateMany({"accession":{"$regex":"E-PROT-*"},"database":"ExpressionAtlas"},{"$set": {"additional.omics_type":["Proteomics"]}})