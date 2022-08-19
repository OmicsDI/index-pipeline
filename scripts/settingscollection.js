db.settings.insert({
    "_id" : ObjectId("594bd4477ceafd171816da20"),
    "_class" : "uk.ac.ebi.ddi.ws.modules.dataset.util.FacetSettings",
    "maxFacetCount" : 33,
    "facetProperties" : [ 
        {
            "name" : "Repository",
            "caption" : "Repository"
        }, 
        {
            "name" : "Organism",
            "caption" : "Organism"
        }, 
        {
            "name" : "Tissue",
            "caption" : "Tissue"
        }, 
        {
            "name" : "Source",
            "caption" : "Source",
            "parentFacetValue" : "XXXX",
            "parentFacetName" : "XXXX"
        }, 
        {
            "name" : "Disease",
            "caption" : "Disease"
        }, 
        {
            "name" : "Technology type",
            "caption" : "Technology Type"
        }, 
        {
            "name" : "Instrument platform",
            "caption" : "Instument Platform"
        }, 
        {
            "name" : "Publication Date",
            "caption" : "Publication Date"
        }, 
        {
            "name" : "UNIPROT",
            "caption" : "UNIPROT ID/AC"
        }, 
        {
            "name" : "ENSEMBL",
            "caption" : "ENSEMBL ID"
        }, 
        {
            "name" : "CHEBI",
            "caption" : "CHEBI ID"
        }, 
        {
            "name" : "Metabolite name",
            "caption" : "Metabolite Name"
        }, 
        {
            "name" : "METABOLIGHTS",
            "caption" : "METABOLIGHTS ID",
            "parentFacetValue" : "MetaboLights",
            "parentFacetName" : "MetaboLights"
        }, 
        {
            "name" : "Curation status",
            "caption" : "Curation Status",
            "parentFacetName" : "XXXXX",
            "parentFacetValue" : "XXXXX"
        }, 
        {
            "name" : "GO",
            "caption" : "GO Term",
            "parentFacetName" : "Repository",
            "parentFacetValue" : "BioModels Database"
        }, 
        {
            "name" : "Model format",
            "caption" : "Model Format",
            "parentFacetName" : "XXXXX",
            "parentFacetValue" : "XXXX"
        }
    ]
})