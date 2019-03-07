var events = db.logger.event.aggregate([ { "$group" : { "_id" : "$abstractResource._id" , "total" : { "$sum" : 1} , 
"accession" : { "$first" : "$abstractResource.accession"} , "database" : { "$first" : "$abstractResource.database"}}} , 
{ "$project" : { "total" : 1 , "accession" : 1 , "database" : 1}} , { "$sort" : { "total" : -1}}] ,{ "allowDiskUse" : true})

events.forEach(function(input_data){
        
        db.datasets.dataset.update({"accession":input_data.accession, "database":input_data.database},
        {"$set":{"additional.view_count":[input_data.total.toString()],"scores.viewCount":input_data.total}})
        
    })
