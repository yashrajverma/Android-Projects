package com.yashraj.BlogIt.Model;

public class Blog {
    public String title;
    public String description;
    public String dateadded;
    public String userID;
    public String image;

    public Blog() {
    }

    public Blog(String title, String description, String dateadded, String userID, String image) {
        this.title = title;
        this.description = description;
        this.dateadded = dateadded;
        this.userID = userID;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateadded() {
        return dateadded;
    }

    public void setDateadded(String dateadded) {
        this.dateadded = dateadded;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
