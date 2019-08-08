package doodoo.doodooordoodont;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by David on 4/3/2018.
 */

public class Rating {
    private String UID; //The uid used by the database
    private Date date;
    private double rating;
    private String reviewer;
    private String review;
    private int[] reactions;

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public Rating(Date date, String user, double rating, String review){
        this.date = date;
        this.reviewer = user;
        this.rating = rating;
        this.review = review;
        reactions = new int[]{0,0,0};
    }

    public Date getDate() { return date; }

    public double getRating() { return rating; }

    public String getReviewer() { return reviewer; }

    public String getReview() { return review; }

    public Rating(Map map){
        this.date = (Date) map.get("date");
        this.reviewer = (String) map.get("user");
        this.rating =  (double) map.get("rating");
        this.review = (String) map.get("review");
        int useful = (int) map.get("useful");
        int funny = (int) map.get("funny");
        int cool = (int) map.get("cool");
        this.reactions = new int[]{useful, funny, cool};
    }

    public Map toMap() {

        Map<String, Object> map  = new HashMap<>();

        map.put("date", date);
        map.put("rating", rating);
        map.put("reviewer", reviewer);
        map.put("review", review);
        map.put("useful", reactions[0]);
        map.put("funny", reactions[1]);
        map.put("cool", reactions[2]);

        return map;
    }


    /**
     * addReaction
     *
     * This method is used to add to the reactions tally. A user is allowed to mark whether or not
     * they think a review is useful, funny, or cool. Returns true if a valid option was selected.
     *
     * @param reaction The string representing the user's reaction
     * @return Returns true if the reaction was successfully stored.
     */
    private boolean addReaction(String reaction){
        switch (reaction){
            case "Useful":
                reactions[0] += 1;
                break;
            case "Funny":
                reactions[1] += 1;
                break;
            case "Cool":
                reactions[2] += 1;
                break;
            default:
                return false;
        }

        return true;
    }
}
