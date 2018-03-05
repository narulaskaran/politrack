package cs.teslastem.politrack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class HomeActivity extends AppCompatActivity {

    public static SharedPreferences settings;
    public static SharedPreferences.Editor editSettings;

    public static String json, stateAbbr, districtNum, APIKey;

    ArrayList<String> urlArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ((ToggleButton) findViewById(R.id.repTypeToggle)).setText("Senate");
        ((ToggleButton) findViewById(R.id.repTypeToggle)).setTextOn("Congress");
        ((ToggleButton) findViewById(R.id.repTypeToggle)).setTextOff("Senate");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            json = extras.getString("data");
            stateAbbr = extras.getString("stateAbbr");
            districtNum = extras.getString("districtNum");
            APIKey = extras.getString("APIKey");
        }

        populateListView();

        ((ListView) findViewById(R.id.SenateListView)).setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                String url = urlArray.get(position);

                displayProfile(url);
            }

        });

        ((ToggleButton) findViewById(R.id.repTypeToggle)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                GETRequest repRequest;

                if (isChecked) {
                    repRequest = new GETRequest(String.format("https://api.propublica.org/congress/v1/members/house/%s/%s/current.json", stateAbbr, districtNum), APIKey);
                    try {
                        json = repRequest.execute().get();
                        populateListView();
                    } catch (Exception e) {
                        Log.e("Request Error", "Could not update Houe Members");
                    }

                } else {

                    repRequest = new GETRequest(String.format("https://api.propublica.org/congress/v1/members/senate/%s/%s/current.json", stateAbbr, districtNum), APIKey);
                    try {
                        json = repRequest.execute().get();
                        populateListView();
                    } catch (Exception e) {
                        Log.e("Request Error", "Could not update Houe Members");
                    }
                }
            }
        });

        findViewById(R.id.mainButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToMain();
            }
        });

    }

    public void populateListView() {
        settings = getPreferences(0);

        ListView senateListView = (ListView) findViewById(R.id.SenateListView);

        try {
            JSONObject jObject = new JSONObject(json);
            JSONArray jArray = jObject.getJSONArray("results");

            ArrayList<String> senateArray = new ArrayList<>();
            ArrayAdapter<String> senateListViewAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_selectable_list_item, senateArray);
            senateListView.setAdapter(senateListViewAdapter);

            urlArray.clear();

            for (int parse = 0; parse < jArray.length(); parse++) {
                String name = jArray.getJSONObject(parse).getString("name");
                String url = jArray.getJSONObject(parse).getString("api_url");

                senateArray.add(name);
                urlArray.add(url);
            }

            Log.e("Finished", "Loop ended and no JSONException thrown");
        } catch (JSONException error) {
            Log.e("JSON Error", String.valueOf(error));
        }
    }

    public void returnToMain() {
        Intent launchHome = new Intent(this, MainActivity.class);
        startActivity(launchHome);
        this.finish();
    }

    public void displayProfile(String url) {
        Intent launchProfile = new Intent(this, ProfileActivity.class);
        launchProfile.putExtra("url", url);
        launchProfile.putExtra("stateAbbr", stateAbbr);
        launchProfile.putExtra("districtNum", districtNum);
        launchProfile.putExtra("APIKey", APIKey);

        startActivity(launchProfile);
    }

}
