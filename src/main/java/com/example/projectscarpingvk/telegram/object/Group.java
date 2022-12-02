package com.example.projectscarpingvk.telegram.object;

public class Group {

    private int idGroup;
    private String titleGroup;
    private String domainGroup;

    private String countMembers;

    public Group(int idGroup, String titleGroup, String domainGroup, String countMembers){
        this.idGroup = idGroup;
        this.titleGroup = titleGroup;
        this.domainGroup = domainGroup;
        this.countMembers = countMembers;
    }

    public int getIdGroup() {
        return idGroup;
    }

    public String getTitleGroup() {
        return titleGroup;
    }

    public String getDomainGroup() {
        return domainGroup;
    }

    public String getCountMembers() {
        return countMembers;
    }

    @Override
    public String toString() {
        return "Group{" +
                "idGroup=" + idGroup +
                ", titleGroup='" + titleGroup + '\'' +
                ", domainGroup='" + domainGroup + '\'' +
                '}';
    }
}
