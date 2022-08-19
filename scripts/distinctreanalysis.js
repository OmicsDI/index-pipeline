var similars =  db.datasets.similars.find({});


similars.forEach(function(input_data)
{
    var similarsArray = input_data.similars;
    similarsArray.forEach(function(sim_data){
        print(sim_data);
        db.datasets.similars.update({"_id":input_data._id},{"$set":{"similars":[]}});
        db.datasets.similars.update({"_id":input_data._id},{"$addToSet":{"similars":sim_data}});
        });
}
);