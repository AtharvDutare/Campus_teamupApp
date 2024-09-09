package com.example.campusteamup.MyModels;

import java.util.List;

public class Team_Details_List_Model {
    List<Team_Members_Model>totalMember;

    public Team_Details_List_Model() {
    }

    public Team_Details_List_Model(List<Team_Members_Model> totalMember) {
        this.totalMember = totalMember;
    }

    public List<Team_Members_Model> getTotalMember() {
        return totalMember;
    }

    public void setTotalMember(List<Team_Members_Model> totalMember) {
        this.totalMember = totalMember;
    }
}
