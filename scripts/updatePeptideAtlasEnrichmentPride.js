db.datasets.dataset.find({"accession":"PAe004729"})

db.datasets.similars.find({"accession":"PAe004729"})

db.enrichment.DatasetStatInfo.find({"intersectionInfos.relatedDatasetDatabase":"pride"})

db.enrichment.DatasetStatInfo.find({"accession":"PAe002032"})


db.enrichment.DatasetStatInfo.find({"intersectionInfos.relatedDatasetDatabase":"pride"}).forEach(function(input_dataset){
while(db.enrichment.DatasetStatInfo.count({"accession":input_dataset.accession,"intersectionInfos.relatedDatasetDatabase":"pride"}) > 0){
db.enrichment.DatasetStatInfo.update({"accession":input_dataset.accession,"intersectionInfos.relatedDatasetDatabase":"pride"},
{"$set":{"intersectionInfos.$.relatedDatasetDatabase":"Pride"}})
}})

db.enrichment.DatasetStatInfo.count({"intersectionInfos.relatedDatasetDatabase":"pride"})


function updatePridePeptideEnrichment(){
    try{
db.enrichment.DatasetStatInfo.find({"intersectionInfos.relatedDatasetDatabase":"pride"}).forEach(function(input_dataset)
{ while(db.enrichment.DatasetStatInfo.count({"accession":input_dataset.accession,"intersectionInfos.relatedDatasetDatabase":"pride"}) > 0)
{ db.enrichment.DatasetStatInfo.update({"accession":input_dataset.accession,"intersectionInfos.relatedDatasetDatabase":"pride"}, {"$set":{"intersectionInfos.$.relatedDatasetDatabase":"Pride"}}) }})
}catch(err){updatePridePeptideEnrichment()}}

db.enrichment.TermInDB.update({"database":"pride"},{"$set":{"database":"Pride"}},false,true)

db.enrichment.TermInDB.getIndexes()

db.enrichment.TermInDB.find({"database":"pride"}).count()

db.enrichment.TermInDB.find({"database":"pride"}).limit(2).forEach(function(input_data){
    print(input_data.accession);
    print(input_data.database);
    print(input_data.termAccession)
    print(db.enrichment.TermInDB.update({"accession":input_data.accession,"database":input_data.database,"termAccession":input_data.termAccession},
    {"$set":{"database":"Pride"}}))
});

db.enrichment.TermInDB.dropIndex("accession_database")

db.enrichment.TermInDB.createIndex({'accession' : 1, 'database': 1, 'termAccession': 1},{"name":"accession_database","unique":true})