db.datasets.dataset.find({"accession":"PRJNA749319"})

db.datasets.dataset.find({"database":"BioModels"}).count()

db.datasets.dataset.find({"additional.omics_type":"Multiomics","database":"Pride"})

db.datasets.dataset.find({"dates.first_public":{"$exists":false},"database":"ENA"}).count()

db.datasets.dataset.find({"accession":"PRJEB3770"})

db.datasets.dataset.find({"accession":"PRJNA360110"})

db.publications.publicationdataset.find({"accession":"MTBLS226","database":"MetaboLights"})

db.datasets.dataset.find({"database":"BioModels"}).count()

db.datasets.dataset.find({"dates.first_public":{"$exists":false},"database":"ENA"}).count()

db.datasets.dataset.remove({"database":"INSDC Project"})

db.publications.publicationdataset.find({"accession":"MTBLS226","database":"MetaboLights"})

db.datasets.dataset.find({"accession":"MODEL2009110001"})

db.databases.find()

db.databases

db.datasets.dataset.find({"accession":"MODEL1904080001"})

db.datasets.dataset.find({"dates.first_public":{"$exists":false},"database":"ENA","initHashCode":0.0})

db.datasets.dataset.remove({"dates.first_public":{"$exists":false},"database":"ENA","initHashCode":0.0})

db.datasets.dataset.update({"dates.first_public":{"$exists":false},"database":"ENA"},{"$set":{"initHashCode":0}}, false, true)

db.datasets.dataset.find({"database":"Cell Collective","crossReferences.pubmed":{"$exists":true}})

db.datasets.dataset.find({"database":"BioModels","crossReferences.pubmed":{"$exists":true}})

db.datasets.dataset.find({"database":"BioModels"}).count()

