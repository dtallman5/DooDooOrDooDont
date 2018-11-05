package doodoo.doodooordoodont;


import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by David on 2/11/2018.
 */

public class Restroom {
    private String UID;

    private String name;

    private double mAvgRating;
    private int mNumRatings;
    private String mNumStalls;
    private double fAvgRating;
    private int fNumRatings;
    private String fNumStalls;
    private double lat,lon;
    private FirebaseFirestore db;
    private static String TAG = "Restroom";

    public static boolean menDisplayed;

    public Restroom(){
        menDisplayed=true;

    }

    public Restroom(String UID,boolean marker) {
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("restrooms").document(UID);
        docRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            setMarkerData(document.getData());
                            setUID(document.getId());
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
        if (!marker) {
            db.document("/restroomData/" + UID)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                setOtherData(document.getData());
                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });
        }
        menDisplayed = true;
    }

    public Restroom (String UID, Map map){
        this.UID = UID;
        setMarkerData(map);
    }

    private void setMarkerData(Map map){
        name = (String) map.get("name");
        mAvgRating = (double) map.get("mAvgRating");
        mNumRatings = (int) ((long) map.get("mNumRatings"));
        fAvgRating = (double) map.get("fAvgRating");
        fNumRatings = (int) ((long)map.get("fNumRatings"));
        lat = (double) map.get("lat");
        lon = (double) map.get("lon");
        menDisplayed = true;
    }

    public void setOtherData(Map map){
        mNumStalls = (String) map.get("mNumStalls");
        fNumStalls = (String) map.get("fNumStalls");
    }

    public void setName(String name) { this.name = name; }

    public String getmNumStalls() { return mNumStalls; }

    public void setmNumStalls(String mNumStalls) { this.mNumStalls = mNumStalls; }

    public String getfNumStalls() { return fNumStalls; }

    public void setfNumStalls(String fNumStalls) { this.fNumStalls = fNumStalls; }

    public void setRatings(float mAvgRating, int mNumRatings, float fAvgRating, int fNumRatings) {
        this.fNumRatings = fNumRatings;
        this.fAvgRating = fAvgRating;
        this.mNumRatings = mNumRatings;

        this.mAvgRating = mAvgRating;
    }

    public void setUID(String UID) { this.UID = UID; }

    public double getmAvgRating() { return mAvgRating; }

    public int getmNumRatings() { return mNumRatings; }

    public double getfAvgRating() { return fAvgRating; }

    public int getfNumRatings() { return fNumRatings; }

    public String getUID() { return UID; }

    public String getName() { return name; }

    public static boolean isMenDisplayed() { return menDisplayed; }

    public static void setMenDisplayed(boolean bool) { menDisplayed = bool; }

    public void setLocation(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() { return lat; }

    public double getLon() { return lon; }

    public Map toMap() {

        Map<String, Object> map  = new HashMap<>();

        map.put("name", name);
        map.put("mAvgRating", mAvgRating);
        map.put("mNumRatings", mNumRatings);
        map.put("fAvgRating", fAvgRating);
        map.put("fNumRatings", fNumRatings);
        map.put("lat",lat);
        map.put("lon",lon);

        return map;
    }

    public Map extractData(){
        Map<String, Object> map  = new HashMap<>();
        map.put("mNumStalls", mNumStalls);
        map.put("fNumStalls", fNumStalls);
        return map;
    }
}
