package com.example.campusteamup.MyModels;

public class College_Details_Model {
    private String year , branch , course;

    public College_Details_Model() {
    }

    public College_Details_Model(String year, String branch, String course) {
        this.year = year;
        this.branch = branch;
        this.course = course;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }
}
