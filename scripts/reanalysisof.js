var reanalysis = db.datasets.dataset.aggregate([{"$match":{"database":"BioModels Database",
    "crossReferences.biomodels__db":{"$exists":true}}}])
    
reanalysis.forEach(function(all_data){
    all_data.crossReferences.biomodels__db.forEach(function(input_data){
    print(input_data);
    var dataset = db.datasets.dataset.find({"accession":input_data,"database":"BioModels Database"})
});
});