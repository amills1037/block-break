use block_break_dev

// db.stats() is a function, so use "statistics"
db.statistics.insertOne({ player_id: "global", stat_name: "blocksBroken", stat: 0 });

db.statistics.createIndex({ player_id: 1 });
db.statistics.createIndex({ player_id: 1, stat_name: 1  }, { unique: true });

db.statistics.findOne(
    {player_id: "global", stat_name: "blocksBroken"}
);

//db.statistics.findOneAndUpdate(
//    {player_id: "global", stat_name: "blocksBroken"},
//   { $inc: { "stat" : 1 } },
//   {returnNewDocument: true}
//);
