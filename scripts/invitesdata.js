db.invites.aggregate([{"$unwind":"$dataSets"},{"$project":{"accession":"$dataSets._id","source":"$dataSets.source","email":1,"inviteId":{"$concat":["https://www.omicsdi.org/welcome/","$_id"]},"_id":0}},{"$out":"info_invites"}])

db.info_invites.aggregate([{"$group":{"_id":"$email","source":{"$addToSet":"$source"},"inviteurl":{"$addToSet":"$inviteId"}}}])

db.info_invites.aggregate([{"$group":{"_id":"$email","source":{"$addToSet":"$source"},"inviteurl":{"$addToSet":"$inviteId"}}},{"$out":"info_last"}])

mongoexport --host mongos-hxvm7-dev-001.ebi.ac.uk --username "ddi_user" --password "V5f3SThe"  --db ddi_db --collection info_invites --authenticationDatabase admin --csv --fields email,accession,source,inviteId  --out invites.csv

mongoexport --host mongos-hxvm-001.ebi.ac.uk --username "ddi_user" --password "tDzDd81J"  --db ddi_db --collection info_last --authenticationDatabase admin --csv --fields _id,source,inviteurl  --out invites.csv


