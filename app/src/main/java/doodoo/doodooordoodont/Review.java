package doodoo.doodooordoodont;

import java.util.Date;

/**
 * Created by David on 4/3/2018.
 */

public class Review {

    private Date date;
    private double rating;
    private User reviewer;
    private String review;
    private int[] reactions;


    public Review(Date date, User user, double rating, String review){
        this.date = date;
        this.reviewer = user;
        this.rating = rating;
        this.review = review;
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
