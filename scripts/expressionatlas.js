db.datasets.dataset.find({"accession":"E-MTAB-3718"})


db.getCollection('datasets.dataset').find({"database":"ExpressionAtlas"}).count()

db.getCollection('datasets.dataset').find({"database":"Expression Atlas"}).count()

db.getCollection('datasets.dataset').update({"database":"ExpressionAtlas"},{"$set":{"database":"Expression Atlas "}}, false, true)


db.getCollection('datasets.dataset').update({"database":"Expression Atlas"},{"$set":{"database":"ExpressionAtlas"}}, false, true)


db.datasets.dataset.remove({"database":"Expression Atlas "})