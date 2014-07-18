package org.gringene.concentricclock;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;

public class ClockActivity extends Activity {

    ConcentricClock mClock;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mClock = new ConcentricClock(this);
        setContentView(mClock);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("org.gringene.concentricclock", "Resuming...");
        mClock.startTick();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("org.gringene.concentricclock", "Pausing...");
        mClock.stopTick();
    }
}