db.users.find({roles:/ADMIN/}).forEach(function(user_data){
    var role = user_data.roles;
    var new_role = role.replace(",ADMIN","");
    print(new_role);
    print(user_data._id);
    db.users.update({"_id":user_data._id},{"$set":{"roles":new_role}})
    })