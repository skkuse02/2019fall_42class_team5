package com.example.se_team5;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.se_team5.item.Item;
import com.example.se_team5.item.ItemListViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class BrowseActivity extends AppCompatActivity {
    private WebView mWebView; // 웹뷰 선언
    private WebSettings mWebSettings; //웹뷰세팅
    private ArrayList<Item> ITEM_LIST;
    private ArrayList<Item> AllItems_;


    ListView listview ;
    ItemListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        SharedPreferences pref = getSharedPreferences("userFile", MODE_PRIVATE);
        AllItems_ = Item.gsonParsing(pref.getString("allItems",""));
        ITEM_LIST = jsonParsing(pref.getString("userBasket",""));

        listview = findViewById(R.id.memo);
        adapter = new ItemListViewAdapter(ITEM_LIST, this) ;
        listview.setAdapter(adapter);

        mWebView = findViewById(R.id.webView);

        mWebView.setWebViewClient(new WebViewClient());
        mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setSupportMultipleWindows(false);
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setSupportZoom(false);
        mWebSettings.setBuiltInZoomControls(false);
        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebSettings.setDomStorageEnabled(true);

        mWebView.loadUrl("https://www.kurly.com");
    }

    private ArrayList<Item> jsonParsing(String json) {
        SharedPreferences sp = getSharedPreferences("userFile", Context.MODE_PRIVATE);
        ArrayList<Item> AllItems_ = Item.gsonParsing(sp.getString("allItems",""));
        try{
            JSONObject jsonObject = new JSONObject(json);
            JSONArray ItemArray = jsonObject.getJSONArray("items");

            ArrayList<Item> li = new ArrayList<Item>();

            for(int i=0; i<ItemArray.length(); i++)
                li.add(AllItems_.get((int)ItemArray.get(i)-1));

            return li;
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}