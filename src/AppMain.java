import javax.swing.SwingUtilities;

public class AppMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                AppGUI gui = new AppGUI();
                gui.createGUI();
                // System.out.println(WeatherData.getLocationData("Tokyo"));
                // System.out.println(WeatherData.getCurrentTime());
            }
        });
    }
}
