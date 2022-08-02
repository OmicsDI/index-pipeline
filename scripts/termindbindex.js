db.getCollection('enrichment.TermInDB').createIndex({"accession":1,"database":1})

db.getCollection('enrichment.TermInDB').getIndexes()

,{"$limit":10}

db.enrichment.TermInDB.aggregate([{"$group":{"_id":{"accession":"$accession", "databse":"$database"},"count":{"$sum":1}}}]).
forEach(function(input){
    print(input.count);
    print(input._id.accession);
    print(input._id.database);
    var total = input.count - 1;
    while(total > 0){
       db.enrichment.TermInDB.remove({"accession":input._id.accession}, true);
       total = total - 1 ;
    } 
//     if(input.count > 1)
//     {
//         print("greater than 1");
//     }
    })

db.enrichment.TermInDB.find({"accession":"PAe004823"})