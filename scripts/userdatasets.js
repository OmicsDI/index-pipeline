db.datasets.dataset.aggregate([{"$match":{"additional.submitter_email":{"$exists":true},"database":{"$in":["Massive","Pride",
    "MetabolomicsWorkbench","MetaboLights"]}}},
{"$unwind":"$additional.submitter_email"},
{"$group":{"_id":"$additional.submitter_email","dataSets":{"$push":{"_id":"$accession","database":"$database"}},
"count":{"$sum":1}}},{
"$lookup": {
            "from": "databases",
            "localField": "database",
            "foreignField": "_id",
            "as": "source"
        }},{"$sort":{"count":-1}},{"$project":{"email":"$_id","dataSets":1,"_id":{"$literal":(new ObjectId).valueOf()}}}
        
        ]);