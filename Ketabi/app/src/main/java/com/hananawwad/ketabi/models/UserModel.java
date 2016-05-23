package com.hananawwad.ketabi.models;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author hananawwad
 */
@Setter
@Getter
@ToString
public class UserModel implements Parcelable {

    private String userName;

    private String emailId;

    private String phoneNumber;

    public UserModel(String userName, String emailId, String phoneNumber) {
        this.userName = userName;
        this.emailId = emailId;
        this.phoneNumber = phoneNumber;
    }

    protected UserModel(Parcel in) {
        userName = in.readString();
        emailId = in.readString();
        phoneNumber = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
        dest.writeString(emailId);
        dest.writeString(phoneNumber);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<UserModel> CREATOR = new Parcelable.Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };

}
