package com.example.weatherapp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

public class WeatherAppFX extends Application {

    private static final String API_KEY = "your_api_key_here";
    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/weather";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        String city = "London"; // Default city
        if (getParameters().getRaw().size() > 0) {
            city = getParameters().getRaw().get(0);
        }

        VBox root = new VBox();
        Label weatherLabel = new Label();

        try {
            String response = getWeather(city);
            weatherLabel.setText(parseWeather(response));
        } catch (IOException e) {
            weatherLabel.setText("Error fetching weather data: " + e.getMessage());
        }

        root.getChildren().add(weatherLabel);
        Scene scene = new Scene(root, 300, 200);

        primaryStage.setTitle("Weather App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static String getWeather(String city) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String url = BASE_URL + "?q=" + city + "&appid=" + API_KEY;
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        }
    }

    private static String parseWeather(String jsonResponse) {
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        String cityName = jsonObject.get("name").getAsString();
        JsonObject main = jsonObject.getAsJsonObject("main");
        double temp = main.get("temp").getAsDouble();
        int humidity = main.get("humidity").getAsInt();

        return "City: " + cityName + "\nTemperature: " + temp + "K\nHumidity: " + humidity + "%";
    }
}
