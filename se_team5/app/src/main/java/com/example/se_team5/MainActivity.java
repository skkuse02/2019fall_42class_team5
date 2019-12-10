package com.example.se_team5;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

public class MainActivity extends AppCompatActivity {

    List<String> names = new ArrayList<>();
    List<String> categories = new ArrayList<>();
    List<String> category = new ArrayList<>();
    ArrayList<ArrayList<Integer>> classification = new ArrayList<ArrayList<Integer>>();
    private final List<Integer> images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        SharedPreferences check = getSharedPreferences("userFile", MODE_PRIVATE);
        if(true){//check.getString("allItems",null).length()<1

            try {
                // Excel 파일 읽기
                InputStream inputStream = getBaseContext().getResources().getAssets().open("item.xls");
                Workbook workbook = Workbook.getWorkbook(inputStream);
                WorkbookSettings ws = new WorkbookSettings();
                ws.setEncoding("Cp1252");
                
                Sheet sheet = workbook.getSheet(0); // 0번째 sheet 읽기
                int endIdx = sheet.getColumn(1).length - 1;

                Log.d("name", String.valueOf(endIdx));
                // id 0 값 없으므로 채워넣기
                names.add(null);
                categories.add(null);
                images.add(0);

                for (int i = 1; i <= endIdx; i++) {
                    // 두번째 열(B)
                    String name = sheet.getCell(1, i).getContents();
                    // 세번째 열(C)
                    String category = sheet.getCell(2, i).getContents();
                    names.add(name);
                    categories.add(category);
                    images.add(getResources().getIdentifier("@drawable/"+"item"+String.valueOf(i),"drawable",this.getPackageName()));
                }

            } catch (BiffException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            /*로컬에 아이템관련 정보 저장*/
            SharedPreferences pref = getSharedPreferences("userFile", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            try {
                JSONObject jsonObject = new JSONObject();
                JSONArray arr = new JSONArray();
                JSONObject temp = new JSONObject();

                /* 모든 아이템 저장 */
                for (int i = 1; i < names.size(); i++) {
                    temp = new JSONObject();
                    temp.put("name", names.get(i));
                    temp.put("category", categories.get(i));
                    temp.put("image", images.get(i));
                    temp.put("id", i);
                    arr.put(temp);

                    if(category.indexOf(categories.get(i))>=0){
                        continue;
                    }else{
                        category.add(categories.get(i));
                    }
                }

                jsonObject.put("items", arr);
                editor.putString("allItems", jsonObject.toString());
                editor.commit();

                /* 카테고리 명 저장 */
                temp = new JSONObject();
                arr = new JSONArray();

                for(int i=0;i<category.size();i++){
                    arr.put(category.get(i));

                }
                temp.put("categories", arr);

                editor.putString("category", temp.toString());
                editor.commit();

                /* 카테고리별 아이템 분류*/
                for(int i=0;i<category.size();i++){
                    ArrayList<Integer> tempClass = new ArrayList<>();
                    for (int j = 1; j < categories.size(); j++){
                        if(category.indexOf(categories.get(j))==i)
                            tempClass.add(j);
                    }
                    classification.add(tempClass);
                }

                temp = new JSONObject();
                arr = new JSONArray();

                for(int i=0;i<classification.size();i++){
                    JSONObject temp0 = new JSONObject();
                    JSONArray arr0 = new JSONArray();

                    for(int j=0;j<classification.get(i).size();j++){
                        arr0.put(classification.get(i).get(j));
                    }

                    temp0.put("category", arr0);
                    arr.put(temp0);
                }
                temp.put("categories", arr);

                editor.putString("category", temp.toString());
                editor.commit();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /* 하단 네비게이션 바 생성*/
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_refrigerator, R.id.navigation_recipe, R.id.navigation_basket)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        /* 로그아웃 기능 */
        Toast toast = Toast.makeText(getApplicationContext(),"", Toast.LENGTH_LONG);

        switch(item.getItemId())
        {
            case R.id.menu_logout:
                toast.setText("로그아웃 되었습니다.");

                Intent intent = new Intent(getApplicationContext(), JoinLoginActivity.class);
                startActivity(intent);

                /*로그아웃 시 로컬에 저장된 로그인 데이터 제거*/
                SharedPreferences pref = getSharedPreferences("userFile", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.remove("username");
                editor.remove("password");
                editor.commit();

                finish();
                break;
        }

        toast.show();

        return super.onOptionsItemSelected(item);
    }
}
