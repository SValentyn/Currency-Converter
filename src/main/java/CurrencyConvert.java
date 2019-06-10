import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * The class for retrieving data from the server API.
 */
class CurrencyConvert {

    // Server host address (access_key is required for access)
    private static final String API_SERVER = "https://free.currconv.com/api/";

    private Controller controller;

    CurrencyConvert(Controller controller) {
        this.controller = controller;
    }

    /**
     * The method of obtaining the conversion rate animationTimer the specified URL.
     */
    public double getConversionRate(String fromCurrencyCode, String toCurrencyCode) {
        // for example: {"USD_EUR":0.881715}
        String response = getResponse(API_SERVER + "v7/convert?q=" +
                fromCurrencyCode + "_" + toCurrencyCode + "&compact=ultra&apiKey=3ef8a6dea3d475ac8802");

        // it would be better to parse the string using GSON
        if (response != null) {
            return Double.parseDouble(response.substring(11, response.length() - 1));
        }
        return 0.0;
    }

    /**
     * The method of obtaining the response string from the API server.
     */
    private String getResponse(String baseURL) {
        if (baseURL == null || baseURL.isEmpty()) {
            System.out.println("Application Error!");
            return null;
        }

        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(baseURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream stream = connection.getInputStream();

            // Read the server's response to the request
            int data = stream.read();
            while (data != -1) {
                response.append((char) data);
                data = stream.read();
            }
            stream.close();
        } catch (MalformedURLException | UnknownHostException e) {
            controller.getInputLabel().setText("Unable to fetch data check your internet connection");
            controller.getInputLabel().setVisible(true);
            e.printStackTrace();
        } catch (IOException e) {
            controller.getInputLabel().setText("Unavailable data for this country's currency");
            controller.getInputLabel().setVisible(true);
            e.printStackTrace();
        }
        return response.toString();
    }

}
