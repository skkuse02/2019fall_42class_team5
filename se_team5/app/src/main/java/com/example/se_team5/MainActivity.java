package com.example.se_team5;

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
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class MainActivity extends AppCompatActivity {

    public String user_id;
    private final List<String> names = new ArrayList<>();
    private final List<Integer> images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        try {
            // Excel 파일 읽기
            InputStream inputStream = getBaseContext().getResources().getAssets().open("items.xls");
            Workbook workbook = Workbook.getWorkbook(inputStream);

            Sheet sheet = workbook.getSheet(0); // 0번째 sheet 읽기
            int endIdx = sheet.getColumn(1).length - 1;

            // id 0 값 없으므로 채워넣기
            names.add(null);
            images.add(0);

            for (int i = 1; i <= endIdx; i++) {
                // 두번째 열(B)
                String name = sheet.getCell(1, i).getContents();
                names.add(name);
                images.add(getResources().getIdentifier("@drawable/"+"item"+i+".png","drawable",this.getPackageName()));
            }

        } catch (BiffException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        SharedPreferences pref = getSharedPreferences("userFile", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray arr = new JSONArray();

            for (int i = 1; i < names.size(); i++) {
                JSONObject temp = new JSONObject();
                temp.put("name", names.get(i));
                temp.put("image", images.get(i));
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
