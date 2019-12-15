package com.example.mine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends BaseAdapter {
    Context mContent;
    ArrayList<Score> list;
    private LayoutInflater inflater;
    public MyAdapter(Context context,ArrayList list){
        this.mContent = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public Score getItem(int position){
        return list.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if(view==null){
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.score_list, null);
            holder.number = (TextView) view.findViewById(R.id.textView_number);
            holder.player = (TextView) view.findViewById(R.id.textView_name);
            holder.time = (TextView) view.findViewById(R.id.textView_time);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        holder.player.setText(list.get(i).getName().trim());
        holder.time.setText(list.get(i).getTime());
        return view;



    }
    class ViewHolder {
        TextView number;// 名次
        TextView player;// 玩家
        TextView time;// 时间
    }
}
