package com.amupys.myinvoidlibrary;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;

public class InvoidVerification {
    public Activity activity;
    private Class nextActivity;

    public void verify(Activity activity, Class nextActivity){
        this.activity=activity;
        this.nextActivity = nextActivity;

        activity.startActivity(new Intent(activity, VerificationActivity.class).putExtra("activity", nextActivity));
    }
}
