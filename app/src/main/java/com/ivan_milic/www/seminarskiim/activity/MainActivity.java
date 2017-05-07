package com.ivan_milic.www.seminarskiim.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ivan_milic.www.seminarskiim.R;
import com.ivan_milic.www.seminarskiim.animations.MyBounceInterpolator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private EditText enterCity;
    private TextView temperature;
    private TextView unit;
    private TextView humidity;
    private TextView humUnit;
    private TextView tempMin;
    private TextView tempMinValue;
    private TextView tempMinUnit;
    private TextView tempMax;
    private TextView tempMaxValue;
    private TextView tempMaxUnit;
    private TextView overcast;
    private TextView windSpeed;
    private TextView windSpeedValue;
    private TextView windSpeedUnit;
    private ConstraintLayout layout;
    private Animation animation;
    private MyBounceInterpolator interpolator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.Weather);
        setContentView(R.layout.activity_main);

        //Setting toolbar to be invisible
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        setViews();
        setDefaultPref();

        if (!isNetworkAvailable()) {
            Toast.makeText(getApplicationContext(), "Internet connection not available", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        this.recreate();
    }

    public void setViews() {
        layout = (ConstraintLayout) findViewById(R.id.activity_main);
        temperature = (TextView) findViewById(R.id.temperature);
        unit = (TextView) findViewById(R.id.unit);
        overcast = (TextView) findViewById(R.id.overcast);
        humidity = (TextView) findViewById(R.id.humidity);
        humUnit = (TextView) findViewById(R.id.humidity_value);
        tempMin = (TextView) findViewById(R.id.tempMin);
        tempMinValue = (TextView) findViewById(R.id.minTempValue);
        tempMinUnit = (TextView) findViewById(R.id.minTempUnit);
        tempMax = (TextView) findViewById(R.id.tempMax);
        tempMaxValue = (TextView) findViewById(R.id.maxTempValue);
        tempMaxUnit = (TextView) findViewById(R.id.maxTempUnit);
        windSpeed = (TextView) findViewById(R.id.windSpeed);
        windSpeedValue = (TextView) findViewById(R.id.windSpeedValue);
        windSpeedUnit = (TextView) findViewById(R.id.windSpeedUnit);

        enterCity = (EditText) findViewById(R.id.enter_city);

        animation = AnimationUtils.loadAnimation(this, R.anim.scale);
        interpolator = new MyBounceInterpolator(0.2, 20);
    }

    //Check if internet is available
    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    //Getting city data from www.openweathermap.org
    public void findWeather(View view) {
        animation.setInterpolator(interpolator);
        view.startAnimation(animation);

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(100);

        //Closing keyboard after the button is pressed
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        //Getting the JSON data with API key
        try {
            String encodedCity = URLEncoder.encode(enterCity.getText().toString(), "UTF-8");
            DownloadTask task = new DownloadTask();
            task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + encodedCity + "&APPID=0081465f5fe2ad00232ea0ad23a67e49");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Could not find", Toast.LENGTH_LONG).show();
        }
    }

    //Saving data from Views
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        String city = enterCity.getText().toString();
        String nOvercast = overcast.getText().toString();
        String nTemperature = temperature.getText().toString();
        String nUnit = unit.getText().toString();
        String nHumUnit = humUnit.getText().toString();
        String nMinTempVal = tempMinValue.getText().toString();
        String nMaxTempVal = tempMaxValue.getText().toString();
        String nWindSpeedVal = windSpeedValue.getText().toString();
        String nWindSpeedUnit = windSpeedUnit.getText().toString();

        savedInstanceState.putString("City", city);
        savedInstanceState.putString("Overcast", nOvercast);
        savedInstanceState.putString("Temp", nTemperature);
        savedInstanceState.putString("MinTemp", nMinTempVal);
        savedInstanceState.putString("MaxTemp", nMaxTempVal);
        savedInstanceState.putString("Unit", nUnit);
        savedInstanceState.putString("Hum", nHumUnit);
        savedInstanceState.putString("WinSpeed", nWindSpeedVal);
        savedInstanceState.putString("WinSpeedUnit", nWindSpeedUnit);
        super.onSaveInstanceState(savedInstanceState);
    }

    //Restoring the data
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        String city = savedInstanceState.getString("City");
        enterCity.setText(city);

        String nOvercast = savedInstanceState.getString("Overcast");
        overcast.setText(nOvercast);
        changeBackground(nOvercast);

        String nTemperature = savedInstanceState.getString("Temp");
        String nUnit = savedInstanceState.getString("Unit");
        String nMinTempVal = savedInstanceState.getString("MinTemp");
        String nMaxTempVal = savedInstanceState.getString("MaxTemp");
        savedTemperature(nTemperature, nUnit, nMinTempVal, nMaxTempVal);

        String nHumUnit = savedInstanceState.getString("Hum");
        saveHumidity(nHumUnit);

        String nWindSpeedVal = savedInstanceState.getString("WinSpeed");
        String nWindSpeedUnit = savedInstanceState.getString("WinSpeedUnit");
        saveWindSpeed(nWindSpeedVal, nWindSpeedUnit);

        super.onRestoreInstanceState(savedInstanceState);
    }

    //Setting the default preferences for Weather unit
    private void setDefaultPref() {

        SharedPreferences pref = getSharedPreferences(SettingsActivity.MY_GLOBAL_PREFS, MODE_PRIVATE);
        if (pref.getString("TEMP", null) == null
                && pref.getString("WIND", null) == null) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("TEMP", " °C");
            editor.putString("WIND", " km/h");
            editor.apply();
        }
    }

    //Change the background depending on the weather
    private void changeBackground(String mOvercast) {

        switch (mOvercast) {
            case "Clear":
                layout.setBackgroundResource(R.drawable.bck_main_sun);
                break;
            case "Thunderstorm":
                layout.setBackgroundResource(R.drawable.bck_main_thunder);
                break;
            case "Rain":
                layout.setBackgroundResource(R.drawable.bck_main_rain);
                break;
            case "Snow":
                layout.setBackgroundResource(R.drawable.bck_main_snow);
                break;
            case "Fog":
                layout.setBackgroundResource(R.drawable.bck_main_mist);
                break;
            case "Clouds":
                layout.setBackgroundResource(R.drawable.bck_main_cloud);
                break;
            case "Drizzle":
                layout.setBackgroundResource(R.drawable.bck_main_drizzle);
                break;
            case "Haze":
                layout.setBackgroundResource(R.drawable.bck_main_mist);
                break;
            default:
                layout.setBackgroundResource(R.drawable.bck_main_sun);
                break;
        }
    }

    private void changeTemperature(double mTemperature, double mMinTemp, double mMaxTemp) {

        //T(°C) = T(K) - 273.15
        //T(°F) = T(K) x 9 / 5 - 459.67
        DecimalFormat decimalFormat = new DecimalFormat("0");
        SharedPreferences preferences = getSharedPreferences(SettingsActivity.MY_GLOBAL_PREFS, MODE_PRIVATE);
        String prefTemperature = preferences.getString("TEMP", "");

        if (prefTemperature.contains(" °C")) {
            double celTemp = mTemperature - 273.15;
            double celMinTemp = mMinTemp - 273.15;
            double celMaxTemp = mMaxTemp - 273.15;

            temperature.setText(decimalFormat.format(celTemp));
            unit.setText(prefTemperature);

            tempMin.setText(R.string.Tmin);
            tempMinValue.setText(decimalFormat.format(celMinTemp));
            tempMinUnit.setText(prefTemperature);

            tempMax.setText(R.string.Tmax);
            tempMaxValue.setText(decimalFormat.format(celMaxTemp));
            tempMaxUnit.setText(prefTemperature);
        } else {
            double fahrTemp = mTemperature * 9 / 5 - 459.67;
            double fahrMinTemp = mMinTemp * 9 / 5 - 459.67;
            double fahrMaxTemp = mMaxTemp * 9 / 5 - 459.67;

            temperature.setText(decimalFormat.format(fahrTemp));
            unit.setText(prefTemperature);

            tempMin.setText(R.string.Tmin);
            tempMinValue.setText(decimalFormat.format(fahrMinTemp));
            tempMinUnit.setText(prefTemperature);

            tempMax.setText(R.string.Tmax);
            tempMaxValue.setText(decimalFormat.format(fahrMaxTemp));
            tempMaxUnit.setText(prefTemperature);
        }
    }

    private void savedTemperature(String mTemperature, String nUnit, String mMinTemp, String mMaxTemp) {
        DecimalFormat decimalFormat = new DecimalFormat("0");
        SharedPreferences preferences = getSharedPreferences(SettingsActivity.MY_GLOBAL_PREFS, MODE_PRIVATE);
        String prefTemperature = preferences.getString("TEMP", "");

        //when returning from Settings Activity setting the string for double parsing
        if (mTemperature.isEmpty() && mMinTemp.isEmpty() && mMaxTemp.isEmpty()) {
            mTemperature = "1";
            mMinTemp = "1";
            mMaxTemp = "1";
        }
        double dTemperature = Double.parseDouble(mTemperature);
        double dMinTemp = Double.parseDouble(mMinTemp);
        double dMaxTemp = Double.parseDouble(mMaxTemp);

        //Setting the temperature if is changed in Settings
        if (prefTemperature.equals(nUnit)) {
            temperature.setText(mTemperature);
            unit.setText(prefTemperature);
            tempMin.setText(R.string.Tmin);
            tempMinValue.setText(mMinTemp);
            tempMinUnit.setText(prefTemperature);
            tempMax.setText(R.string.Tmax);
            tempMaxValue.setText(mMaxTemp);
            tempMaxUnit.setText(prefTemperature);
        } else if (nUnit.equals(" °C") && prefTemperature.equals(" °F")) {
            // T(°F) = T(°C) × 1.8 + 32
            dTemperature = dTemperature * 1.8 + 32;
            dMinTemp = dMinTemp * 1.8 + 32;
            dMaxTemp = dMaxTemp * 1.8 + 32;
            temperature.setText(decimalFormat.format(dTemperature));
            unit.setText(prefTemperature);
            tempMin.setText(R.string.Tmin);
            tempMinValue.setText(decimalFormat.format(dMinTemp));
            tempMinUnit.setText(prefTemperature);
            tempMax.setText(R.string.Tmax);
            tempMaxValue.setText(decimalFormat.format(dMaxTemp));
            tempMaxUnit.setText(prefTemperature);
        } else if (nUnit.equals(" °F") && prefTemperature.equals(" °C")) {
            // T(°C) = (T(°F) - 32) / 1.8
            dTemperature = (dTemperature - 32) / 1.8;
            dMinTemp = (dMinTemp - 32) / 1.8;
            dMaxTemp = (dMaxTemp - 32) / 1.8;
            temperature.setText(decimalFormat.format(dTemperature));
            unit.setText(prefTemperature);
            tempMin.setText(R.string.Tmin);
            tempMinValue.setText(decimalFormat.format(dMinTemp));
            tempMinUnit.setText(prefTemperature);
            tempMax.setText(R.string.Tmax);
            tempMaxValue.setText(decimalFormat.format(dMaxTemp));
            tempMaxUnit.setText(prefTemperature);
        }
    }

    private void changeHumidity(double mHumidity) {
        DecimalFormat decimalFormat = new DecimalFormat("0");

        humidity.setText(R.string.Hum);
        humUnit.setText(String.format("%s%s", decimalFormat.format(mHumidity), getString(R.string.hum_value)));
    }

    private void saveHumidity(String mHumidity) {
        if (mHumidity.isEmpty()) {
            humidity.setText("");
        } else {
            humidity.setText(R.string.Hum);
        }
        humUnit.setText(mHumidity);
    }

    private void changeWindSpeed(double mWindSpeed) {
        DecimalFormat decimalFormat = new DecimalFormat("0");
        SharedPreferences preferences = getSharedPreferences(SettingsActivity.MY_GLOBAL_PREFS, MODE_PRIVATE);
        String prefWind = preferences.getString("WIND", "");

        //km/h = m/s x 3.6
        //KNOT = m/s * 1.9438444924574
        if (prefWind.contains(" km/h")) {
            double kmhWind = mWindSpeed * 3.6;
            windSpeed.setText(R.string.wind_speed);
            windSpeedValue.setText(decimalFormat.format(kmhWind));
            windSpeedUnit.setText(prefWind);
        } else {
            double knotWind = mWindSpeed * 1.9438444924574;
            windSpeed.setText(R.string.wind_speed);
            windSpeedValue.setText(decimalFormat.format(knotWind));
            windSpeedUnit.setText(prefWind);
        }
    }

    private void saveWindSpeed(String windValue, String windUnit) {
        SharedPreferences preferences = getSharedPreferences(SettingsActivity.MY_GLOBAL_PREFS, MODE_PRIVATE);
        String prefWind = preferences.getString("WIND", "");
        DecimalFormat decimalFormat = new DecimalFormat("0");
        if (windValue.isEmpty()) {
            windValue = "1";
        }
        double mWindValue = Double.parseDouble(windValue);

        //Setting the wind speed if is changed in Settings
        if (windUnit.equals(prefWind)) {
            windSpeed.setText(R.string.wind_speed);
            windSpeedValue.setText(windValue);
            windSpeedUnit.setText(prefWind);
        } else if (windUnit.equals(" km/h") && prefWind.equals(" KNOTS")) {
            windSpeed.setText(R.string.wind_speed);
            //KNOT = km/h * 0.539956803
            mWindValue = mWindValue * 0.539956803;
            windSpeedValue.setText(decimalFormat.format(mWindValue));
            windSpeedUnit.setText(prefWind);
        } else if (windUnit.equals(" KNOTS") && prefWind.equals(" km/h")) {
            windSpeed.setText(R.string.wind_speed);
            //km/h = KNOT * 1.852
            mWindValue = mWindValue * 1.852;
            windSpeedValue.setText(decimalFormat.format(mWindValue));
            windSpeedUnit.setText(prefWind);
        }
    }

    //Async Task is used for getting the data in separate thread
    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {

                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {

                //Setting the JSON and gettin data from it
                JSONObject object = new JSONObject(result);

                //Temperatura
                double mTemperature = object.getJSONObject("main").getDouble("temp");

                //Vlaznost
                double mHumidity = object.getJSONObject("main").getDouble("humidity");

                //Minimalna Temperatura
                double mMinTemperature = object.getJSONObject("main").getDouble("temp_min");

                //Maksimalna Temperatura
                double mMaxTemperature = object.getJSONObject("main").getDouble("temp_max");

                changeTemperature(mTemperature, mMinTemperature, mMaxTemperature);
                changeHumidity(mHumidity);

                //Overcast
                String overInfo = object.getString("weather");
                JSONArray arr = new JSONArray(overInfo);
                for (int i = 0; i < arr.length(); i++) {

                    JSONObject jsonPart = arr.getJSONObject(i);
                    String mOvercast = jsonPart.getString("main");
                    overcast.setText(mOvercast);

                    changeBackground(mOvercast);
                }

                //Wind Speed
                double mWindSpeed = object.getJSONObject("wind").getDouble("speed");
                changeWindSpeed(mWindSpeed);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return false;
        }
    }
}



