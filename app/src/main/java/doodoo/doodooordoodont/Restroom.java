package doodoo.doodooordoodont;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by David on 2/11/2018.
 */

public class Restroom implements Parcelable{
    private String UID;
    private String name;

    private float mAvgRating;
    private int mNumRatings;
    private int mNumStalls;
    private float fAvgRating;
    private int fNumRatings;
    private int fNumStalls;
    private double lat,lon;

    public static boolean menDisplayed;

    public Restroom(String UID, String name) {
        this.UID = UID;
        this.name = name;
        menDisplayed = true;
    }


    public void setRatings(float mAvgRating, int mNumRatings,float fAvgRating, int fNumRatings) {
        this.fNumRatings = fNumRatings;
        this.fAvgRating = fAvgRating;
        this.mNumRatings = mNumRatings;
        this.mAvgRating = mAvgRating;
    }

    public float getmAvgRating() {
        return mAvgRating;
    }

    public int getmNumRatings() {
        return mNumRatings;
    }

    public float getfAvgRating() {
        return fAvgRating;
    }

    public int getfNumRatings() {
        return fNumRatings;
    }

    public String getUID() {
        return UID;
    }

    public String getName() {
        return name;
    }

    public static boolean isMenDisplayed() {
        return menDisplayed;
    }

    public static void setMenDisplayed(boolean bool) {
        menDisplayed = bool;
    }

    public void setLocation(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    protected Restroom(Parcel in) {
        UID = in.readString();
        name = in.readString();
        mAvgRating = in.readFloat();
        mNumRatings = in.readInt();
        mNumStalls = in.readInt();
        fAvgRating = in.readFloat();
        fNumRatings = in.readInt();
        fNumStalls = in.readInt();
        lat = in.readDouble();
        lon = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(UID);
        dest.writeString(name);
        dest.writeFloat(mAvgRating);
        dest.writeInt(mNumRatings);
        dest.writeInt(mNumStalls);
        dest.writeFloat(fAvgRating);
        dest.writeInt(fNumRatings);
        dest.writeInt(fNumStalls);
        dest.writeDouble(lat);
        dest.writeDouble(lon);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Restroom> CREATOR = new Parcelable.Creator<Restroom>() {
        @Override
        public Restroom createFromParcel(Parcel in) {
            return new Restroom(in);
        }

        @Override
        public Restroom[] newArray(int size) {
            return new Restroom[size];
        }
    };
}
