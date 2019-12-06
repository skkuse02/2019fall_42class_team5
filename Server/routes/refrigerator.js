// connect with database
var mysql = require('mysql');
var connection = mysql.createConnection({
    //host     : '192.168.25.25',
    host     : '192.168.0.79',
    user     : 'seteam5',
    password : 'se55555',
    port     : 3306,
    database : 'seteam5'
  });

connection.connect();


// 냉장고 조회
exports.items = function (req, res){
  console.log(req.query.username);
  connection.query('SELECT * FROM refrigerator WHERE user_id = ?', req.query.username, function(error, rows, fields) {
    if(error){
      console.log("error ocurred", error);
      res.status(400).send('Database error').end();
    }
    else {
      // get item_id
      var itemId = new Array();
      for(var i=0; i<rows.length; i++){
        for(var key in rows[i]) {
          if(key == 'item_id')
            itemId.push(rows[i][key]);
        }
      }
      var info = new Object();
      info.items = itemId;
      res.status(200).json(info).end();
    }
  });
}


// 냉장고 재료 추가
exports.add_items = function (req, res){
  console.log("req", req.body);

  var values = new Array();
  for(var i=0;i<req.body.items.length;i++){
    values.push([req.body.username, req.body.items[i]]);
  }

  var str_query = connection.query('INSERT IGNORE INTO refrigerator (user_id, item_id) VALUES ?', [values], function(error, rows, fields) {
    console.log(str_query.sql);
    if(error){
      console.log("Error ocurred: ", error);
      res.status(400).send('Database error').end();
    }
    else {
      res.sendStatus(200).end();
    }
  });
}


// 냉장고 재료 삭제
exports.delete_items = function (req, res){
  console.log("req", req.body);

  var values = new Array();
  for(var i=0; i<req.body.items.length; i++){
    values.push([req.body.username, req.body.items[i]]);
  }

  connection.query('DELETE FROM refrigerator WHERE (user_id, item_id) IN (?)', [values], function(error, rows, fields) {
    if(error){
      console.log('Error ocurred: ', error);
      res.status(400).send('Database error').end();
    }
    else
      res.sendStatus(200).end();
  });
}
