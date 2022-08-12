db.datasets.dataset.find({"database":"Cell Collective","crossReferences.pubmed":{"$exists":true}}).forEach(function(input_data){
    //print(input_data.accession);
    input_data.crossReferences.pubmed.forEach(function(pubmedid){
        print(pubmedid)
        db.publications.publicationdataset.insert({
    "_class" : "uk.ac.ebi.ddi.service.db.model.publication.PublicationDataset",
    "pubmedId" : pubmedid,
    "accession" : input_data.accession,
    "database" : input_data.database,
    "omicsType" : "Models"
})
        })
})