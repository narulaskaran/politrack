package cs.teslastem.politrack;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {


    JSONArray roleJArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Declare globals
        String url = "", json, stateAbbr, districtNum, APIKey = "", facebookID = "", name = "";
        ImageView profileImageView = (ImageView) findViewById(R.id.profileImageView);

        //Get strings from previous activity via intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            url = extras.getString("url");
            stateAbbr = extras.getString("stateAbbr");
            districtNum = extras.getString("districtNum");
            APIKey = extras.getString("APIKey");
        }

        //Pull JSON from API for the individual
        GETRequest individualRequest = new GETRequest(url, APIKey);
        try {
            json = individualRequest.execute().get();
            //Parse JSON
            final JSONObject jObject = new JSONObject(json);
            final JSONArray jArray = jObject.getJSONArray("results");

            for (int parse = 0; parse < jArray.length(); parse++) {
                //Example: Get value for ID: name
                name = String.format("%s %s %s", jArray.getJSONObject(parse).getString("first_name"), jArray.getJSONObject(parse).getString("middle_name"), jArray.getJSONObject(parse).getString("last_name"));
                setTitle(name);

                ((TextView) findViewById(R.id.stateTextView)).setText(State.valueOfAbbreviation(jArray.getJSONObject(parse).getJSONArray("roles").getJSONObject(0).getString("state")).toString().toUpperCase());
                ((TextView) findViewById(R.id.roleTextView)).setText(jArray.getJSONObject(parse).getJSONArray("roles").getJSONObject(0).getString("title"));
                ((TextView) findViewById(R.id.partyTextView)).setText(jArray.getJSONObject(parse).getJSONArray("roles").getJSONObject(0).getString("party"));
                if (((TextView) findViewById(R.id.partyTextView)).getText().equals("R"))
                    ((TextView) findViewById(R.id.partyTextView)).setTextColor(getResources().getColor(R.color.republicanRed));
                if (!jArray.getJSONObject(parse).getString("url").equals("")) {
                    findViewById(R.id.webButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            try {
                                i.setData(Uri.parse(jArray.getJSONObject(0).getString("url")));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            startActivity(i);
                        }
                    });
                }
                if (!jArray.getJSONObject(parse).getString("facebook_id").equals("")) {
                    findViewById(R.id.fbButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            try {
                                i.setData(Uri.parse("http://facebook.com/" + jArray.getJSONObject(0).getString("facebook_id")));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            startActivity(i);
                        }
                    });
                }
                if (!jArray.getJSONObject(parse).getString("twitter_account").equals("")) {
                    findViewById(R.id.twitterButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            try {
                                i.setData(Uri.parse("http://twitter.com/" + jArray.getJSONObject(0).getString("twitter_account")));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            startActivity(i);
                        }
                    });
                }
                if (!jArray.getJSONObject(parse).getString("times_topics_url").equals("")) {
                    findViewById(R.id.nyTimesButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            try {
                                i.setData(Uri.parse(jArray.getJSONObject(0).getString("times_topics_url")));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            startActivity(i);
                        }
                    });
                }
                if (!jArray.getJSONObject(parse).getString("roles").equals("")) {
                    roleJArray = jArray.getJSONObject(parse).getJSONArray("roles");
                    ((TextView) findViewById(R.id.SeniorityTextView)).setText("Seniority: " + roleJArray.getJSONObject(0).getString("seniority") + " Years");
                    ((TextView) findViewById(R.id.DistrictTextView)).setText("District: " + roleJArray.getJSONObject(0).getString("district"));
                    if(((TextView) findViewById(R.id.DistrictTextView)).getText().equals("District: N/A")) findViewById(R.id.DistrictTextView).setVisibility(View.INVISIBLE);
                    else findViewById(R.id.DistrictTextView).setVisibility(View.VISIBLE);
                    parseRolesArray(roleJArray);
                }
                facebookID = jArray.getJSONObject(parse).getString("facebook_id");
            }
        } catch (Exception e) {
            Log.e("Profile Request Error", e.toString());
        }

        //Pull image from Facebook SDK
        ProfileImageDownload profilePicture = new ProfileImageDownload(facebookID, profileImageView);
        try {
            profileImageView.setImageBitmap(profilePicture.execute().get());
        } catch (Exception e) {
            Log.e("Profile Picture", e.toString());
        }

        ((Spinner) findViewById(R.id.congressSpinner)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Get number of districts from first selection
                String thisCongress = (((Spinner) findViewById(R.id.congressSpinner)).getSelectedItem()).toString();
                int startIndex = dataByCongress.indexOf(thisCongress);

                try {
                    ((TextView) findViewById(R.id.billsSponsoredTextView)).setText("Bills Sponsored: " + dataByCongress.get(startIndex + 1));
                } catch (IndexOutOfBoundsException e) {
                }
                try {
                    ((TextView) findViewById(R.id.billsCosponsoredTextView)).setText("Bills Cosponsored: " + dataByCongress.get(startIndex + 2));
                } catch (IndexOutOfBoundsException e) {
                }
                try {
                    ((TextView) findViewById(R.id.votesWPartyTextView)).setText("Votes With Party: " + dataByCongress.get(startIndex + 3) + "%");
                } catch (IndexOutOfBoundsException e) {
                }
                try {
                    ((TextView) findViewById(R.id.missedVotesTextView)).setText("Missed Votes:  " + dataByCongress.get(startIndex + 4) + "%");
                } catch (IndexOutOfBoundsException e) {
                }

                parseCommitteesArray(roleJArray, thisCongress);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    ArrayList<String> dataByCongress = new ArrayList<>();

    public void parseRolesArray(JSONArray roleJArray) {
        String congressNum = "", sponsor = "", cosponser = "", voteWith = "", missedVotes = "";
        try {
            for (int parse = 0; parse < roleJArray.length(); parse++) {
                try {
                    if (!roleJArray.getJSONObject(parse).getString("congress").equals("")) {
                        congressNum = roleJArray.getJSONObject(parse).getString("congress");
                    }
                } catch (Exception e) {
                }
                try {
                    if (!roleJArray.getJSONObject(parse).getString("bills_sponsored").equals("")) {
                        sponsor = roleJArray.getJSONObject(parse).getString("bills_sponsored");
                    }
                } catch (Exception e) {
                }
                try {
                    if (!roleJArray.getJSONObject(parse).getString("bills_cosponsored").equals("")) {
                        cosponser = roleJArray.getJSONObject(parse).getString("bills_cosponsored");
                    }
                } catch (Exception e) {
                }
                try {
                    if (!roleJArray.getJSONObject(parse).getString("votes_with_party_pct").equals("")) {
                        voteWith = roleJArray.getJSONObject(parse).getString("votes_with_party_pct");
                    }
                } catch (Exception e) {
                }
                try {
                    if (!roleJArray.getJSONObject(parse).getString("missed_votes_pct").equals("")) {
                        missedVotes = roleJArray.getJSONObject(parse).getString("missed_votes_pct");
                    }
                } catch (Exception e) {
                }

                ArrayList<String> temp = new ArrayList<>();
                dataByCongress.add(congressNum);
                dataByCongress.add(sponsor);
                dataByCongress.add(cosponser);
                dataByCongress.add(voteWith);
                dataByCongress.add(missedVotes);
            }

            Spinner congressSpinner = (Spinner) findViewById(R.id.congressSpinner);
            ArrayList<String> congressSpinnerArray = new ArrayList<>();
            for (int count = 0; count < dataByCongress.size(); count = count + 5) {
                try {
                    congressSpinnerArray.add(dataByCongress.get(count));
                } catch (IndexOutOfBoundsException e) {
                }
            }

            ArrayAdapter<String> congressAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, congressSpinnerArray);
            congressAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            congressSpinner.setAdapter(congressAdapter);

        } catch (Exception e) {
            Log.e("Role Parse Error", e.toString());
        }
    }

    public void parseCommitteesArray(JSONArray passedArray, String congressNum){
        JSONArray jArray = null;

        try {
            for (int parse = 0; parse < passedArray.length(); parse++) {
                if(passedArray.getJSONObject(parse).getString("congress").equals(congressNum)){
                    jArray = passedArray.getJSONObject(parse).getJSONArray("committees");
                    break;
                }
            }

            ListView committeeListView = (ListView) findViewById(R.id.committeesListView);
            ArrayList<String> committeeArrayList = new ArrayList<>();
            ArrayAdapter<String> commiteeArrayAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_selectable_list_item, committeeArrayList);
            committeeListView.setAdapter(commiteeArrayAdapter);

            for(int parse = 0; parse< jArray.length(); parse++){
                String name = jArray.getJSONObject(parse).getString("name");
                committeeArrayList.add(name);
            }

        }
        catch (JSONException e){

        }
    }
}

enum State {

    ALABAMA("Alabama", "AL"), ALASKA("Alaska", "AK"), AMERICAN_SAMOA("American Samoa", "AS"), ARIZONA("Arizona", "AZ"), ARKANSAS(
            "Arkansas", "AR"), CALIFORNIA("California", "CA"), COLORADO("Colorado", "CO"), CONNECTICUT("Connecticut", "CT"), DELAWARE(
            "Delaware", "DE"), DISTRICT_OF_COLUMBIA("District of Columbia", "DC"), FEDERATED_STATES_OF_MICRONESIA(
            "Federated States of Micronesia", "FM"), FLORIDA("Florida", "FL"), GEORGIA("Georgia", "GA"), GUAM("Guam", "GU"), HAWAII(
            "Hawaii", "HI"), IDAHO("Idaho", "ID"), ILLINOIS("Illinois", "IL"), INDIANA("Indiana", "IN"), IOWA("Iowa", "IA"), KANSAS(
            "Kansas", "KS"), KENTUCKY("Kentucky", "KY"), LOUISIANA("Louisiana", "LA"), MAINE("Maine", "ME"), MARYLAND("Maryland", "MD"), MARSHALL_ISLANDS(
            "Marshall Islands", "MH"), MASSACHUSETTS("Massachusetts", "MA"), MICHIGAN("Michigan", "MI"), MINNESOTA("Minnesota", "MN"), MISSISSIPPI(
            "Mississippi", "MS"), MISSOURI("Missouri", "MO"), MONTANA("Montana", "MT"), NEBRASKA("Nebraska", "NE"), NEVADA("Nevada",
            "NV"), NEW_HAMPSHIRE("New Hampshire", "NH"), NEW_JERSEY("New Jersey", "NJ"), NEW_MEXICO("New Mexico", "NM"), NEW_YORK(
            "New York", "NY"), NORTH_CAROLINA("North Carolina", "NC"), NORTH_DAKOTA("North Dakota", "ND"), NORTHERN_MARIANA_ISLANDS(
            "Northern Mariana Islands", "MP"), OHIO("Ohio", "OH"), OKLAHOMA("Oklahoma", "OK"), OREGON("Oregon", "OR"), PALAU("Palau",
            "PW"), PENNSYLVANIA("Pennsylvania", "PA"), PUERTO_RICO("Puerto Rico", "PR"), RHODE_ISLAND("Rhode Island", "RI"), SOUTH_CAROLINA(
            "South Carolina", "SC"), SOUTH_DAKOTA("South Dakota", "SD"), TENNESSEE("Tennessee", "TN"), TEXAS("Texas", "TX"), UTAH(
            "Utah", "UT"), VERMONT("Vermont", "VT"), VIRGIN_ISLANDS("Virgin Islands", "VI"), VIRGINIA("Virginia", "VA"), WASHINGTON(
            "Washington", "WA"), WEST_VIRGINIA("West Virginia", "WV"), WISCONSIN("Wisconsin", "WI"), WYOMING("Wyoming", "WY"), UNKNOWN(
            "Unknown", "");

    /**
     * The state's name.
     */
    private String name;

    /**
     * The state's abbreviation.
     */
    private String abbreviation;

    /**
     * The set of states addressed by abbreviations.
     */
    private static final Map<String, State> STATES_BY_ABBR = new HashMap<String, State>();

    /* static initializer */
    static {
        for (State state : values()) {
            STATES_BY_ABBR.put(state.getAbbreviation(), state);
        }
    }

    /**
     * Constructs a new state.
     *
     * @param name the state's name.
     * @param abbreviation the state's abbreviation.
     */
    State(String name, String abbreviation) {
        this.name = name;
        this.abbreviation = abbreviation;
    }

    /**
     * Returns the state's abbreviation.
     *
     * @return the state's abbreviation.
     */
    public String getAbbreviation() {
        return abbreviation;
    }

    public static State valueOfAbbreviation(final String abbr) {
        final State state = STATES_BY_ABBR.get(abbr);
        if (state != null) {
            return state;
        } else {
            return UNKNOWN;
        }
    }

    public static State valueOfName(final String name) {
        final String enumName = name.toUpperCase().replaceAll(" ", "_");
        try {
            return valueOf(enumName);
        } catch (final IllegalArgumentException e) {
            return State.UNKNOWN;
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
