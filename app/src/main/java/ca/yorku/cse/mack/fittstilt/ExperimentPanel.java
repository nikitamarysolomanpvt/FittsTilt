package ca.yorku.cse.mack.fittstilt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FlingAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import kotlin.ranges.RangesKt;

import static kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull;
import static kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull;

/**
 * ExperimentPanel -- panel to present the targets
 * <p>
 *
 * @author Scott MacKenziem 2011-2016
 */
public class ExperimentPanel extends View {
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
  Context context;
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
//  boolean isFling;
  //**************************************************
  private DynamicAnimation.OnAnimationUpdateListener xAnimationUpdate;
  private DynamicAnimation.OnAnimationUpdateListener yAnimationUpdate;
  private DynamicAnimation.OnAnimationEndListener xAnimationEnd;
  private DynamicAnimation.OnAnimationEndListener yAnimationEnd;
  private float circleRadius;
  private float circleStrokeWidth;
  private float minX;
  private float minY;
  private Paint paint;
  @Nullable
  private OnPositionChangedListener onPositionChangedListener;
  private float friction;
  private float circleX;
  private float circleY;
  private float xVelocity;
  private float yVelocity;
  private VelocityTracker velocityTracker;
  private FlingAnimation xFling;
  private FlingAnimation yFling;
  private PointF lastSetPosition;
  public static final float MAX_FRICTION = Float.MAX_VALUE;
  boolean isNotTouch=false;

  /**
   * A pressed gesture has started, the
   * motion contains the initial starting location.
   * <p>
   * This is also a good time to check the button state to distinguish
   * secondary and tertiary button clicks and handle them appropriately.
   * </p>
   */
  public static final int ACTION_DOWN = 0;

  /**
   * A pressed gesture has finished, the
   * motion contains the final release location as well as any intermediate
   * points since the last down or move event.
   */
  public static final int ACTION_UP = 1;

  /**
   * A change has happened during a
   * press gesture (between {@link #ACTION_DOWN} and {@link #ACTION_UP}).
   * The motion contains the most recent point, as well as any intermediate
   * points since the last down or move event.
   */
  public static final int ACTION_MOVE = 2;

  /**
   * The current gesture has been aborted.
   * You will not receive any more points in it.  You should treat this as
   * an up event, but not perform any action that you normally would.
   */
  public static final int ACTION_CANCEL = 3;


  public ExperimentPanel(Context contextArg) {
    super(contextArg);

    initialize(contextArg);

  }

  public ExperimentPanel(Context contextArg, AttributeSet attrs) {
    super(contextArg, attrs);

    initialize(contextArg);

  }

  public ExperimentPanel(Context contextArg, AttributeSet attrs, int defStyle) {
    super(contextArg, attrs, defStyle);
    initialize(contextArg);


  }

  // things that can be initialized from within this View
  private void initialize(Context c) {
    this.context = c;

    readyToDraw = false; // don't draw UI until ball onWindowFocusChanged finishes in main activity

    pixelDensity = c.getResources().getDisplayMetrics().density;
    gap = GAP * (int) (pixelDensity);
    startTextSize = (int) (START_TEXT_SIZE * pixelDensity);
    startCircleRadius = (int) (START_CIRCLE_RADIUS * pixelDensity); // same for practice circle
    xOffset = (int) (X_OFFSET * pixelDensity);
    yOffset1 = (int) (Y_OFFSET * pixelDensity);
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
    init(c);
  }

  protected void onDraw(Canvas canvas) {
    if (!readyToDraw)
      return;

    if (waitStartCircleSelect) // draw start circle (and results string)
    {
//      if (orderOfControl.equals("Flicker")) {
//        startCircle.x = startX;
//        startCircle.y = startY;
//      }

      // draw the start circle
      canvas.drawCircle(startCircle.x, startCircle.y, startCircle.radius, startPaint);


      // draw the results text
//      if (orderOfControl.equals("Flicker")) {
//        canvas.drawText(results[0], startCircle.x - 2 * gap, startCircle.y + 2 * startCircle.radius + gap, startPaint);
//
//        if (results.length > 1) {
//          for (int i = 1; i < results.length; ++i) {
//            canvas.drawText(results[i], xOffset, yOffset1 + 2 * startCircle.radius + (i + 1) * (startTextSize +
//                gap), startPaint);
//          }
//        }
//      } else
      {
        for (int i = 0; i < results.length; ++i)
          canvas.drawText(results[i], xOffset, yOffset1 + 2 * startCircle.radius + (i + 1) * (startTextSize +
              gap), startPaint);
      }

      if (waitPracticeCircleSelect) // only at beginning of a block
      {
//        if (orderOfControl.equals("Flicker")) {
//          practiceCircle.x = practiceX;
//          practiceCircle.y = practiceY;
//        }
        // draw the practice circle
        canvas.drawCircle(practiceCircle.x, practiceCircle.y, practiceCircle.radius, practicePaint);


        // draw the practice greeting (also indicates the order of control, tilt gain, and selection method)
//        if (orderOfControl.equals("Flicker")) {
//          canvas.drawText(practice[0], practiceCircle.x - 2 * gap, practiceCircle.y + 2 * practiceCircle.radius + gap, practicePaint);
//          for (int i = 1; i < practice.length; ++i) {
//            canvas.drawText(practice[i], xOffset, yOffset3 + (i + 1) * (startTextSize + gap), practicePaint);
//          }
//
//        } else
        {
          for (int i = 0; i < practice.length; ++i) {
            // Amit changes for readable color of text
            practicePaint.setColor(0xff000000);
            canvas.drawText(practice[i], xOffset, yOffset3 + (i + 1) * (startTextSize + gap), practicePaint);
          }
        }
      }
    } else if (!done) // draw task circles
    {
      for (Circle c : taskCircles)
        if (c != null) {
          canvas.drawCircle(c.x, c.y, c.radius, normalPaint);
        }

      // draw target circle last (so it is on top of any overlapping circles)
      canvas.drawCircle(targetCircle.x, targetCircle.y, targetCircle.radius, targetPaint);
      canvas.drawCircle(targetCircle.x, targetCircle.y, targetCircle.radius, targetRimPaint);
    }

    // draw the ball in its new location
    canvas.drawBitmap(ball, xBall, yBall, null);


  } // end onDraw

  //  protected void onDraw(@NotNull Canvas canvas) {
//    checkParameterIsNotNull(canvas, "canvas");
//    Bitmap ball = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
//
//    ball = Bitmap.createScaledBitmap(ball, (int)this.circleRadius, (int)this.circleRadius, true);
//    canvas.drawBitmap(ball, xBall-40, yBall-40, null);
////    canvas.drawCircle(xBall, yBall, this.circleRadius, this.paint);
//  }
  @Nullable
  public final OnPositionChangedListener getOnPositionChangedListener() {
    return this.onPositionChangedListener;
  }

  public final void setOnPositionChangedListener(@Nullable OnPositionChangedListener var1) {
    this.onPositionChangedListener = var1;
  }

  public final float getFriction() {
    return this.friction;
  }

  public final void setFriction(float value) {
    this.friction = value;
    if (value == MAX_FRICTION) {
      this.stopAnimations();
    }

  }

  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    this.stopAnimations();
  }

  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    if ((w != oldw || h != oldh) && orderOfControl.equals("Flicker")) {
      this.setPosition(this.lastSetPosition);
    }

  }

  public boolean hasOverlappingRendering() {
    return false;
  }


  @SuppressLint({"ClickableViewAccessibility"})
  public boolean onTouchEvent(@NotNull MotionEvent event) {
    float xTouchPosition = event.getX();
    float xTouchPositionDifference = xBall > xTouchPosition ? xBall - xTouchPosition : xTouchPosition - xBall;
    float yTouchPosition = event.getY();
    float yTouchPositionDifference = yBall > yTouchPosition ? yBall - yTouchPosition : yTouchPosition - yBall;

    float minimumChange = 120.0F;
    float leftBorder = minimumChange;
    float rightBorder = this.getWidth() - minimumChange;
    float topBorder = minimumChange;
    float bottomBorder = this.getHeight() - minimumChange;
    boolean isFlicker = orderOfControl.equals("Flicker");
    boolean isBorder = (xTouchPosition <= leftBorder || xTouchPosition >= rightBorder || yTouchPosition <= topBorder || yTouchPosition >= bottomBorder) ? true : false;
if(event.getActionMasked()==ACTION_DOWN)
    isNotTouch = (xTouchPositionDifference > minimumChange-40 || yTouchPositionDifference > minimumChange-40) ? false : true;

    if (isFlicker && (isBorder || isNotTouch)) {
      checkParameterIsNotNull(event, "event");
      int index = event.getActionIndex();
      int action = event.getActionMasked();
      int pointerId = event.getPointerId(index);
      switch (action) {
        case ACTION_DOWN:
          this.onActionDown(event);
          break;
        case ACTION_UP:
          this.onActionUp();
          break;
        case ACTION_MOVE:
          this.onActionMove(event, pointerId);
          break;
        case ACTION_CANCEL:
          this.onActionCancel();
      }
    }

    return true;
  }

  public final void setPosition(@NotNull PointF position) {
    checkParameterIsNotNull(position, "position");
    this.lastSetPosition = position;
    if (this.getWidth() != 0 || this.getHeight() != 0) {
      float newX = position.x * (this.maxX() - this.minX) + this.minX;
      float newY = ((float) 1 - position.y) * (this.maxY() - this.minY) + this.minY;
      this.updatePosition(newX, newY);
    }
  }

  private final void onActionDown(MotionEvent event) {
    this.stopAnimations();
    VelocityTracker var10000;
    if (this.velocityTracker == null) {
      this.velocityTracker = VelocityTracker.obtain();
    } else {
      var10000 = this.velocityTracker;
      if (var10000 != null) {
        var10000.clear();
      }
    }

    var10000 = this.velocityTracker;
    if (var10000 != null) {
      var10000.addMovement(event);
    }

    this.updatePosition(event.getX(), event.getY());
  }

  private final void onActionMove(MotionEvent event, int pointerId) {
    if (this.friction < MAX_FRICTION) {
      VelocityTracker var10000 = this.velocityTracker;
      if (var10000 != null) {
        VelocityTracker var3 = var10000;
        var3.addMovement(event);
        var3.computeCurrentVelocity(500, 10000.0F);
        this.xVelocity = var3.getXVelocity(pointerId);
        this.yVelocity = var3.getYVelocity(pointerId);
      }
    }

    this.updatePosition(event.getX(), event.getY());
  }

  private final void onActionUp() {
    VelocityTracker var10000 = this.velocityTracker;
    if (var10000 != null) {
      var10000.recycle();
    }

    this.velocityTracker = (VelocityTracker) null;
    if (this.friction < MAX_FRICTION) {
      this.startXAnimation();
      this.startYAnimation();
    }

  }

  private final void onActionCancel() {
    VelocityTracker var10000 = this.velocityTracker;
    if (var10000 != null) {
      var10000.recycle();
    }

    this.velocityTracker = (VelocityTracker) null;
  }

  private final float maxX() {
    return (float) this.getWidth() - this.minX;
  }

  private final float maxY() {
    return (float) this.getHeight() - this.minY;
  }

  public final void stopAnimations() {
    FlingAnimation var10000 = this.xFling;
    if (var10000 != null) {
      var10000.cancel();
    }

    this.xFling = (FlingAnimation) null;
    var10000 = this.yFling;
    if (var10000 != null) {
      var10000.cancel();
    }

    this.yFling = (FlingAnimation) null;
  }

  private final void startXAnimation() {
    FlingAnimation var1 = this.createAnimation(xBall, this.xVelocity, this.maxX(), this.minX);
    var1.addUpdateListener(this.xAnimationUpdate);
    var1.addEndListener(this.xAnimationEnd);
    var1.start();
    this.xFling = var1;
  }

  //  x[0]=454.5791, y[0]=1249.3492,414.42297,1225.3618--raw 454.5791, 1249.3492
  private final void startYAnimation() {
    FlingAnimation var1 = this.createAnimation(yBall, this.yVelocity, this.maxY(), this.minY);
    var1.addUpdateListener(this.yAnimationUpdate);
    var1.addEndListener(this.yAnimationEnd);
    var1.start();
    this.yFling = var1;
  }

  private final FlingAnimation createAnimation(float startValue, float startVelocity, float maxValue, float minValue) {
    Log.e("TAG", "startValue=" + startValue + "  startVelocity" + startVelocity + "  maxValue=" + maxValue + " minValue=" + minValue + "");
    FlingAnimation var10000 ;
        if(friction>0.0)
          var10000=   ((FlingAnimation) (new FlingAnimation(new FloatValueHolder(startValue))).setStartVelocity(startVelocity).setMaxValue(maxValue).setMinValue(minValue).setMinimumVisibleChange(1F)).setFriction(this.friction);
        else
          var10000=   ((FlingAnimation) (new FlingAnimation(new FloatValueHolder(startValue))).setStartVelocity(startVelocity).setMaxValue(maxValue).setMinValue(minValue).setMinimumVisibleChange(1F));

    checkExpressionValueIsNotNull(var10000, "FlingAnimation(FloatValue.setFriction(friction)");
    return var10000;
  }

  private final void updatePosition(float newX, float newY) {
    float validX = RangesKt.coerceIn(newX, this.minX, this.maxX());
    float validY = RangesKt.coerceIn(newY, this.minY, this.maxY());
    if (validX != xBall || validY != yBall) {
      xBall = validX;
      yBall = validY;
      this.notifyPositionChanged();
      this.invalidate();
    }
  }

  private final void notifyPositionChanged() {
    float posX = RangesKt.coerceIn((xBall - this.minX) / (this.maxX() - this.minX), 0.0F, 1.0F);
    float posY = (float) 1 - RangesKt.coerceIn((yBall - this.minY) / (this.maxY() - this.minY), 0.0F, 1.0F);
    if (onPositionChangedListener != null) {
      onPositionChangedListener.onPositionChanged(new PointF(posX, posY));
    }
  }

  private void init(@NotNull Context context) {

    checkParameterIsNotNull(context, "context");

    this.xAnimationUpdate = (DynamicAnimation.OnAnimationUpdateListener) (new DynamicAnimation.OnAnimationUpdateListener() {
      public final void onAnimationUpdate(DynamicAnimation $noName_0, float newX, float $noName_2) {
        updatePosition(newX, yBall);
      }
    });
    this.yAnimationUpdate = (DynamicAnimation.OnAnimationUpdateListener) (new DynamicAnimation.OnAnimationUpdateListener() {
      public final void onAnimationUpdate(DynamicAnimation $noName_0, float newY, float $noName_2) {
        updatePosition(xBall, newY);
      }
    });
    this.xAnimationEnd = (DynamicAnimation.OnAnimationEndListener) (new DynamicAnimation.OnAnimationEndListener() {
      public final void onAnimationEnd(DynamicAnimation $noName_0, boolean canceled, float $noName_2, float velocity) {
        if (!canceled) {
          boolean var6 = false;
          if (Math.abs(velocity) > (float) 0 && ViewCompat.isAttachedToWindow((View) ExperimentPanel.this)) {
            xVelocity = -velocity;
            startXAnimation();
          }
        }

      }
    });
    this.yAnimationEnd = (DynamicAnimation.OnAnimationEndListener) (new DynamicAnimation.OnAnimationEndListener() {
      public final void onAnimationEnd(DynamicAnimation $noName_0, boolean canceled, float $noName_2, float velocity) {
        if (!canceled) {
          boolean var6 = false;
          if (Math.abs(velocity) > (float) 0 && ViewCompat.isAttachedToWindow((View) ExperimentPanel.this)) {
            yVelocity = -velocity;
            startYAnimation();
          }
        }

      }
    });
    this.circleRadius = this.getResources().getDimension(R.dimen.circle_radius);
    this.circleStrokeWidth = this.getResources().getDimension(R.dimen.circle_stroke_width);
    this.minX = this.circleStrokeWidth / 2.0F + this.circleRadius;
    this.minY = this.circleStrokeWidth / 2.0F + this.circleRadius;
    Paint var4 = new Paint(1);
    var4.setStyle(Paint.Style.STROKE);
    var4.setColor(ContextCompat.getColor(context, R.color.colorAccent));
    var4.setStrokeWidth(this.circleStrokeWidth);
    this.paint = var4;
    this.friction = MAX_FRICTION;
    this.lastSetPosition = new PointF(0.5F, 0.5F);
  }
  public interface OnPositionChangedListener {
    void onPositionChanged(@NotNull PointF var1);
  }
}
