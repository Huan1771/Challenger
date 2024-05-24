package com.challenger.app.Model;

public class Challenge {

    private String id;
    private String startDate;
    private String endDate;
    private String description;
    private String name;
    private int imageResource;
    private String category;

    private String megszerezhetoPont;

    private int participant;

    public String getMegszerezhetoPont() {
        return megszerezhetoPont;
    }

    public void setMegszerezhetoPont(String megszerezhetoPont) {
        this.megszerezhetoPont = megszerezhetoPont;
    }




    public Challenge(String startDate, String endDate, String description, String name, int imageResource, String megszerezhetoPont, String category,int participant) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.name = name;
        this.imageResource = imageResource;
        this.megszerezhetoPont = megszerezhetoPont;
        this.category = category;
        this.participant = participant;
    }
    public Challenge() {
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public int getParticipant() {
        return participant;
    }

    public void setParticipant(int participant) {
        this.participant = participant;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
