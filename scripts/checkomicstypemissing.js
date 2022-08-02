db.datasets.dataset.aggregate([{"$match":{"additional.omics_type":{"$exists":false}}},{"$group":{"_id":"$database","mycount":{"$sum":1}}}])

//db.datasets.dataset.find({"database":"Pride" , "additional.omics_type":{"$exists":false}})