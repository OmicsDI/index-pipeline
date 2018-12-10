db.datasets.dataset.find({"additional.download_count":{"$exists":true}}).forEach(function(input_obj)
{
    print(parseInt(input_obj.additional.download_count[0]))
    //db.datasets.dataset.update({"accession":input_obj.accession,"database":input_obj.database},
    //{"$set":{"scores.downloadCount":parseInt(input_obj.additional.download_count[0])}})
})