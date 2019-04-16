var totalDatasetsClaimed = 0

db.users.find({"dataSets":{"$exists":true}}).forEach(function(input_data){
    var length = input_data.dataSets.length
    totalDatasetsClaimed = totalDatasetsClaimed + length;
    //print(length);
    })
    
print(totalDatasetsClaimed)    