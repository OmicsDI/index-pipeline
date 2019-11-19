db.datasets.dataset.aggregate([{"$match":{"additional.labhead":{"$exists":true}}},{"$unwind":"$additional.labhead"}, 
{"$group":{"_id":"$additional.labhead","count":{"$sum":1}}},{"$sort":{"count":-1}},{"$out":"labheads"}])


 db.users.aggregate([{"$match":{"dataSets":{"$exists":true}}}, 
 {"$project":{"_id":"$userName","count":{"$size":"$dataSets" }}},{"$match" :{ "count" :{"$gt" : 0}}},
 {"$merge":"labheads"}])
 
 db.datasets.dataset.aggregate([{"$match":{"additional.submitter":{"$exists":true}}},{"$unwind":"$additional.submitter"}, 
{"$group":{"_id":"$additional.submitter","count":{"$sum":1}}},{"$sort":{"count":-1}},{"$out":"labheads"}])