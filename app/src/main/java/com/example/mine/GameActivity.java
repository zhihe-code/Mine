package com.example.mine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {
    MineSweepingView mineSweepingView;
    TextView timeView,num_view;
    Timer timer;

    private MyDatabaseHelper dbHelper;
    int time = 0;
    String name;

    private static final int START = 0x123;
    private static final int END = 0x124;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            if(msg.what == START){
                time++;
            }else if(msg.what == END){
                time = 0;
            }
            timeView.setText(time+"S");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        //        全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mineSweepingView = (MineSweepingView)findViewById(R.id.MineView);
        timeView = (TextView)findViewById(R.id.time_view);
        num_view = (TextView)findViewById(R.id.num_view);
        dbHelper=new MyDatabaseHelper(this,"ScoreList.db",null,1);
        mineSweepingView.setSignal(new MineSweepingView.Signal() {
            @Override
            public void onStart() {
                if(mineSweepingView.getStart()){
                    timer = new Timer();
                    timer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            handler.sendEmptyMessage(START);
                        }
                    },1000,1000);

                }
            }

            @Override
            public void onFinish() {
                timer.cancel();
                handler.sendEmptyMessage(END);
                time = 0;
            }

            @Override
            public void onFlag(int num) {
                num_view.setText(num+"");
            }

            @Override
            public void onWin(String nam) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                name=nam;
                values.put("player",name);
                values.put("score",time);
                db.insert("Score",null,values);
            }

            @Override
            public void exitGame() {
                Intent intent = new Intent(GameActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

    }
}
