import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class WeatherData {
    public static JSONObject getWeatherData(String locationName) {
        JSONArray locationData = getLocationData(locationName);

        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        String urlString = "https://api.open-meteo.com/v1/forecast?latitude=" +
                latitude + "&longitude=" + longitude + "&hourly=temperature_2m,weathercode";

        try {
            HttpURLConnection connection = fetchApiResponse(new URI(urlString));

            if (connection.getResponseCode() != 200) {
                System.out.println("Bad Connection! Failed to reach API");
                return null;
            }

            StringBuilder result = new StringBuilder();
            Scanner scan = new Scanner(connection.getInputStream());
            while (scan.hasNext()) {
                result.append(scan.nextLine());
            }

            scan.close();
            connection.disconnect();

            JSONParser parser = new JSONParser();
            JSONObject resultObject = (JSONObject) parser.parse(String.valueOf(result));
            JSONObject hourly = (JSONObject) resultObject.get("hourly");
            JSONArray time = (JSONArray) hourly.get("time");

            int index = findTime(time);
            JSONArray tempData = (JSONArray) hourly.get("temperature_2m");
            double temp = (double) tempData.get(index);

            JSONArray weathercode = (JSONArray) hourly.get("weathercode");
            String weatherCondition = convertWeatherCode((long) weathercode.get(index));

            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temp);
            weatherData.put("weather_condition", weatherCondition);
            return weatherData;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONArray getLocationData(String locationName) {
        locationName = locationName.replaceAll(" ", "+");

        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=en&format=json";

        try {
            HttpURLConnection connection = fetchApiResponse(new URI(urlString));

            if (connection.getResponseCode() != 200) {
                System.out.println("Bad Connection! Failed to reach API");
                return null;
            } else {
                StringBuilder result = new StringBuilder();
                Scanner scan = new Scanner(connection.getInputStream());
                while (scan.hasNext()) {
                    result.append(scan.nextLine());
                }

                scan.close();
                connection.disconnect();

                JSONParser parser = new JSONParser();
                JSONObject resultObject = (JSONObject) parser.parse(String.valueOf(result));

                JSONArray locationData = (JSONArray) resultObject.get("results");
                return locationData;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static HttpURLConnection fetchApiResponse(URI uri) {
        try {
            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            ;
            return connection;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int findTime(JSONArray timeList) {
        String currentTime = getCurrentTime();

        for (int i = 0; i < timeList.size(); i++) {
            String time = (String) timeList.get(i);
            if (time.equalsIgnoreCase(currentTime)) {
                return i;
            }
        }
        return 0;
    }

    public static String getCurrentTime() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");
        String formattedDateTime = currentDateTime.format(f);

        return formattedDateTime;
    }

    private static String convertWeatherCode(long weathercode) {
        String weatherCondition = "";
        if (weathercode == 0L) {
            weatherCondition = "Clear";
        } else if (weathercode > 0L && weathercode <= 3L) {
            weatherCondition = "Cloudy";
        } else if ((weathercode >= 51L && weathercode <= 67L)
                || (weathercode >= 80L && weathercode <= 99L)) {
            weatherCondition = "Rain";
        } else if (weathercode >= 71L && weathercode <= 77L) {
            weatherCondition = "Snow";
        }
        return weatherCondition;
    }
}
