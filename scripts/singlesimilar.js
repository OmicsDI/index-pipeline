db.datasets.similars.find({}).forEach(function(data){
var myMap = new Map();

data.similars.forEach(function(element){
    if(element["similarDataset"] != null){
    myMap.put(element["similarDataset"]["$id"], element);
    }
});
data.similars = Array.from(myMap.values());
//print(data.accession);
//print(data.database);
db.datasets.similars.update({"accession":data.accession,"database":data.database},{"$set":{"similars":data.similars}})
//print('unique data is ')
//print(data);
});

