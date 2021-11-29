package com.example.weather;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    final String API_KEY = "f9b026f81f61223575e48a7adf53aca6";
    final String WEATHER_URL_FORMAT = "https://api.openweathermap.org/data/2.5/onecall?appid=%s&lat=%s&lon=%s&units=metric&lang=ru";
    final String LOCATION_URL_FORMAT = "https://api.openweathermap.org/geo/1.0/reverse?appid=%s&lat=%s&lon=%s&limit=1&lang=ru";

    private LocationManager locationManager;
    private RequestQueue requestQueue;
    private ProgressBar progressBar;
    private ConstraintLayout mainLayout;
    private TextView textLocation, textLastUpdate, textCurrentTemperature, textCurrentWeather,
            textCurrentFeelsLike, textCurrentHumidity, textCurrentVisibility, textCurrentPressure,
            textCurrentWind;
    private ImageView imageCurrentWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainLayout = findViewById(R.id.constraint_layout_main);
        progressBar = findViewById(R.id.progress_bar);

        textLocation = findViewById(R.id.text_location);
        textLastUpdate = findViewById(R.id.text_last_update);
        textCurrentTemperature = findViewById(R.id.text_current_temperature);
        textCurrentWeather = findViewById(R.id.text_current_weather);
        textCurrentFeelsLike = findViewById(R.id.text_current_feels_like);
        textCurrentHumidity = findViewById(R.id.text_current_humidity);
        textCurrentVisibility = findViewById(R.id.text_current_visibility);
        textCurrentPressure = findViewById(R.id.text_current_pressure);
        textCurrentWind = findViewById(R.id.text_current_wind);
        imageCurrentWeather = findViewById(R.id.image_current_weather);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        requestQueue = Volley.newRequestQueue(this);

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityResultLauncher<String> requestPermissionLauncher =
                    registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                            isGranted -> {
                                if (isGranted) {
                                    updateWeather();
                                }
                                else {
                                    showError("Для работы приложения необходим доступ к геолокации. Перезапустите приложение и попробуйте снова");
                                }
                            }
                    );
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            updateWeather();
        }
    }

    public void onRefreshButtonClick(View view) {
        updateWeather();
    }

    private void showLoading(boolean show) {
        if (show) {
            mainLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            mainLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void updateWeather() {
        showLoading(true);
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            locationManager.requestSingleUpdate(LocationManager.FUSED_PROVIDER, location -> {
                        if (location != null) {
                            double lat = location.getLatitude();
                            double lon = location.getLongitude();
                            Response.ErrorListener errorListener = error ->
                                    showError("Нам не удалось определить местоположение. Перезапустите приложение и попробуйте снова");
                            showWeather(lat, lon, errorListener);
                            showLocation(lat, lon, errorListener);
                            showLastUpdate();
                        }
                    },
                    null);
        }
    }

    private void requestJsonObject(String url, Response.Listener<JSONObject> listener,
                                   Response.ErrorListener errorListener) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                listener, errorListener);
        requestQueue.add(request);
    }

    private void requestJsonArray(String url, Response.Listener<JSONArray> listener,
                                  Response.ErrorListener errorListener) {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                listener, errorListener);
        requestQueue.add(request);
    }

    private void showWeather(double lat, double lon, Response.ErrorListener errorListener) {
        String url = String.format(WEATHER_URL_FORMAT, API_KEY, lat, lon);

        System.out.println(url);

        requestJsonObject(url, response -> {
            try {
                showCurrentWeather(response);
                showForecast(response, "hourly");
                showForecast(response, "daily");
                showLoading(false);
            } catch (Exception exception) {
                exception.printStackTrace();
                showError("В процессе обработки возникла ошибка. Перезапустите приложение и попробуйте снова");
            }
        }, errorListener);
    }

    private void showLocation(double lat, double lon, Response.ErrorListener errorListener) {
        String url = String.format(LOCATION_URL_FORMAT, API_KEY, lat, lon);
        requestJsonArray(url, response -> {
            try {
                String location = response.getJSONObject(
                        0).getJSONObject("local_names").getString("ru");
                textLocation.setText(location);
            } catch (Exception exception) {
                showError("В процессе обработки возникла ошибка. Перезапустите приложение и попробуйте снова");
            }
        }, errorListener);
    }

    private void showCurrentWeather(JSONObject response) throws JSONException {
        JSONObject current = response.getJSONObject("current");

        String temp = current.getString("temp");
        String feelsLike = current.getString("feels_like");
        String humidity = current.getString("humidity");
        String visibility = current.getString("visibility");
        String pressure = current.getString("pressure");
        String windSpeed = current.getString("wind_speed");

        JSONObject weather = current.getJSONArray("weather").getJSONObject(0);
        String description = weather.getString("description");
        String icon = weather.getString("icon");

        textCurrentTemperature.setText(
                String.format(Locale.US, "%.0f°", Double.parseDouble(temp))
        );
        textCurrentFeelsLike.setText(
                String.format(Locale.US, "%.0f°", Double.parseDouble(feelsLike))
        );
        textCurrentHumidity.setText(
                String.format("%s%%", humidity)
        );
        textCurrentVisibility.setText(
                String.format(Locale.US, "%.2f км",
                        Double.parseDouble(visibility) / 1000)
        );
        textCurrentPressure.setText(
                String.format("%s гПа", pressure)
        );
        textCurrentWind.setText(
                String.format("%s м/c", windSpeed)
        );
        textCurrentWeather.setText(
                description.substring(0, 1).toUpperCase() + description.substring(1).toLowerCase()
        );
        imageCurrentWeather.setImageDrawable(getDrawable("ic_" + icon));
    }

    private void showForecast(JSONObject response, String name) throws JSONException {
        JSONArray forecast = response.getJSONArray(name);
        LayoutInflater inflater = getLayoutInflater();
        LinearLayout parent;
        if (name.equals("hourly")) {
            parent = findViewById(R.id.layout_daily_forecast);
        } else {
            parent = findViewById(R.id.layout_weekly_forecast);
        }
        parent.removeAllViews();
        for (int i = 0; i < forecast.length(); i++) {
            JSONObject periodForecast = forecast.getJSONObject(i);
            String temp;
            String timestamp = periodForecast.getString("dt");
            if (name.equals("hourly")) {
                temp = periodForecast.getString("temp");
            } else {
                temp = periodForecast.getJSONObject("temp").getString("day");
            }
            String icon = periodForecast.getJSONArray(
                    "weather").getJSONObject(0).getString("icon");

            LinearLayout item = (LinearLayout) inflater.inflate(R.layout.forecast_item, null);
            TextView textTime = item.findViewById(R.id.text_forecast_time);
            ImageView imageIcon = item.findViewById(R.id.image_forecast);
            TextView textTemperature = item.findViewById(R.id.text_forecast_temperature);

            String dateFormat;
            if (name.equals("hourly")) {
                dateFormat = "%tR";
            } else {
                dateFormat = "%ta";
            }
            textTime.setText(
                    String.format(dateFormat, new Date(Long.parseLong(timestamp) * 1000L))
            );
            imageIcon.setImageDrawable(getDrawable("ic_" + icon));
            textTemperature.setText(
                    String.format(Locale.US, "%.0f°", Double.parseDouble(temp))
            );

            parent.addView(item);
        }
    }

    private Drawable getDrawable(String name) {
        int resourceId = getResources().getIdentifier(name, "drawable", getPackageName());
        return getResources().getDrawable(resourceId);
    }

    private void showLastUpdate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM, HH:mm");
        textLastUpdate.setText(formatter.format(new Date()));
    }

    private void showError(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
        showLoading(false);
    }
}