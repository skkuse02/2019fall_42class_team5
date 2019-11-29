// connect with database
var mysql = require('mysql');
var connection = mysql.createConnection({
    host     : '115.145.239.128',
    user     : 'seteam5',
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
      res.sendStatus(200);

      // get item_id
      var itemId = new Array();
      for(var i=0; i<rows.length; i++){
        for(var key in rows[i]) {
          if(key == 'item_id')
            itemId.push(rows[i][key]);
        }
      }
      // find items' name
      connection.query('SELECT * FROM items WHERE item_id IN (?)', [itemId], function(error, rows, fields){
        if(error){
          console.log("error ocurred", error);
          res.sendStatus(400).end();
        }
        else{
          //res.sendStatus(200);
          var itemName = new Array();
          for(var j=0; j<rows.length; j++){
            var item = new Object();
            for(var key in rows[j]){
              if(key == "item_name1")
                item.name1 = rows[j][key];
              if(key == 'item_name2')
                item.name2 = rows[j][key];
            }
            itemName.push(item);
          }
          var result = new Object();
          result.itemlist = itemName;
          console.log(result);
          res.json(result);
        }
      }); // item name 찾는 query
  }); // item id 찾는 query
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
