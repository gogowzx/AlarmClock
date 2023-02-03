package com.boll.alarmclock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import com.boll.alarmclock.utils.Util;

import java.util.ArrayList;

public class ClockAdapter extends BaseAdapter {
    private ArrayList<String> list;
    private Context context;
    private SwitchChangeListener listener;

    public ClockAdapter(Context context,ArrayList<String> list,SwitchChangeListener listener) {
        this.context=context;
        this.list=list;
        this.listener=listener;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            holder=new ViewHolder();//创建静态类对象
            view = LayoutInflater.from(context).inflate(R.layout.alarm_clock_item_layout,null);
            holder.itemTime=view.findViewById(R.id.item_time);
            holder.itemRepeat=view.findViewById(R.id.item_repeat);
            holder.state=view.findViewById(R.id.switch_state);
            view.setTag(holder);//保存静态类对象
        }else {
            holder=(ViewHolder)view.getTag();//获取静态类对象
        }
        boolean isOpen=Boolean.parseBoolean(Util.stringToArray(list.get(i))[8]);
        if (!isOpen){
            holder.itemTime.setTextColor(context.getResources().getColor(R.color.color_3));
            holder.itemRepeat.setTextColor(context.getResources().getColor(R.color.color_3));
        }else {
            holder.itemTime.setTextColor(context.getResources().getColor(R.color.color_1));
            holder.itemRepeat.setTextColor(context.getResources().getColor(R.color.color_2));
        }
        holder.itemTime.setText(Util.stringToArray(list.get(i))[0]);
        String s=Util.dataParseText(list.get(i));
        if ("无".equals(s))
            s="不重复";
        holder.itemRepeat.setText(s);
        holder.state.setChecked(isOpen);
        holder.state.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isPressed()){
                    holder.state.setChecked(b);
                    listener.changeListener(i,b);
                }
            }
        });
        return view;
    }

    static class ViewHolder{
        TextView itemTime;
        TextView itemRepeat;
        SwitchCompat state;
    }

    public interface SwitchChangeListener{
        void changeListener(int position, boolean state);
    }
}
