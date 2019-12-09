// connect with database
var connection = require('./db');

// 장바구니 조회
exports.items = function (req, res){
  console.log("GET basket");

  console.log(req.query.username);
  connection.query('SELECT * FROM cart WHERE user_id = ?', req.query.username, function(error, rows, fields) {
    if(error){
      console.log("error ocurred", error);
      res.status(400).send("Database error").end();
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
      console.log(info);
      res.status(200).json(info).end();
    }
  }); // item id 찾는 query
}


// 장바구니 재료 추가
exports.add_items = function (req, res){
  console.log("POST basket");
  console.log("req", req.body);

  var values = new Array();
  for(var i=0;i<req.body.items.length;i++){
    values.push([req.body.username, req.body.items[i]]);
  }
  var str_query = connection.query('INSERT IGNORE INTO cart (user_id, item_id) VALUES ?', [values], function(error, rows, fields) {
    console.log(str_query.sql);
    if(error){
      console.log("Error ocurred: ", error);
      res.sendStatus(400).end();
    }
    else {
      // 정상적으로 insert
      res.sendStatus(200).end();
    }
  }); // end of insert query
}


// 장바구니 재료 삭제
exports.delete_items = function (req, res){
  console.log("DELETE basket");
  console.log("req", req.body);

  var values = new Array();
  for(var i=0; i<req.body.items.length; i++){
    values.push([req.body.username, req.body.items[i]]);
  }

  connection.query('DELETE FROM cart WHERE (user_id, item_id) = (?)', [values], function(error, rows, fields) {
    if(error){
      console.log('Error ocurred: ', error);
      res.sendStatus(400).end();
    }
    else{
      if(rows.affectedRow)
        res.status(400).send("Invalid Delete Request").end();
      else
        res.sendStatus(200).end();
    }
  });
}

// 장바구니 재료 구입 완료 -> 냉장고 재료 추가
exports.purchase_items = function (req, res){
  console.log("Purchase basket");
  console.log("req", req.body);

  var values = new Array();
  for(var i=0; i<req.body.items.length; i++){
    values.push([req.body.username, req.body.items[i]]);
  }

  connection.query('DELETE FROM cart WHERE (user_id, item_id) IN (?)', [values], function(error, rows, fields) {
    if(error){
      console.log('Error ocurred: ', error);
      res.sendStatus(400).end();
    }
    else{
      if(rows.affectedRow)
        res.status(400).send("Invalid Delete Request").end();
      else {
        connection.query('INSERT IGNORE INTO refrigerator (user_id, item_id) VALUES ?', [values], function(error, rows, fields) {
        if(error){
            console.log("Error ocurred: ", error);
            res.sendStatus(400).end();
        }
        else {
        // 정상적으로 insert
          res.sendStatus(200).end();
          }
        }); // end of insert str_query
      }
    }
  }); // end of delete query
}
