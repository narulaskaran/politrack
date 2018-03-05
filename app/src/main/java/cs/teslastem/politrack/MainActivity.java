package cs.teslastem.politrack;

        import android.app.Activity;
        import android.app.AlertDialog;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.Spinner;

        import java.util.ArrayList;

public class MainActivity extends Activity {

    public static SharedPreferences settings;
    public static SharedPreferences.Editor editSettings;
    public static ConnectionDetector cd;

    private static Spinner stateSpinner, districtSpinner;
    private static Button fetch;

    public static String stateAbbr, districtNum;

    String APIKey = "YOUR_API_KEY_HERE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fetch = (Button) findViewById(R.id.button);
        stateSpinner = (Spinner) findViewById(R.id.StateSpinner);
        districtSpinner = (Spinner) findViewById(R.id.DistrictSpinner);
        districtSpinner.setEnabled(false);

        cd = new ConnectionDetector(getApplicationContext());

        settings = getPreferences(0);
        editSettings = settings.edit();

        stateAbbr = settings.getString("stateAbbr", "");
        districtNum = settings.getString("districtNum", "");


//        if (stateAbbr.length() != 0 && districtNum.length() != 0) {
//            if(settings.getBoolean("data", false))
//                home();
//            else {
//                stateSpinner.setSelection(ArrayAdapter.createFromResource(this, R.array.states_array, android.R.layout.simple_spinner_item).getPosition(stateAbbr));
//
//                //Get number of districts from first selection
//                String districtStr = (stateSpinner.getSelectedItem()).toString();
//                districtStr = districtStr.substring(districtStr.indexOf(",")+1);
//
//                int districtNum = Integer.parseInt(districtStr);
//
//                //Populate array with districts
//                ArrayList<String> districtSpinnerArray = new ArrayList<>();
//                for (int count = 0; count < districtNum; count++) {
//                    districtSpinnerArray.add(""+ (count + 1));
//                }
//
//                //Enable spinner and assign entries
//                districtSpinner.setEnabled(true);
//                ArrayAdapter<String> districtAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, districtSpinnerArray);
//                districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                districtSpinner.setAdapter(districtAdapter);
//                districtSpinner.setSelection(districtAdapter.getPosition(settings.getString("districtNum", ""))); // Todo
//            }
//        }

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Get number of districts from first selection
                int districts[] = {7, 1, 9, 4, 53, 7, 5, 2, 2, 27, 14, 2, 2, 18, 9, 4, 4, 6, 6, 2, 8, 9, 14, 8, 4, 8, 1, 3, 4, 2, 12, 3, 27, 13, 1, 16, 5, 5, 18, 2, 7, 1, 9, 36, 4, 1, 11, 10, 3, 8, 1};
                int districtNum = districts[i];

                //Populate array with districts
                ArrayList<String> districtSpinnerArray = new ArrayList<>();
                for (int count = 0; count < districtNum; count++) {
                    districtSpinnerArray.add(""+ (count + 1));
                }

                //Enable spinner and assign entries
                districtSpinner.setEnabled(true);
                ArrayAdapter<String> districtAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, districtSpinnerArray);
                districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                districtSpinner.setAdapter(districtAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                districtSpinner.setEnabled(false);
            }
        });

        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stateAbbr = State.valueOfName(stateSpinner.getSelectedItem().toString()).getAbbreviation();

                districtNum = districtSpinner.getSelectedItem().toString();
                editSettings.putString("stateAbbr", stateAbbr);
                editSettings.putString("districtNum", districtNum);
                editSettings.commit();

                if (cd.isConnectingToInternet()) {
                    GETRequest homeRequest = new GETRequest(String.format("https://api.propublica.org/congress/v1/members/senate/%s/%s/current.json", stateAbbr, districtNum),APIKey);
                    String json = "";
                    try {
                        json = homeRequest.execute().get();
                    }
                    catch(Exception e){

                    }
                    Log.e("JSON Response", json);
                    if(homeRequest.responseCode == 200) {
                        editSettings.remove("data");
                        editSettings.putString("data", json);
                        editSettings.putBoolean("data", true);
                        editSettings.commit();
                        home(json);
                    }
                    else{
                        new AlertDialog.Builder(MainActivity.this).setTitle("Network Problems").setMessage("No 200").show();
                        editSettings.putBoolean("data", false);
                    }
                    editSettings.commit();
                } else {
                    new AlertDialog.Builder(MainActivity.this).setTitle("Network Problems").setMessage("Real Error").show();
                }
            }
        });
    }


    private void home(String json) {
        Intent launchHome = new Intent(this, HomeActivity.class);
        launchHome.putExtra("data", json);
        launchHome.putExtra("stateAbbr", stateAbbr);
        launchHome.putExtra("districtNum", districtNum);
        launchHome.putExtra("APIKey", APIKey);

        startActivity(launchHome);
        this.finish();
    }
}