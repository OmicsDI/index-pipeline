db.datasets.dataset.find({"files":{"$exists":true},"files":{"$ne":{}}}).limit(10).
forEach(
function(data){
    //print(data.files)
    data.files.entries(obj).forEach(([key, value]) => {
        console.log(`${key} ${value}`); // "a 5", "b 7", "c 9"
        });
        
    val map_data =  new Map(Object.entries(obj));    
        print(map_data);
    })

function getExt(path){
    return (path.match(/(?:.+..+[^\/]+$)/ig) != null) ? path.split('.').slice(-1): 'null';
}