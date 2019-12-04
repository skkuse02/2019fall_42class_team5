var mysql = require('mysql');
var connection = mysql.createConnection({
    host     : '115.145.239.153',
    user     : 'seteam5',
    password : 'se55555',
    port     : 3306,
    database : 'seteam5'
  });

connection.connect();

exports.register = function (req, res) {
    console.log("req", req);

    var users = {
        "user_id": req.body.username,
        "user_password": req.body.password
    }
    // check duplication before register
    connection.query('SELECT COUNT(*) as dup FROM user WHERE user_id= ?', users.user_id, function (error, results, fields){
      if (error) {
        console.log("error ocurred", error);
        res.status(400).send('Database error');
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
          res.status(400).send('해당하는 아이디가 이미 존재합니다');
        }
      }
    });
}


exports.login = function (req, res) {


    console.log("req", req.body);
    var user_id = req.body.username;
    var user_password = req.body.password;

    console.log(user_id);
    console.log(user_password);


    connection.query('SELECT * FROM user WHERE user_id = ?', [user_id],
    function( error, rows, fields) {
        if (error) {
            console.log("error ocurred", error);
            res.status(400).send('Database error');
        } else {
            console.log('The solution is: ', rows);
            if(rows.length > 0) {
              if(rows[0].user_password == user_password) {
                  res.sendStatus(200);
              } else {
                  res.status(400).send('잘못된 비밀번호입니다');
              }
          } else {
              res.status(400).send('존재하지 않는 아이디입니다');
          }
      }
    });
}
