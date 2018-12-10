var arrayExpressDb = "ArrayExpress";
var geoDb = "GEO";

db.datasets.dataset.find({"database":arrayExpressDb,"additional.additional_accession":{"$exists":true},"accession":"E-GEOD-24369"}).forEach(function(input_data)
    {
        input_data.additional.additional_accession.forEach(function(arr_element){
                
            if(db.datasets.dataset.count({"accession":arr_element,"database":"GEO" }) > 0)
            {
                print(arr_element)
                print(input_data.accession);
                var scores =db.datasets.dataset.findOne({"accession":input_data.accession,"database":arrayExpressDb }).scores;
                if(scores != null) {
                        var reanalysisCount = scores.reanalysisCount;
                        var citationCount = scores.citationCount;
                        var searchCount = scores.searchCount;
                        var viewCount = scores.viewCount;
                        
                        var secondaryScores = db.datasets.dataset.findOne({"accession":arr_element,"database":geoDb }).scores;
                        if(secondaryScores != null){
                            scores.reanalysisCount = reanalysisCount + secondaryScores.reanalysisCount;
                            scores.citationCount = citationCount + secondaryScores.citationCount;
                            scores.searchCount = searchCount + secondaryScores.searchCount;
                            scores.viewCount = viewCount + secondaryScores.viewCount;
                            db.datasets.dataset.update({"accession":input_data.accession,"database": arrayExpressDb},{"$set":{"scores":scores}});
                        }
                }
                
            }
            
        });
        

    });