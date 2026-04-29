package com.example.java_lms_group_01.Controller.TechnicalOfficer;

import com.example.java_lms_group_01.Repository.TechnicalOfficerRepository;
import com.example.java_lms_group_01.model.ExamAttendance;
import com.example.java_lms_group_01.model.request.ExamAttendanceRequest;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class TechnicalOfficerExamAttendanceController {

    //ui components
    @FXML private TextField txtStudentRegNo, txtCourseCode, txtSearch;
    //attendance status(p/a)
    @FXML private ComboBox<String> cmbStatus;
    //date of attendance
    @FXML private DatePicker dpAttendanceDate;

    //main table display records
    @FXML private TableView<ExamAttendance> tblExamAttendance;
    //tb column
    @FXML private TableColumn<ExamAttendance, String> colExamAttendanceId, colStudentRegNo,
            colCourseCode, colStatus, colAttendanceDate;

    //repository to handle db
    private final TechnicalOfficerRepository technicalOfficerRepository = new TechnicalOfficerRepository();

    //intialize
    @FXML
    public void initialize() {
        // Setup Status Dropdown
        cmbStatus.setItems(FXCollections.observableArrayList("present", "absent"));

        // Setup Table Columns (Standard Readable Style)
        colExamAttendanceId.setCellValueFactory(data -> { return data.getValue().examAttendanceIdProperty(); });
        colStudentRegNo.setCellValueFactory(data -> { return data.getValue().studentRegNoProperty(); });
        colCourseCode.setCellValueFactory(data -> { return data.getValue().courseCodeProperty(); });
        colStatus.setCellValueFactory(data -> { return data.getValue().statusProperty(); });
        colAttendanceDate.setCellValueFactory(data -> { return data.getValue().attendanceDateProperty(); });

        // Selection Listener: When a row is clicked, fill the text boxes
        tblExamAttendance.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, row) -> {
            if (row != null) {
                txtStudentRegNo.setText(row.getStudentRegNo());
                txtCourseCode.setText(row.getCourseCode());
                cmbStatus.setValue(row.getStatus());

                // Convert String date to LocalDate for the DatePicker
                if (row.getAttendanceDate() != null && !row.getAttendanceDate().isEmpty()) {
                    dpAttendanceDate.setValue(LocalDate.parse(row.getAttendanceDate()));
                } else {
                    dpAttendanceDate.setValue(null);
                }
            }
        });

        // 4. Initial load of data
        loadTableData("");
    }

    //add new exam attendance record
    @FXML
    private void addRecord(ActionEvent event) {
        if (isFormValid()) {
            try {
                ExamAttendanceRequest request = createRequest();
                technicalOfficerRepository.addExamAttendance(request);

                loadTableData(txtSearch.getText());//refresh tb
                clearFormFields();
                showInfo("Exam attendance added!");
            } catch (Exception e) {
                showError("Failed to add record.", e);
            }
        }
    }

    //update the selected exam attendance
    @FXML
    private void updateRecord(ActionEvent event) {
        ExamAttendance selected = tblExamAttendance.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showWarning("Please select a record from the table first.");
            return;
        }

        if (isFormValid()) {
            try {
                int id = Integer.parseInt(selected.getExamAttendanceId());
                ExamAttendanceRequest request = createRequest();

                technicalOfficerRepository.updateExamAttendance(id, request);
                loadTableData(txtSearch.getText());
                showInfo("Record updated successfully!");
            } catch (Exception e) {
                showError("Failed to update record.", e);
            }
        }
    }

    //delete exam attendance record
    @FXML
    private void deleteRecord(ActionEvent event) {
        ExamAttendance selected = tblExamAttendance.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showWarning("Select a record to delete.");
            return;
        }

        try {
            int id = Integer.parseInt(selected.getExamAttendanceId());
            technicalOfficerRepository.deleteExamAttendance(id);

            loadTableData(txtSearch.getText());
            clearFormFields();
            showInfo("Record deleted.");
        } catch (Exception e) {
            showError("Failed to delete record.", e);
        }
    }

    //search
    @FXML
    private void searchRecords(ActionEvent event) {
        loadTableData(txtSearch.getText());
    }

    //refresh and clear search feild
    @FXML
    private void refreshRecords(ActionEvent event) {
        txtSearch.clear();
        loadTableData("");
    }

    //clear all inputs feilds in the form
    @FXML
    private void clearForm(ActionEvent event) {
        clearFormFields();
    }

    //loads exam attendance data into table view
    private void loadTableData(String keyword) {
        try {
            List<ExamAttendance> list = technicalOfficerRepository.findExamAttendance(keyword);
            tblExamAttendance.getItems().setAll(list);
        } catch (SQLException e) {
            showError("Could not load exam attendance data.", e);
        }
    }

    //validation
    private boolean isFormValid() {
        // Simple checks to ensure no fields are empty
        if (txtStudentRegNo.getText().isBlank() ||
                txtCourseCode.getText().isBlank() ||
                cmbStatus.getValue() == null ||
                dpAttendanceDate.getValue() == null) {

            showWarning("Please fill in all fields.");
            return false;
        }
        return true;
    }

    //creates ExamAttendanceRequest object from the form inputs
    private ExamAttendanceRequest createRequest() {
        // Package the data for the database
        return new ExamAttendanceRequest(
                txtStudentRegNo.getText().trim(),
                txtCourseCode.getText().trim(),
                cmbStatus.getValue(),
                dpAttendanceDate.getValue()
        );
    }

    //clear form
    private void clearFormFields() {
        txtStudentRegNo.clear();
        txtCourseCode.clear();
        cmbStatus.setValue(null);
        dpAttendanceDate.setValue(null);
        tblExamAttendance.getSelectionModel().clearSelection();
    }

    //info alert
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.showAndWait();
    }

    //error mzg
    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message);
        alert.showAndWait();
    }

    private void showError(String message, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message + "\n" + e.getMessage());
        alert.showAndWait();
    }
}