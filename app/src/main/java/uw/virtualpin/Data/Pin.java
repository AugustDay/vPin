package uw.virtualpin.Data;

/**
 * Created by Tyler on 11/19/2016.
 */

public class Pin {

    private String id;
    private String message;
    private String userName;
    private double latitude;
    private double longitude;
    private String encodedImage;
    private int upvotes;
    private int downvotes;
    private int views;
    private int score;

    public Pin(String userName, double latitude, double longitude, String message, String encodedImage) {
        this.message = message;
        this.userName = userName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.encodedImage = encodedImage;
    }

    public Pin(String userName, double latitude, double longitude
            , String message, String encodedImage, int upvotes, int downvotes, int views) {

        this(userName, latitude, longitude, message, encodedImage);
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.views = views;
        this.score = generateScore();
    }

    public int generateScore() {
        int viewScore = 0;
        if(views >= 0) {
            viewScore = views/5;
        }
        return viewScore + upvotes + downvotes;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    public int getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(int downvotes) {
        this.downvotes = downvotes;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getEncodedImage() {
        return encodedImage;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEncodedImage(String encodedImage) {
        this.encodedImage = encodedImage;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
