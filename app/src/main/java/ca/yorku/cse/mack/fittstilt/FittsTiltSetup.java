package ca.yorku.cse.mack.fittstilt;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

@SuppressWarnings("unused")
public class FittsTiltSetup extends Activity {
  final String MYDEBUG = "MYDEBUG"; // for Log.i messages

  // ORDER OF CONTROL
  final static String VELOCITY = "Velocity";
  final static String POSITION = "Position";
  final static String PHYSICS1 = "Physics1";
  final static String FLICKER = "Flicker";
  final static String FRICTION = "Friction";

  //Friction Coefficient
  final static String WOOD_METAL_0 = "0.0";
  final static String WOOD_METAL_1 = "0.1";
  final static String WOOD_METAL_2 = "0.2";
  final static String WOOD_METAL_3 = "0.3";
  final static String WOOD_METAL_4 = "0.4";
  final static String WOOD_METAL_5 = "0.5";
  final static String WOOD_METAL_6 = "0.6";
  final static String WOOD_METAL_7 = "0.7";
  final static String WOOD_METAL_8 = "0.8";
  final static String WOOD_METAL_9 = "0.9";
  final static String WOOD_METAL_10 = "1.0";


  //Visualuzation
  final static String VISULAZATION_0 = "Low";
  final static String VISULAZATION_1 = "Medium";
  final static String VISULAZATION_2 = "High";

  //Visualuzation Matching
  final static String VISULAZATION_MATCH = "Matching";
  final static String VISULAZATION_NOMATCH = "Non Matching";


  //Radius
  final static String RADIUS_1 = "50";
  final static String RADIUS_2 = "60";
  final static String RADIUS_3 = "70";
  final static String RADIUS_4 = "80";
  final static String RADIUS_5 = "90";
  final static String RADIUS_6 = "100";
  final static String RADIUS_7 = "150";

  //Hand
  final static String RIGHT = "Right";
  final static String LEFT = "Left";


  final static String Flicker_1 = "1";
  final static String Flicker_2 = "2";
  final static String Flicker_3 = "3";
  final static String Flicker_4 = "4";
  final static String Flicker_5 = "5";
  final static String Flicker_6 = "6";
  final static String Flicker_7 = "7";
  final static String Flicker_8 = "8";
  final static String Flicker_9 = "9";
  final static String Flicker_10 = "10";

  // GAIN
  final static String VERY_LOW = "Very low";
  final static String LOW = "Low";
  final static String MEDIUM = "Medium";
  final static String HIGH = "High";
  final static String VERY_HIGH = "Very high";

  // somewhat arbitrary mappings for gain by order of control
  final static int[] GAIN_ARG_POSITION_CONTROL = {5, 10, 20, 40, 80};
  final static int[] GAIN_ARG_VELOCITY_CONTROL = {25, 50, 100, 200, 400};
  final static int[] GAIN_ARG_PHYSICS1_CONTROL = {25, 50, 100, 200, 400};
  final static int[] GAIN_ARG_PHYSICS2_CONTROL = {25, 50, 100, 200, 400};

  // selection mode
  final static String FIRST_ENTRY = "First_Entry";
  final static String DWELL_500 = "Dwell_500";
  final static String DWELL_400 = "Dwell_400";
  final static String DWELL_300 = "Dwell_300";

  String[] participantCode = {"P01", "P01", "P02", "P03", "P04", "P05",
      "P06", "P07", "P08", "P09", "P10", "P11", "P12", "P13", "P14", "P15",
      "P16", "P17", "P18", "P19", "P20", "P21", "P22", "P23", "P24", "P25"};
  String[] sessionCode = {"S01", "S01", "S02", "S03", "S04", "S05", "S06", "S07", "S08", "S09", "S10", "S11",
      "S12", "S13", "S14", "S15", "S16", "S17", "S18", "S19", "S20", "S21", "S22", "S23", "S24", "S25"};
  String[] blockCode = {"(auto)", "4"};
  String[] groupCode = {"G01", "G01", "G02", "G03", "G04", "G05", "G06", "G07", "G08", "G09", "G10", "G11", "G12",
      "G13", "G14", "G15", "G16", "G17", "G18", "G19", "G20", "G21", "G22", "G23", "G24", "G25"};
  String[] conditionCode = {"C01", "C01", "C02", "C03", "C04", "C05", "C06", "C07", "C08", "C09", "C10", "C11",
      "C12", "C13", "C14", "C15", "C16", "C17", "C18", "C19", "C20", "C21", "C22", "C23", "C24", "C25"};
  String[] numberOfTargetsArray = {"15", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16",
      "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"};
  String[] amplitudesArray = {"125, 250, 500", "125, 250, 500", "250, 500", "500", "500", "750", "1000", "1500", "auto"};
  String[] widthsArray = {"40, 60, 100", "40, 60, 100", "60, 100", "100", "150", "200", "100, 150, 200"};
  String[] ballScaleArray = {"0.5", "0.1", "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9", "1.0"};
  String[] orderOfControlArray = {VELOCITY, VELOCITY, POSITION, PHYSICS1, FLICKER, FRICTION};
  String[] frictionCoefficientArray = {WOOD_METAL_5, WOOD_METAL_0, WOOD_METAL_1, WOOD_METAL_2, WOOD_METAL_3, WOOD_METAL_4, WOOD_METAL_5, WOOD_METAL_6, WOOD_METAL_7, WOOD_METAL_8, WOOD_METAL_9, WOOD_METAL_10};
  // Amit changes
  String[] frictionCoefficientVisualizationArray = {VISULAZATION_0, VISULAZATION_1, VISULAZATION_2};
  String[] frictionCoefficientVisualizationMatchingArray = {VISULAZATION_MATCH, VISULAZATION_NOMATCH};

  String[] flickerMultiplier = {"1", Flicker_1, Flicker_2, Flicker_3, Flicker_4, Flicker_5, Flicker_6, Flicker_7, Flicker_8, Flicker_9, Flicker_10};
  String[] handArray = {RIGHT, RIGHT, LEFT};
  String[] radiusArray = {RADIUS_1, RADIUS_1, RADIUS_2, RADIUS_3, RADIUS_4, RADIUS_5, RADIUS_5, RADIUS_6, RADIUS_7};
  String[] gainArray = {MEDIUM, VERY_LOW, LOW, MEDIUM, HIGH, VERY_HIGH};
  String gainString;
  String[] selectionModeArray = {FIRST_ENTRY, FIRST_ENTRY, DWELL_500, DWELL_400, DWELL_300};
  boolean vibrotactileFeedback = true;
  boolean auditoryFeedback = false;

  SharedPreferences sp;
  SharedPreferences.Editor spe;

  private Spinner spinParticipant, spinSession, spinGroup, spinCondition, spinHand, spinRadius;
  private Spinner spinNumTargets, spinAmplitude, spinWidth, spinBallScale;
  private Spinner spinOrderOfControl;
  private Spinner spinFrictionCoefficient, spinFlickMult, spinFrictionCoefficientVisualuzation, paramFrictionCoefficientVisualuzationMatching;
  private Spinner spinTG;
  private Spinner spinSelectionMode;
  private CheckBox checkVibrotactileFeedback;
  private CheckBox checkAuditoryFeedback;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.setup);

    sp = this.getPreferences(MODE_PRIVATE);

    // load preferences
    sp = this.getPreferences(MODE_PRIVATE);
    participantCode[0] = sp.getString("participantCode", participantCode[0]);
    sessionCode[0] = sp.getString("sessionCode", sessionCode[0]);
    // block code initialized in main activity (based on existing filenames)
    groupCode[0] = sp.getString("groupCode", groupCode[0]);
    conditionCode[0] = sp.getString("conditionCode", conditionCode[0]);
    numberOfTargetsArray[0] = sp.getString("numberOfTargets", numberOfTargetsArray[0]);
    amplitudesArray[0] = sp.getString("amplitudes", amplitudesArray[0]);
    orderOfControlArray[0] = sp.getString("orderOfControl", orderOfControlArray[0]);
    frictionCoefficientArray[0] = sp.getString("frictionCoefficient", frictionCoefficientArray[0]);
    radiusArray[0] = sp.getString("radius", radiusArray[0]);
    handArray[0] = sp.getString("hand", handArray[0]);
    flickerMultiplier[0] = sp.getString("flickMultiplier", flickerMultiplier[0]);
    widthsArray[0] = sp.getString("widths", widthsArray[0]);
    ballScaleArray[0] = sp.getString("ballScale", ballScaleArray[0]);
    gainArray[0] = sp.getString("gain", gainArray[0]);
    selectionModeArray[0] = sp.getString("selectionMode", selectionModeArray[0]);
    vibrotactileFeedback = sp.getBoolean("vibrotactileFeedback", vibrotactileFeedback);
    auditoryFeedback = sp.getBoolean("auditoryFeedback", auditoryFeedback);

    // get references to widget elements
    spinParticipant = (Spinner) findViewById(R.id.paramPart);
    spinSession = (Spinner) findViewById(R.id.paramSess);
    Spinner spinBlock = (Spinner) findViewById(R.id.paramBlock);
    spinFrictionCoefficient = (Spinner) findViewById(R.id.paramFrictionCoefficient);

    //Amit code
    paramFrictionCoefficientVisualuzationMatching = (Spinner) findViewById(R.id.paramFrictionCoefficientVisualuzationMatching);
    spinFrictionCoefficientVisualuzation = (Spinner) findViewById(R.id.paramFrictionCoefficientVisualuzation);


    spinFlickMult = (Spinner) findViewById(R.id.paramFlicketMultiplier);
    spinGroup = (Spinner) findViewById(R.id.paramGroup);
    spinCondition = (Spinner) findViewById(R.id.paramCondition);
    spinNumTargets = (Spinner) findViewById(R.id.paramTargets);
    spinAmplitude = (Spinner) findViewById(R.id.paramAmplitude);
    spinWidth = (Spinner) findViewById(R.id.paramWidth);
    spinBallScale = (Spinner) findViewById(R.id.paramBallScale);
    spinOrderOfControl = (Spinner) findViewById(R.id.paramOrderOfControl);
    spinHand = (Spinner) findViewById(R.id.paramHand);
    spinRadius = (Spinner) findViewById(R.id.paramRaduis);
    spinTG = (Spinner) findViewById(R.id.paramTG);
    spinSelectionMode = (Spinner) findViewById(R.id.paramSelectionMode);
    checkVibrotactileFeedback = (CheckBox) findViewById(R.id.paramVibrotactileFeedback);
    checkAuditoryFeedback = (CheckBox) findViewById(R.id.paramAuditoryFeedback);

    // initialise spinner adapters
    ArrayAdapter<CharSequence> adapterPC
        = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle, participantCode);
    spinParticipant.setAdapter(adapterPC);

    ArrayAdapter<CharSequence> adapterSS
        = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle, sessionCode);
    spinSession.setAdapter(adapterSS);

    ArrayAdapter<CharSequence> adapterB
        = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle, blockCode);
    spinBlock.setAdapter(adapterB);

    ArrayAdapter<CharSequence> adapterG
        = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle, groupCode);
    spinGroup.setAdapter(adapterG);

    ArrayAdapter<CharSequence> adapterC
        = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle, conditionCode);
    spinCondition.setAdapter(adapterC);

    ArrayAdapter<CharSequence> adapterNP
        = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle, numberOfTargetsArray);
    spinNumTargets.setAdapter(adapterNP);

    ArrayAdapter<CharSequence> adapterA
        = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle, amplitudesArray);
    spinAmplitude.setAdapter(adapterA);

    ArrayAdapter<CharSequence> adapterW
        = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle, widthsArray);
    spinWidth.setAdapter(adapterW);

    ArrayAdapter<CharSequence> adapterBS
        = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle, ballScaleArray);
    spinBallScale.setAdapter(adapterBS);

    ArrayAdapter<CharSequence> adapterOC
        = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle, orderOfControlArray);
    spinOrderOfControl.setAdapter(adapterOC);

    ArrayAdapter<CharSequence> adapterFriction
        = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle, frictionCoefficientArray);
    spinFrictionCoefficient.setAdapter(adapterFriction);


    // Amit changes
    ArrayAdapter<CharSequence> adapterFrictionVisualuzation
        = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle, frictionCoefficientVisualizationArray);
    spinFrictionCoefficientVisualuzation.setAdapter(adapterFrictionVisualuzation);

    ArrayAdapter<CharSequence> adapterFrictionVisualuzationMatching
        = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle, frictionCoefficientVisualizationMatchingArray);

    paramFrictionCoefficientVisualuzationMatching.setAdapter(adapterFrictionVisualuzationMatching);


    ArrayAdapter<CharSequence> adapterHand
        = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle, handArray);
    spinHand.setAdapter(adapterHand);

    ArrayAdapter<CharSequence> adapterRadius
        = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle, radiusArray);
    spinRadius.setAdapter(adapterRadius);

    ArrayAdapter<CharSequence> adapterFlicker
        = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle, flickerMultiplier);
    spinFlickMult.setAdapter(adapterFlicker);

    ArrayAdapter<CharSequence> adapterTVG
        = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle, gainArray);
    spinTG.setAdapter(adapterTVG);

    ArrayAdapter<CharSequence> adapterSM
        = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle, selectionModeArray);
    spinSelectionMode.setAdapter(adapterSM);

    checkVibrotactileFeedback.setChecked(vibrotactileFeedback);
    checkAuditoryFeedback.setChecked(auditoryFeedback);
  }

  // called when the "OK" button is pressed
  public void clickOK(View view) {
    // get user's choices
    String part = participantCode[spinParticipant.getSelectedItemPosition()];
    String sess = sessionCode[spinSession.getSelectedItemPosition()];
    //String block = blockCode[spinBlock.getSelectedItemPosition()];
    String group = groupCode[spinGroup.getSelectedItemPosition()];
    String condition = conditionCode[spinCondition.getSelectedItemPosition()];
    int numTargets = Integer.parseInt(numberOfTargetsArray[spinNumTargets.getSelectedItemPosition()]);
    String amplitude = amplitudesArray[spinAmplitude.getSelectedItemPosition()];
    String width = widthsArray[spinWidth.getSelectedItemPosition()];
    String bScaleString = ballScaleArray[spinBallScale.getSelectedItemPosition()];
    String frictionCoefficient = frictionCoefficientArray[spinFrictionCoefficient.getSelectedItemPosition()];

    // Amit changes
    String frictionCoefficientVisualizationMatching = frictionCoefficientVisualizationMatchingArray[paramFrictionCoefficientVisualuzationMatching.getSelectedItemPosition()];
    String frictionCoefficientVisualization = frictionCoefficientVisualizationArray[spinFrictionCoefficientVisualuzation.getSelectedItemPosition()];

    String flickMutliplier = flickerMultiplier[spinFlickMult.getSelectedItemPosition()];
    String radius = radiusArray[spinRadius.getSelectedItemPosition()];
    String hand = handArray[spinHand.getSelectedItemPosition()];
    String orderOfControl = orderOfControlArray[spinOrderOfControl.getSelectedItemPosition()];
    String gainString = gainArray[spinTG.getSelectedItemPosition()];

    // actual gain value depends on order of control
    int gain = getGain(orderOfControl, gainString);
    float bScale = Float.parseFloat(bScaleString);

    String selectionMode = selectionModeArray[spinSelectionMode.getSelectedItemPosition()];
    boolean vibrotactileFeedback = checkVibrotactileFeedback.isChecked();
    boolean auditoryFeedback = checkAuditoryFeedback.isChecked();

    // put the setup up parameters in a bundle
    Bundle b = new Bundle();
    b.putString("participantCode", part);
    b.putString("sessionCode", sess);
    //b.putString("blockCode", block);
    b.putString("groupCode", group);
    b.putString("conditionCode", condition);
    b.putInt("numberOfTargets", numTargets);
    b.putString("amplitudes", amplitude);
    b.putString("widths", width);
    b.putFloat("ballScale", bScale);
    b.putString("radius", radius);
    b.putString("hand", hand);
    b.putString("orderOfControl", orderOfControl);
    b.putString("frictionCoefficient", frictionCoefficient);
    // Amit changes
    b.putString("frictionCoefficientVisualizationMatching", frictionCoefficientVisualizationMatching);
    b.putString("frictionCoefficientVisualization", frictionCoefficientVisualization);


    b.putString("flickMultiplier", flickMutliplier);
    b.putInt("gain", gain);
    b.putString("selectionMode", selectionMode);
    b.putBoolean("vibrotactileFeedback", vibrotactileFeedback);
    b.putBoolean("auditoryFeedback", auditoryFeedback);

    // start experiment activity (passing along the bundle)
    Intent i = new Intent(getApplicationContext(), FittsTiltActivity.class);
    i.putExtras(b);
    startActivity(i);
    //finish();
  }

  // called when the "Save button is pressed
  public void clickSave(View view) {
    spe = sp.edit();
    spe.putString("participantCode", participantCode[spinParticipant.getSelectedItemPosition()]);
    spe.putString("sessionCode", sessionCode[spinSession.getSelectedItemPosition()]);
    spe.putString("groupCode", groupCode[spinGroup.getSelectedItemPosition()]);
    spe.putString("conditionCode", conditionCode[spinCondition.getSelectedItemPosition()]);
    spe.putString("numberOfTargets", numberOfTargetsArray[spinNumTargets.getSelectedItemPosition()]);
    spe.putString("amplitudes", amplitudesArray[spinAmplitude.getSelectedItemPosition()]);
    spe.putString("widths", widthsArray[spinWidth.getSelectedItemPosition()]);
    spe.putString("ballScale", ballScaleArray[spinBallScale.getSelectedItemPosition()]);
    spe.putString("orderOfControl", orderOfControlArray[spinOrderOfControl.getSelectedItemPosition()]);
    spe.putString("gain", gainArray[spinTG.getSelectedItemPosition()]);
    spe.putString("radius", radiusArray[spinRadius.getSelectedItemPosition()]);
    spe.putString("hand", handArray[spinHand.getSelectedItemPosition()]);
    spe.putString("frictionCoefficient", frictionCoefficientArray[spinFrictionCoefficient.getSelectedItemPosition()]);
    spe.putString("flickMultiplier", flickerMultiplier[spinFlickMult.getSelectedItemPosition()]);
    spe.putString("selectionMode", selectionModeArray[spinSelectionMode.getSelectedItemPosition()]);
    spe.putBoolean("vibrotactileFeedback", checkVibrotactileFeedback.isChecked());
    spe.putBoolean("auditoryFeedback", checkAuditoryFeedback.isChecked());
    spe.apply();
    Toast.makeText(this, "Preferences saved!", Toast.LENGTH_SHORT).show();
  }

  // called when the "Exit" button is pressed
  public void clickExit(View view) {
    super.onDestroy(); // cleanup
    this.finish(); // terminate
  }

  // convoluted way to get gain (should probably use a key-value Map)
  public int getGain(String orderOfControl, String gainArg) {
    int gain = -1;
    if (orderOfControl.equals(VELOCITY)) {
      if (gainArg.equals(VERY_LOW))
        gain = GAIN_ARG_VELOCITY_CONTROL[0];
      else if (gainArg.equals(LOW))
        gain = GAIN_ARG_VELOCITY_CONTROL[1];
      else if (gainArg.equals(MEDIUM))
        gain = GAIN_ARG_VELOCITY_CONTROL[2];
      else if (gainArg.equals(HIGH))
        gain = GAIN_ARG_VELOCITY_CONTROL[3];
      else if (gainArg.equals(VERY_HIGH))
        gain = GAIN_ARG_VELOCITY_CONTROL[4];
    } else if (orderOfControl.equals(POSITION)) {
      if (gainArg.equals(VERY_LOW))
        gain = GAIN_ARG_POSITION_CONTROL[0];
      else if (gainArg.equals(LOW))
        gain = GAIN_ARG_POSITION_CONTROL[1];
      else if (gainArg.equals(MEDIUM))
        gain = GAIN_ARG_POSITION_CONTROL[2];
      else if (gainArg.equals(HIGH))
        gain = GAIN_ARG_POSITION_CONTROL[3];
      else if (gainArg.equals(VERY_HIGH))
        gain = GAIN_ARG_POSITION_CONTROL[4];

    } else if (orderOfControl.equals(PHYSICS1)) {
      if (gainArg.equals(VERY_LOW))
        gain = GAIN_ARG_PHYSICS1_CONTROL[0];
      else if (gainArg.equals(LOW))
        gain = GAIN_ARG_PHYSICS1_CONTROL[1];
      else if (gainArg.equals(MEDIUM))
        gain = GAIN_ARG_PHYSICS1_CONTROL[2];
      else if (gainArg.equals(HIGH))
        gain = GAIN_ARG_PHYSICS1_CONTROL[3];
      else if (gainArg.equals(VERY_HIGH))
        gain = GAIN_ARG_PHYSICS1_CONTROL[4];

    } else if (orderOfControl.equals(FLICKER)) {
      if (gainArg.equals(VERY_LOW))
        gain = GAIN_ARG_PHYSICS2_CONTROL[0];
      else if (gainArg.equals(LOW))
        gain = GAIN_ARG_PHYSICS2_CONTROL[1];
      else if (gainArg.equals(MEDIUM))
        gain = GAIN_ARG_PHYSICS2_CONTROL[2];
      else if (gainArg.equals(HIGH))
        gain = GAIN_ARG_PHYSICS2_CONTROL[3];
      else if (gainArg.equals(VERY_HIGH))
        gain = GAIN_ARG_PHYSICS2_CONTROL[4];
    }
    return gain;
  }
}
