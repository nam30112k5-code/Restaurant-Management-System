package model;

public class Feedback {

    private int feedbackId;
    private String content;
    private int rating;
    private int guestId;
    private String appointmentId;
    private String guestName;
    private String tableName;

    public Feedback() {
    }

    public Feedback(int feedbackId, String content, int rating, int guestId, String appointmentId) {
        this.feedbackId = feedbackId;
        this.content = content;
        this.rating = rating;
        this.guestId = guestId;
        this.appointmentId = appointmentId;
    }

    public int getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

}
