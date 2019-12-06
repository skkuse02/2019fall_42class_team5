var express = require("express");
var login = require('./routes/login');
var refrigerator = require('./routes/refrigerator');
var recipe = require('./routes/recipe');
var purchase = require('./routes/purchase');
var credit = require('./routes/credit');
var bodyParser = require('body-parser');

var multer = require('multer');
var storage = multer.diskStorage({
  destination: function (req, file, cb) {
    cb(null, 'uploads/')
  },
  filename: function (req, file, cb) {
    cb(null, file.originalname)
  }
})
var upload = multer({ storage: storage })


var app = express();
app.use( bodyParser.urlencoded({ extended: true }) );
app.use( bodyParser.json() );
app.use('/image', express.static('uploads'));


var router = express.Router();


// test route
router.get('/', function(req, res) {
    res.json({ message: 'welcome to our upload module apis' });
});


// route to handle user registration
router.post('/user/signup', login.register);
router.post('/user/login', login.login);
// route to handle refrigerator management
router.get('/user/refrigerator', refrigerator.items);
router.post('/user/refrigerator', refrigerator.add_items);
router.delete('/user/refrigerator', refrigerator.delete_items);
// route to handle recipe operation
router.get('/recipe/search', recipe.recipe_search);
router.post('/recipe/recommendation', recipe.recipe_recommendation);
router.get('/recipe/detail', recipe.recipe_detail);
// route to handle item purchase
router.get('/user/basket', purchase.items);
router.post('/user/basket', purchase.add_items);
router.delete('/user/basket/remove', purchase.delete_items);
router.delete('/user/basket/move', purchase.purchase_items);
// route to handle recipe like
router.get('/recipe/detail/like', credit.recipe_credit);

app.use('/', router);
app.listen(5000);
