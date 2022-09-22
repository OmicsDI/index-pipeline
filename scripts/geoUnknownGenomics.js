function UnknownChange(str){
    if(str=="Unknown"){
        return "Genomics";
    } else {
        return str;
    }     
}
var omicsData = db.datasets.dataset.find({"additional.repository":"GEO", "database":"ArrayExpress",
    "additional.omics_type":{"$exists":true}, "additional.omics_type":"Unknown"});

omicsData.forEach(function(ob){
    var updatedOmics = ob.additional.omics_type.map(function(word){
        return UnknownChange(word);
    });

    db.datasets.dataset.update({"accession":ob.accession,"database":ob.database},{"$set":{"additional.omics_type":updatedOmics}});
});