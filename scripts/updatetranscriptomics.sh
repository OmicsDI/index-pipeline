use ddi_db

function titleCase(str) {
  return str.toLowerCase().split(' ').map(function(word) {
    return (word.charAt(0).toUpperCase() + word.slice(1));
  }).join(' ');
}

var omicsData = db.datasets.dataset.find({"additional.omics_type":{"$exists":true},"additional.omics_type":"transcriptomics"});

omicsData.forEach(function(ob){
var updatedOmics = ob.additional.omics_type.map(function(word){
    print("old is ");
    print(word);
    print("new is ");
    print(titleCase(word));
    return titleCase(word);
    });
    print("old is ");
    //print(ob.additional.omics_type);
    print("new is ");
    //print(updatedOmics.additional.omics_type);
    db.datasets.dataset.update({"accession":ob.accession,"database":ob.database},{"$set":{"additional.omics_type":updatedOmics}});
});