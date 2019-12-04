var mysql = require('mysql');
var connection = mysql.createConnection({
    host     : '115.145.240.151',
    user     : 'seteam5',
    password : 'se55555',
    port     : 3306,
    database : 'seteam5'
  });

connection.connect();

// iike
exports.like = function (req, res){

}

// unlike
exports.unlike = function (req, res){

}
