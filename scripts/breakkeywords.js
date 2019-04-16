db.datasets.dataset.find({"additional.submitter_keywords":/;/}).forEach(function(input)
    {
        if(input.additional != null && input.additional.submitter_keywords != null)
        {   
            //print(input.additional.submitter_keywords);
            var keywords = input.additional.submitter_keywords;
            keywords.forEach(function(keywrd){
                    print(keywrd.indexOf(";"))
                    if(keywrd.indexOf(";") > 0){
                        var tobremoved = keywords.indexOf(keywrd)
                        keywords.splice(tobremoved,1)
                        //print(keywords);
                        var words = keywrd.split(";")
                        //print(words);
                        //keywords.pop(keywrd);
                        keywords = keywords.concat(words);
                        //print(keywords);
                        }
                        
                });
             //print(keywords);    
           //db.datasets.dataset.update({"accession":input.accession,"database":input.database},{"$addToSet":{"additional.submitter_keywords":{"$each":keywords}}});
           //db.datasets.dataset.update({"accession":input.accession,"database":input.database},{"$set":{"additional.submitter_keywords":{"$each":keywords}}});
           db.datasets.dataset.update({"accession":input.accession,"database":input.database},{"$set":{"additional.submitter_keywords":[]}});
           db.datasets.dataset.update({"accession":input.accession,"database":input.database},{"$addToSet":{"additional.submitter_keywords":{"$each":keywords}}});
        }
    })