db.datasets.dataset.find({ "database":"European Genome-phenome Archive"}).forEach(
        function(model) {
            //print(model.additional.submissionId)
            var accession = model.accession;
            db.datasets.dataset.update({"accession":accession},{"$set":{"database":"EGA"}},false, true)
        }
        
    );
