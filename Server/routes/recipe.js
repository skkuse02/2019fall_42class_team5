
var mysql = require('mysql');
var connection = mysql.createConnection({
  host     : '115.145.239.128',
  user     : 'seteam5',
  password : 'se55555',
  port     : 3306,
  database : 'seteam5'
  });

connection.connect();


//이용자가 레시피를 검색하면, db에서 일치하는 레시피들을 찾아서 대략적인 정보를 어플에 보내준다.
exports.recipe_search = function (req, res) {
  var category = "aaa";

  connection.query('SELECT * FROM authentic_recipe WHERE category = ? order by recipe_id', [category],
  function( error, results, fields) {
    console.log(results);
  });


}

//이용자가 선택한 재료들을 기반으로, db에서 일치하는 레시피들을 찾아서 대략적인 정보를 어플에 보내준다.
exports.recipe_recommendation = function (req, res) {

  var 싫은 재료;
  var 좋은 재료;

  connection.query('SELECT item_id FROM items WHERE item_name1 in ?', [싫은 재료],
  function( error, 싫은 재료 id, fields) {
    console.log(results1);
  });

  connection.query('SELECT item_id FROM items WHERE item_name1 in ?', [좋은 재료],
  function( error, 좋은 재료 id, fields) {
    console.log(results2);
  });

  connection.query('SELECT recipe_id FROM authentic_ingredient WHERE item_id in ? and recipe_id  not in (SELECT recipe_id FROM authentic_ingredient WHERE item_id in ?)', [좋은 재료_id, 싫은 재료_id],
  function( error, 추천할 레시피 id, fields) {
    console.log(results2);
  });

  connection.query('SELECT * FROM authentic_recipe WHERE recipe_id in ?', [추천할 레시피 id],
  function( error, 추천할 레시피 정보, fields) {
    console.log(results2);
  });
}


//이용자가 한가지 레시피를 선택 했을 떄, 해당 레시피의 디테일한 정보를 전달한다.
exports.recipe_detail = function (req, res) {
  //var recipe_name = "매운 닭볶음탕";
  var info = new Object();
  var recipe_id = new Object();

  // get recipe_id
  connection.query('SELECT recipe_id, main_img_src FROM authentic_recipe WHERE recipe_name = ?', req.body.recipe_name,
  function( error, rows, fields) {
    recipe_id = rows[0]["recipe_id"];
    info.name = recipe_name;
    if(error){
      console.log("error ocurred: ", error);
      res.sendStatus(400);
    }
    else{
      for(var key in rows[0]){
        if(key == "main_img_src")
          info.mainImage = rows[0][key];
      }
      console.log("recipe_id = " + recipe_id);

      // get credit
      connection.query('SELECT COUNT(*) as credit FROM recipe_credit WHERE recipe_id = ?', recipe_id, function(error, rows, fields) {
        if(error){
          console.log("error ocurred: ", error);
          res.sendStatus(400);
        }
        else{
          info.like = rows[0]["credit"];
        }
      });

      // get ingredients
      connection.query('SELECT item_id FROM authentic_ingredient WHERE recipe_id = ?', recipe_id, function(error, rows, fields){
          if(error){
            console.log("error ocurred: ", error);
            res.sendStatus(400);
          }
          else {
            var itemList = new Array();
            var item = new Object();
            for(var i=0;i<rows.length;i++){
              item.item_id = rows[i]["item_id"];
              itemList.push(item);
            }
            info.items = itemList;
          }
      });

      // get description
      connection.query('SELECT description, img_src FROM recipe_detail WHERE recipe_id = ? order by procedure_num', recipe_id, function(error, rows, fields){
        if(error){
          console.log("error ocurred: ", error);
          res.sendStatus(400);
        }
        else {
          var descList = new Array();
          var desc = new Object();
          for(var i=0;i<rows.length;i++){
            for(var key in rows[i]){
              if(key == "description")
                desc.description = rows[i][key];
              if(key == "img_src")
                desc.image = rows[i][key];
            }
            descList.push(desc);
          }
          info.description = descList;
          console.log(info);

          res.sendStatus(200);
          res.json(info);
        }
      });
    }
  });
}
