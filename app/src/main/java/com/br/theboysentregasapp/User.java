package com.br.theboysentregasapp;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    private String uuid;
    private String username;
    private String profileUrl;
    private String token;
    private boolean online;
    private String occupation;

    public User() {

    }

    public User(String uuid, String username, String profileUrl, String occupation) {
        this.uuid = uuid;
        this.username = username;
        this.profileUrl = profileUrl;
        this.occupation = occupation;
    }

    protected User(Parcel in) {
        uuid = in.readString();
        username = in.readString();
        profileUrl = in.readString();
        token = in.readString();
        online = in.readInt() == 1;
        occupation = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getToken() {
        return token;
    }

    public boolean isOnline() {
        return online;
    }

    public String getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uuid);
        parcel.writeString(username);
        parcel.writeString(profileUrl);
        parcel.writeString(token);
        parcel.writeInt(online ? 1 : 0);
        parcel.writeString(occupation);
    }
}
