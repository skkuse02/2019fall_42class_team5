// connect with database
var connection = require('./db');

exports.register = function (req, res) {

    var users = {
        "user_id": req.body.username,
        "user_password": req.body.password
    }
    // check duplication before register
    connection.query('SELECT COUNT(*) as dup FROM user WHERE user_id= ?', users.user_id, function(error, rows, fields){
      if (error) {
        console.log("error ocurred", error);
        res.status(400).send('Database error').end();
      } else {
        if(results[0].dup==0){ // if there's no same user_id
          connection.query('INSERT INTO user SET ?' , users, function(error, rows, fields) {
              if (error) {
                  console.log("error ocurred", error);
                  res.sendStatus(400).end();
              } else {
                  res.sendStatus(200).end();
              }
          });
        }
        else { // duplication of user id
          console.log("Duplication");
          res.status(400).send('해당하는 아이디가 이미 존재합니다').end();
        }
      }
    });
}


exports.login = function (req, res) {
    console.log("Login");
    console.log("req", req.body);
    var user_id = req.body.username;
    var user_password = req.body.password;

    connection.query('SELECT * FROM user WHERE user_id = ?', [user_id], function(error, rows, fields) {
        if (error) {
            console.log("error ocurred", error);
            res.status(400).send('Database error').end();
        } else {
            if(rows.length > 0) { // Login success
              if(rows[0].user_password == user_password) {
                  res.sendStatus(200).end();
              } else { // Wrong password
                  res.status(400).send('잘못된 비밀번호입니다').end();
              }
          } else { // if there is not exist certain ID
              res.status(400).send('존재하지 않는 아이디입니다').end();
          }
      }
    });
}
