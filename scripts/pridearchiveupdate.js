db.datasets.dataset.find({"database":"Pride Archive"}).forEach(function(input_data){
    print(input_data.accession);
    print(input_data.database);
    db.datasets.dataset.update({"accession":input_data.accession,"database":input_data.database},{"$set":{"database":"Pride"}})
});