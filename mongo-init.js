// Switch to the kitchensinkdb database
db = db.getSiblingDB("kitchensinkdb");

// Create some initial collections if needed
db.createCollection("members");
db.createCollection("users");

print("MongoDB initialization completed successfully!");