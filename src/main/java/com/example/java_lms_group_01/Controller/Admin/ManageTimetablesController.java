package com.example.java_lms_group_01.Controller.Admin;

import com.example.java_lms_group_01.Repository.AdminRepository;
import com.example.java_lms_group_01.model.Timetable;
import com.example.java_lms_group_01.session.LoggedInAdmin;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class ManageTimetablesController implements Initializable {

    @FXML private ComboBox<String> cmbFilterDepartment, cmbFilterSemester;
    @FXML private TableView<Timetable> tblTimetable;
    @FXML private TableColumn<Timetable, String> colTimetableId, colDepartmentId, colLecId, colCourseCode, colAdminId, colSemester, colStartTime, colEndTime, colAcademicYear;
    @FXML private TextField txtSearchAcademicYear;

    private final AdminRepository adminRepository = new AdminRepository();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupColumns();
        refreshFilters();

        // Auto-refresh table when filters change
        cmbFilterDepartment.valueProperty().addListener((obs, old, val) -> applyFilters());
        cmbFilterSemester.valueProperty().addListener((obs, old, val) -> applyFilters());
        txtSearchAcademicYear.textProperty().addListener((obs, old, val) -> applyFilters());
    }

    private void setupColumns() {
        colTimetableId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTimeTableId()));
        colDepartmentId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDepartment()));
        colLecId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getLecId()));
        colCourseCode.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCourseCode()));
        colAdminId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAdminId()));
        colSemester.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDay()));
        colStartTime.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getStartTime())));
        colEndTime.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getEndTime())));
        colAcademicYear.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getSessionType()));
    }

    @FXML
    private void btnOnActionAddNewSchedule(ActionEvent event) {
        Timetable result = openTimetableDialog(null);
        if (result != null) {
            try {
                if (adminRepository.saveTimetable(result)) {
                    refreshFilters();
                    showMsg("Success", "Timetable added.");
                }
            } catch (SQLException e) {
                showError("Add Failed", e);
            }
        }
    }

    @FXML
    private void btnOnActionUpdateSchedule(ActionEvent event) {
        Timetable selected = tblTimetable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showMsg("Selection Required", "Please select a timetable to edit.");
            return;
        }

        Timetable updated = openTimetableDialog(selected);
        if (updated != null) {
            try {
                if (adminRepository.updateTimetable(updated)) {
                    refreshFilters();
                    showMsg("Success", "Timetable updated.");
                }
            } catch (SQLException e) {
                showError("Update Failed", e);
            }
        }
    }

    @FXML
    private void btnOnActionDeleteSchedule(ActionEvent event) {
        Timetable selected = tblTimetable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + selected.getTimeTableId() + "?", ButtonType.YES, ButtonType.NO);
        if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            try {
                adminRepository.deleteTimetableById(selected.getTimeTableId());
                refreshFilters();
            } catch (SQLException e) {
                showError("Delete Failed", e);
            }
        }
    }

    private void applyFilters() {
        try {
            String dept = "All".equals(cmbFilterDepartment.getValue()) ? null : cmbFilterDepartment.getValue();
            String day = "All".equals(cmbFilterSemester.getValue()) ? null : cmbFilterSemester.getValue();
            String search = txtSearchAcademicYear.getText().trim();
            tblTimetable.getItems().setAll(adminRepository.findTimetablesByFilters(dept, day, search));
        } catch (SQLException e) {
            showError("Load Error", e);
        }
    }

    private void refreshFilters() {
        try {
            cmbFilterDepartment.getItems().setAll("All");
            cmbFilterDepartment.getItems().addAll(adminRepository.findAllTimetableDepartments());
            cmbFilterDepartment.setValue("All");

            cmbFilterSemester.getItems().setAll("All");
            cmbFilterSemester.getItems().addAll(adminRepository.findAllTimetableDays());
            cmbFilterSemester.setValue("All");
            applyFilters();
        } catch (SQLException e) {
            showError("Filter Error", e);
        }
    }

    private Timetable openTimetableDialog(Timetable existing) {
        Dialog<Timetable> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Timetable" : "Edit Timetable");
        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        // All Fields
        TextField id = new TextField(existing != null ? existing.getTimeTableId() : "");
        TextField dept = new TextField(existing != null ? existing.getDepartment() : "");
        TextField lec = new TextField(existing != null ? existing.getLecId() : "");
        TextField course = new TextField(existing != null ? existing.getCourseCode() : "");
        TextField admin = new TextField(existing != null ? existing.getAdminId() : LoggedInAdmin.getRegistrationNo());
        TextField day = new TextField(existing != null ? existing.getDay() : "");
        TextField start = new TextField(existing != null ? String.valueOf(existing.getStartTime()) : "08:00");
        TextField end = new TextField(existing != null ? String.valueOf(existing.getEndTime()) : "10:00");
        ComboBox<String> type = new ComboBox<>();
        type.getItems().addAll("theory", "practical","both");
        if (existing != null) {
            type.setValue(existing.getSessionType());
            id.setDisable(true);
        }

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.add(new Label("ID:"), 0, 0); grid.add(id, 1, 0);
        grid.add(new Label("Dept:"), 0, 1); grid.add(dept, 1, 1);
        grid.add(new Label("Lecturer ID:"), 0, 2); grid.add(lec, 1, 2);
        grid.add(new Label("Course:"), 0, 3); grid.add(course, 1, 3);
        grid.add(new Label("Admin:"), 0, 4); grid.add(admin, 1, 4);
        grid.add(new Label("Day:"), 0, 5); grid.add(day, 1, 5);
        grid.add(new Label("Start (HH:mm):"), 0, 6); grid.add(start, 1, 6);
        grid.add(new Label("End (HH:mm):"), 0, 7); grid.add(end, 1, 7);
        grid.add(new Label("Type:"), 0, 8); grid.add(type, 1, 8);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                try {
                    // Simple Validation
                    if (id.getText().isEmpty() || dept.getText().isEmpty() || day.getText().isEmpty()) {
                        throw new Exception("ID, Department, and Day are required.");
                    }
                    return new Timetable(id.getText(), dept.getText(), lec.getText(), course.getText(),
                            admin.getText(), day.getText(), LocalTime.parse(start.getText()),
                            LocalTime.parse(end.getText()), type.getValue());
                } catch (Exception e) {
                    showMsg("Input Error", e.getMessage());
                }
            }
            return null;
        });
        return dialog.showAndWait().orElse(null);
    }

    private void showMsg(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showError(String title, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(title);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
}
