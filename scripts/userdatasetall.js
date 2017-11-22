function guid() {
  return s4() + s4() + s4();
}

function s4() {
  return Math.floor((1 + Math.random()) * 0x10000)
    .toString(16)
    .substring(1);
}

var aggregate = db.datasets.dataset.aggregate([{"$match":{"additional.submitter_email":{"$exists":true},"database":{"$in":["Massive","Pride",
    "MetabolomicsWorkbench","MetaboLights"]}}},
{"$unwind":"$additional.submitter_email"},
{"$group":{"_id":"$additional.submitter_email","dataSets":{"$push":{"_id":"$accession","source":"$database"}},"id":{"$last":(new ObjectId).valueOf()},
"count":{"$sum":1}}},{"$sort":{"count":-1}},{"$project":{"email":"$_id","dataSets":1,"_id":0}}]);

aggregate.forEach(function(input_data){
        input_data._id = guid();
        db.invites.save(input_data);
    });

var databases = ["Massive","Pride",
    "MetabolomicsWorkbench","MetaboLights"];
    
databases.forEach(function(element){
        print(element);
        source = db.databases.findOne({"_id":element}).source;
        print(source);
        while(db.invites.count({"dataSets.source":element}) > 0){
        print(db.invites.update({"dataSets.source":element},{"$set":{"dataSets.$.source":source}},false,true));
    }
    });


var aggr = db.datasets.dataset.aggregate([{"$match":{"additional.submitter_mail":{"$exists":true},"database":{"$in":["Massive","Pride",
    "MetabolomicsWorkbench","MetaboLights"]}}},
{"$unwind":"$additional.submitter_mail"},
{"$group":{"_id":"$additional.submitter_mail","dataSets":{"$push":{"_id":"$accession","source":"$database"}},"id":{"$last":(new ObjectId).valueOf()},
"count":{"$sum":1}}},{"$sort":{"count":-1}},{"$project":{"email":"$_id","dataSets":1,"_id":0}}]);

aggr.forEach(function(input_data){
        input_data._id = guid();
        db.invites.save(input_data);
    });

var datab = ["Massive","Pride",
    "MetabolomicsWorkbench","MetaboLights"];
    
datab.forEach(function(element){
        print(element);
        source = db.databases.findOne({"_id":element}).source;
        print(source);
        while(db.invites.count({"dataSets.source":element}) > 0){
        print(db.invites.update({"dataSets.source":element},{"$set":{"dataSets.$.source":source}},false,true));
    }
    });
