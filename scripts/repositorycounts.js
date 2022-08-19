db.datasets.dataset.find({"additional.repository":"JPOST Repository"}).count()

db.datasets.dataset.find({"additional.repository":"BioModels"}).count()

db.datasets.dataset.find({"additional.repository":"BioModels","additional.isPrivate":"true"}).count()

db.datasets.dataset.find({"additional.repository":"BioModels","additional.isPrivate":"true"}).count()

db.datasets.dataset.find({"additional.repository":"jPOST"})

db.datasets.dataset.update({"additional.repository":"JPOST Repository"},{"$set": {"additional.repository":"jPOST"}},false,true)


db.datasets.dataset.find({"database":"BioModels"}).count()

db.datasets.dataset.find({"additional.repository":"JPOST Repository"}).count()

db.datasets.dataset.find({"additional.repository":"jPOST"}).count()
db.datasets.dataset.find({"database":"BioModels"})

db.databases.find()

db.datasets.dataset.aggregate([{"$match":{"database":"Pride"}},{"$group":{"_id":"$additional.repository"}}])