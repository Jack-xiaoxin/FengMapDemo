package com.example.fengmapdemo;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.fengmapdemo.utils.AnalysisUtils;
import com.fengmap.android.analysis.search.FMSearchAnalyser;
import com.fengmap.android.exception.FMObjectException;
import com.fengmap.android.map.marker.FMModel;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import static com.example.fengmapdemo.MainActivity.mFMMap;
import static com.example.fengmapdemo.MainActivity.mSearchAnalyser;

public class SearchActivity extends AppCompatActivity{

    ArrayList<FMModel> searchList;
    ListView mListView;
    ArrayList<String> displayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // 初始化Toolbar控件
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("查询地图信息");
        setSupportActionBar(toolbar);

        EditText keywordText = (EditText) findViewById(R.id.keyword_text);


        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("search", searchList.get(i).getName());
//                FMModel model = searchList.get(i);
//                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
//                intent.putExtra("selectedModel", new model);
//                startActivity(intent);
            }
        });

        try {
            mSearchAnalyser = FMSearchAnalyser.getFMSearchAnalyserById("1495596552216612865");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (FMObjectException e) {
            e.printStackTrace();
        }

        Button searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayList.clear();
                String keyword = keywordText.getText().toString();
                //搜索请求
                searchList = AnalysisUtils.queryModelByKeyword(mFMMap, mSearchAnalyser, keyword);

                for (FMModel model : searchList) {
                    displayList.add(model.getName() + "  " + model.getGroupId() + "F");
                }

                ArrayAdapter<String> adapter=new ArrayAdapter<String>(SearchActivity.this,android.R.layout.simple_list_item_1,displayList);
                mListView.setAdapter(adapter);
            }
        });
    }

}