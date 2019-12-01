
var mysql = require('mysql');
var connection = mysql.createConnection({
    //host     : '192.168.35.181',
    host     : '192.168.43.236',
    user     : 'seteam5',
    password : 'se55555',
    port     : 3306,
    database : 'seteam5'
  });

connection.connect();


//이용자가 레시피를 검색하면, db에서 일치하는 레시피들을 찾아서 대략적인 정보를 어플에 보내준다.
exports.recipe_search = function (req, res) {
  var info = new Array(); // recipe 정보 목록
  var result = new Object(); // 보내지는 JSON

  connection.query('SELECT * FROM authentic_recipe WHERE category = ?', req.query.keyword, function(error, rows, fields) {
    if(error){
      console.log('error ocurred: ', error);
      res.status(400).send('Database error');
    }
    else {
      for(var i=0;i<rows.length;i++){
        var recipe = new Object(); // 개별 레시피 정보
        var items = new Array();
        for(var key in rows[i]){
          switch(key){
            case "recipe_name" :
              recipe.recipe_name = rows[i][key];
              break;
            case "main_img_src" :
              recipe.mainImage = rows[i][key];
              break;
            case "credit" :
              recipe.like = rows[i][key];
              break;
            case "item1" :
              //console.log(rows[i][key]);
              if(rows[i][key]) {
                items.push(rows[i][key]);
              }
              break;
            case "item2" :
              //console.log(rows[i][key]);
              if(rows[i][key]) {
                items.push(rows[i][key]);
              }
              break;
            case "item3" :
              //console.log(rows[i][key]);
              if(rows[i][key]) {
                items.push(rows[i][key]);
              }
              break;
            case "description" :
              recipe.description = rows[i][key];
              break;
            default :
              break;
          }
          recipe.items = items;
        }
        info.push(recipe);
      }
      result.recipe_main = info;
      console.log(result);
      res.status(200).json(result);
    }
  });
}

//이용자가 선택한 재료들을 기반으로, db에서 일치하는 레시피들을 찾아서 대략적인 정보를 어플에 보내준다.
exports.recipe_recommendation = function (req, res) {
  var good = req.body.good;
  var bad = req.body.bad;
  // get recipe_id of recommended recipe
  connection.query('SELECT recipe_id FROM authentic_recipe WHERE (item1 in (?) or item2 in (?) or item3 in (?)) and recipe_id NOT IN (SELECT recipe_id FROM authentic_ingredient WHERE item_id in (?)) ORDER BY credit desc', [good, good, good, bad], function( error, rows, fields) {
    if(error){
      console.log("error ocurred: ", error);
      res.status(400).send("Database error");
    }
    else {
      var idList = new Array(); // recipe_id list
      var result = new Object(); // final result
      for(var i=0;i<rows.length;i++){
        for(var key in rows[i]){
          idList.push(rows[i]["recipe_id"]);
        }
      }
      console.log(idList);

      // get recipe summary
      connection.query('SELECT * FROM authentic_recipe WHERE recipe_id IN (?)', [idList], function(error, rows, fields) {
        if(error){
          console.log('error ocurred: ', error);
          res.status(400).send('Database error');
        }
        else {
          var recipeList = new Array();
          for(var i=0;i<rows.length;i++){
            var recipe = new Object();
            for(var key in rows[i]){
              switch(key){
                case "recipe_name" :
                  recipe.recipe_name = rows[i][key];
                  break;
                case "main_img_src" :
                  recipe.mainImage = rows[i][key];
                  break;
                case "credit" :
                  recipe.like = rows[i][key];
                  break;
                case "item1" :
                  recipe.item1 = rows[i][key];
                  break;
                case "item2" :
                  recipe.item2 = rows[i][key];
                  break;
                case "item3" :
                  recipe.item3 = rows[i][key];
                  break;
                case "description" :
                  recipe.description = rows[i][key];
                  break;
                case "recipe_id" :
                  var recipe_id = rows[i][key];
                  break;
                default :
                  break;
              }
            }
            recipeList.push(recipe);
          }
          result.recipe_main = recipeList;
          console.log(result);
          res.status(200).json(info);
        }
      });
    }
  });
}


//이용자가 한가지 레시피를 선택 했을 떄, 해당 레시피의 디테일한 정보를 전달한다.
exports.recipe_detail = function (req, res) {
  //var recipe_name = "매운 닭볶음탕";
  var info = new Object();
  var recipe_id = new Object();

  // get recipe_id
  connection.query('SELECT recipe_id, main_img_src FROM authentic_recipe WHERE recipe_name = ?', req.body.recipe_name,
  function(error, rows, fields) {
    recipe_id = rows[0]["recipe_id"];
    info.name = recipe_name;
    if(error){
      console.log("error ocurred: ", error);
      res.status(400).send('Database error');
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
          res.status(400).send('Database error');
        }
        else{
          info.like = rows[0]["credit"];
        }
      });

      // get ingredients
      connection.query('SELECT item_id FROM authentic_ingredient WHERE recipe_id = ?', recipe_id, function(error, rows, fields){
          if(error){
            console.log("error ocurred: ", error);
            res.status(400).send('Database error');
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
          res.status(400).send('Database error');
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

          res.status(200).json(info);
        }
      });
    }
  });
}
