// connect with database
var connection = require('./db');

// When user request reciep search with keyword
exports.recipe_search = function (req, res) {
  console.log("Search Recipe");
  var recipeList = new Array(); // recipe 정보 목록
  var result = new Object(); // 보내지는 JSON

  connection.query('SELECT * FROM authentic_recipe WHERE recipe_name LIKE ? ORDER BY credit desc', ["%" + req.query.keyword + "%"], function(error, rows, fields) {
    if(error){
      console.log('error ocurred: ', error);
      res.status(400).send('Database error').end();
    }
    else {
      for(var i=0;i<rows.length;i++){
        var recipe = new Object(); // each recipe's info.
        var items = new Array(); // array of recipes
        for(var key in rows[i]){
          switch(key){
            case "recipe_id" :
              recipe.recipe_id = rows[i][key];
              break;
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
              if(rows[i][key]) { // not 0 (=없음)
                items.push(rows[i][key]);
              }
              break;
            case "item2" :
              if(rows[i][key]) { // not 0 (=없음)
                items.push(rows[i][key]);
              }
              break;
            case "item3" :
              if(rows[i][key]) { // not 0 (=없음)
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
        recipeList.push(recipe);
      }
      result.recipe_main = recipeList;
      console.log(result);
      res.status(200).json(result).end();
    }
  });
}

// Recommend recipes based on selected items by user
exports.recipe_recommendation = function (req, res) {
  console.log("Recipe recommendation");
  var good = req.body.good;
  for(var i=0;i<good.length;i++){
    if(good[i] == 69){ // include '닭가슴살', '닭다리살' to '닭고기'
      good.push(24);
      good.push(36);
    }
    if(good[i] == 74){ // include '삼겹살', '돼지앞다리살' to '돼지고기'
      good.push(30);
      good.push(67);
    }
  }
  var bad = req.body.bad;
  if(req.body.bad.length == 0){
   bad = [0];
  }
  for(var i=0;i<bad.length;i++){
    if(bad[i] == 69){ // include '닭가슴살', '닭다리살' to '닭고기'
      bad.push(24);
      bad.push(36);
    }
    if(bad[i] == 74){ // include '삼겹살', '돼지앞다리살' to '돼지고기'
      bad.push(30);
      bad.push(67);
    }
  }

  console.log(good);
  console.log(bad);

  // get recipe_id of recommended recipe
  connection.query('SELECT recipe_id FROM authentic_recipe WHERE (item1 in (?) or item2 in (?) or item3 in (?)) and recipe_id NOT IN (SELECT recipe_id FROM authentic_ingredient WHERE item_id in (?)) ORDER BY credit desc', [good, good, good, bad], function( error, rows, fields) {
    if(error){
      console.log("error ocurred: ", error);
      res.status(400).send("Database error").end();
    }
    else {
      // Exist recipe based on recommendation algorithm
      if(rows.length){
        var idList = new Array(); // recipe_id list
        var result = new Object(); // final result
        for(var i=0;i<rows.length;i++){
          for(var key in rows[i])
            idList.push(rows[i]["recipe_id"]);
        }
        console.log(idList);

        // get recipe summary
        connection.query('SELECT * FROM authentic_recipe WHERE recipe_id IN (?)', [idList], function(error, rows, fields) {
          if(error){
            console.log('error ocurred: ', error);
            res.status(400).send('Database error').end();
          }
          else {
            var recipeList = new Array();
            for(var i=0;i<rows.length;i++){
              var recipe = new Object(); // each recipe's info.
              var items = new Array(); // array of recipes
              for(var key in rows[i]){
                switch(key){
                  case "recipe_id" :
                  recipe.recipe_id = rows[i][key];
                  break;
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
                    if(rows[i][key]) {
                      items.push(rows[i][key]);
                    }
                    break;
                  case "item2" :
                    if(rows[i][key]) {
                      items.push(rows[i][key]);
                    }
                    break;
                  case "item3" :
                    if(rows[i][key]) {
                      items.push(rows[i][key]);
                    }
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
                recipe.items = items;
              }
              recipeList.push(recipe);
            }
            result.recipe_main = recipeList;
            console.log(result);
            res.status(200).json(result).end();
          }
        });
      } // end of 우선책

      // No recipe satisfy recommendation algorithm
      else{
        connection.query('SELECT recipe_id FROM authentic_ingredient WHERE item_id = ?', [good], function(error, rows, fields){
          if(error){
            console.log("error ocurred: ", error);
            res.status(400).send("Database error").end();
          }
          else{
            var idList = new Array(); // recipe_id list
            var result = new Object();
            for(var j=0;j<rows.length;j++){
              for(var key in rows[j])
                idList.push(rows[j]["recipe_id"]);
            }
            console.log(idList);

            // get recipe summary
            connection.query('SELECT * FROM authentic_recipe WHERE recipe_id IN (?)', [idList], function(error, rows, fields) {
              if(error){
                console.log('error ocurred: ', error);
                res.status(400).send('Database error').end();
              }
              else {
                var recipeList = new Array();
                for(var i=0;i<rows.length;i++){
                  var recipe = new Object(); // each recipe's info.
                  var items = new Array(); // array of recipes
                  for(var key in rows[i]){
                    switch(key){
                      case "recipe_id" :
                      recipe.recipe_id = rows[i][key];
                      break;
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
                        if(rows[i][key]) {
                          items.push(rows[i][key]);
                        }
                        break;
                      case "item2" :
                        if(rows[i][key]) {
                          items.push(rows[i][key]);
                        }
                        break;
                      case "item3" :
                        if(rows[i][key]) {
                          items.push(rows[i][key]);
                        }
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
                    recipe.items = items;
                  }
                  recipeList.push(recipe);
                }
                result.recipe_main = recipeList;
                console.log(result);
                res.status(200).json(result).end();
              }
            }); // get main info.
          }
        });
      } // end of 차선책
    }
  });
}


// Send detail information of selected recipe
exports.recipe_detail = function (req, res) {
  console.log("Recipe detailed info.");
  // get credit
  connection.query('SELECT credit FROM authentic_recipe WHERE recipe_id = ?', req.query.recipe_id, function(error, rows, fields) {
    var info = new Object();
    if(error){
      console.log("error ocurred: ", error);
      res.status(400).send('Database error').end();
    }
    else{
      info.like = rows[0]["credit"];

      // get ingredients
      connection.query('SELECT item_id FROM authentic_ingredient WHERE recipe_id = ?', req.query.recipe_id, function(error, rows, fields){
          if(error){
            console.log("error ocurred: ", error);
            res.status(400).send('Database error').end();
          }
          else {
            var itemList = new Array();
            for(var i=0;i<rows.length;i++){
              itemList.push(rows[i]["item_id"]);
            }
            info.items = itemList;

            // get description
            connection.query('SELECT description, img_src FROM recipe_detail WHERE recipe_id = ? order by procedure_num', req.query.recipe_id, function(error, rows, fields){
              if(error){
                console.log("error ocurred: ", error);
                res.status(400).send('Database error').end();
              }
              else {
                var descList = new Array();
                for(var j=0;j<rows.length;j++){
                var desc = new Object();
                  for(var key in rows[j]){
                    if(key == "description")
                      desc.description = rows[j][key];
                    if(key == "img_src")
                      desc.image = rows[j][key];
                  }
                  descList.push(desc);
                }
                info.steps = descList;

                // get credit boolean
                connection.query('SELECT count(*) as bool FROM recipe_credit WHERE recipe_id = ? AND user_id = ?', [req.query.recipe_id, req.query.username], function(error, rows, fields){
                  if(error){
                    console.log("error ocurred: ", error);
                    res.status(400).send("Database error").end();
                  }
                  else {
                    if(rows[0]["bool"] == 0){
                      info.liked = 0;
                    }
                    else {
                      info.liked = 1;
                    }
                    console.log(info);

                    res.status(200).json(info).end();
                  }
                }); // end of get credit boolean
              }
            }); // end of get description
          }
      }); // end of get ingredients
    }
  }); // end of get credit number
}
