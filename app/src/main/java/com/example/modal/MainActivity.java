package com.example.modal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.modal.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    SwipeRefreshLayout swipeLayout;
    //ListView 나 RecyclerView의 데이터를 업데이트할 의도가 있는 경우
    //Adapter 와 데이터는 인스턴스 변수로 같이 선언
    ListView listView;
    ArrayAdapter<String> adapter;
    ArrayList<String> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //데이터 생성
        list = new ArrayList<>();
        list.add("java.lang");
        list.add("java.util");
        list.add("java.io");
        list.add("java.net");

        //어댑터 생성
        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                list);
        //ListView 에 설정
        //최근의 안드로이드 스튜디오에서는 강제 형 변환을 하지 않아도 됩니다.
        //코드 최적화를 이용해서 안드로이드 스튜디오가 자동 형 변환을 수행
        listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);

        swipeLayout =
                (SwipeRefreshLayout)findViewById(
                        R.id.swipeLayout);
        //아래로 swipe 했을 때를 처리하는 리스너를 연결
        swipeLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    //아래로 스와이프 했을 때 호출되는 메소드
                    @Override
                    public void onRefresh() {
                        //데이터를 업데이트하고 ListView 나 RecyclerView를 업데이트
                        //pull to refresh는 앞쪽에 추가하는 것이 일반적
                        list.add(0, "패키지의 개념");
                        list.add("java.sql");
                        adapter.notifyDataSetChanged();
                        //RefreshView를 화면에서 제거
                        swipeLayout.setRefreshing(false);
                    }
                });
    }

    //버튼의 클릭 이벤트 처리를 위하나 메소드
    public void click(View view){
        //방송을 송신
        Intent intent = new Intent();
        intent.setAction("com.example.sendbroadcast");
        intent.addFlags(
                Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(intent);
    }
}