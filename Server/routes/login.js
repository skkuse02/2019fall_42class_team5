var mysql = require('mysql');
var connection = mysql.createConnection({
    host     : 'localhost',
    user     : 'seteam5',
    password : 'SEteam5_password',
    port     : 3306,
    database : 'seteam5'
  });

connection.connect();

exports.register = function (req, res) {
    console.log("req", req.body);

    var users = {
        "user_id": req.body.username,
        "user_password": req.body.password,
        "nickname": req.body.nickname
    }
    // check duplication before register
    connection.query('SELECT COUNT(*) as dup FROM user WHERE user_id= ?', users.user_id, function (error, results, fields){
      if (error) {
        console.log("error ocurred", error);
        res.sendStatus(400);
      } else {
        if(results[0].dup==0){ // if there's no same user_id
          connection.query('INSERT INTO user SET ?' , users, function (error, results, fields) {
              if (error) {
                  console.log("error ocurred", error);
                  res.sendStatus(400);
              } else {
                  console.log('The solution is: ', results);
                  res.sendStatus(200);
              }
          });
        }
        else { // duplication of user id
          console.log("Duplication");
          res.sendStatus(400);
        }
      }
    });
}


exports.login = function (req, res) {


    console.log("req", req.body);
    var user_id = req.body.username;
    var user_password = req.body.password;


    connection.query('SELECT * FROM user WHERE user_id = ?', [user_id],
    function( error, rows, fields) {
        if (error) {
            console.log("error ocurred", error);
            res.sendStatus(400);
        } else {
            console.log('The solution is: ', rows);
            if(rows.length > 0) {
                if(rows[0].user_password == user_password) {
                    console.log('It is matched!');
                    res.sendStatus(200);
                } else {
                    res.sendStatus(400);
                }
            } else {
                res.sendStatus(400);
            }
        }
    })
}
