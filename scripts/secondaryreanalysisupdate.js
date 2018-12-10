var arrayExpressDb = "ArrayExpress";
var geoDb = "GEO";

db.datasets.dataset.find({"database":arrayExpressDb,"additional.additional_accession":{"$exists":true}}).
forEach(function(input_data)
    {
        input_data.additional.additional_accession.forEach(function(arr_element){
                
            if(db.datasets.similars.count({"accession":arr_element,"database":"GEO" }) > 0)
            {
                print(arr_element)
                print(input_data.accession);
                var scores = db.datasets.dataset.findOne({"accession":input_data.accession,"database":arrayExpressDb}).scores;
                //print(scores.reanalysisCount)
                var similardata = db.datasets.similars.findOne({"accession":arr_element,"similars.relationType":"Reanalyzed by"})
                if(similardata != null){
                var secondaryScores = similardata.similars.length;
                print(secondaryScores)
                if(scores != null) {
                        var reanalysisCount = scores.reanalysisCount;                        
                        if(secondaryScores != null){
                            scores.reanalysisCount = reanalysisCount + secondaryScores;
                            db.datasets.dataset.update({"accession":input_data.accession,"database": arrayExpressDb},
                            {"$set":{"scores":scores,"additional.reanalysis_count":[scores.reanalysisCount.toString()]}});
                        }
                }
                else{
                db.datasets.dataset.update({"accession":input_data.accession,"database": arrayExpressDb},
                {"$set":{"scores.reanalysisCount":secondaryScores,"additional.reanalysis_count":[secondaryScores.toString()]}});
                    }
                }
                
            }
            
        });
        

    });
    