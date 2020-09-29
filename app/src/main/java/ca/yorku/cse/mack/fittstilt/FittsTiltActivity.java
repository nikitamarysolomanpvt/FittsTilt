package ca.yorku.cse.mack.fittstilt;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

import static java.lang.Math.abs;
import static java.lang.Math.floor;
import static java.lang.Math.max;

/**
 * <h1>FittsTilt</h1>
 *
 * <h3>Summary</h3>
 *
 * <ul> <li>Android experiment software for Fitts' law target acquisition using device tilt <p>
 *
 * <li>Implements the two-dimensional (2D) Fitts' law task described in ISO 9241-9 (updated in 2012 as ISO/TC 9241-411).
 * <p>
 *
 * <li>User performance data gathered and saved in output files for follow-up analyses. <p> </ul>
 *
 * <h3>Related References</h3>
 *
 * The following publications present research where this software was used. <p>
 *
 * <ul> <li> <a href="http://www.yorku.ca/mack/nordichi2012.html">"FittsTilt: The Application of Fitts' Law to
 * Tilt-based Interaction"</a>, by MacKenzie and Teather (<i>NordiCHI 2012</i>). <p>
 *
 * <li> <a href="http://www.yorku.ca/mack/gi2014.html">"Position vs. Velocity Control for Tilt-based Interaction"</a>,
 * by Teather and MacKenzie (<i>Graphics Interface 2014</i>). </ul> <p>
 *
 * The following publications provide background information on Fitts' law and experimental testing using the Fitts'
 * paradigm. <p>
 *
 * <ul> <li><a href="http://www.yorku.ca/mack/ijhcs2004.html">"Towards a Standard for Pointing Device Evaluation:
 * Perspectives on 27 Years of Fitts' Law Research in HCI"</a>, by Soukoreff and MacKenzie (<i>IJHCS 2004</i>). <p>
 *
 * <li><a href="http://www.yorku.ca/mack/HCI.html">"Fitts' Law as a Research and Design Tool in Human-Computer
 * Interaction"</a>, by MacKenzie (<i>HCI 1992</i>). <p> </ul> <p>
 *
 * <h3>Setup Parameters</h3>
 *
 * Upon launching, the program presents a setup dialog: <p> <center><a href="FittsTilt-1.jpg"><img src="FittsTilt-1.jpg"
 * width="200"></a></center> <p> </center>
 *
 * In all, there are 14 setup parameters organized as 12 spinners and two checkboxes. The setup parameters are as
 * follows: <p>
 *
 * <blockquote> <table border="1" cellspacing="0" cellpadding="6"> <tr bgcolor="#cccccc"> <th>Parameter <th>Description
 *
 * <tr> <td valign="top">Participant code <td>Identifies the current participant. <p>
 *
 * <tr> <td valign="top">Session code <td>Identifies the session. This code is useful if testing proceeds over multiple
 * sessions to gauge the progression of learning. <p>
 *
 * <tr> <td valign="top">Block code (auto) <td>Identifies the block of testing. This code is generated automatically.
 * The first block of testing is "B01", then "B02", and so on. Output data files include the block code in the filename.
 * The first available block code is used in opening data files for output. This prevents overwriting data from an
 * earlier block of testing. <p>
 *
 * <tr> <td valign="top">Group code <td>Identifies the group to which the participant was assigned. This code is needed
 * if counterbalancing was used (i.e., participants were assigned to groups to offset order effects). This is common
 * practice for testing the levels of a within-subjects independent variable. <p>
 *
 * <tr> <td valign="top">Condition code <td>An arbitrary code to associate a test condition with a block of trials. This
 * parameter might be useful if the user study includes conditions that are not inherently part of the application
 * (e.g., Gender &rarr; male, female; User stance &rarr; sitting, standing, walking). <p>
 *
 * <tr> <td valign="top">Number of targets <td>Specifies the number of targets that appear in the layout circle. <p>
 *
 * <tr> <td valign="top">Target amplitude (A) <td>Specifies the diameter of the layout circle. The spinner offers three
 * choices: "150, 250, 500", "250, 500", or "500" (but see note 2 below). <p>
 *
 * <tr> <td valign="top">Target width (W) <td>Specifies the width of targets. This is the diameter of the target
 * circles. The spinner offers three choices: "40, 60, 100", "60, 100", or "100". <p>
 *
 * Notes:<br> 1. The total number of <i>A-W</i> conditions (sequences) in a block is <i>n &times; m</i>, where <i>n</i>
 * is the number of target amplitudes and <i>m</i> is the number of target widths.<br> 2. The <i>A-W</i> values are
 * scaled such that the widest condition (largest A, largest W) spans the device's display with minus 10 pixels on each
 * side. <p>
 *
 * <tr> <td valign="top">Ball scale <td>A scaling factor from the set { 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9 }.
 * The diameter of the virtual ball is adjusted to be the diameter of the smallest target times this scaling factor.<p>
 *
 * <tr valign="top"> <td>Order of control <td>The options are
 *
 * <ul> <li>Velocity &ndash; tilting the device controls the velocity of the virtual ball <li>Position &ndash; tilting
 * the device controls the position of the virtual ball </ul><p>
 *
 * <tr valign="top"> <td>Tilt gain <td>A value from the set { Very low, Low, Medium, High, Very high } roughly
 * corresponding to gain sensitivities ranging for very low to very high.<p>
 *
 * <tr valign="top"> <td>Selection mode <td>The options are <ul> <li>First_entry &ndash; a target is selected as soon as
 * the virtual ball is fully inside the target <li>Dwell_500 &ndash; a target is selected after the virtual ball is
 * fully inside the target for 500 ms <li>Dwell_400 &ndash; a target is selected after the virtual ball is fully inside
 * the target for 400 ms <li>Dwell_300 &ndash; a target is selected after the virtual ball is fully inside the target
 * for 300 ms </ul> <p>
 *
 * <tr> <td valign="top">Vibrotactile feedback <td>A checkbox parameter. If checked, a vibrotactile pulse is emitted
 * upon each target selection. <p>
 *
 * <tr> <td valign="top">Audio feedback <td>A checkbox parameter. If checked, an auditory beep is heard upon each target
 * selection. <p> </table> </blockquote>
 *
 * <h3>Operation</h3>
 *
 * Once the setup parameters are chosen, the testing begins by tapping "OK". The first screen to appear is a transition
 * screen.  See below.  The transition screen provides two options.  To select an option the user must manoeuvre the
 * virtual ball to the green or blue circle and dwell on the circle for one second.  If the green circle is selected,
 * the user has the opportunity to do a sequence of practice trials.  No data are saved for practice trials. If the blue
 * circle is selected, the application transitions to the first sequence of experiment trials. <p>
 *
 * <center> <a href="FittsTilt-2.jpg"><img src="FittsTilt-2.jpg" width=200 alt="image"></a></center> <p>
 *
 *
 *
 * A series of trials for a single <i>A-W</i> condition is called a "sequence".  The target to select is highlighted in
 * pink (below, left).  After a selection, the highlight moves to a target on the opposite side of the layout circle.
 * The pattern of selecting moves around the layout circle until all targets are selected. At the end of a sequence,
 * results appear on the display (below, right). <p>
 *
 * <center> <a href="FittsTilt-3.jpg"><img src="FittsTilt-3.jpg" width=200 alt="image"></a> <a
 * href="FittsTilt-4.jpg"><img src="FittsTilt-4.jpg" width=200 alt="image"></a> </center> <p>
 *
 * Once all the <i>A-W</i> conditions in a block are finished, the application terminates. User performance data are
 * saved in files for follow-up analyses. The data files are located in the device's public storage directory in a
 * sub-directory named FittsTiltData. <p>
 *
 *
 * <h3>Output Data Files</h3>
 *
 * For each block of testing, three output data files are created: sd1, sd2, and sd3. ("sd" is for "summary data".) The
 * sd1 file contains data for each trial of input.  The sd2 file contains data summarizing each sequence of trials.  The
 * sd3 file contains trace data for each trial, including <i>x</i>, <i>y</i>, and <i>tilt angle</i> for each sample.  A
 * separate utility called FittsTiltTrace is available to plot the data in the sd3 files. The following are examples of
 * the summary data files: <p>
 *
 * <ul> <li>sd1 &ndash; <a href="FittsTilt-P03-S01-B01-G01-C01-POS-TG20-FIRST_ENTRY.sd1">FittsTilt-P03-S01-B01-G01
 * -C01-POS-TG20-FIRST_ENTRY.sd1</a>
 * <li>sd2 &ndash; <a href="FittsTilt-P03-S01-B01-G01-C01-POS-TG20-FIRST_ENTRY.sd2">FittsTilt-P03-S01-B01-G01-C01-POS
 * -TG20-FIRST_ENTRY.sd2</a>
 * <li>sd3 &ndash; <a href="FittsTilt-P03-S01-B01-G01-C01-POS-TG20-FIRST_ENTRY.sd3">FittsTilt-P03-S01-B01-G01-C01-POS
 * -TG20-FIRST_ENTRY.sd3</a>
 *
 * </ul> <p>
 *
 * For most analyses, the data in the sd2 files are sufficient.  The data are comma delimited for easy importing into
 * Excel or a statistics application for follow-up analyses.  An example is shown below for the data in the sd2 file
 * above. <p>
 *
 * <center> <a href="FittsTilt-5.jpg"><img src="FittsTilt-5.jpg" width=800 alt="image"></a> </center> <p>
 *
 * @author Scott MacKenzie, 2011-2016
 */
public class FittsTiltActivity extends Activity implements SensorEventListener
{
    // int constants to setup a sensor mode (see Demo_TiltMeter API for discussion)
    final static int ORIENTATION = 0;
    final static int ACCELEROMETER_ONLY = 1;
    final static int ACCELEROMETER_AND_MAGNETIC_FIELD = 2;
    final String MYDEBUG = "MYDEBUG"; // for Log.i messages
    final String APP = "FittsTilt";
    final String WORKING_DIRECTORY = "/FittsTiltData/";
    final String SD1_HEADER = "App,Participant,Session,Block,Group,Condition,OrderOfControl,Gain,Trial,targetID,A,W,fromX," +
            "fromY,targetX,targetY,selectX,selectY,transformedSelectX,transformedSelectY,positioningTime," +
            "selectionTime,movementTime,tre,tac,mdc,odc,mv,me,mo,errorFlicker,maxTilt\n";
    final String SD2_HEADER = "App,Participant,Session,Block,Group,Condition,OrderOfControl,Gain,SelectionMode,"
            + "Trials,A,W,ID," + "Ae,sdx,We,IDe,TP',PT(ms),ST(ms),MT(ms),TP(bps),"
            + "TRE,TAC,MDC,ODC,MV,ME,MO,MaxTilt\n";
    final String SD3_HEADER = "TRACE_DATA\nSequence,A,W,trial,from_x,from_y,to_x,to_y,{t_x_y_tilt}\n";
    final float RADIANS_TO_DEGREES = 57.2957795f;
    final float DEGREES_TO_RADIANS = 0.0174532925f;
    final float TWO_TIMES_PI = 6.283185307f;
    final float LOG_TWO = 0.693147181f;
    final int VIBRATION_PULSE_DURATION = 5;
    final int REFRESH_INTERVAL = 20; // milliseconds (i.e., updates @ 50 Hz)
    public float velocityXinit, velocityYinit = 0.0f;
    public float prevXvelocity, prevYvelocity = 0.0f;
    public float prevXposition, prevYposition = 0.0f;

    int touchRadius = 100;


    // setup parameters
    ExperimentPanel ep;
    String participantCode, sessionCode, blockCode, groupCode, conditionCode;
    int numberOfTargets;
    float[] amplitude, width;
    float ballScale; // scaling factor for ball (relative to smallest width)
    String orderOfControl;
    int radius;
    String hand;
    float frictionCoefficient;

    // Amit changes
    String frictionCoefficientVisualization,frictionCoefficientVisualizationMatching;

    int flicker_multiplier;
    float gain;
    long startTimeTouch = -1l;
    String selectionMode;
    boolean vibrotactileFeedback, auditoryFeedback;
    float initPosX, initPosY = 0;
    boolean even;
    int blockNumber;
    String sd2Leader;
    int screenOrientation;
    boolean canFlick, startMoving;
    float[] accValues = new float[3];
    float[] magValues = new float[3];
    float x,y,z, pitch, roll;
    int numberOfCircles, selectionCount;
    float xCenter, yCenter, xBallCenter, yBallCenter;
    AmplitudeWidth[] aw; // task conditions (A-W pairs)

    int awIdx;
    float throughput, id;
    int tre, tac, mdc, odc;
    float mv, me, mo;
    float sdx, ae, we, ide, tpe;
    int[] sdxArray;

    float panelWidth, panelHeight;
    boolean practiceMode, sequenceStarted, targetEntered;
    Vibrator vib;
    MediaPlayer clickSound;
    float tiltAngle, tiltMagnitude;
    float maxTilt, maxTiltTrial, tiltGain;
    ArrayList<TracePoint> traceArray;
    long now, lastT, startTime, elapsedTime;
    float positioningTime, selectionTime, movementTime, dT;
    TracePoint[] p; // points for the trace of the ball during a trial
    Trace t;
    BufferedWriter sd1, sd2, sd3, sd4;
    File f1, f2, f3,f4;
    StringBuilder sb1, sb2, sb3, results;
    long dwellTime;
    int stopX, stopY, startX, startY;
    boolean dwellPending;
    CountDownTimer dwellTimer;
    public float xmax,ymax;
    float velocity; // in pixels/second (velocity = tiltMagnitude * tiltVelocityGain
    float dBall; // the amount to move the ball (in pixels): dBall = dT * velocity
    float velocityInitX, velocityInitY = 0;
    float accXinit, accYinit = 0;
    float totalXtravel, totalYtravel, totalTime=0;

    float positionInitX, positionInitY = 0;
    boolean moving_y, moving_x= false;

    int ballDiameter;
    float oldT = 0;
    ScreenRefreshTimer screenRefreshTimer;

    /**
     * Below are the alpha values for the low-pass filter. The four values in each array are for the slowest to fastest
     * sampling rates, respectively. These values were determined by trial and error. There is a trade-off. Generally,
     * lower values produce smooth but sluggish responses, while higher values produced jerky but fast responses. There
     * is also a difference by order of control; hence the use of two arrays. Furthermore, there appears to be
     * difference by device (e.g., Samsung Galaxy Tab 10.1 vs. HTC Desire C). More work is needed here.
     */
    // final float[] ALPHA_VELOCITY = { 0.99f, 0.8f, 0.4f, 0.15f };
    // final float[] ALPHA_POSITION = { 0.5f, 0.3f, 0.15f, 0.10f };
    float alpha;
    int sensorMode;
    private SensorManager sm;
    private Sensor sO, sA, sM;
    int errorCount = 0;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        LinearLayout linearLayoutMain = (LinearLayout) findViewById(R.id.linarLayoutMain);
        // init study parameters
        Bundle b = getIntent().getExtras();
        orderOfControl = b.getString("orderOfControl");
        ep = (ExperimentPanel)findViewById(R.id.experimentpanel);
        radius = Integer.parseInt(b.getString("radius"));
        ep.orderOfControl = orderOfControl;
        participantCode = b.getString("participantCode");
        sessionCode = b.getString("sessionCode");
        // blockCode = b.getString("blockCode");
        groupCode = b.getString("groupCode");
        conditionCode = b.getString("conditionCode");
        numberOfTargets = b.getInt("numberOfTargets");
        String amplitudeStr = b.getString("amplitudes");
        if(amplitudeStr.equals("auto")){
            amplitude = getAutoValues();
        }
        else{
            amplitude = getValues(amplitudeStr);
        }
        width = getValues(b.getString("widths"));
        ballScale = b.getFloat("ballScale");

        hand = b.getString("hand");
        frictionCoefficient = Float.parseFloat(b.getString("frictionCoefficient"));

        // Amit changes
        frictionCoefficientVisualization = (b.getString("frictionCoefficientVisualization"));
        frictionCoefficientVisualizationMatching = (b.getString("frictionCoefficientVisualizationMatching"));

        flicker_multiplier = Integer.parseInt(b.getString("flickMultiplier"));
        gain = b.getInt("gain");
        selectionMode = b.getString("selectionMode");
        vibrotactileFeedback = b.getBoolean("vibrotactileFeedback");
        auditoryFeedback = b.getBoolean("auditoryFeedback");
        auditoryFeedback = b.getBoolean("auditoryFeedback");
        screenOrientation = b.getInt("screenOrientation");
        //Reads the touch events if its Flicker
        if(orderOfControl.equals("Flicker")){
            ep.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                     return onTouchFlick(motionEvent);
                }
            });
        }


//        ep.setBackground();
        // Amit changes background image
        if(frictionCoefficientVisualizationMatching.equalsIgnoreCase("matching")) {
            if (frictionCoefficientVisualization.equalsIgnoreCase("low")) {
                ep.setBackgroundResource(R.drawable.ic_glass);
            } else if (frictionCoefficientVisualization.equalsIgnoreCase("medium")) {
                ep.setBackgroundResource(R.drawable.ic_wood);
            } else {
                ep.setBackgroundResource(R.drawable.ic_grass);
            }
        }

        // ball scaling will be done in the ExperimentPanel
        //ep.ballScale = ballScale;

        // get this device's default orientation
        int defaultOrientation = getDefaultDeviceOrientation();

        // force the UI to appear in the device's default orientation (and stay that way)
        if (defaultOrientation == Configuration.ORIENTATION_LANDSCAPE)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // =====================
        // Sensor initialization
        // =====================

        // get sensors
        sm = (SensorManager)getSystemService(SENSOR_SERVICE);
        sO = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION); // supported on many devices
        sA = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); // supported on most devices
        sM = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD); // null on many devices

        // setup the sensor mode (see API for discussion)
        if (sO != null)
        {
            sensorMode = ORIENTATION;
            sA = null;
            sM = null;
            Log.i(MYDEBUG, "Sensor mode: ORIENTATION");
        } else if (sA != null && sM != null)
        {
            sensorMode = ACCELEROMETER_AND_MAGNETIC_FIELD;
            Log.i(MYDEBUG, "Sensor mode: ACCELEROMETER_AND_MAGNETIC_FIELD");
        } else if (sA != null)
        {
            sensorMode = ACCELEROMETER_ONLY;
            Log.i(MYDEBUG, "Sensor mode: ACCELEROMETER_ONLY");
        } else
        {
            Log.i(MYDEBUG, "Can't run demo.  Requires Orientation sensor or Accelerometer");
            this.finish();
        }

        // ===================
        // File initialization
        // ===================

        // make a working directory (if necessary) to store data files
        File dataDirectory = new File(Environment.getExternalStorageDirectory() + File.separator + WORKING_DIRECTORY);
        if (!dataDirectory.exists() && !dataDirectory.mkdirs())
        {
            Log.e(MYDEBUG, "ERROR --> FAILED TO CREATE DIRECTORY: " + WORKING_DIRECTORY);
            super.onDestroy(); // cleanup
            this.finish(); // terminate
        }
        Log.i(MYDEBUG, "Working directory=" + dataDirectory);

        /*
         * The following do-loop creates data files for output and a string sd2Leader to write to the sd2
         * output files.  Both the filenames and the sd2Leader are constructed by combining the setup parameters
         * so that the filenames and sd2Leader are unique and also reveal the conditions used for the block of input.
         *
         * The block code begins "B01" and is incremented on each loop iteration until an available
         * filename is found.  The goal, of course, is to ensure data files are not inadvertently overwritten.
         */
        blockNumber = 0;
        do
        {
            ++blockNumber;
            blockCode = String.format(Locale.CANADA, "B%02d", blockNumber);
            String baseFilename = String.format(Locale.CANADA, "%s-%s-%s-%s-%s-%s-%s-TG%d-%s", APP, participantCode,
                    sessionCode, blockCode, groupCode, conditionCode, orderOfControl.substring(0, 3).toUpperCase
                            (Locale.CANADA), (int)gain, selectionMode.toUpperCase(Locale.CANADA));

            f1 = new File(dataDirectory, baseFilename + ".sd1");
            f2 = new File(dataDirectory, baseFilename + ".sd2");
            f3 = new File(dataDirectory, baseFilename + ".sd3");
            f4 = new File(dataDirectory, baseFilename+"_EM_test.txt");


            // also make a comma-delimited leader that will begin each data line written to the sd2 file
            sd2Leader = String.format(Locale.CANADA, "%s,%s,%s,%s,%s,%s,%s,TG%d,%s,", APP, participantCode, sessionCode,
                    blockCode, groupCode, conditionCode, orderOfControl.substring(0, 3)
                            .toUpperCase(Locale.CANADA), (int)gain, selectionMode.toUpperCase(Locale.CANADA));

        } while (f1.exists() || f2.exists());

        try
        {
            sd1 = new BufferedWriter(new FileWriter(f1));
            sd2 = new BufferedWriter(new FileWriter(f2));
            sd3 = new BufferedWriter(new FileWriter(f3));
            //sd4 = new BufferedWriter(new FileWriter(f4));

            // output header in sd1 file
            sd1.write(SD1_HEADER, 0, SD1_HEADER.length());
            sd1.flush();

            // output header in sd2 file
            sd2.write(SD2_HEADER, 0, SD2_HEADER.length());
            sd2.flush();

            // output header in sd3 file
            sd3.write(SD3_HEADER, 0, SD3_HEADER.length());
            sd3.flush();


        } catch (IOException e)
        {
            Log.e(MYDEBUG, "ERROR OPENING DATA FILES! e=" + e.toString());
            super.onDestroy();
            this.finish();
        } // end file initialization

        numberOfCircles = numberOfTargets;

        tiltGain = gain;

        dwellTime = -1;
        if (selectionMode.equals(FittsTiltSetup.DWELL_500))
            dwellTime = 500;
        else if (selectionMode.equals(FittsTiltSetup.DWELL_400))
            dwellTime = 400;
        else if (selectionMode.equals(FittsTiltSetup.DWELL_300))
            dwellTime = 300;

        dwellTimer = new CountDownTimer(dwellTime, dwellTime)
        {
            public void onTick(long millisUntilFinished)
            {
            }

            public void onFinish()
            {
                dwellPending = false;
                doTargetSelected();
            }
        };

        vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        clickSound = MediaPlayer.create(this, R.raw.click);

        // The following size parameters were chosen by examining the output files. The goal here
        // is to prevent the StringBuilder objects from requesting more space as data are added.
        sb1 = new StringBuilder(3000);
        sb2 = new StringBuilder(400);
        sb3 = new StringBuilder(30000); // a lot of space needed for trace data
        results = new StringBuilder(500);
        //traceArray = new Vector<TracePoint>();
        traceArray = new ArrayList<TracePoint>();

        String[] s = new String[4];
        s[0] = "Practice";
        s[1] = "Order of control = " + orderOfControl;
        s[2] = "Tilt gain = " + (int)tiltGain;
        s[3] = "Selection mode = " + selectionMode;

        ep.practice = s;

        lastT = System.nanoTime();
        alpha = 0.05f; // constant for low-pass filter

        // setup the screen refresh timer (updates every REFRESH_INTERVAL milliseconds)
        screenRefreshTimer = new ScreenRefreshTimer(REFRESH_INTERVAL, REFRESH_INTERVAL);

    } // end onCreate

    private float[] getAutoValues() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float maxAmplitude = displayMetrics.heightPixels * .8f;
        float[] output = new float[3];
        output[0] = maxAmplitude * 1/3;
        output[1] = maxAmplitude * 2/3;
        output[2] = maxAmplitude;
        return output;
    }

    /*
    * Event Listener for Touch Events
    * */
    public boolean onTouchFlick(MotionEvent motionEvent){

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int X = (int) motionEvent.getX();
        int Y = (int) motionEvent.getY();
        int eventAction = motionEvent.getAction();
        if(!canFlick) {
            //Checks if the screen has been touched saves the info at that time.
            if (eventAction == MotionEvent.ACTION_DOWN) {

                if ((X >= (ep.xBall - touchRadius) && X <= (ep.xBall + touchRadius)) && (Y >= (ep.yBall - touchRadius) && Y <= (ep.yBall + touchRadius))) {
                    startTimeTouch = System.currentTimeMillis();
                    canFlick = true;
                    startX = X;
                    startY = Y;
                }
            }
        }
        //When the touch is released checks initializes the velocity and acc variables
            if (eventAction == MotionEvent.ACTION_UP) {
                stopX = X;
                stopY = Y;
                if (canFlick) {
                    positionInitX =0;
                    positionInitY = 0;
                    float deltaX = stopX - startX;
                    float deltaY = stopY - startY;
                    double difference;
//                    if ((deltaX >= -50 && deltaX <= 50)) {
//                        deltaX = 0;
//                    }
//                    if ((deltaY >= -50 && deltaY <= 50)) {
//                        deltaY = 0;
//                    }
                    deltaX = deltaX / displayMetrics.xdpi;
                    deltaY = deltaY / displayMetrics.ydpi;
                    long stopTime = System.currentTimeMillis();
                    difference = (stopTime - startTimeTouch) * 0.001;


                    velocityXinit = flicker_multiplier * (deltaX / (float) difference);
                    velocityYinit = flicker_multiplier * (deltaY / (float) difference);
//                    float maxVal = Math.max(abs(velocityXinit), abs(velocityYinit));
//                    velocityXinit = velocityXinit<0?maxVal*(-1):maxVal;
//                    velocityYinit = velocityYinit<0?maxVal*(-1):maxVal;
                    accXinit = frictionCoefficient * ((float) 286);
                    accYinit = frictionCoefficient * ((float) 286);
                    if (accXinit != 0 && accYinit != 0) {
                        totalXtravel = abs((velocityXinit) / (-1 * (accXinit)));
                        totalYtravel = abs((velocityYinit) / (-1 * (accXinit)));
                    } else {
                        totalXtravel = 1;
                        totalYtravel = 1;
                    }

                    startMoving = true;
                    startX = stopX;
                    startY = stopY;
                }
            }



        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        if (!hasFocus)
            return;

        panelWidth = ep.getWidth();
        panelHeight = ep.getHeight();
        xCenter = panelWidth / 2f;
        yCenter = panelHeight / 2f;
        if(orderOfControl.equals("Flicker"))
        {

            if(hand.equals("Left")){
                ep.xBall = radius;
                ep.yBall = panelHeight - radius;
                ep.startX = radius - 300 ;
                ep.startY = panelHeight - radius ;
                ep.practiceX = radius;
                ep.practiceY = panelHeight - radius - 300;
            }
            else{
            ep.xBall = panelWidth - radius;
            ep.yBall = panelHeight - (radius);

                ep.startX = panelWidth - radius - 300 ;
                ep.startY = panelHeight - radius ;
                ep.practiceX = panelWidth - radius;
                ep.practiceY = panelHeight - radius - 300;
            }
        }
        else{
        ep.xBall = xCenter;
        ep.yBall = yCenter; // start the ball in the center of the display

        }

        // scale target amplitudes and widths as appropriate for screen size
        // NOTE:  smaller of panelWidth or panelHeight constrains allowable span for target conditions
        float span = Math.min(panelWidth, panelHeight);

        span *= .9f; // only use 90% of available space for widest/tallest condition
        float largestAmplitude = 0;
        for (float a : amplitude)
            largestAmplitude = Math.max(largestAmplitude, a);

        float largestWidth = 0;
        for (float w : width)
            largestWidth = Math.max(largestWidth, w);

        // compute scaling factor
        float scaleFactor = span / (largestAmplitude + largestWidth); // scale factor computed

        if(orderOfControl.equals("Flicker")){
            // now do the scaling
            scaleFactor = 1;
            for (int i = 0; i < amplitude.length; ++i) // scale amplitudes
                amplitude[i] *= scaleFactor;
            for (int i = 0; i < width.length; ++i) // scale widths
                width[i] *= scaleFactor;
        }
        else {
            // now do the scaling
            for (int i = 0; i < amplitude.length; ++i) // scale amplitudes
                amplitude[i] *= scaleFactor;
            for (int i = 0; i < width.length; ++i) // scale widths
                width[i] *= scaleFactor;
        }
        // get an amplitude-width array (from the scaled amplitudes and widths)
        aw = getAmplitudeWidthArray(amplitude, width);
        awIdx = 0;

        // find smallest target width (needed to scale ball diameter)
        float smallestWidth = Float.MAX_VALUE;
        for (float w : width)
            smallestWidth = Math.min(smallestWidth, w);

        // scale the ball as per setup parameter
        ballDiameter = Math.round(smallestWidth * ballScale);
        ep.ball = Bitmap.createScaledBitmap(ep.ball, ballDiameter, ballDiameter, true);

        ep.readyToDraw = true; // now that the ball is correctly size, it's OK to draw the UI

        // OK, we're ready to start regular updates of the UI
        screenRefreshTimer.start();
    }

    /*
     * Get the default orientation of the device. This is needed to correctly map the sensor data
     * for pitch and roll (see onSensorChanged). See...
     *
     * http://stackoverflow.com/questions/4553650/how-to-check-device-natural-default-orientation-on-
     * android-i-e-get-landscape
     */
    public int getDefaultDeviceOrientation()
    {
        WindowManager windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
        Configuration config = getResources().getConfiguration();
        int rotation = windowManager.getDefaultDisplay().getRotation();

        if (((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) && config.orientation ==
                Configuration.ORIENTATION_LANDSCAPE)
                || ((rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) && config.orientation ==
                Configuration.ORIENTATION_PORTRAIT))
            return Configuration.ORIENTATION_LANDSCAPE;
        else
            return Configuration.ORIENTATION_PORTRAIT;
    }

    // convert the amplitude/width string in the spinners to float array
    private float[] getValues(String valuesArg)
    {
        String[] s = valuesArg.split("[\\s]*,[\\s]*");
        float[] f = new float[s.length];
        for (int i = 0; i < s.length; ++i)
            f[i] = Float.parseFloat(s[i]);
        return f;
    }

    protected void onResume()
    {
        super.onResume();
        sm.registerListener(this, sO, SensorManager.SENSOR_DELAY_GAME);
        sm.registerListener(this, sA, SensorManager.SENSOR_DELAY_GAME);
        sm.registerListener(this, sM, SensorManager.SENSOR_DELAY_GAME);
    }

    protected void onPause()
    {
        super.onPause();
        sm.unregisterListener(this);
    }

    // implement SensorEventListener methods
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
    }

    public void onSensorChanged(SensorEvent se)
    {
        // =======================================================
        // DETERMINE DEVICE PITCH AND ROLL (VARIES BY SENSOR MODE)
        // =======================================================

        switch (sensorMode)
        {
            // ---------------------------------------------------------------------------------------------
            case ORIENTATION:
                pitch = se.values[1];
                roll = se.values[2];
                break;

            // ---------------------------------------------------------------------------------------------
            case ACCELEROMETER_AND_MAGNETIC_FIELD:
                // smooth the sensor values using a low-pass filter
                if (se.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                    accValues = lowPass(se.values.clone(), accValues, alpha); // filtered
                if (se.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                    magValues = lowPass(se.values.clone(), magValues, alpha); // filtered

                if (accValues != null && magValues != null)
                {
                    // compute pitch and roll
                    float R[] = new float[9];
                    float I[] = new float[9];
                    boolean success = SensorManager.getRotationMatrix(R, I, accValues, magValues);
                    if (success) // see SensorManager API
                    {
                        float[] orientation = new float[3];
                        SensorManager.getOrientation(R, orientation); // see getOrientation API
                        pitch = orientation[1] * RADIANS_TO_DEGREES;
                        roll = -orientation[2] * RADIANS_TO_DEGREES;
                    }
                }
                break;

            // ---------------------------------------------------------------------------------------------
            case ACCELEROMETER_ONLY:

				/*
                 * Use this mode if the device has an accelerometer but no magnetic field sensor and
				 * no orientation sensor (e.g., HTC Desire C, Asus MeMOPad). This algorithm doesn't
				 * work quite as well, unfortunately. See...
				 *
				 * http://www.hobbytronics.co.uk/accelerometer-info
				 */

                // smooth the sensor values using a low-pass filter
                if (se.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                    accValues = lowPass(se.values.clone(), accValues, alpha);

                x = accValues[0];
                y = accValues[1];
                z = accValues[2];
                pitch = -(float)Math.atan(y / Math.sqrt(x * x + z * z)) * RADIANS_TO_DEGREES;
                roll = (float)Math.atan(x / Math.sqrt(y * y + z * z)) * RADIANS_TO_DEGREES;
                break;
        }
    }

    private void configureTaskCircles(int awIdx)
    {
        if(orderOfControl.equals("Flicker")){
            int i = 0;
            float radius = (aw[awIdx].a);

            //Getting the angle of the arch via start and end points
            float xCenter = ep.xBall;
            float yCenter = ep.yBall;
            float endX = panelWidth-(panelWidth*0.9f);
            float dX = (endX-xCenter);
            float endY = (float)Math.sqrt((radius*radius)-(dX*dX));
            float dY = (yCenter-endY) - (yCenter-radius);
            float dis = (float) Math.sqrt(dX*dX + dY*dY); //distance between the points
            double a = Math.acos(1-((dis*dis)/(2*(radius*radius)))); //the angle between the points is 1 - (dis^2/2radius^2)
            float diff = xCenter/numberOfCircles-1; //Getting the distance between the points
            float prevPoint = xCenter;
            if(radius>700) {
                while (i < numberOfCircles) {
                    float x = prevPoint;
                    float difX = x-xCenter;
                    float y = (float) Math.sqrt((radius * radius) - (difX * difX));
                    ep.taskCircles[i] = new Circle(x, abs(yCenter - y), aw[awIdx].w / 2f, Circle.NORMAL);
                    prevPoint = prevPoint - diff;
                    i++;
                }
            }
            else{
                while(i<numberOfCircles)
                {
                    float x = ep.xBall - (aw[awIdx].a) * (float)Math.cos(a * ((float)i/ (numberOfCircles)));
                    if(x<0){
                        x = xCenter + x;
                    }
                    float y = ep.yBall - (aw[awIdx].a) * (float)Math.sin(a * ((float)i/ (numberOfCircles)));
                    ep.taskCircles[i] = new Circle(abs(x), abs(y), aw[awIdx].w / 2f, Circle.NORMAL);
                    i++;
                }
            }

        }
        else{
            for (int i = 0; i < numberOfCircles; ++i)
            {
                float x = xCenter + (aw[awIdx].a / 2f) * (float)Math.cos(TWO_TIMES_PI * ((float)i / numberOfCircles));
                float y = yCenter + (aw[awIdx].a / 2f) * (float)Math.sin(TWO_TIMES_PI * ((float)i / numberOfCircles));
                ep.taskCircles[i] = new Circle(x, y, aw[awIdx].w / 2f, Circle.NORMAL);
            }

        }
        id = (float)Math.log(aw[awIdx].a / (aw[awIdx].w - ballDiameter) + 1f) / LOG_TWO;

        // Don't set target yet. This is done when start circle is selected.
    }

    private AmplitudeWidth[] getAmplitudeWidthArray(float[] aArray, float[] wArray)
    {
        AmplitudeWidth[] aw = new AmplitudeWidth[aArray.length * wArray.length];
        for (int i = 0; i < aw.length; ++i)
            aw[i] = new AmplitudeWidth(aArray[i / wArray.length], wArray[i % wArray.length]);

        // shuffle
        Random r = new Random();
        for (int i = 0; i < aw.length; ++i)
        {
            int idx = r.nextInt(aw.length);
            AmplitudeWidth temp = aw[idx];
            aw[idx] = aw[i];
            aw[i] = temp;
        }
        return aw;
    }

    private void doStartCircleClicked()
    {
        resetPosition();
        if (ep.done) // start circle displayed after last sequence, select to finish
            doEndBlock();

        ep.waitStartCircleSelect = false;
        ep.waitPracticeCircleSelect = false;

        if (practiceMode)
        {
            practiceMode = false;
            awIdx = 0;
        }
        ep.waitPracticeCircleSelect = false; // decline practice trials
        even = false;

        // provide feedback (as per setup)
        if (vibrotactileFeedback)
            vib.vibrate(VIBRATION_PULSE_DURATION);
        if (auditoryFeedback)
            clickSound.start();

        if (awIdx < aw.length)
        {
            ep.taskCircles = new Circle[numberOfCircles];
            configureTaskCircles(awIdx);

        }
//        ep.taskCircles = Arrays.copyOfRange(ep.taskCircles, 0, numberOfCircles);
        ep.taskCircles[0].status = Circle.TARGET;
        ep.targetCircle = ep.taskCircles[0];
        selectionCount = 0;
    }

    private void doPracticeCircleClicked()
    {
        resetPosition();
        // provide feedback (as per setup)
        if (vibrotactileFeedback)
            vib.vibrate(VIBRATION_PULSE_DURATION);
        if (auditoryFeedback)
            clickSound.start();

        ep.waitPracticeCircleSelect = false;
        ep.waitStartCircleSelect = false;

        if (practiceMode)
        {
            even = false;
            ++awIdx;
            if (awIdx < aw.length)
            {
                configureTaskCircles(awIdx);
                ep.taskCircles[0].status = Circle.TARGET;
                ep.targetCircle = ep.taskCircles[0];
            } else
                ep.done = true;
        } else
        {
            practiceMode = true;
            even = false;
            awIdx = 0;
            ep.taskCircles = new Circle[numberOfCircles];
            configureTaskCircles(awIdx);
            ep.taskCircles[0].status = Circle.TARGET;
            ep.targetCircle = ep.taskCircles[0];
        }
        selectionCount = 0;
    }

    // Done! close data files and exit
    private void doEndBlock()
    {
        try
        {
            sd1.close();
            sd2.close();
            sd3.close();

            // Make the saved data files visible in Windows Explorer
            // There seems to be bug doing this with Android 4.4.  I'm using the following
            // code, instead of sendBroadcast.  See...
            // http://code.google.com/p/android/issues/detail?id=38282
            MediaScannerConnection.scanFile(this, new String[] {f1.getAbsolutePath(),
                    f2.getAbsolutePath(), f3.getAbsolutePath()}, null, null);
        } catch (IOException e)
        {
            Log.d("MYDEBUG", "file close exception: " + e);
        }
        this.finish();
    }
    void resetPosition(){
        if(orderOfControl.equals("Flicker")){
            startMoving = false;
            canFlick = false;
            if(hand.equals("Left")){
                ep.xBall = radius;
                ep.yBall = panelHeight - radius;
            }
            else{
            ep.xBall = panelWidth - radius;
            ep.yBall = panelHeight - radius;
            }
        }
    }
    void failed(){
        errorCount++;
        if (vibrotactileFeedback)
            vib.vibrate(VIBRATION_PULSE_DURATION);
        if (auditoryFeedback)
            clickSound.start();
    }



    void doTargetSelected()
    {
        // provide feedback (as per setup)
            resetPosition();

        if (vibrotactileFeedback)
            vib.vibrate(VIBRATION_PULSE_DURATION);
        if (auditoryFeedback)
            clickSound.start();

        if (!sequenceStarted) // 1st target selection (beginning of sequence)
        {
            // first trace sample for sequence (on select of 1st target)
            traceArray.add(new TracePoint((int)xBallCenter, (int)yBallCenter, (int)(now / 1000000), tiltMagnitude));
            sequenceStarted = true;
            startTime = now;
            advanceTarget();
            ep.fromTargetCircle = ep.taskCircles[0];
            positioningTime = selectionTime = movementTime = throughput = 0f;
            tre = tac = mdc = odc = 0;
            mv = me = mo = 0f;
            sdx = ae = 0;
            sdxArray = new int[numberOfCircles];
            maxTilt = 0f;
            return;
        }

        if (!practiceMode) // collect data (but not in practice mode)
        {
            p = new TracePoint[traceArray.size()];
            p = traceArray.toArray(p);
            traceArray = new ArrayList<TracePoint>();

            // a lot of time is consumed in this constructor (all accuracy measures below are computed)
            t = new Trace((int)aw[awIdx].a, (int)aw[awIdx].w, (int)ep.fromTargetCircle.x, (int)ep.fromTargetCircle.y,
                    (int)ep.targetCircle.x, (int)ep.targetCircle.y, ballDiameter, p);

            positioningTime += t.positioningTime;
            selectionTime += t.selectionTime;
            movementTime += t.movementTime;
            throughput += (id / (t.movementTime / 1000f));
            tre += t.tre;
            tac += t.tac;
            mdc += t.mdc;
            odc += t.odc;
            mv += t.mv;
            me += t.me;
            mo += t.mo;
            sdxArray[selectionCount] = t.transformedSelectX;
            ae += t.transformedSelectX;
            maxTilt += maxTiltTrial;

            sb1.append(String.format(Locale.CANADA, "%s,%s,%s,%s,%s,%s,%s,%d,%s,%d,%s,%.1f\n", APP, participantCode,
                    sessionCode, blockCode, groupCode, conditionCode, orderOfControl, (int)tiltGain, selectionMode,
                    selectionCount, t.getMeasures(),errorCount,maxTiltTrial));

            String base = awIdx + "," + aw[awIdx].a + "," + aw[awIdx].w + "," + selectionCount + ","
                    + ep.fromTargetCircle.x + "," + ep.fromTargetCircle.y + "," + ep.targetCircle.x + ","
                    + ep.targetCircle.y + ",";

            sb3.append(String.format("%s,t=,%s\n", base, t.tPoints()));
            sb3.append(String.format("%s,x=,%s\n", base, t.xPoints()));
            sb3.append(String.format("%s,y=,%s\n", base, t.yPoints()));
            sb3.append(String.format("%s,tilt=,%s\n", base, t.tiltPoints()));
        }

        advanceTarget();
        targetEntered = false;
        maxTiltTrial = 0f; // reset at end of each trial
        ++selectionCount;
        if (selectionCount == numberOfCircles) // finished sequence
            doEndSequence();
        else
        // not finished sequence
        {
            /*
             * For all trials, except the last in a sequence, the last sample is the also the first sample of the
			 * next trial.
			 */
            traceArray.add(new TracePoint((int)xBallCenter, (int)yBallCenter, (int)(now / 1000000), tiltMagnitude));
        }
    }

    void doEndSequence()
    {
        if (practiceMode)
        {
            // practiceMode = false;
            ep.waitStartCircleSelect = true;
            ep.waitPracticeCircleSelect = true;
            sequenceStarted = false;
            return;
        }
        positioningTime /= numberOfCircles;
        selectionTime /= numberOfCircles;
        movementTime /= numberOfCircles;
        throughput /= numberOfCircles;
        mv /= numberOfCircles;
        me /= numberOfCircles;
        mo /= numberOfCircles;
        sdx = standardDeviation(sdxArray);
        ae /= numberOfCircles;
        we = 4.133f * sdx;
        ide = (float)Math.log(ae / we + 1) / LOG_TWO;
        tpe = ide / (movementTime / 1000f);
        maxTilt /= numberOfCircles;

        sb2.append(sd2Leader);
        sb2.append(String.format(Locale.CANADA, "%d,%d,%d,%.1f,%.1f,%.1f,%.1f,%.1f,%.1f,%.1f,%.1f,%.1f,%.1f,%d,%d,%d," +
                        "%d,%.1f,%.1f,%.1f,%.1f\n", numberOfCircles, (int)aw[awIdx].a, (int)aw[awIdx].w, id, ae, sdx,
                we, ide, tpe, positioningTime, selectionTime, movementTime, throughput, tre, tac, mdc, odc, mv, me, mo,
                maxTilt));

        // write data to files at end of each sequence
        try
        {
            sd1.write(sb1.toString(), 0, sb1.length());
            sd1.flush();
            sd2.write(sb2.toString(), 0, sb2.length());
            sd2.flush();
            sd3.write(sb3.toString(), 0, sb3.length());
            sd3.flush();
        } catch (IOException e)
        {
            Log.d("MYDEBUG", "ERROR WRITING TO DATA FILES! e = " + e);
            this.finish();
        }

        ep.waitStartCircleSelect = true;
        elapsedTime = (now - startTime); // nanoseconds
        elapsedTime = elapsedTime / 1000000; // milliseconds
        sequenceStarted = false;
        ++awIdx; // next A-W condition

        results.append(String.format(Locale.CANADA, "Block %d:", Integer.parseInt(blockCode.substring(1))));
        results.append(String.format(Locale.CANADA, "Sequence %d of %d:", awIdx, aw.length));
        results.append(String.format(Locale.CANADA, "Tilt gain = %d:", (int)tiltGain));
        results.append(String.format("Selection mode = %s:", selectionMode));
        results.append(String.format(Locale.CANADA, "Time = %.1f seconds:", (elapsedTime / 1000f)));
        results.append(String.format(Locale.CANADA, "Throughput = %.2f bps", throughput));
        ep.results = results.toString().split(":");

        positioningTime = 0;
        selectionTime = 0;
        movementTime = 0;
        throughput = 0;
        maxTilt = 0;
        sb1.delete(0, sb1.length());
        sb2.delete(0, sb2.length());
        sb3.delete(0, sb3.length());
        results.delete(0, results.length());

        if (awIdx < aw.length)
            configureTaskCircles(awIdx);
        else
            ep.done = true;
    }

    void advanceTarget()
    {
        int i;
        for (i = 0; i < ep.taskCircles.length; ++i)
            if(ep.taskCircles != null) {
                if (ep.taskCircles[i].status == Circle.TARGET)
                    break; // i is index of current target
            }


        ep.taskCircles[i].status = Circle.NORMAL; // change current target to "normal", then find the next target
        int halfWay = ep.taskCircles.length / 2;
        int oddAdjust = ep.taskCircles.length % 2;

        int next;
        if (oddAdjust == 0) // even number of targets (it's a bit trickier for an even number of targets)
            next = even ? (i + halfWay) % ep.taskCircles.length : (i + halfWay + 1) % ep.taskCircles.length;
        else // odd number of targets
            next = (i + halfWay + oddAdjust) % ep.taskCircles.length;

        ep.taskCircles[next].status = Circle.TARGET;
        ep.fromTargetCircle = ep.taskCircles[i];
        ep.targetCircle = ep.taskCircles[next];
        even = !even;
    }

    // Low pass filter (smoothing algorithm) for sensor data.
    // See http://blog.thomnichols.org/2011/08/smoothing-sensor-data-with-a-low-pass-filter
    // More work is needed to decide on the best alpha. See comments above.
    protected float[] lowPass(float[] input, float[] output, float alpha)
    {
        for (int i = 0; i < input.length; i++)
            output[i] = output[i] + alpha * (input[i] - output[i]);
        return output;
    }

    // compute standard deviation of values in an int array
    private float standardDeviation(int[] n)
    {
        float m = mean(n);
        float t = 0.0f;
        for (int i : n)
            t += (m - i) * (m - i);
        return (float)Math.sqrt(t / (n.length - 1.0));
    }

    // compute the mean of values in a int array
    private float mean(int n[])
    {
        float mean = 0.0f;
        for (int i : n)
            mean += i;
        return mean / n.length;
    }

    // simple class to hold the amplitude and width for a Fitts' law task
    private class AmplitudeWidth
    {
        float a, w;

        AmplitudeWidth(float aArg, float wArg)
        {
            a = aArg;
            w = wArg;
        }
    }

    // screen updates are initiated in onFinish which executes every REFRESH_INTERVAL milliseconds
    private class ScreenRefreshTimer extends CountDownTimer
    {

        ScreenRefreshTimer(long millisInFuture, long countDownInterval)
        {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished)
        {
        }

        @Override
        public void onFinish()
        {
            // get current time and delta since last onDraw
            now = System.nanoTime();
            dT = (now - lastT) / 1000000000f; // seconds
            lastT = now;

            tiltMagnitude = (float)Math.sqrt(pitch * pitch + roll * roll);
            tiltAngle = tiltMagnitude == 0f ? 0f : (float)Math.asin(roll / tiltMagnitude) * RADIANS_TO_DEGREES;

            if (pitch > 0 && roll > 0)
                tiltAngle = 360f - tiltAngle;
            else if (pitch > 0 && roll < 0)
                tiltAngle = -tiltAngle;
            else if (pitch < 0 && roll > 0)
                tiltAngle = tiltAngle + 180f;
            else if (pitch < 0 && roll < 0)
                tiltAngle = tiltAngle + 180f;

            // This is the only code that distinguishes velocity-control from position-control
            if (orderOfControl.equals("Velocity")) // velocity control
            {
                // compute how far the ball should move
                velocity = tiltMagnitude * tiltGain;
                dBall = dT * velocity; // make the ball move this amount (pixels)

                // compute the ball's new coordinates
                float dx = (float)Math.sin(tiltAngle * DEGREES_TO_RADIANS) * dBall;
                float dy = -(float)Math.cos(tiltAngle * DEGREES_TO_RADIANS) * dBall;
                ep.xBall += dx;
                ep.yBall += dy;

            }
            else if (orderOfControl.equals("Position")) // position control
            {
                // compute how far the ball should move
                dBall = tiltMagnitude * tiltGain;

                // compute the ball's new coordinates

                // the following is an attempt to fix some jitter/jump issues with ball movement
                float sin = (float)Math.sin(tiltAngle * DEGREES_TO_RADIANS);
                sin = (sin == 1) ? -sin : sin;
                float cos = (float)Math.cos(tiltAngle * DEGREES_TO_RADIANS);
                cos = (cos == 1) ? -cos : cos;

                float dx = sin * dBall;
                float dy = -cos * dBall;
                ep.xBall = xCenter + dx;
                ep.yBall = yCenter + dy;


            } else if (orderOfControl.equals("Physics1"))
            {
                float multiplier = 1  ; //Multiplier
                float dPitch = Math.round(-pitch);
                float dRoll = Math.round(-roll);
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels;
                int width = displayMetrics.widthPixels;
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);

                //Getting the position of X
                float accX = ((float)5/7)*((float)386.09) * ((float)Math.sin(dRoll*DEGREES_TO_RADIANS));
                float velocityX;
                float positionX = 0f;
                velocityX = velocityInitX + accX*dT;
                float dX = velocityX*dT;
                float oldPositionX = positionX;
                positionX =positionInitX + dX*multiplier + (1/2 * accX * (float)Math.pow(dT,2));
                //positionX =positionInitX + dX*multiplier;
                float positionX_pixels = positionX * displayMetrics.xdpi;
                if(abs(positionX) > (width/2)) positionX = oldPositionX;



                //Getting the position of Y
                float accY = ((float)5/7)*((float)386.09) * ((float)Math.sin(dPitch*DEGREES_TO_RADIANS));
                float velocityY;
                float positionY = positionInitY;
                velocityY = velocityInitY + accY*dT;
                float dY = velocityY*dT;
                float oldPosition = positionY;
                positionY =positionInitY + dY*multiplier + (1/2 * accY * (float)Math.pow(dT, 2));
                //positionY =positionInitY + dY*multiplier;
                float positionY_pixels = positionY * displayMetrics.ydpi;
                if(abs(positionY) > (height/2)) positionY = oldPosition;

                float x =  xCenter + positionX_pixels;
                float positionX_old = positionInitX + xCenter;
                if((positionX_old < width && positionX_old>=0) && !(x < width && x>=0)){
                  if(x<=0){x=0;} else {x=width-1;}
                }

                if(x < width && x>=0) {
                    ep.xBall = x;
                    positionInitX = positionX;
                    velocityInitX = velocityX - velocityInitX;

                }

                float y = yCenter + positionY_pixels;
                if(y <height && y>=0)
                {
                    ep.yBall =  y;
                    positionInitY = positionY;
                    velocityInitY =  velocityY - velocityInitY;

                }


                initPosX = positionX;
                initPosY = positionY;

                Log.e("Info Delta", "Position X:"+ positionX+" Position Y:"+ positionY);
            } else if (orderOfControl.equals("Friction"))
            {

                float dPitch = Math.round(-pitch);
                float dRoll = Math.round(-roll);
                float frictionX, frictionY;

                //Check if it was moving from prev round and based on that checks friction
                if(moving_x || moving_y){
                    frictionX = (frictionCoefficient)*(float)Math.cos(dRoll*DEGREES_TO_RADIANS);
                    frictionY = (frictionCoefficient)*(float)Math.cos(dPitch*DEGREES_TO_RADIANS);
                }
                else{
                    frictionX = ((float)abs(Math.tan(dRoll*DEGREES_TO_RADIANS)));
                    frictionY = ((float)abs(Math.tan(dPitch*DEGREES_TO_RADIANS)));
                }

                float multiplier = 1  ; //Multiplier

                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels;
                int width = displayMetrics.widthPixels;
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);


                //Getting the position of X
                float accX = ((float)5/7)*((float)386.09) * ((float)Math.sin(dRoll*DEGREES_TO_RADIANS));
                float velocityX;
                float positionX = 0f;
                velocityX = (velocityInitX + accX*dT) - ((frictionX)*(velocityInitX + accX*dT));
                float dX = velocityX*dT;
                positionX =positionInitX + dX*multiplier + (1/2 * accX * (float)Math.pow(dT,2));
                float positionX_pixels = positionX * displayMetrics.xdpi;

                //Getting the position of Y
                float accY = ((float)5/7)*((float)386.09) * ((float)Math.sin(dPitch*DEGREES_TO_RADIANS));
                float velocityY;
                float positionY = positionInitY;
                velocityY = (velocityInitY + accY*dT) - ((frictionY)*(velocityInitY + accY*dT));
                float dY = velocityY*dT;
                float oldPosition = positionY;
                positionY =positionInitY + dY*multiplier + (1/2 * accY * (float)Math.pow(dT, 2));
                float positionY_pixels = positionY * displayMetrics.ydpi;
                if(abs(positionY) > (height/2)) positionY = oldPosition;

                //Updates the position of the ball
                if((xCenter + positionX_pixels) < width && (xCenter + positionX_pixels) >=0) {
                    ep.xBall = xCenter + positionX_pixels;
                    positionInitX = positionX;
                    velocityInitX = velocityX - velocityInitX;
                    if(abs(velocityInitX) > 0){
                        moving_x = true;
                    }
                    else{
                        moving_x = false;
                    }

                }
                if((yCenter + positionY_pixels) <height && (yCenter + positionY_pixels)>=0)
                {
                    ep.yBall =  yCenter + positionY_pixels;
                    positionInitY = positionY;
                    velocityInitY =  velocityY - velocityInitY;
                    if(abs(velocityInitY) > 0){
                        moving_y = true;
                    }
                    else{
                        moving_y = false;
                    }

                }


            }
            else if (orderOfControl.equals("Flicker"))
            {
                //The Flick Order of Control for Testing
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels;
                int width = displayMetrics.widthPixels;

                if(startMoving) {
                    accXinit = ((velocityXinit>0)? accXinit*(-1) : accXinit);
                    accYinit = ((velocityYinit >0)? accYinit*(-1) : accYinit);
                    float positionX = velocityXinit*dT - ((accXinit/2)*(float)Math.pow(((double) dT),2));
                    positionX = (positionX) * displayMetrics.xdpi;
                    float positionY = velocityYinit *dT - ((accYinit/2)*(float)Math.pow(((double) dT),2));
                    positionY = (positionY) * displayMetrics.ydpi;
                    float x = ep.xBall + positionX;
                    float y = ep.yBall + positionY;
                    if(x<width && x>0)
                    {
                        ep.xBall += positionX;
                        positionInitX = positionX - positionInitX;
                        totalTime += dT;
                        prevXvelocity = velocityXinit;
                        float f = velocityXinit + accYinit*dT;
                        if(f>0 && velocityXinit>0){
                            velocityXinit = f;
                        }
                        else if(f<0 &&  velocityXinit<0){
                            velocityXinit = f;
                        }
                        else{
                            velocityXinit = 0;
                            positionInitX = 0;
                        }
                    }
                    else{
                        positionX = 0;

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        resetPosition();
                        failed();
                    }

                    if(y<height && y>0)
                    {
                        ep.yBall += positionY;
                        positionInitY = positionY - positionInitY;
                        totalTime += dT;
                        prevYvelocity = velocityYinit;
                        float f = velocityYinit +accYinit*dT;
                        if(f>0 && velocityYinit >0){
                            velocityYinit = f;
                        }
                        else if(f<0 &&  velocityYinit <0){
                            velocityYinit = f;
                        }
                        else{
                            velocityYinit = 0;
                            positionInitY = 0;

                        }
                    }
                    else{
                        positionY = 0;
                        startMoving = false;

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        resetPosition();
                        failed();
                    }
//                    Log.d("testing_passed", "X: "+ ep.xBall+ ", targetX:"+ep.targetCircle.x+" ,y:"+ep.yBall+" ,targetY:"+ep.targetCircle.y);

//                    if(prevXvelocity != 0 && velocityXinit == 0 ){
//                    }else if(prevYposition != 0 && velocityYinit==0){
//                        Log.d("movements", "stopY");
//
//                    }

                    Log.d("movements", "prevY:"+prevYvelocity+", velocityY:"+velocityYinit+", prevX:"+prevXvelocity+", velocityX:"+velocityXinit);
//
                    if((prevYvelocity == 0 && velocityYinit == 0) && velocityXinit == 0){
                        Log.d("movements", "stopY");

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        resetPosition();
                        failed();
                    }
                    else if((prevXvelocity == 0 && velocityXinit == 0) && velocityYinit == 0){
                        Log.d("movements", "stopX");

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        resetPosition();
                        failed();}
//                    }else if((prevXvelocity != 0 && velocityXinit == 0) || (prevYvelocity!=0 && velocityYinit == 0)){
//                        //ep.targetCircle.collidedWithCircle(ep.xBall, ep.yBall);
//                        try {
//                            Thread.sleep(100);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        resetPosition();
//                        failed();
//                    }
                }
            }


            // keep the ball visible

            ep.xBall = Math.max(0, ep.xBall); // left edge
            ep.xBall = Math.min(panelWidth - ballDiameter, ep.xBall); // right edge
            ep.yBall = Math.max(0, ep.yBall); // top edge
            ep.yBall = Math.min(panelHeight - ballDiameter, ep.yBall); // bottom edge

            // adjust the ball's centre coordinates
            xBallCenter = ep.xBall + ballDiameter / 2f;
            yBallCenter = ep.yBall + ballDiameter / 2f;

            // if trials in progress, collect sample (and check for new maximum tilt)
            if (sequenceStarted)
            {
                traceArray.add(new TracePoint((int)xBallCenter, (int)yBallCenter, (int)(now / 1000000), tiltMagnitude));
                maxTiltTrial = Math.max(tiltMagnitude, maxTiltTrial);
            }

            if(orderOfControl.equals("Flicker")){
                if (!ep.waitStartCircleSelect && ep.targetCircle != null
                        && (ep.targetCircle.inCircle(xBallCenter, yBallCenter, ballDiameter)
                        || ep.targetCircle.collidedWithCircle(xBallCenter, yBallCenter, prevXposition, prevYposition, ballDiameter)
                )
                        )// YES
                {

                    if (selectionMode.equals("First_Entry")) // CAUTION: potential string edit bug
                        doTargetSelected();
                    else
                    // dwell-time selection
                    {
                        if (sequenceStarted && !targetEntered)
                            targetEntered = true;
                        if (!dwellPending) {
                            dwellPending = true;
                            dwellTimer.start();
                        }
                    }
                }

            }else {
                // Is the ball inside target circle?
                if (!ep.waitStartCircleSelect && ep.targetCircle != null
                        && ep.targetCircle.inCircle(xBallCenter, yBallCenter, ballDiameter)
                )
                        // YES
                {

                    if (selectionMode.equals("First_Entry")) // CAUTION: potential string edit bug
                        doTargetSelected();
                    else
                    // dwell-time selection
                    {
                        if (sequenceStarted && !targetEntered)
                            targetEntered = true;
                        if (!dwellPending) {
                            dwellPending = true;
                            dwellTimer.start();
                        }
                    }
                }
            }


            // Is there a need to cancel the dwell timer?
            if (!selectionMode.equals(FittsTiltSetup.FIRST_ENTRY) && dwellPending
                    && !ep.targetCircle.inCircle(xBallCenter, yBallCenter, ballDiameter)) // YES
            {
                dwellPending = false;
                dwellTimer.cancel();
            }
            if(orderOfControl.equals("Flicker"))
            {
                if (ep.waitStartCircleSelect && (ep.startCircle.inCircle(xBallCenter, yBallCenter, ballDiameter) || ep.startCircle.collidedWithCircle(xBallCenter, yBallCenter, prevXposition, prevYposition, ballDiameter)))
                    doStartCircleClicked();
                else if (ep.waitPracticeCircleSelect && !sequenceStarted
                        && (ep.practiceCircle.inCircle(xBallCenter, yBallCenter, ballDiameter) || ep.practiceCircle.collidedWithCircle(xBallCenter, yBallCenter, prevXposition, prevYposition, ballDiameter)))
                    doPracticeCircleClicked();
            }
            else{
                if (ep.waitStartCircleSelect && ep.startCircle.inCircle(xBallCenter, yBallCenter, ballDiameter))
                    doStartCircleClicked();
                else if (ep.waitPracticeCircleSelect && !sequenceStarted
                        && ep.practiceCircle.inCircle(xBallCenter, yBallCenter, ballDiameter))
                    doPracticeCircleClicked();
            }
            // Did the ball enter the start or practice circle?

            ep.invalidate(); // will cause onDraw to run again immediately
            prevXposition = ep.xBall + ballDiameter / 2f;
            prevYposition = ep.yBall + ballDiameter / 2f;
            this.start(); // prepare for next screen refresh (REFRESH_INTERVAL milliseconds from now)
        }
    }
}