package com.example.java_lms_group_01.Controller;


import com.example.java_lms_group_01.model.BMIModel;
import com.example.java_lms_group_01.model.UnitType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class InputController {

    @FXML private TextField heightField;
    @FXML private TextField weightField;
    @FXML private RadioButton metricBtn;
    @FXML private RadioButton englishBtn;

    @FXML
    private void handleCalculate(ActionEvent event) {

        try {
            double height = Double.parseDouble(heightField.getText());
            double weight = Double.parseDouble(weightField.getText());

            UnitType unit = metricBtn.isSelected()
                    ? UnitType.METRIC
                    : UnitType.ENGLISH;

            BMIModel model = new BMIModel(height, weight, unit);

            double bmi = model.calculateBMI();

            String status = model.getStatus(bmi);

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/Admin/result.fxml")
            );

            Scene scene = new Scene(loader.load());

            ResultController controller = loader.getController();
            controller.setResult(bmi, status);

            Stage stage = (Stage) heightField.getScene().getWindow();
            stage.setScene(scene);

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please enter valid numeric values!");
            alert.show();
        }
    }
}