package org.gringene.concentricclock;

import android.util.Log;

import java.util.TimerTask;

/**
 * Created by gringer on 17/07/14.
 */
public class ClockTicker implements Runnable {
//    boolean ticking = true;
    int tickCount = 0;
    private ConcentricClock mClock;
    public ClockTicker(ConcentricClock tClock) {
        mClock = tClock;
    }

    public void run() {
        if(tickCount == 0) {
            Log.d("org.gringene.concentricclock", "tick");
        }
        tickCount = (tickCount + 1) % 20;
//        if(ticking) {
            mClock.updateTime();
//        }
    }

    public void pause(){
//        ticking = false;
    }

    public void resume(){
//        ticking = true;
    }
}
