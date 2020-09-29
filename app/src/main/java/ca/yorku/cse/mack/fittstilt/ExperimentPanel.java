package ca.yorku.cse.mack.fittstilt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * ExperimentPanel -- panel to present the targets
 * <p>
 *
 * @author Scott MacKenziem 2011-2016
 */
public class ExperimentPanel extends View
{
    // size and positioning constants (density-independent pixels)
    final int START_TEXT_SIZE = 18;
    final int GAP = 10; // gap between lines
    final int DEFAULT_BALL_DIAMETER = 22;
    final int START_CIRCLE_RADIUS = 20;
    final int X_OFFSET = 20;
    final int Y_OFFSET = 20;

    float xBall, yBall;
    float startX, startY, practiceX, practiceY;
    Bitmap ball; // initialized from main activity (see onWindowFocusChanged)
    Circle[] taskCircles;
    Circle targetCircle; // the destination target to select
    Circle fromTargetCircle; // the source target from where the trial began
    Circle startCircle;
    Circle practiceCircle;
    Circle startCircleFlick;
    Circle getPracticeCircleFlick;
    String orderOfControl;
    boolean done = false;
    boolean waitStartCircleSelect, waitPracticeCircleSelect;
    Paint targetPaint, targetRimPaint, normalPaint, startPaint, practicePaint;
    String[] results = {"Start"};
    String[] practice;
    float pixelDensity;
    //int ballDiameter;
    int gap, xOffset, yOffset1, yOffset2, yOffset3;
    int startTextSize, startCircleRadius;
    //float ballScale;
    boolean readyToDraw;

    public ExperimentPanel(Context contextArg)
    {
        super(contextArg);
        initialize(contextArg);
    }

    public ExperimentPanel(Context contextArg, AttributeSet attrs)
    {
        super(contextArg, attrs);
        initialize(contextArg);
    }

    public ExperimentPanel(Context contextArg, AttributeSet attrs, int defStyle)
    {
        super(contextArg, attrs, defStyle);
        initialize(contextArg);
    }

    // things that can be initialized from within this View
    private void initialize(Context c)
    {
        readyToDraw = false; // don't draw UI until ball onWindowFocusChanged finishes in main activity

        pixelDensity = c.getResources().getDisplayMetrics().density;
        gap = GAP * (int)(pixelDensity);
        startTextSize = (int)(START_TEXT_SIZE * pixelDensity);
        startCircleRadius = (int)(START_CIRCLE_RADIUS * pixelDensity); // same for practice circle
        xOffset = (int)(X_OFFSET * pixelDensity);
        yOffset1 = (int)(Y_OFFSET * pixelDensity);
        yOffset2 = yOffset1 + 2 * startCircleRadius + startTextSize + gap + 2 * startCircleRadius;
        yOffset3 = yOffset1 + 2 * startCircleRadius + yOffset1 + 2 * startCircleRadius + startTextSize + gap;
//        if(orderOfControl.equals("Flicker")){
//            startCircle = new Circle(xBall + xOffset + startCircleRadius, yBall+yOffset1+ startCircleRadius,
//                    startCircleRadius, Circle.NORMAL);
//            practiceCircle = new Circle(yBall + startCircleRadius, yBall +  yOffset2, startCircleRadius, Circle.NORMAL);
//        }
//        else {
            startCircle = new Circle(xOffset + startCircleRadius, yOffset1 + startCircleRadius, startCircleRadius, Circle
                    .NORMAL);
            practiceCircle = new Circle(xOffset + startCircleRadius, yOffset2, startCircleRadius, Circle.NORMAL);
       // }
        // create bitmap for ball (will be replaced with scaled bitmap from main activity (see onWindowFocusChanged)
        ball = BitmapFactory.decodeResource(getResources(), R.drawable.ball);

        targetCircle = new Circle(1, 1, 1, 1);

        targetPaint = new Paint();
        targetPaint.setColor(0xffffaaaa);
        targetPaint.setStyle(Paint.Style.FILL);
        targetPaint.setAntiAlias(true);

        targetRimPaint = new Paint();
        targetRimPaint.setColor(Color.RED);
        targetRimPaint.setStyle(Paint.Style.STROKE);
        targetRimPaint.setStrokeWidth(2);
        targetRimPaint.setAntiAlias(true);

        normalPaint = new Paint();
        normalPaint.setColor(0xffff9999); // lighter red (to minimize distraction)
        normalPaint.setStyle(Paint.Style.STROKE);
        normalPaint.setStrokeWidth(2);
        normalPaint.setAntiAlias(true);

        startPaint = new Paint();
        startPaint.setColor(0xff0000ff);
        startPaint.setStyle(Paint.Style.FILL);
        startPaint.setAntiAlias(true);
        startPaint.setTextSize(startTextSize);

        practicePaint = new Paint();
        practicePaint.setColor(0xff008800);
        practicePaint.setStyle(Paint.Style.FILL);
        practicePaint.setAntiAlias(true);
        practicePaint.setTextSize(startTextSize);

        waitStartCircleSelect = true;
        waitPracticeCircleSelect = true;

        this.setBackgroundColor(Color.LTGRAY);
    }

    protected void onDraw(Canvas canvas)
    {
        if (!readyToDraw)
            return;

        if (waitStartCircleSelect) // draw start circle (and results string)
        {
            if(orderOfControl.equals("Flicker")){
                startCircle.x = startX;
                startCircle.y = startY;
            }

            // draw the start circle
            canvas.drawCircle(startCircle.x, startCircle.y, startCircle.radius, startPaint);


            // draw the results text
                if(orderOfControl.equals("Flicker")){
                    canvas.drawText(results[0], startCircle.x - 2*gap, startCircle.y + 2*startCircle.radius + gap, startPaint);

                    if(results.length > 1){
                    for (int i = 1; i < results.length; ++i){
                        canvas.drawText(results[i], xOffset, yOffset1 + 2 * startCircle.radius + (i + 1) * (startTextSize +
                                gap), startPaint);
                    }}
                }
                else{
                    for (int i = 0; i < results.length; ++i)
                canvas.drawText(results[i], xOffset, yOffset1 + 2 * startCircle.radius + (i + 1) * (startTextSize +
                        gap), startPaint);
                }

            if (waitPracticeCircleSelect) // only at beginning of a block
            {
                if(orderOfControl.equals("Flicker")){
                    practiceCircle.x = practiceX;
                    practiceCircle.y = practiceY;
                }
                    // draw the practice circle
                    canvas.drawCircle(practiceCircle.x, practiceCircle.y, practiceCircle.radius, practicePaint);


                // draw the practice greeting (also indicates the order of control, tilt gain, and selection method)
                if(orderOfControl.equals("Flicker")){
                    canvas.drawText(practice[0], practiceCircle.x - 2*gap, practiceCircle.y + 2*practiceCircle.radius + gap, practicePaint);
                    for (int i = 1; i < practice.length; ++i)
                    {
                        canvas.drawText(practice[i], xOffset, yOffset3 + (i + 1) * (startTextSize + gap), practicePaint);
                    }

                }
                else{
                    for (int i = 0; i < practice.length; ++i)
                    {
                        // Amit changes for readable color of text
                        practicePaint.setColor(0xff000000);
                        canvas.drawText(practice[i], xOffset, yOffset3 + (i + 1) * (startTextSize + gap), practicePaint);
                    }
                }
            }
        } else if (!done) // draw task circles
        {
            for (Circle c : taskCircles)
                if(c != null){
                canvas.drawCircle(c.x, c.y, c.radius, normalPaint);}

            // draw target circle last (so it is on top of any overlapping circles)
            canvas.drawCircle(targetCircle.x, targetCircle.y, targetCircle.radius, targetPaint);
            canvas.drawCircle(targetCircle.x, targetCircle.y, targetCircle.radius, targetRimPaint);
        }

        // draw the ball in its new location
        canvas.drawBitmap(ball, xBall, yBall, null);

    } // end onDraw
}
