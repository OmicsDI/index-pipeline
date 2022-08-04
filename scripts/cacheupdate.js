db.serverStatus().wiredTiger.cache

db.adminCommand( { "setParameter": 1, "wiredTigerEngineRuntimeConfig": "cache_size=25G"})

db.serverStatus().wiredTiger.cache