package com.agiliztech.musicescape.models.apimodels;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Asif on 18-08-2016.
 */
public class ErrorObject implements Parcelable {
    @SerializedName("errorCode")
    String errorCode;
    @SerializedName("errorMsg")
    String errorMsg;

    protected ErrorObject(Parcel in) {
        errorCode = in.readString();
        errorMsg = in.readString();
    }

    public static final Creator<ErrorObject> CREATOR = new Creator<ErrorObject>() {
        @Override
        public ErrorObject createFromParcel(Parcel in) {
            return new ErrorObject(in);
        }

        @Override
        public ErrorObject[] newArray(int size) {
            return new ErrorObject[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(errorCode);
        dest.writeString(errorMsg);
    }

//    public String getErrorCode() {
//        return errorCode;
//    }
//
//    public void setErrorCode(String errorCode) {
//        this.errorCode = errorCode;
//    }
//
//    public String getErrorMsg() {
//        return errorMsg;
//    }
//
//    public void setErrorMsg(String errorMsg) {
//        this.errorMsg = errorMsg;
//    }
}
