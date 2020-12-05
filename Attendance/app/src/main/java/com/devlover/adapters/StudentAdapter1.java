package com.devlover.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.devlover.attendance.R;
import com.devlover.classes.StudentsList;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StudentAdapter1 extends ArrayAdapter<StudentsList> {

    List<StudentsList> dataSett;
    ArrayList<StudentsList> MyList;
    Context context;
    LayoutInflater layoutInflater;

    public StudentAdapter1(Context context,List<StudentsList> MyList){
        super(context, R.layout.student_adapter1,MyList);
        this.dataSett = MyList;
        this.MyList = new ArrayList<StudentsList>();
        this.MyList.addAll(MyList);
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void filter(String toString) {
        toString = toString.toLowerCase(Locale.getDefault());
        dataSett.clear();
        if(toString.length() == 0){
            dataSett.addAll(MyList);
        }else{
            for (StudentsList model : MyList){
                if(model.getStudentName().toLowerCase(Locale.getDefault()).contains(toString)){
                    dataSett.add(model);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class Viewholder{;
        TextView StudentView;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final StudentsList clist = getItem(position);
        StudentAdapter1.Viewholder viewholder;
        final  View result;
        if(convertView == null) {
            viewholder = new StudentAdapter1.Viewholder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.student_adapter1, parent, false);
            viewholder.StudentView = (TextView) convertView.findViewById(R.id.stuName1);
            convertView.setTag(viewholder);
        }else{
            viewholder = (StudentAdapter1.Viewholder)convertView.getTag();
        }
        viewholder.StudentView.setText(clist.getStudentName());
        return convertView;
    }


}
