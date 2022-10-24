package com.adventour.android;

public class PreviousAdventourModel
{
    private String firstLocation;
    private String secondLocation;
    private String thirdLocation;

    public PreviousAdventourModel(String firstLocation, String secondLocation, String thirdLocation)
        {
            this.firstLocation = firstLocation;
            this.secondLocation = secondLocation;
            this.thirdLocation = thirdLocation;

        }

    public String getFirstLocation() {
        return firstLocation;
    }

    public void setFirstLocation(String firstLocation) {
        this.firstLocation = firstLocation;
    }

    public String getSecondLocation() {
        return secondLocation;
    }

    public void setSecondLocation(String secondLocation) {
        this.secondLocation = secondLocation;
    }

    public String getThirdLocation() {
        return thirdLocation;
    }

    public void setThirdLocation(String thirdLocation) {
        this.thirdLocation = thirdLocation;
    }
}
