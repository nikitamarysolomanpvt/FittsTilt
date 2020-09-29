package ca.yorku.cse.mack.fittstilt;

// information needed to for a trace
public class Trace {
    final float TWO_TIMES_PI = 6.283185307f;

    int a, w; // target amplitude, target width
    int fromX, fromY, targetX, targetY;
    int selectX, selectY;
    int positioningTime, selectionTime, movementTime;
    int ballDiameter; // diameter of the ball that is moved to a target
    int transformedSelectX;

    TracePoint[] p;  // raw trace points
    TracePoint[] tp; // transformed trace points (1D horizontal movement to right)

    int tre; // target re-entries
    int tac; // task axis crossings
    int mdc; // movement direction changes
    int odc; // orthogonal direction changes
    float mv; // movement variability
    float me; // movement error
    float mo; // movement offset

    Trace(int aArg, int wArg, int fromXArg, int fromYArg,
          int targetXArg, int targetYArg, int ballDiameterArg, TracePoint[] pArg) {
        a = aArg;
        w = wArg;
        fromX = fromXArg;
        fromY = fromYArg;
        targetX = targetXArg;
        targetY = targetYArg;
        ballDiameter = ballDiameterArg;
        p = pArg;
        tp = transform();

        selectX = p[p.length - 1].x;
        selectY = p[p.length - 1].y;
        transformedSelectX = Math.abs(tp[tp.length - 1].x);

        computeMeasures();
    }

    public String getMeasures() {
        return a + "," + w + "," + fromX + "," + fromY + "," +
                targetX + "," + targetY + "," + selectX + "," + selectY + "," +
                transformedSelectX + "," +
                positioningTime + "," + selectionTime + "," + movementTime + "," +
                tre + "," + tac + "," + mdc + "," + odc + "," +
                mv + "," + me + "," + mo;
    }

    public String tPoints() {
        StringBuilder sb = new StringBuilder();
        for (TracePoint tp : p)
            sb.append(String.format("%d,", tp.t));
        sb.deleteCharAt(sb.length() - 1); // delete last comma
        return sb.toString();
    }

    public String xPoints() {
        StringBuilder sb = new StringBuilder();
        for (TracePoint tp : p)
            sb.append(String.format("%d,", tp.x));
        sb.deleteCharAt(sb.length() - 1); // delete last comma
        return sb.toString();
    }

    public String yPoints() {
        StringBuilder sb = new StringBuilder();
        for (TracePoint tp : p)
            sb.append(String.format("%d,", tp.y));
        sb.deleteCharAt(sb.length() - 1); // delete last comma
        return sb.toString();
    }

    public String tiltPoints() {
        StringBuilder sb = new StringBuilder();
        for (TracePoint tp : p)
            sb.append(String.format("%.1f,", tp.tilt));
        sb.deleteCharAt(sb.length() - 1); // delete last comma
        return sb.toString();
    }

    private TracePoint[] transform() {
        TracePoint[] tp = new TracePoint[p.length];

        int dx = fromX;
        int dy = fromY;
        for (int i = 0; i < p.length; ++i)
            tp[i] = new TracePoint((p[i].x - dx), (int) (p[i].y - dy), p[i].t, p[i].tilt);

        dx = targetX - fromX;
        dy = targetY - fromY;
        double theta = Math.atan((double) dy / dx);
        theta = (TWO_TIMES_PI) - theta;

        for (TracePoint tracePoint : tp) {
            float xx = tracePoint.x;
            float yy = tracePoint.y;
            tracePoint.x = (int) Math.round(xx * Math.cos(theta) - yy * Math.sin(theta));
            tracePoint.y = (int) Math.round(xx * Math.sin(theta) + yy * Math.cos(theta));
        }
        return tp;
    }

    private void computeMeasures() {
        // ----------------------
        int i;
        float distanceFromTargetCenter;
        float radius = (float) w / 2f;
        for (i = 0; i < p.length; ++i) {
            distanceFromTargetCenter =
                    (float) Math.sqrt((p[i].x - targetX) * (p[i].x - targetX) +
                            (p[i].y - targetY) * (p[i].y - targetY));
            if (distanceFromTargetCenter < radius - (ballDiameter / 2f))
                break;
        }
        i = i < p.length ? i : p.length - 1; // in case 'break' didn't occur
        positioningTime = p[i].t - p[0].t;
        selectionTime = p[p.length - 1].t - p[i].t;
        movementTime = positioningTime + selectionTime;

        // ----------------------
        tre = 0;
        boolean inTargetPrevious;
        boolean inTargetCurrent = false;
        for (i = 1; i < p.length; ++i) {
            inTargetPrevious = inTargetCurrent;
            distanceFromTargetCenter =
                    (float) Math.sqrt((p[i].x - targetX) * (p[i].x - targetX) +
                            (p[i].y - targetY) * (p[i].y - targetY));

            inTargetCurrent = distanceFromTargetCenter < radius - (ballDiameter / 2f);

            if (inTargetCurrent && !inTargetPrevious)
                ++tre;
        }
        --tre; // adjust (don't count first target entry)
        tre = tre < 0 ? 0 : tre;

        // -----------------------
        tac = 0;
        final int THRESHOLD = 3;
        boolean belowAxis = false;
        boolean aboveAxis = false;
        for (i = 0; i < tp.length; ++i) {
            // don't check for TAC if pointer is inside either target
            double d1 = Math.hypot(tp[i].x, tp[i].y); // distance from 0,0
            double d2 = Math.hypot(a - tp[i].x, tp[i].y); // distance from A,0
            if (d1 < radius || d2 < radius)
                continue;

            // NOTE: y axis is "flipped" in Java's coordinate system
            if (tp[i].y > THRESHOLD)
                belowAxis = true;
            if (tp[i].y < -THRESHOLD)
                aboveAxis = true;
            if (i == 0)
                continue; // just get bearings on first sample

            if (belowAxis && tp[i].y < -THRESHOLD) {
                ++tac;
                belowAxis = false;
                aboveAxis = true;
            } else if (aboveAxis && tp[i].y > THRESHOLD) {
                ++tac;
                belowAxis = true;
                aboveAxis = false;
            }
        }

        // -------------------
        mdc = 0;
        String pattern = "";
        for (i = 0; i < tp.length - 1; ++i) {
            double d1 = Math.hypot(tp[i].x, tp[i].y); // distance from 0,0
            double d2 = Math.hypot(a - tp[i].x, tp[i].y); // distance from A,0
            if (d1 < radius || d2 < radius)
                continue;
            if ((tp[i + 1].y - tp[i].y) >= 0)
                pattern += "1";
            else
                pattern += "0";
        }
        // smooth pattern (010 -> 000, 101 -> 111)
        for (i = 0; i < pattern.length() - 2; ++i)
            if (pattern.charAt(i) == pattern.charAt(i + 2)
                    && pattern.charAt(i) != pattern.charAt(i + 1))
                pattern = pattern.substring(0, i) + pattern.charAt(i)
                        + pattern.charAt(i) + pattern.substring(i + 2);

        // scan pattern for changes
        for (i = 0; i < pattern.length() - 1; ++i)
            if (pattern.charAt(i) != pattern.charAt(i + 1))
                ++mdc;

        // ------------------
        odc = 0;
        pattern = "";
        for (i = 0; i < tp.length - 1; ++i) {
            double d1 = Math.hypot(tp[i].x, tp[i].y); // distance from 0,0
            double d2 = Math.hypot(a - tp[i].x, tp[i].y); // distance from A,0
            if (d1 < radius || d2 < radius)
                continue;
            if ((tp[i + 1].x - tp[i].x) >= 0)
                pattern += "1";
            else
                pattern += "0";
        }
        // smooth pattern (010 -> 000, 101 -> 111)
        for (i = 0; i < pattern.length() - 2; ++i)
            if (pattern.charAt(i) == pattern.charAt(i + 2)
                    && pattern.charAt(i) != pattern.charAt(i + 1))
                pattern = pattern.substring(0, i) + pattern.charAt(i)
                        + pattern.charAt(i) + pattern.substring(i + 2);

        for (i = 0; i < pattern.length() - 1; ++i)
            if (pattern.charAt(i) != pattern.charAt(i + 1))
                ++odc;

        // ----------------------
        mv = me = mo = 0.0f;
        float[] yy = new float[tp.length];
        for (i = 0; i < tp.length; ++i)
            yy[i] = tp[i].y;
        float meanY = mean(yy);
        mo = meanY;
        for (i = 0; i < tp.length; ++i) {
            mv += ((double) tp[i].y - meanY) * ((double) tp[i].y - meanY);
            me += Math.abs(tp[i].y);
        }
        mv = (float) Math.sqrt(mv / (tp.length - 1));
        me = me / tp.length;
    }

    // compute the mean of values in a float array
    private float mean(float n[]) {
        float mean = 0.0f;
        for (float f : n)
            mean += f;
        return mean / n.length;
    }
}