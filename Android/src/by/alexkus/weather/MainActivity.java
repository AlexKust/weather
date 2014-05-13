package by.alexkus.weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static String currentDataUrl = "http://192.168.2.1/cgi-bin/luci/weather/current";
	//private static String currentDataUrl = "http://192.168.1.2/cgi-bin/luci/weather/current";

	private static IntentFilter s_timeIntentFilter;

	static {
		s_timeIntentFilter = new IntentFilter();
		s_timeIntentFilter.addAction(Intent.ACTION_TIME_TICK);
		s_timeIntentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
		s_timeIntentFilter.addAction(Intent.ACTION_TIME_CHANGED);
	}
	
	private final BroadcastReceiver m_timeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_TIME_CHANGED.equals(intent.getAction()) || 
                Intent.ACTION_TIMEZONE_CHANGED.equals(intent.getAction()) ||
                Intent.ACTION_DATE_CHANGED.equals(intent.getAction())) {
            	Clock clock = (Clock) findViewById(R.id.clock);
            	Date date = (Date) findViewById(R.id.date);
            	
            	clock.updateTime();
            	date.updateDate();
            }
        }
    };
    
    private static IntentFilter s_networkIntentFilter;

	static {
		s_networkIntentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
	}
	
	private final BroadcastReceiver m_networkChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()){
            	Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
            	new HttpAsyncTask().execute(currentDataUrl);
            }
        }
    };    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        registerReceiver(m_timeChangedReceiver, s_timeIntentFilter);
        registerReceiver(m_networkChangedReceiver, s_networkIntentFilter);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(m_timeChangedReceiver);
        unregisterReceiver(m_networkChangedReceiver);
    }
    
    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {
 
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
 
            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
 
            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();
 
            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";
 
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
 
        return result;
    }
 
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
 
        inputStream.close();
        return result;
 
    }
 
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
 
            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            TextView currentTemperatureView = (TextView) findViewById(R.id.currentTemperature);
            TextView currentPressureView = (TextView) findViewById(R.id.currentPressure);
            TextView currentWindView = (TextView) findViewById(R.id.currentWind);
            JSONObject currentData = null;
            int temperature = 0;
            int pressure = 0;
            int windSpeed = 0;
			try {
				currentData = new JSONObject(result);
				pressure = currentData.getInt("pressure");
				
				JSONObject temperatureData = currentData.getJSONObject("temperature");
				temperature = temperatureData.getInt("maxValue");
				
				JSONObject windData = currentData.getJSONObject("wind");
				windSpeed = windData.getInt("maxValue");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String sign = "";
			if (temperature > 0)
				sign = "+";
			currentTemperatureView.setText(sign + String.valueOf(temperature));
			currentPressureView.setText(String.valueOf(pressure) + " mm");
			currentWindView.setText(String.valueOf(windSpeed) + " m/s");
        }
        
    }
}
