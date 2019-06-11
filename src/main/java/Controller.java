import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.*;

public class Controller implements Initializable {

    @FXML
    private StackPane stackPane;

    @FXML
    public Label mainLabel;

    @FXML
    private Label inputLabel;

    @FXML
    private JFXTextField inputField;

    @FXML
    private JFXComboBox<Label> fromCountry;

    @FXML
    private JFXComboBox<Label> toCountry;

    @FXML
    private JFXButton convertButton;

    @FXML
    public ImageView swapButton;

    @FXML
    public JFXSpinner spinner;

    private CurrencyConvert currencyConvert;
    private Map<String, String> mapCountries;
    private ObservableList<Label> listCountries;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currencyConvert = new CurrencyConvert(this);
        setCountries();
        fromCountry.setItems(listCountries);
        toCountry.setItems(listCountries);
        inputField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) convert();
        });
        swapButton.setImage(new Image("/img/change.jpg"));
        inputLabel.setVisible(false);
        spinner.setVisible(false);
    }

    /**
     * The method of creating an array of two-letter ISO 3166 codes and, based on this, the names of countries.
     * After that, the data will be added to the list, for the ComboBox components,
     * and in the HashMap, for internal processing.
     */
    private void setCountries() {
        mapCountries = new HashMap<>();
        listCountries = FXCollections.observableArrayList();

        String[] locales = Locale.getISOCountries();
        for (String code : locales) {
            Locale locale = new Locale("en", code);
            Label country = new Label(locale.getDisplayCountry(Locale.ENGLISH));
            if (!locale.getCountry().toLowerCase().equals("an")) {
                Image icon = new Image("/flags/" + locale.getCountry().toLowerCase() + ".png");
                country.setGraphic(new ImageView(icon));
            }
            mapCountries.put(country.getText(), code);
            listCountries.add(country);
        }
    }

    @FXML
    public void selectCountry() {
        setInputLabel();
    }

    @FXML
    public void swapCountries() {
        Label tempItemCountry = toCountry.getSelectionModel().getSelectedItem();
        toCountry.setValue(fromCountry.getSelectionModel().getSelectedItem());
        fromCountry.setValue(tempItemCountry);
    }

    public Label getInputLabel() {
        return inputLabel;
    }

    /**
     * Update input label with new conversion rate (if possible).
     */
    private void setInputLabel() {
        Platform.runLater(() -> {
            setConversionRateOnLabel(getConversionRate());
        });
    }

    private void setConversionRateOnLabel(Double rate) {
        if (rate != null) {
            Formatter formatter = new Formatter(Locale.ENGLISH);
            formatter.format("Conversion rate = %.3f", rate);
            inputLabel.setVisible(true);
            inputLabel.setStyle("-fx-text-fill: #4239f7");
            inputLabel.setText(formatter.toString());
        }
    }

    /**
     * The method of getting the country currency code from
     * the ComboBox components and calling the conversion method for
     * the object of the CurrencyConvert {@link CurrencyConvert#getConversionRate(String, String)} class object.
     */
    private Double getConversionRate() {
        try {
            if (toCountry.getValue() != null) {
                String fromCountry = this.fromCountry.getSelectionModel().getSelectedItem().getText();
                String toCountry = this.toCountry.getSelectionModel().getSelectedItem().getText();
                String fromCurrency = Currency.getInstance(new Locale("en", mapCountries.get(fromCountry))).getCurrencyCode();
                String toCurrency = Currency.getInstance(new Locale("en", mapCountries.get(toCountry))).getCurrencyCode();
                return currencyConvert.getConversionRate(fromCurrency, toCurrency);
            }
        } catch (NullPointerException ignored) {
        }
        return null;
    }

    /**
     * The result of this method is a dialog box with the result of the conversion calculation.
     * In another case, the label will be available containing information about the error.
     */
    @FXML
    public void convert() {
        inputLabel.setVisible(true);
        if (fromCountry.getSelectionModel().getSelectedIndex() != -1 &&
                toCountry.getSelectionModel().getSelectedIndex() != -1) {
            if (validateInput()) {
                Double rate = getConversionRate();
                setConversionRateOnLabel(rate);

                new Thread(() -> {
                    // start download animation
                    startAnimation();

                    // animation time
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException ignored) {
                    }

                    // stop animation and show dialog
                    stopAnimation(rate);
                }).start();
            } else {
                inputLabel.setStyle("-fx-text-fill: #f70d17;");
                inputLabel.setText("Something went wrong fill the input correctly!");
            }
        } else {
            inputLabel.setStyle("-fx-text-fill: #f70d17;");
            inputLabel.setText("Unable to get conversion rate!");
        }
    }

    /**
     * A method for checking the input of a number in a text field and selected countries in the ComboBox components.
     *
     * @return true if the country is selected
     */
    private boolean validateInput() {
        if (!inputField.getText().equals("") && inputField.validate()) {
            if (isPositiveNumber(inputField.getText())) {
                return (fromCountry.getSelectionModel().getSelectedIndex() != -1 &&
                        toCountry.getSelectionModel().getSelectedIndex() != -1);
            }
        }
        return false;
    }

    private boolean isPositiveNumber(String input) {
        try {
            return Double.parseDouble(input) >= 0.0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void startAnimation() {
        Platform.runLater(() -> {
            spinner.setVisible(true);
            disableAllElements();
        });
    }

    private void stopAnimation(Double rate) {
        Platform.runLater(() -> {
            spinner.setVisible(false);
            turnOnAllElements();
            Platform.runLater(() -> {
                try {
                    convertButton.requestFocus(); // protection against accidental clicks
                    double result = rate * Double.parseDouble(inputField.getText());
                    Formatter formatter = new Formatter(Locale.ENGLISH);
                    formatter.format("The result of currency conversion: %.3f", result);
                    showDialog(formatter.toString(), stackPane);
                } catch (NumberFormatException e) {
                    inputLabel.setStyle("-fx-text-fill: #f70d17;");
                    inputLabel.setText("Something went wrong fill the input correctly!");
                }
            });
        });
    }

    private void disableAllElements() {
        convertButton.setDisable(true);
        inputField.setDisable(true);
        fromCountry.setDisable(true);
        toCountry.setDisable(true);
        swapButton.setDisable(true);
        inputLabel.setVisible(false);
        swapButton.setStyle("-fx-opacity: 0.3");
    }

    private void turnOnAllElements() {
        convertButton.setDisable(false);
        inputField.setDisable(false);
        fromCountry.setDisable(false);
        toCountry.setDisable(false);
        swapButton.setDisable(false);
        inputLabel.setVisible(true);
        swapButton.setStyle("-fx-opacity: 1");
    }

    private void showDialog(String body, StackPane stackPane) {
        JFXButton cancel = new JFXButton("Cancel");
        cancel.setPrefSize(120, 30);
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text("Conversion result"));
        content.setBody(new Text(body));
        content.setActions(cancel);
        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        cancel.setOnAction(event -> dialog.close());
        dialog.show();
    }

}
