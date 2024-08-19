package com.example.campusteamup.MyModels;

import com.example.campusteamup.MyAdapters.VacancyListAdapter;
import com.google.firebase.Timestamp;

public class VacancyModel {
    String  postedBy , teamName ,  roleLookingFor , hackathonName;
    long timestamp ;
    public VacancyModel(){

    }

    public VacancyModel(String postedBy, String teamName, String roleLookingFor, String hackathonName, long timestamp) {
        this.postedBy = postedBy;
        this.teamName = teamName;
        this.roleLookingFor = roleLookingFor;
        this.hackathonName = hackathonName;
        this.timestamp = timestamp;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getRoleLookingFor() {
        return roleLookingFor;
    }

    public void setRoleLookingFor(String roleLookingFor) {
        this.roleLookingFor = roleLookingFor;
    }

    public String getHackathonName() {
        return hackathonName;
    }

    public void setHackathonName(String hackathonName) {
        this.hackathonName = hackathonName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
