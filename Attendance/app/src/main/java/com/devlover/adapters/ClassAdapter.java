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
import com.devlover.classes.ClassList;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ClassAdapter extends ArrayAdapter<ClassList> {

    List<ClassList> dataSett;
    ArrayList<ClassList> MyList;
    Context context;
    LayoutInflater layoutInflater;
    public static ArrayList classList,classListNames;

    public ClassAdapter(Context context,List<ClassList> MyList){
        super(context, R.layout.class_adapter,MyList);
        classList = new ArrayList();
        classListNames = new ArrayList();
        this.dataSett = MyList;
        this.MyList = new ArrayList<ClassList>();
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
            for (ClassList model : MyList){
                if(model.getClassName().toLowerCase(Locale.getDefault()).contains(toString)){
                    dataSett.add(model);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class Viewholder{
        TextView ClassName;
        TextView SectionName;
        TextView TeacherName;
        TextView Subject ;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ClassList clist = getItem(position);
        Viewholder viewholder;
        final  View result;
        if(convertView == null) {
            viewholder = new Viewholder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.class_adapter, parent, false);
            viewholder.ClassName = (TextView) convertView.findViewById(R.id.ClassName);
            viewholder.SectionName = (TextView) convertView.findViewById(R.id.SectionName);
            viewholder.TeacherName = (TextView) convertView.findViewById(R.id.TeacherName);
            viewholder.Subject = (TextView) convertView.findViewById(R.id.SubjectName);
            convertView.setTag(viewholder);
        }else{
            viewholder = (Viewholder)convertView.getTag();
        }
        classList.add(clist.getSl_No());
        classListNames.add(clist.getClassName() + " " + clist.getSection());
        viewholder.ClassName.setText(clist.getClassName());
        viewholder.SectionName.setText(clist.getSection());
        viewholder.TeacherName.setText(clist.getTeacherName());
        viewholder.Subject.setText(clist.getSubject());
        return convertView;
    }
}
