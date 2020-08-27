
package com.example.broadcastreceiver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

public class SoundActivity extends AppCompatActivity {
    //화면에 보여지는 뷰 변수
    ImageView playBtn, stopBtn;
    TextView titleView;
    ProgressBar progressBar;

    //스레드 동작 여부
    boolean runThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound);

        playBtn = (ImageView)findViewById(R.id.play);
        stopBtn = (ImageView)findViewById(R.id.stop);
        progressBar = (ProgressBar)findViewById(R.id.progress);
        titleView = (TextView)findViewById(R.id.title);

        //리시버 등록
        //리시버는 사용하는 반대편에서 등록
        registerReceiver(receiver,
                new IntentFilter(
                        "com.example.PLAY_TO_ACTIVITY"));
        //서비스 시작
        Intent intent = new Intent(
                this, PlayService.class);
        startService(intent);

        //이미지 클릭 처리
        playBtn.setOnClickListener(
                new ImageView.OnClickListener(){

                    @Override
                    public void onClick(View view) {
                        //com.example.PLAY_TO_SERVICE에게 방송
                        Intent intent =
                                new Intent(
                                        "com.example.PLAY_TO_SERVICE");
                        //필요한 데이터 작성
                        intent.putExtra("mode", "start");
                        //방송 시작
                        sendBroadcast(intent);

                        //진행율을 표시하기 위한 스레드 시작
                        runThread = true;
                        ProgressThread thread = new ProgressThread();
                        thread.start();

                        //UI 고려
                        //토글형태로 동작해야 하는 요소가 있다면 각각의 동작을 구분해 줄
                        //필요가 있습니다.
                        //숨기기, 동작하지 않도록 하기, 색상을 변경하기 등이 있습니다.
                        playBtn.setEnabled(false);
                        stopBtn.setEnabled(true);

                    }
                });
        //중지 버튼 클릭했을 때 이벤트 처리
        stopBtn.setOnClickListener(
                new ImageView.OnClickListener(){

                    @Override
                    public void onClick(View view) {
                        //com.example.PLAY_TO_SERVICE에게 방송
                        Intent intent =
                                new Intent(
                                        "com.example.PLAY_TO_SERVICE");
                        //필요한 데이터 작성
                        intent.putExtra("mode", "stop");
                        //방송 시작
                        sendBroadcast(intent);

                        //진행율을 초기
                        runThread = false;
                        progressBar.setProgress(0);

                        //UI 고려
                        //토글형태로 동작해야 하는 요소가 있다면 각각의 동작을 구분해 줄
                        //필요가 있습니다.
                        //숨기기, 동작하지 않도록 하기, 색상을 변경하기 등이 있습니다.
                        playBtn.setEnabled(true);
                        stopBtn.setEnabled(false);

                    }
                });
    }

    //프로그래스 바의 진행율을 표시할 스레드를 생성
    class ProgressThread extends Thread{
        //스레드로 동작할 메소드
        public void run(){
            while(runThread){
                progressBar.incrementProgressBy(
                        1000);
                SystemClock.sleep(1000);
                if(progressBar.getProgress() ==
                        progressBar.getMax()){
                    runThread = false;
                }
            }
        }
    }

    //Service 와 통신하기 위한 Broadcast Receiver 생성
    //상대방이 mode에 start 와 stop 이라는 글자를 전송해주고
    //duration에 전체 재생 시간을 전송해줍니다.
    BroadcastReceiver receiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(
                        Context context, Intent intent) {
                    String mode =
                            intent.getStringExtra("mode");
                    if(mode != null){
                        if("start".equals(mode)){
                            //duration의 값을 정수로 가져오고 없으면 0
                            //Map은 없는 값을 가져오면 null 입니다.
                            int duration = intent.getIntExtra(
                                    "duration",
                                    0);
                            progressBar.setMax(duration);
                            progressBar.setProgress(0);

                        }else if("stop".equals(mode)){
                            runThread = false;
                        }else if("restart".equals(mode)){
                            //재시작 하는 경우 전체 재생 시간과 현재 재생 위치를
                            //가져와서 프로그래스 바에 설정
                            int duration =
                                    intent.getIntExtra(
                                            "duration",
                                            0);
                            int current =
                                    intent.getIntExtra(
                                            "current", 0);
                            progressBar.setMax(duration);
                            progressBar.setProgress(current);
                            //스레드는 재시작이 안되므로 새로 생성해서 시작
                            runThread = true;
                            ProgressThread thread = new ProgressThread();
                            thread.start();
                            playBtn.setEnabled(false);
                            stopBtn.setEnabled(true);
                        }
                    }
                }
            };
}