package org.gringene.concentricclock;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ConcentricClock extends SurfaceView implements
        SurfaceHolder.Callback {
    private SurfaceHolder sh;
    private final Paint brushes = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Time mCalendar;
    private float mHours;
    private float mMinutes;
    private float mSeconds;
    private float centreX;
    private float centreY;
    private float clockRadius;

    public ConcentricClock(Context context) {
        super(context);
        init();
    }

    public ConcentricClock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        sh = getHolder();
        sh.addCallback(this);
        mCalendar = new Time();
        updateTime();
    }

    private void updateTime(){
        mCalendar.setToNow();
        int hour = mCalendar.hour;
        int min = mCalendar.minute;
        int secInt = mCalendar.second;
        float secFrac = (mCalendar.toMillis(true) % 1000) / 1000f;
        float sec = (float) (secInt + (1 - Math.sin((0.5f - secFrac) * Math.PI))/2);
        mSeconds = sec;
        mMinutes = mCalendar.minute + sec/60;
        mHours = mCalendar.hour + mMinutes/60;
    }

    public void surfaceCreated(SurfaceHolder holder) {
    }

    public void drawClock(Canvas tPainting){
        RectF clockRect = new RectF(centreX - clockRadius,
                centreY - clockRadius, centreX + clockRadius, centreY + clockRadius);
        float startAng = 0;
        float hour = (mCalendar.hour + (float)mCalendar.minute/60);
        float hourAng = hour * 30; // 360/12
        float hourX = (float)(Math.cos(hourAng) * clockRadius);
        float hourY = (float)(Math.sin(hourAng) * clockRadius);
        tPainting.drawColor(Color.BLACK);
        brushes.setColor(Color.BLUE);
        brushes.setStyle(Paint.Style.FILL);
        tPainting.drawCircle(centreX, centreY, 50, brushes);
        brushes.setStyle(Paint.Style.STROKE);
        brushes.setColor(Color.RED);
        brushes.setStrokeWidth(3);
        tPainting.drawArc(clockRect, startAng, hourAng, true, brushes);
        brushes.setColor(Color.GREEN);
        brushes.setTextSize(30);
        tPainting.drawText(String.format("%.2f", hour), centreX, centreY, brushes);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        centreX = width/2f;
        centreY = height/2f;
        clockRadius = Math.min(width,height)/2f;
        Canvas canvas = sh.lockCanvas();
        drawClock(canvas);
        sh.unlockCanvasAndPost(canvas);
    }
    public void surfaceDestroyed(SurfaceHolder holder) {
    }
}