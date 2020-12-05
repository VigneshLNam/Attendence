package com.devlover.classes;

public class ClassList {
    int Sl_No;
    String className;
    String Section;
    String teacherName;
    String Subject;

    public int getSl_No() {
        return Sl_No;
    }

    public void setSl_No(int sl_No) {
        Sl_No = sl_No;
    }

    public String getClassName() {
        return className;
    }

    public void setClass(String aClass) {
        className = aClass;
    }

    public String getSection() {
        return Section;
    }

    public void setSection(String section) {
        Section = section;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }
}
