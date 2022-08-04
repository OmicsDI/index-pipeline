db.datasets.dataset.updateMany( {"database":"ENA","crossReferences.taxon":{"$exists":true}}, 
{ $rename: { "crossReferences.taxon": "crossReferences.taxonomy" } } )