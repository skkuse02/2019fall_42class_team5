var mysql = require('mysql');
var connection = mysql.createConnection({
    host     : '115.145.239.6',
    user     : 'seteam5',
    password : 'se55555',
    port     : 3306,
    database : 'seteam5'
  });

connection.connect();
module.exports = connection;
