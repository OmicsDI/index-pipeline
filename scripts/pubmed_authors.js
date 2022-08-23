var datasets_info = db.datasets.dataset.aggregate([{"$match":{"additional.pubmed_authors":{"$exists":true}}},{"$unwind":"$additional.pubmed_authors"},
{"$group":{"_id":"$additional.pubmed_authors","count":{"$sum":1}}},
{"$sort":{"count":-1}},{"$limit":100}]);

var all_datasets = db.datasets.dataset.find({"additional.pubmed_authors":{"$exists":true}}).limit(10);

all_datasets.forEach(function(input_data){
    var authors = []
    input_data.additional.pubmed_authors.forEach(function(in_data){
        print(in_data);
        var arr_data = in_data.split(",")
        print("length is " + arr_data.length)
        if(arr_data.length > 0){
            authors.concat(arr_data);
            }else{
                    authors.push(in_data);
                }   
    });
    var nData = {};
    nData.id = input_data._id;
    nData.authors_new = authors;
    nData.authors_old = input_data.additional.pubmed_authors;
    db.newData.save(nData);
    })