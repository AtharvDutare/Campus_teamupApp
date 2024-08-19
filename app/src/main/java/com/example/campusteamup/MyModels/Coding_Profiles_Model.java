package com.example.campusteamup.MyModels;

public class Coding_Profiles_Model {
    String  githubUrl , leetcodeUrl ,  gfgUrl ,  codeforcesUrl ,  codechefUrl;

    public Coding_Profiles_Model() {
    }

    public Coding_Profiles_Model(String githubUrl , String leetcodeUrl, String gfgUrl, String codeforcesUrl, String codechefUrl) {
        this.githubUrl = githubUrl;
        this.leetcodeUrl = leetcodeUrl;
        this.gfgUrl = gfgUrl;
        this.codeforcesUrl = codeforcesUrl;
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

    public String getCodeforcesUrl() {
        return codeforcesUrl;
    }

    public void setCodeforcesUrl(String codeforcesUrl) {
        this.codeforcesUrl = codeforcesUrl;
    }

    public String getCodechefUrl() {
        return codechefUrl;
    }

    public void setCodechefUrl(String codechefUrl) {
        this.codechefUrl = codechefUrl;
    }
}
