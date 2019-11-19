var count = 0;
db.datasets.dataset.find({ "accession":/BIOMD/i, "database":"BioModels", "additional.submissionId": {"$exists":true} }).forEach(
        function(model) {
            //print(model.additional.submissionId)
            var submissionId = model.additional.submissionId;
            if(db.datasets.dataset.find({"accession":submissionId}).count > 0 )
            {
                    print(submissionId);
                    count = count + 1;
            }
        }
        
    );
print(count);        