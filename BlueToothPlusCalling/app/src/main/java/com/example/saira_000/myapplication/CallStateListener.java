package com.example.saira_000.myapplication;

/**
 * Created by stefan on 19.01.2016.
 */

import android.app.Activity;
import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

/**
 * Listener to detect incoming calls.
 */
class CallStateListener extends PhoneStateListener {
    Context ApplicationContext;
    Activity mActivity;
    public CallStateListener(Activity activity) {
        //
        mActivity = activity;

    }
    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                // somone is calling
                Toast.makeText(mActivity,
                        "Incoming: " + incomingNumber,
                        Toast.LENGTH_SHORT).show();

                MainActivity.beingCalled = true;
                break;
            default:
                MainActivity.beingCalled = false;
                break;
        }
    }
}