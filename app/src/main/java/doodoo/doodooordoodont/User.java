package doodoo.doodooordoodont;

import android.media.Image;

import java.util.Map;


/**
 * Created by David on 4/3/2018.
 */

public class User {
    private Image pic;
    private String name;
    private int numReviews;
    private String userId;
    private String gender;

    public User(Map<String,Object> map) {
        name = (String) map.get("name");
        numReviews = (int) map.get("numReviews");
        userId = (String) map.get("userId");
        gender = (String) map.get("gender");
    }



    public Image getPic() {
        return pic;
    }

    public String getName() {
        return name;
    }

    public int getNumReviews() {
        return numReviews;
    }

    public String getUserId() {
        return userId;
    }

    public String getGender() {
        return gender;
    }
}
