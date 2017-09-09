package com.example.zhaolexi.horizontalscrollviewex;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ZHAOLEXI on 2017/9/4.
 */

public class ListViewAdapter extends BaseAdapter {

    private ArrayList<String> data;

    public ListViewAdapter(ArrayList<String> data) {
        this.data=data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView != null) {
            vh = (ViewHolder) convertView.getTag();
        }else{
            vh=new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_list_item, parent, false);
            vh.tv = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(vh);
        }
        vh.tv.setText(data.get(position));
        return convertView;
    }

    class ViewHolder{
        TextView tv;
    }
}
