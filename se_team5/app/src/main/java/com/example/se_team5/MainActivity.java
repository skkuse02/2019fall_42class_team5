package com.example.se_team5;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class MainActivity extends AppCompatActivity {

    public String user_id;
    private final String[] names = {"계란", "김", "닭고기", "대파","양파", "삼겹살", "쪽파", "소고기"};
    private final int[] images = {R.drawable.egg, R.drawable.gim, R.drawable.chicken_breast, R.drawable.green_onion,
                                  R.drawable.onion,R.drawable.pork_belly,R.drawable.sect,R.drawable.soup_beef};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        SharedPreferences pref = getSharedPreferences("userFile", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        try{
            JSONObject jsonObject = new JSONObject();
            JSONArray arr = new JSONArray();

            for(int i=0;i<8;i++){
                JSONObject temp = new JSONObject();
                temp.put("name", names[i]);
                temp.put("image", images[i]);
                temp.put("id", i);
                arr.put(temp);
            }

            jsonObject.put("items", arr);
            Log.d("name", jsonObject.toString());
            editor.putString("allItems", jsonObject.toString());
            editor.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }


        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_refrigerator, R.id.navigation_recipe, R.id.navigation_basket)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }
}