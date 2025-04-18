package com.example.androidgameproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

public class CallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
            // Phone is ringing (incoming call)
            pauseGame(context);
        } else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
            // Call is answered (call is active)
        } else if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
            // Call has ended or no call (idle state)
            resumeGame(context);
        }
    }

    private void pauseGame(Context context) {
        if (context instanceof FrameActivity) {
            ((FrameActivity) context).pauseGame();
        }
    }

    private void resumeGame(Context context) {
        if (context instanceof FrameActivity) {
            ((FrameActivity) context).resumeGame();
        }
    }

}

