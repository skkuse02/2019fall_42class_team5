// connect with database
var connection = require('./db');

// like on recipe
exports.recipe_credit = function (req, res) {
  console.log("Recipe Credit");
  var username = req.query.username;
  var recipe_id = req.query.recipe_id;

  // LIKE
  if(req.query.like == 1){ // add a log
    connection.query('INSERT INTO recipe_credit values(?, ?)', [recipe_id, username], function(error, rows, fields){
      if(error){
        console.log("error ocurred: ", error);
        res.status(400).send("Databse error").end();
      }
      else { // increment credit of the recipe
        connection.query('UPDATE authentic_recipe SET credit = credit + 1 where recipe_id = ? ', [recipe_id], function(error, rows, fields){
          if(error){
            console.log("error ocurred: ", error);
            res.status(400).send("Databse error").end();
          }
          else { // send updated credit of the recipe
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

  // DISLIKE
  else { // delete a log
    connection.query('DELETE FROM recipe_credit WHERE recipe_id = ? and user_id = ?', [recipe_id, username], function(error, rows, fields){
      if(error){
        console.log("error ocurred: ", error);
        res.status(400).send("Databse error").end();
      }
      else { // decrement credit of the recipe
        connection.query('UPDATE authentic_recipe SET credit = credit - 1 where recipe_id = ? ', [recipe_id], function(error, rows, fields){
          if(error){
            console.log("error ocurred: ", error);
            res.status(400).send("Databse error").end();
          }
          else { // send updated credit of the recipe
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
