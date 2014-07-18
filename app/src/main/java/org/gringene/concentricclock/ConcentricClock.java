package org.gringene.concentricclock;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ConcentricClock extends View {

    private final Paint brushes = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Time mCalendar;
    private float mHours;
    private float mMinutes;
    private float mSeconds;

    private float centreX;
    private float centreY;
    private float bandWidth;

    private Bitmap backing;
    private Canvas painting;
    boolean ticking = false;

    private ScheduledThreadPoolExecutor tickerTimer;
    ScheduledFuture clockTicker = null;


    private boolean started = false;

    public ConcentricClock(Context context) {
        super(context);
        init();
    }

    public ConcentricClock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mCalendar = new Time();
        tickerTimer = new ScheduledThreadPoolExecutor(1);
        startTick();
    }

    protected void updateTime(){
        mCalendar.setToNow();
        int hour = mCalendar.hour;
        int min = mCalendar.minute;
        int secInt = mCalendar.second;
        float mSecFrac = (System.currentTimeMillis() % 1000) / 1000f;
        mSecFrac = (float) (1 - Math.sin((0.5f - mSecFrac) * Math.PI))/2;
        mSeconds = secInt + mSecFrac;
        mMinutes = min + mSeconds/60;
        mHours = hour + mMinutes/60;
        //Log.d("org.gringene.concentricclock",String.format("Updating time, now %02d:%02d:%02.2f", hour, min, mSeconds));
        if(started) {
            drawClock(painting);
            this.postInvalidate();
        }
    }

    public void drawClock(Canvas tPainting){
        float hourAng = (mHours * 15); // 360/24
        float minAng = (mMinutes * 6); // 360/60
        float secAng = (mSeconds * 6) + ((int)mMinutes % 2) * 360; // 360/60
        tPainting.drawColor(Color.WHITE); // fill in background
        brushes.setColor(Color.GRAY);
        brushes.setStyle(Paint.Style.FILL);
        tPainting.drawCircle(centreX, centreY, bandWidth / 2, brushes);
        brushes.setStyle(Paint.Style.STROKE);
        brushes.setStrokeCap(Paint.Cap.ROUND);
        brushes.setStrokeWidth(bandWidth);
        for(int i = 1; i < 6; i++) {
            float loopAngle = ((60/i) * secAng) % 720;
            if((loopAngle < 0.1) || (loopAngle > 719.9)){ // avoid unsightly blank ring
                loopAngle = 0.1f;
            }
            float loopMod = loopAngle % 360;
            if(loopAngle < 360){
                tPainting.drawArc(getArcRect(i), minAng - 90, loopMod, false, brushes);
            } else {
                tPainting.drawArc(getArcRect(i), minAng+loopMod - 90, (360-loopMod), false, brushes);
            }
        }
        float hourLoopAngle = ((60/6) * secAng) % 720;
        float hourLoopMod = hourLoopAngle % 360;
        float hourMod = (hourAng * 2) % 360;
        if(hourAng < 180) {
            tPainting.drawArc(getArcRect(6), minAng + hourLoopMod - 90, hourMod, false, brushes);
        } else {
            tPainting.drawArc(getArcRect(6), minAng + hourLoopMod + hourMod - 90, (360 - hourMod), false, brushes);
        }
        brushes.setTextSize(30);
        brushes.setStrokeWidth(1);
        brushes.setStyle(Paint.Style.FILL);
        brushes.setTextAlign(Paint.Align.CENTER);
        tPainting.drawText(mCalendar.format("%l:%M"),
                centreX - bandWidth * 12, centreY - bandWidth * 12, brushes);
        tPainting.drawText(mCalendar.format("%Y-%b-%d"),
                centreX + bandWidth * 12, centreY + bandWidth * 12, brushes);
    }

    private RectF getArcRect(int arcNum) {
        float dist = bandWidth * arcNum * 2;
        return new RectF(centreX - dist, centreY - dist, centreX + dist, centreY + dist);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(backing, 0, 0, null);
    }

    public void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        backing = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        painting = new Canvas(backing);
        centreX = width/2f;
        centreY = height/2f;
        float clockRadius = Math.min(width, height) / 2f;
        bandWidth = clockRadius / 16;
        started = true;
        updateTime();
    }

    public void stopTick() {
        /* try to remove all traces of the update threads and stop them from running */
        ticking = false;
        clockTicker.cancel(true);
        for(Runnable t : tickerTimer.getQueue()){
            tickerTimer.remove(t);
        }
        clockTicker = null;
    }

    public void startTick() {
        if(!ticking) {
            ticking = true;
            int refreshRate = 100;
            clockTicker = tickerTimer.scheduleWithFixedDelay(new ClockTicker(this),
                    0, refreshRate, TimeUnit.MILLISECONDS);
        }
    }
}