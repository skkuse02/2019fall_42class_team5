// connect with database
var mysql = require('mysql');
var connection = mysql.createConnection({
    host     : '115.145.227.249',
    user     : 'seteam5',
    password : 'se55555',
    port     : 3306,
    database : 'seteam5'
  });

connection.connect();


// 장바구니 조회
exports.items = function (req, res){

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
  console.log("req", req.body);

  var values = new Array();
  for(var i=0;i<req.body.items.length;i++){
    values.push([req.body.username, req.body.items[i]]);
  }

  connection.query('SELECT count(*) as bool FROM cart WHERE user_id = ? and item_id = ?', [values], function(error, rows, fields){
    if(error){
      console.log("Error ocurred: ", error);
      res.sendStatus(400).end();
    }
    else{
      if(rows[0]["bool"]){ // no duplication
        var str_query = connection.query('INSERT INTO cart (user_id, item_id) VALUES ?', [values], function(error, rows, fields) {
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
      else {
        res.status(400).send("Insert Request of Duplication").end();
      }
    }
  }); // end of check duplication query
}


// 장바구니 재료 삭제
exports.delete_items = function (req, res){
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
        connection.query('SELECT count(*) as bool FROM refrigerator user_id = ? and item_id = ?', [values], function(error, rows, fields){
          if(error){
            console.log("Error ocurred: ", error);
            res.sendStatus(400).end();
          }
          else {
            if(rows[0]["bool"]){ // no duplication
              connection.query('INSERT INTO refrigerator (user_id, item_id) VALUES ?', [values], function(error, rows, fields) {
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
            else{
              res.status(400).send("Insert Request of Duplication").end();
            }
          }
        }); // end of check duplication query
      }
    }
  }); // end of delete query
}
