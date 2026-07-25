package com.example.myfinalproject;

public class PregnancyWeek {
    private int weekNumber;
    private String sizeLabel, development, tipOne, tipTwo, tipThree, sourceUrl;

    public PregnancyWeek(int weekNumber, String sizeLabel, String development, String tipOne, String tipTwo, String tipThree, String sourceUrl) {
        this.weekNumber = weekNumber;
        this.sizeLabel = sizeLabel;
        this.development = development;
        this.tipOne = tipOne;
        this.tipTwo = tipTwo;
        this.tipThree = tipThree;
        this.sourceUrl = sourceUrl;
    }

    public int getWeekNumber() { return weekNumber; }
    public String getSizeLabel() { return sizeLabel; }
    public String getDevelopment() { return development; }
    public String getTipOne() { return tipOne; }
    public String getTipTwo() { return tipTwo; }
    public String getTipThree() { return tipThree; }
    public String getSourceUrl() { return sourceUrl; }
}
