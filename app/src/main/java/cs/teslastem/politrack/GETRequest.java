package cs.teslastem.politrack;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by karan on 10/31/16.
 */

public class GETRequest extends AsyncTask<String, String, String> {

    Thread thread;
    StringBuilder outputText;
    String url;
    String APIKey;
    int responseCode;

    public GETRequest(String url, String APIKey) {
        outputText = new StringBuilder();
        this.url = url;
        this.APIKey = APIKey;

//        thread = new Thread();
//        thread.start();
    }

    @Override
    public String doInBackground(String... Params) {

        try {
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) (obj.openConnection());
            connection.setRequestProperty("X-API-Key", APIKey);

            responseCode = connection.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            while ((line = in.readLine()) != null) {
                outputText.append(line + "\n");
            }
            in.close();
            connection.disconnect();

        } catch (Exception e) {
            return ("" + e);
        }

        return outputText.toString();
    }

//    protected void onPostExecute(Void v) {
//        try {
//            JSONArray jArray = new JSONArray((outputText.toString()));
//            for (int parse = 0; parse < jArray.length(); parse++){
//                JSONObject jObject = jArray.getJSONObject((parse));
//
//                String name = jObject.getString("name");
//                String tab1_text = jObject.getString(("tab1_text"));
//                int active = jObject.getInt("active");
//            }
//
//        } catch (JSONException e) {
//
//        }
//    }

}