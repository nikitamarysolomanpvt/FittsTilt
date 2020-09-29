package ca.yorku.cse.mack.fittstilt;

import android.util.Log;

import static java.lang.Math.abs;

public class Circle {
  final static int NORMAL = 1;
  final static int TARGET = 2;

  public float x, y, radius;
  int status; // indicates if this circle is the target circle or a normal circle

  Circle(float xArg, float yArg, float radiusArg, int statusArg) {
    x = xArg;
    y = yArg;
    radius = radiusArg;
    status = statusArg;
  }

  public boolean inCircle(float xTest, float yTest, float diameterArg) {
    float dx = xTest - x;
    float dy = yTest - y;
    float distanceFromCenter = (float) Math.sqrt(dx * dx + dy * dy);
    return distanceFromCenter + diameterArg / 2f < radius;
  }

  public boolean collidedWithCircle(float xCurrent, float yCurrent, float xPrev, float yPrev, float diameterArg) {
    float dx = xCurrent - x;
    float dy = yCurrent - y;
    float dxCurrentPrev = xPrev - x;
    float dyCurrentPrev = yPrev - y;
    float distanceFromCenter = (float) Math.sqrt(dx * dx + dy * dy);
    float distanceFromCenterOld = (float) Math.sqrt(dxCurrentPrev * dxCurrentPrev + dyCurrentPrev * dyCurrentPrev);
    if (distanceFromCenter > distanceFromCenterOld
        &&
        (distanceFromCenterOld < radius * 1.5)) {
      return true;
    } else {
      return false;
    }
  }
}