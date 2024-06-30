package com.example.weatherapp;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherController {

    @FXML
    private TextField cityField;

    @FXML
    private Label weatherLabel;

    @FXML
    private void getWeather() {
        String city = cityField.getText();
        if (city.isEmpty()) {
            weatherLabel.setText("Please enter a city name.");
            return;
        }

        // Call the weather API and set the weatherLabel with the result
        String weatherInfo = getWeatherInfo(city);
        weatherLabel.setText(weatherInfo);
    }

    private String getWeatherInfo(String city) {
        String apiKey = "your_api_key"; // Replace with your actual API key
        String urlString = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey
                + "&units=metric";
        HttpURLConnection conn = null;
        BufferedReader in = null;

        try {
            @SuppressWarnings("deprecation")
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return "Error: Unable to fetch weather data";
            }

            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            // Parse JSON response
            JSONObject json = new JSONObject();
            JSONObject main = json.getJSONObject("main");
            double temp = main.getDouble("temp");
            String weatherDescription = ((Object) json.getJSONArray("weather").getJSONObject(0))
                    .getString("description");

            return "Weather in " + city + ": " + weatherDescription + ", " + temp + "°C";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error retrieving weather data";
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
