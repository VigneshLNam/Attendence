package com.devlover.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.devlover.attendance.DataBaseHelper;
import com.devlover.attendance.R;
import com.devlover.classes.TableClass;

import java.util.ArrayList;

public class TableAdapter extends ArrayAdapter<TableClass> {
    ArrayList<TableClass> thisList;
    Context context;
    LayoutInflater layoutInflater;
    DataBaseHelper MyDb;
    public TableAdapter(Context context, ArrayList<TableClass> thisList) {
        super(context, R.layout.tablelayout,thisList);
        this.thisList = thisList;
        this.context = context;
        MyDb = new DataBaseHelper(context);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static class Viewholder{
        TextView thisDay;
        TextView thisTime;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TableClass tList = thisList.get(position);
        Viewholder viewholder;
        String Class = MyDb.getClassName(tList.getSl_No());
        if(convertView == null){
            viewholder = new Viewholder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.tablelayout, parent, false);
            viewholder.thisDay = convertView.findViewById(R.id.thisDay);
            viewholder.thisTime = convertView.findViewById(R.id.thisTime);
            convertView.setTag(viewholder);
        }else {
            viewholder = (Viewholder)convertView.getTag();
        }
        if(Class!=""){
            viewholder.thisDay.setText(tList.getDay()+" {Class : "+Class+"}");
        }else {
            viewholder.thisDay.setText(tList.getDay());
        }
        viewholder.thisTime.setText(tList.getTime());
        return convertView;
    }
}


