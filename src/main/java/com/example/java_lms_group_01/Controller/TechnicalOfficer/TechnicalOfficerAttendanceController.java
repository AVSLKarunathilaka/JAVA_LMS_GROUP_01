package com.example.java_lms_group_01.Controller.TechnicalOfficer;

//import necessary classes
import com.example.java_lms_group_01.Repository.TechnicalOfficerRepository;
import com.example.java_lms_group_01.model.Attendance;
import com.example.java_lms_group_01.model.request.AttendanceRequest;
import com.example.java_lms_group_01.session.LoggedInTechnicalOfficer;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

//attendance ui control
public class TechnicalOfficerAttendanceController {

    //ui components
    @FXML private TextField txtStudentRegNo, txtCourseCode, txtSearch;
    @FXML private DatePicker dpAttendanceDate;
    @FXML private ComboBox<String> cmbSessionType, cmbStatus;

    //table view and columans
    @FXML private TableView<Attendance> tblAttendance;
    @FXML private TableColumn<Attendance, String> colAttendanceId, colStudentRegNo, colCourseCode,
            colDate, colSessionType, colStatus, colTechOfficerReg;

    //repostory object (for db operation)
    private final TechnicalOfficerRepository technicalOfficerRepository = new TechnicalOfficerRepository();

    //intialize method (aoutmatically call when ui load)
    @FXML
    public void initialize() {
        // Setup Dropdown Menus (ComboBoxes)
        cmbSessionType.setItems(FXCollections.observableArrayList("theory", "practical"));
        cmbStatus.setItems(FXCollections.observableArrayList("present", "absent", "medical"));

        // Setup Table Columns (Standard Beginner Style)
        colAttendanceId.setCellValueFactory(data -> { return data.getValue().attendanceIdProperty(); });
        colStudentRegNo.setCellValueFactory(data -> { return data.getValue().studentRegNoProperty(); });
        colCourseCode.setCellValueFactory(data -> { return data.getValue().courseCodeProperty(); });
        colDate.setCellValueFactory(data -> { return data.getValue().dateProperty(); });
        colSessionType.setCellValueFactory(data -> { return data.getValue().sessionTypeProperty(); });
        colStatus.setCellValueFactory(data -> { return data.getValue().statusProperty(); });
        colTechOfficerReg.setCellValueFactory(data -> { return data.getValue().techOfficerRegProperty(); });

        // Setup Selection Listener (When you click a row, fill the input fields)
        tblAttendance.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, row) -> {
            if (row != null) {
                txtStudentRegNo.setText(row.getStudentRegNo());
                txtCourseCode.setText(row.getCourseCode());

                // Convert the String date from database back to a LocalDate for the Picker
                if (row.getDate() != null && !row.getDate().isBlank()) {
                    dpAttendanceDate.setValue(LocalDate.parse(row.getDate()));
                } else {
                    dpAttendanceDate.setValue(null);
                }

                cmbSessionType.setValue(row.getSessionType());
                cmbStatus.setValue(row.getStatus());
            }
        });

        // 4. Initial(frist time data) load of data
        loadAttendanceData("");
    }

    //add button action
    @FXML
    private void addRecord(ActionEvent event) {
        // First, check if form is filled correctly
        if (isFormValid()) {
            try {
                // Build the request and send to database
                AttendanceRequest request = buildRequest();
                technicalOfficerRepository.addAttendance(request);

                // Refresh table and clear inputs
                loadAttendanceData(txtSearch.getText());
                clearFormFields();
                showInfo("Record added successfully!");

            } catch (Exception e) {
                showError("Failed to add record.", e);
            }
        }
    }

    //update button action
    @FXML
    private void updateRecord(ActionEvent event) {
        //get selected row
        Attendance selected = tblAttendance.getSelectionModel().getSelectedItem();

        //not selected warning
        if (selected == null) {
            showWarning("Please select a record from the table first.");
            return;
        }

        if (isFormValid()) {
            try {
                int id = Integer.parseInt(selected.getAttendanceId());
                AttendanceRequest request = buildRequest();

                //db update
                technicalOfficerRepository.updateAttendance(id, request);

                loadAttendanceData(txtSearch.getText());
                showInfo("Record updated successfully!");

            } catch (Exception e) {
                showError("Failed to update record.", e);
            }
        }
    }

    //delete button action
    @FXML
    private void deleteRecord(ActionEvent event) {
        Attendance selected = tblAttendance.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showWarning("Please select a record to delete.");
            return;
        }

        try {
            int id = Integer.parseInt(selected.getAttendanceId());
            technicalOfficerRepository.deleteAttendance(id);

            loadAttendanceData(txtSearch.getText());
            clearFormFields();
            showInfo("Record deleted.");

        } catch (Exception e) {
            showError("Failed to delete record.", e);
        }
    }

    //clear button action
    @FXML
    private void clearForm(ActionEvent event) {
        clearFormFields();
    }

    //search button action
    @FXML
    private void searchRecords(ActionEvent event) {
        loadAttendanceData(txtSearch.getText());
    }

    //refresh button action
    @FXML
    private void refreshRecords(ActionEvent event) {
        txtSearch.clear();
        loadAttendanceData("");
    }

    //put data to table from db
    private void loadAttendanceData(String keyword) {
        try {
            // Fetch list from repository
            List<Attendance> list = technicalOfficerRepository.findAttendance(keyword);
            // Update table items
            tblAttendance.getItems().setAll(list);
        } catch (SQLException e) {
            showError("Could not load data.", e);
        }
    }

    //form validation (field check)
    private boolean isFormValid() {
        // Simple check to see if all fields have values
        if (txtStudentRegNo.getText().isBlank() ||
                txtCourseCode.getText().isBlank() ||
                dpAttendanceDate.getValue() == null ||
                cmbSessionType.getValue() == null ||
                cmbStatus.getValue() == null) {

            showWarning("Please fill in all the required fields.");
            return false;
        }
        return true;
    }

    //create request object
    private AttendanceRequest buildRequest() {
        // Get current officer's registration
        String officerReg = LoggedInTechnicalOfficer.getRegistrationNo();
        if (officerReg == null) officerReg = "";

        // Package all data into an AttendanceRequest object
        return new AttendanceRequest(
                txtStudentRegNo.getText().trim(),
                txtCourseCode.getText().trim(),
                officerReg,
                dpAttendanceDate.getValue(),
                cmbSessionType.getValue(),
                cmbStatus.getValue()
        );
    }

    //clear form feilds
    private void clearFormFields() {
        txtStudentRegNo.clear();
        txtCourseCode.clear();
        dpAttendanceDate.setValue(null);
        cmbSessionType.setValue(null);
        cmbStatus.setValue(null);
        tblAttendance.getSelectionModel().clearSelection();
    }

    //display info alert
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.showAndWait();
    }

    //warning alert
    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message);
        alert.showAndWait();
    }

    //error alert
    private void showError(String message, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message + "\n" + e.getMessage());
        alert.showAndWait();
    }
}