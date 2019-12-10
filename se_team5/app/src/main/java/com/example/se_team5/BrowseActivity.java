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
    private WebView mWebView;             // 웹뷰 선언
    private WebSettings mWebSettings;    // 웹뷰 세팅
    private ArrayList<Item> ITEM_LIST;   // 사용자의 장바구니에 있는 아이템 리스트

    ListView listview ;             // 리스트 뷰 선언
    ItemListViewAdapter adapter;    // 리스트 뷰 어댑터 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        /*로컬에서 사용자 장바구니에 있는 아이템 리스트를 불러와 초기화*/
        SharedPreferences pref = getSharedPreferences("userFile", MODE_PRIVATE);
        ITEM_LIST = Item.jsonParsing(this, pref.getString("userBasket",""));

        /*리스트 뷰와 어댑터를 지정*/
        listview = findViewById(R.id.memo);
        adapter = new ItemListViewAdapter(ITEM_LIST, this) ;
        listview.setAdapter(adapter);

        /*웹뷰를 지정*/
        mWebView = findViewById(R.id.webView);

        /*웹뷰에 대한 세팅 설정*/
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

        /*웹뷰로 브라우징 할 url 설정*/
        mWebView.loadUrl("https://www.kurly.com");
    }
}