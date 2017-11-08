use ddi_db

function MultiChange(str){
    if(str=="Multi-omics"){
        return "Multiomics";
        }
        else{
            return str;
            }
        
}
var omicsData = db.datasets.dataset.find({"additional.omics_type":{"$exists":true},"additional.omics_type":"Multi-omics"});

omicsData.forEach(function(ob){
var updatedOmics = ob.additional.omics_type.map(function(word){
    print("old is ");
    print(word);
    print("new is ");
    print(MultiChange(word));
    return MultiChange(word);
    });
    print("old is ");
    //print(ob.additional.omics_type);
    print("new is ");
    //print(updatedOmics.additional.omics_type);
    db.datasets.dataset.update({"accession":ob.accession,"database":ob.database},{"$set":{"additional.omics_type":updatedOmics}});
});