package edu.usna.mobileos.jsonweatherexample;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends ActionBarActivity {

    // weather feed for Annapolis from forecast.io using Pepin's API key
    private String apiKey = "646463100831aeea0ccec00b18447d24";
    private String location = "38.9784,-76.4922";
    private String feed = "http://api.forecast.io/forecast/"+ apiKey +"/" + location;
    
    private TextView tv1, tv2, tv3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_main2);

        tv1 = (TextView) findViewById(R.id.textView1);
        tv2 = (TextView) findViewById(R.id.textView2);
        tv3 = (TextView) findViewById(R.id.textView3);

        new ReadJsonTask().execute(feed);
    }


    class ReadJsonTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... args) {
            return retrieveSiteContent(args[0]);
        }

        @Override
        protected void onPostExecute(String result){
            //tv1.setText(result);

            try {
                JSONObject jsonObj = new JSONObject(result);

                JSONObject currentlyObj = jsonObj.getJSONObject("currently");
                String currentlyTemp = currentlyObj.getString("temperature");

                JSONObject minutelyObj = jsonObj.getJSONObject("minutely");
                String minutelySummary = minutelyObj.getString("summary");

                JSONObject dailyObj = jsonObj.getJSONObject("daily");
                String dailySummary = dailyObj.getString("summary");

                tv1.setText(currentlyTemp + (char) 0x00B0);
                tv2.setText(minutelySummary);
                tv3.setText(dailySummary);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String retrieveSiteContent(String siteUrl){
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(siteUrl);
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                Log.e("PEPIN", "Status code: " + statusCode);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }
}
