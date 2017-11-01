package smg.xelas;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Reports extends BaseNavDrawer implements View.OnClickListener, OnChartGestureListener, OnChartValueSelectedListener{
    private Button btn_getReport;
    private Spinner sp_date;
    private TextView reportLabel;
    private CheckBox cb_sendCopyToEmail;
    private String passedArgObID;
    private String passedArgPeriod;
    private ProgressBar pb_loading_indicator;
    private LinearLayout ll_graphResults;
    private FitbitApi fitbit = new FitbitApi(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_reports, frameLayout);

        Intent lastPage = getIntent();
        Bundle extras = lastPage.getExtras();
        init();
        Log.d("reporting", "here");
        // for shared data reports
        if(extras != null){
            passedArgObID = extras.getString("obID");
            passedArgPeriod = extras.getString("period");
            pb_loading_indicator.setVisibility(View.VISIBLE);

            displayFullReport(passedArgPeriod, passedArgObID, false);
        } else {
            Log.d("reporting", "here");
            //Log.d("checkbox", "it is " + cb_sendCopyToEmail.isActivated());
            displayFullReport("1w", "null", false); // Autoload, as mentioned by Scott
        }
    }

    private void init(){
        sp_date = (Spinner) findViewById(R.id.sp_date);
        ArrayAdapter reportAdapter = ArrayAdapter.createFromResource(this, R.array.reports_array, R.layout.spinner_first_item);
        reportAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_date.setAdapter(reportAdapter);

        pb_loading_indicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        ll_graphResults = (LinearLayout) findViewById(R.id.ll_graphResults);
        cb_sendCopyToEmail = (CheckBox) findViewById(R.id.cb_sendCopyToEmail);
        btn_getReport = (Button) findViewById(R.id.btn_getReport);
        btn_getReport.setOnClickListener(this);
    }

    private void displayResults(JSONObject report){
        extractAndDisplayData(report, "activities-steps", "0", "value", " (steps)");
        extractAndDisplayData(report, "activities-distance", "0.00", "value"," (km)");
        extractAndDisplayData(report, "activities-calories", "0", "value", " (cal)");
        extractAndDisplayData(report, "activities-heart", "0", "restingHeartRate", " (BPM)");
        extractAndDisplayData(report, "sleep", "0.00", "duration", " (hours)");
        btn_getReport.setEnabled(true);
        btn_getReport.setText("Generate Report");
    }

    /*
     * extracts data from fitbit and makes it ready for graphing..
     */
    private void extractAndDisplayData(JSONObject report, String fieldName, String decimalFormat, String VaueFieldName, String Metric){
        Log.d("fitbitReport", "about to display " + fieldName);
        try {
            JSONArray steps = report.getJSONArray(fieldName);

            float num = 0; // for avg
            int total = 0; // for avg
            int length = steps.length();

            float bestValue = 0;
            float worstValue = Float.MAX_VALUE;

            ArrayList<Entry> yValues = new ArrayList<>();
            String[] xValues = new String[length];

            for (int i=0; i < length; i++) {

                JSONObject thisField = steps.getJSONObject(i);

                float thisValue;
                String thisDate;

                if(thisField.has("dateOfSleep")){
                    thisValue = milisecondsToHours(Float.parseFloat(thisField.getString(VaueFieldName)));
                    thisDate = thisField.getString("dateOfSleep");
                } else if (VaueFieldName == "restingHeartRate"){
                    thisValue = Float.parseFloat(thisField.getJSONObject("value").getString("restingHeartRate"));
                    thisDate = thisField.getString("dateTime");
                } else {
                    thisValue = Float.parseFloat(thisField.getString(VaueFieldName));
                    thisDate = thisField.getString("dateTime");
                }

                //best value
                if(thisValue > bestValue){
                    bestValue = thisValue;
                } else if (thisValue < worstValue) {
                    worstValue = thisValue;
                }

                yValues.add(new Entry(i, thisValue));
                xValues[i] = fitbit.formatDate(thisDate);

                // only average days where steps (or whatever) > 0
                if (thisValue > 0){
                    total += thisValue;
                    num++;
                }
            }

            DecimalFormat df = new DecimalFormat(decimalFormat);
            float avg = total/num;

            String display = "\n Average: " + df.format(avg) + ", Highest: " + df.format(bestValue) + ", Lowest: " + df.format(worstValue) + "\n";

            displayGraph(createGraph(yValues, xValues, avg), fieldName, display, Metric);

            Log.d("fitbitReport", display);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private LineChart createGraph(ArrayList<Entry> data, String[] xValues, Float goal){
        LineChart lineChart = new LineChart(this);

        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);

        //limits
        LimitLine upper_limit = new LimitLine(goal, "average");
        upper_limit.setLineWidth(4f);
        upper_limit.enableDashedLine(10f,10f, 0f);
        upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(upper_limit);
        leftAxis.enableGridDashedLine(5f, 5f, 0);
        leftAxis.setDrawLimitLinesBehindData(true);

        lineChart.getAxisRight().setEnabled(false);
        lineChart.getLegend().setEnabled(false);

        LineDataSet set1 = new LineDataSet(data, " as");
        set1.setFillAlpha(110);
        set1.setColor(Color.BLUE);
        set1.setLineWidth(2f);
        set1.setValueTextSize(10f);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        LineData graphData = new LineData(dataSets);

        lineChart.setData(graphData);
        lineChart.animateY(2500);

        XAxis xAx = lineChart.getXAxis();
        xAx.setValueFormatter(new MyXAxisValueFormatter(xValues));
        //xAx.setGranularity(1);
        xAx.setPosition(XAxis.XAxisPosition.BOTTOM);

        return lineChart;
    }

    private static boolean isBetween(int x, int lower, int upper) {
        return lower <= x && x <= upper;
    }

    private float milisecondsToHours(float ms){
        return (ms/(1000*60*60))%24;
    }

    private String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    private void displayGraph(LineChart graph, String title, String Stats, String Metric) {
        ll_graphResults.setVisibility(View.VISIBLE);
        TextView label = new TextView(this);
        TextView label_stats = new TextView(this);
        label.setText(capitalize(title.replaceAll(".*-", "")) + Metric); // e.g "activites-steps" becomes "steps"
        label.setTextSize(20);
        label_stats.setText(Stats);

        // Label
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.HORIZONTAL);
        l.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        l.addView(label, lp);

        // Graph
        LinearLayout l2 = new LinearLayout(this);
        l2.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 600); // W & H (in that order)
        l2.addView(graph, lp2);

        // Stats label
        LinearLayout l3 = new LinearLayout(this);
        l3.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        l3.addView(label_stats, lp3);

        ll_graphResults.addView(l);
        ll_graphResults.addView(l2);
        ll_graphResults.addView(l3);
    }

    @Override
    public void onClick(View v) {
        int buttonPressed = v.getId();

        switch (buttonPressed) {
            case R.id.btn_getReport:
                Log.d("fitbita", " " + cb_sendCopyToEmail.isChecked());

                displayFullReport(fitbit.spinnerDateToFitbitPeriod(sp_date.getSelectedItem().toString()), "null", cb_sendCopyToEmail.isChecked());
                btn_getReport.setEnabled(false);
                btn_getReport.setText("Loading..");
                break;
        }
    }

    private void displayFullReport(String period, String PassedArgs, Boolean sendCopyToEmail){
        pb_loading_indicator.setVisibility(View.VISIBLE);
        ll_graphResults.setVisibility(View.INVISIBLE); // so title isn't shown..
        // empty previous graphs if exists
        if(((LinearLayout) ll_graphResults).getChildCount() > 0)
            ((LinearLayout) ll_graphResults).removeAllViews();

        reportLabel = new TextView(this);
        reportLabel.setText("Report for " + fitbit.periodToSringDate(period));
        reportLabel.setTextSize(25);

        // report label
        LinearLayout l3 = new LinearLayout(this);
        l3.setOrientation(LinearLayout.HORIZONTAL);
        l3.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        l3.addView(reportLabel, lp3);

        ll_graphResults.addView(l3);

        JSONObject formBody = new JSONObject();
        try {
            formBody.put("period", period);
            formBody.put("passedArgs", PassedArgs);

            if(sendCopyToEmail)
                formBody.put("sendCopyToEmail", sendCopyToEmail);

        } catch (JSONException e) {
            Log.d("fitbita", "couldnt put formbody");
            e.printStackTrace();
        }

        new FitbitReportAsync().execute(formBody, null, formBody);
    }

    private class FitbitReportAsync extends AsyncTask<JSONObject, Void, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(JSONObject... params) {
            JSONObject form = params[0];

            try {
                String period = form.getString("period");
                String passedArgs = form.getString("passedArgs");
                Boolean sendCopy = form.has("sendCopyToEmail");

                Log.d("fitbita", "got from form" + period + passedArgs + sendCopy);
                if (passedArgs == "null"){
                    return fitbit.getReport(period, null, sendCopy);
                } else {
                    return fitbit.getReport(period, passedArgs, false);
                }
            } catch (JSONException e) {
                Log.d("fitbita", "couldnt get form body");
                e.printStackTrace();
            }

            return fitbit.getReport("1w", null, false);
        }

        protected void onPostExecute(JSONObject result) {
            Log.d("fitbita", "post exe " + result.toString());

            if(result.has("message")){
                Log.d("fitbita", "eror... ");
                try {
                    Toast.makeText(getApplicationContext(), result.getString("message"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d("fitbita", "about to display ");
                displayResults(result);
            }

            pb_loading_indicator.setVisibility(View.GONE);
        }
    }

    /* FOLLOWING IS NEEDED FOR GRAPHING */

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    public class MyXAxisValueFormatter implements IAxisValueFormatter{
        private String[] mValues;
        public MyXAxisValueFormatter(String[] values){
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mValues[(int)value];
        }
    }


    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }
}
