use ddi_db

db.datasets.dataset.find({"additional.omics_type":{"$exists":true},"additional.omics_type":"Multiomics","_id":ObjectId("576a52a460b2426a043b6ed3")})
.forEach(function(ob){
var updatedOmics = ob.additional.omics_type.filter(function(word){
    return word != "Multiomics"
});
    print("old is ");
    print(ob.additional.omics_type);
    print("new is ");
    print(updatedOmics);
    db.datasets.dataset.update({"accession":ob.accession,"database":ob.database},{"$set":{"additional.omics_type":updatedOmics}});
});