db.datasets.similars.find({"accession":"GPM32310019900"})
//,"relationType":"Reanalysis of"
//db.datasets.similars.find({"similars":{"$elemMatch":{"relationType":"Reanalyzed by"}}})
    
    
db.datasets.similars.aggregate([{"$project":{"accession":1,"database":1,"reanalyzedsize":{
"$size":{
     "$filter" :{
              input :"$similars",
              as  : "sim",
              cond : {"$eq" : [ "$$sim.relationType", "Reanalyzed by" ]}
         }
 }
}}},{"$match":{"reanalyzedsize" :{"$gt":0}}}]).forEach(function(input_data){
     //print(db.datasets.dataset.findOne({"accession":input_data.accession,"database":input_data.database}))
     var arr_reanalysis = [input_data.reanalyzedsize.toString()]
     db.datasets.dataset.update({"accession":input_data.accession,"database":input_data.database},
     {"$set":{"scores.reanalysisCount":input_data.reanalyzedsize,"additional.reanalysis_count":arr_reanalysis}})
 })