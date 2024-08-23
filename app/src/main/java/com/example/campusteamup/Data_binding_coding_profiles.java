package com.example.campusteamup;

public class Data_binding_coding_profiles {
    String githubUrl , leetcodeUrl , gfgUrl , codeforcesurl , codechefUrl ;

    public Data_binding_coding_profiles() {
    }

    public Data_binding_coding_profiles(String githubUrl, String leetcodeUrl, String gfgUrl, String codeforcesurl, String codechefUrl) {
        this.githubUrl = githubUrl;
        this.leetcodeUrl = leetcodeUrl;
        this.gfgUrl = gfgUrl;
        this.codeforcesurl = codeforcesurl;
        this.codechefUrl = codechefUrl;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getLeetcodeUrl() {
        return leetcodeUrl;
    }

    public void setLeetcodeUrl(String leetcodeUrl) {
        this.leetcodeUrl = leetcodeUrl;
    }

    public String getGfgUrl() {
        return gfgUrl;
    }

    public void setGfgUrl(String gfgUrl) {
        this.gfgUrl = gfgUrl;
    }

    public String getCodeforcesurl() {
        return codeforcesurl;
    }

    public void setCodeforcesurl(String codeforcesurl) {
        this.codeforcesurl = codeforcesurl;
    }

    public String getCodechefUrl() {
        return codechefUrl;
    }

    public void setCodechefUrl(String codechefUrl) {
        this.codechefUrl = codechefUrl;
    }
}
