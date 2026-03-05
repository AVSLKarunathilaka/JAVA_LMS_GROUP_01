package com.example.java_lms_group_01.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ResultController {

    @FXML private Label bmiLabel;
    @FXML private Label statusLabel;

    public void setResult(double bmi, String status) {
        bmiLabel.setText(String.format("%.2f", bmi));
        statusLabel.setText(status);
    }
    @FXML
    private void handleBack(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/Admin/input.fxml")
            );

            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) bmiLabel.getScene().getWindow();
            stage.setScene(scene);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}