var express = require("express");
var login = require('./routes/login');
var bodyParser = require('body-parser');


var app = express();
app.use( bodyParser.urlencoded({ extended: true }) );
app.use( bodyParser.json() );


app.use(function(req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
    next();
});


var router = express.Router();


// test route
router.get('/', function(req, res) {
    res.json({ message: 'welcome to our upload module apis' });
});


// route to handle user registration
router.post('/user/signup', login.register);
router.post('/user/login', login.login);


app.use('/', router);
app.listen(5000);
