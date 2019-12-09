var mysql = require('mysql');
var connection = mysql.createConnection({
    host     : '115.145.227.249',
    user     : 'seteam5',
    password : 'se55555',
    port     : 3306,
    database : 'seteam5'
  });

connection.connect();

// like
exports.recipe_credit = function (req, res) {
  var username = req.query.username;
  var recipe_id = req.query.recipe_id;

  if(req.query.like == 1){ // like
    connection.query('INSERT INTO recipe_credit values(?, ?)', [recipe_id, username], function(error, rows, fields){
      if(error){
        console.log("error ocurred: ", error);
        res.status(400).send("Databse error").end();
      }
      else {
        connection.query('UPDATE authentic_recipe SET credit = credit + 1 where recipe_id = ? ', [recipe_id], function(error, rows, fields){
          if(error){
            console.log("error ocurred: ", error);
            res.status(400).send("Databse error").end();
          }
          else {
            connection.query('SELECT credit FROM authentic_recipe WHERE recipe_id = ?', [recipe_id], function(error, rows, fields){
              if(error){
                console.log("error ocurred: ", error);
                res.status(400).send("Databse error").end();
              }
              else{
                var info = new Object();
                info.action = 1;
                info.like = rows[0]["credit"];
                res.status(200).json(info).end();
              }
            }); // end of get updated credit
          }
        }); // end of update credit
      }
    }); // end of insert credit
  }

  else { // dislike
    connection.query('DELETE FROM recipe_credit WHERE recipe_id = ? and user_id = ?', [recipe_id, username], function(error, rows, fields){
      if(error){
        console.log("error ocurred: ", error);
        res.status(400).send("Databse error").end();
      }
      else {
        connection.query('UPDATE authentic_recipe SET credit = credit - 1 where recipe_id = ? ', [recipe_id], function(error, rows, fields){
          if(error){
            console.log("error ocurred: ", error);
            res.status(400).send("Databse error").end();
          }
          else {
            connection.query('SELECT credit FROM authentic_recipe WHERE recipe_id = ?', [recipe_id], function(error, rows, fields){
              if(error){
                console.log("error ocurred: ", error);
                res.status(400).send("Databse error").end();
              }
              else{
                var info = new Object();
                info.action = 0;
                info.like = rows[0]["credit"];
                res.status(200).json(info).end();
              }
            }); // end of get updated credit
          }
        }); // end of update credit
      }
    }); // end of delete credit
  }
}
