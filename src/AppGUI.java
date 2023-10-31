import javax.imageio.ImageIO;
import javax.swing.*;

import org.json.simple.JSONObject;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class AppGUI {
    private JSONObject weatherData;

    public void createGUI() {

        JFrame frame = new JFrame("Weather App");
        JPanel southPanel = new JPanel();

        southPanel.setBackground(Color.LIGHT_GRAY);
        southPanel.setLayout(new FlowLayout());

        frame.add(southPanel, BorderLayout.SOUTH);
        frame.setSize(400, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        JPanel weatherPanel = new JPanel();
        weatherPanel.setLayout(new BorderLayout());
        weatherPanel.setBackground(new Color(171, 205, 219));

        JLabel tempText = new JLabel("80 F");
        Font font = new Font("Sans Serif", Font.PLAIN, 50);
        tempText.setFont(font);
        tempText.setHorizontalAlignment(SwingConstants.CENTER);
        weatherPanel.add(tempText, BorderLayout.NORTH);

        JPanel tempAndConditionsPanel = new JPanel();
        tempAndConditionsPanel.setBackground(new Color(171, 205, 219));
        tempAndConditionsPanel.setLayout(new BorderLayout());
        tempAndConditionsPanel.add(tempText, BorderLayout.NORTH);

        JLabel conditions = new JLabel("Sunny");
        Font condFont = new Font("Arial", Font.PLAIN, 30);
        conditions.setFont(condFont);
        conditions.setHorizontalAlignment(SwingConstants.CENTER);
        tempAndConditionsPanel.add(conditions, BorderLayout.CENTER);
        weatherPanel.add(tempAndConditionsPanel, BorderLayout.NORTH);

        JLabel conditionImage = new JLabel(loadImage("src/img/cloudy.png"));
        conditionImage.setHorizontalAlignment(SwingConstants.CENTER);
        weatherPanel.add(conditionImage, BorderLayout.CENTER);

        frame.add(weatherPanel);

        JTextField searchField = new JTextField(16);
        southPanel.add(searchField);

        JButton searchButton = new JButton("Search");
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userInput = searchField.getText();
                if (userInput.replaceAll("\\s", "").length() <= 0) {
                    return;
                }

                weatherData = WeatherData.getWeatherData(userInput);
                String weatherCondition = (String) weatherData.get("weather_condition");

                switch (weatherCondition) {
                    case "Clear":
                        conditionImage.setIcon(loadImage("src/img/clear.png"));
                        break;
                    case "Cloudy":
                        conditionImage.setIcon(loadImage("src/img/cloudy.png"));
                        break;
                    case "Rain":
                        conditionImage.setIcon(loadImage("src/img/rain.png"));
                        break;
                    case "Snow":
                        conditionImage.setIcon(loadImage("src/img/snow.png"));
                        break;
                }

                double temperature = (double) weatherData.get("temperature");
                tempText.setText(temperature + " C");

                conditions.setText(weatherCondition);

            }
        });
        southPanel.add(searchButton);

    }

    private ImageIcon loadImage(String resourcePath) {
        try {
            BufferedImage image = ImageIO.read(new File(resourcePath));
            return new ImageIcon(image);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Could not load image");
        return null;
    }
}
