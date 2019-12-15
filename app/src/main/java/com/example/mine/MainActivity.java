package com.example.mine;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Button start_game,score_list,help,exit_game;
    private MediaPlayer music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //        全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        start_game = (Button) findViewById(R.id.start_game);
        score_list = (Button) findViewById(R.id.score_list);
        help = (Button) findViewById(R.id.help);
        exit_game = (Button) findViewById(R.id.exit_game);



        start_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayMusic(R.raw.mainclick);
                Intent intent = new Intent(MainActivity.this,GameActivity.class);
                startActivity(intent);
            }
        });

//        帮助
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(view.getContext())
                        .setCancelable(false)
                        .setTitle("游戏规则")
                        .setMessage("将你认为不是雷的位置全部点开，只留着有雷的位置。")
                        .setPositiveButton("我知道了",null)
                        .create()
                        .show();

            }
        });

//        游戏退出
        exit_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(0);
            }
        });

        score_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionAlertDialog();

            }
        });
    }
    private void PlayMusic(int MusicId) {
        music = MediaPlayer.create(this, MusicId);
        music.start();
    }
    protected void actionAlertDialog(){
        ArrayList<Score> list = new ArrayList<Score>();
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View layout = inflater.inflate(R.layout.score_list, (ViewGroup) findViewById(R.id.layout_score));
        ListView myListView = (ListView) layout.findViewById(R.id.list_view);
        MyAdapter adapter = new MyAdapter(MainActivity.this, list);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
        builder.setView(layout);

        builder.setPositiveButton("确 定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //删除单词
                dialogInterface.cancel();

            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
    }

}
