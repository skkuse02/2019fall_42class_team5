// connect with database
var mysql = require('mysql');
var connection = mysql.createConnection({
    host     : 'localhost',
    user     : 'Admin',
    password : 'se55555',
    port     : 3306,
    database : 'seteam5'
  });

connection.connect();


// 냉장고 조회
exports.items = function (req, res){
  console.log("req", req.body); // user_id

  connection.query('SELECT * FROM refrigerator WHERE user_id = ?', req.body.username, function(error, rows, fields) {
    if(error){
      console.log("error ocurred", error);
      res.sendStatus(400).end();
    }
    else {
      if(rows.length == 0){
        console.log("refrigerator is empty");
        res.sendStatus(400).end();
      }
      res.sendStatus(200);

      // push user_id info.
      var itemList = new Array();
      var user = new Object();
      user.username = req.body.username;
      itemList.push(user);

      // push refrigerator info.
      for(var i=0; i<rows.length; i++){
        var item = new Object;
        for(var key in rows[i]) {
          console.log("key: " + key + ", value: " + rows[i][key]);
          if(key == 'item_id')
            item.item_id = rows[i][key];
          if(key == 'count')
            item.count = rows[i][key];
        }
        itemList.push(item);
      }
      console.log(itemList);
      //var jsonData = JSON.stringify(itemList);
      res.json(itemList);
    }
  });
}


// 냉장고 재료 추가
exports.add_items = function (req, res){
  console.log("req", req.body);

  var values = new Array();
  for(var i=0;i<req.body.itemList.length;i++){
    values.push([req.username, req.itemList[i]]);
  }

  var str_query = connection.query('INSERT INTO refrigerator (user_id, item_id) VALUES ?', [values], function(error, rows, fields) {
    console.log(str_query.sql);
    if(error){
      console.log("Error ocurred: ", error);
      res.sendStatus(400).end();
    }
    if(rows.affectedRows > 0){
      // 정상적으로 insert
      res.sendStatus(200);
      }
    }
  });
}


// 냉장고 재료 삭제
exports.delete_items = function (req, res){
  console.log("req", req.body);

  var values = new Array();
  for(var i=0; i<req.body.itemList.length; i++){
    values.push([req.body.username, req.body.itemList[i]]);
  }

  connection.query('DELETE FROM refrigerator WHERE (user_id, item_id) IN (?)', [values], function(error, rows, fields) {
    if(error){
      console.log('Error ocurred: ', error);
      res.sendStatus(400).end();
    }
    else
      res.sendStatus(200);
  });
}
