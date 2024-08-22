package com.example.campusteamup;

public class Data_binding_college_details {
    String branch , year ;

    public Data_binding_college_details() {
    }

    public Data_binding_college_details(String branch, String year) {
        this.branch = branch;
        this.year = year;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
