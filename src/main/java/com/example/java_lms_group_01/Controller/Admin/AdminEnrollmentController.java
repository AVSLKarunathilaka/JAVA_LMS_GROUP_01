package com.example.java_lms_group_01.Controller.Admin;

import com.example.java_lms_group_01.Repository.AdminRepository;
import com.example.java_lms_group_01.model.Course;
import com.example.java_lms_group_01.model.EnrollmentRecord;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AdminEnrollmentController {

    @FXML private TextField txtSearchEnrollment;
    @FXML private ComboBox<String> cmbBatchFilter;
    @FXML private TableView<EnrollmentRecord> tblEnrollments;
    @FXML private TableColumn<EnrollmentRecord, String> colStudentReg;
    @FXML private TableColumn<EnrollmentRecord, String> colStudentName;
    @FXML private TableColumn<EnrollmentRecord, String> colBatch;
    @FXML private TableColumn<EnrollmentRecord, String> colCourseCode;
    @FXML private TableColumn<EnrollmentRecord, String> colCourseName;
    @FXML private TableColumn<EnrollmentRecord, String> colEnrollmentDate;
    @FXML private TableColumn<EnrollmentRecord, String> colStatus;


    private final AdminRepository adminRepository = new AdminRepository();

    @FXML
    public void initialize() {
        // Tell the table columns which data to show
        setupColumns();

        // Load data into the Batch dropdown
        loadBatchFilter();

        // Load all enrollments into the table
        loadEnrollments();

        // Add listeners: When typing or picking a batch, the table updates automatically
        txtSearchEnrollment.textProperty().addListener((obs, oldVal, newVal) -> loadEnrollments());
        cmbBatchFilter.valueProperty().addListener((obs, oldVal, newVal) -> loadEnrollments());
    }

    private void setupColumns() {
        colStudentReg.setCellValueFactory(data -> data.getValue().studentRegProperty());
        colStudentName.setCellValueFactory(data -> data.getValue().studentNameProperty());
        colBatch.setCellValueFactory(data -> data.getValue().batchProperty());
        colCourseCode.setCellValueFactory(data -> data.getValue().courseCodeProperty());
        colCourseName.setCellValueFactory(data -> data.getValue().courseNameProperty());
        colEnrollmentDate.setCellValueFactory(data -> data.getValue().enrollmentDateProperty());
        colStatus.setCellValueFactory(data -> data.getValue().statusProperty());
    }

    // Add a new course to a student.
    @FXML
    private void btnOnActionAddEnrollment() {
        // Get the student selected in the table
        EnrollmentRecord selected = tblEnrollments.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showInfo("Please select a student from the table first.");
            return;
        }

        try {
            // Get courses the student hasn't taken yet
            List<Course> courses = adminRepository.findAvailableCoursesForStudent(selected.getStudentReg());

            if (courses.isEmpty()) {
                showInfo("No available courses for this student.");
                return;
            }

            // Open a pop-up window to pick a course
            Optional<Course> result = openEnrollmentDialog(selected, courses);

            // If the user picked a course and clicked "Add"
            if (result.isPresent()) {
                boolean success = adminRepository.createEnrollment(selected.getStudentReg(), result.get().getCourseCode());
                if (success) {
                    loadEnrollments();
                    showInfo("Enrollment added successfully!");
                }
            }
        } catch (SQLException e) {
            showError("Database Error", e);
        }
    }

    @FXML private void btnOnActionMakeCompleted() { updateStatus("completed"); }
    @FXML private void btnOnActionMakeDropped() { updateStatus("dropped"); }
    @FXML private void btnOnActionRefresh() { loadEnrollments(); }


    // Updates the status (Completed/Dropped) of the selected record.
    private void updateStatus(String newStatus) {
        EnrollmentRecord selected = tblEnrollments.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showInfo("Please select an enrollment record.");
            return;
        }

        try {
            boolean success = adminRepository.updateEnrollmentStatus(selected.getEnrollmentId(), newStatus);
            if (success) {
                loadEnrollments();
                showInfo("Status updated to: " + newStatus);
            }
        } catch (SQLException e) {
            showError("Update Failed", e);
        }
    }


    // Fills the ComboBox with Batch numbers from the DB.
    private void loadBatchFilter() {
        try {
            cmbBatchFilter.getItems().clear();
            cmbBatchFilter.getItems().add("All");
            cmbBatchFilter.getItems().addAll(adminRepository.findStudentBatches());
            cmbBatchFilter.setValue("All");
        } catch (SQLException e) {
            showError("Could not load batches", e);
        }
    }


    // Refreshes the table data based on Search box and Batch filter.
    private void loadEnrollments() {
        try {
            String searchText = txtSearchEnrollment.getText().trim();
            String batchFilter = cmbBatchFilter.getValue();

            // Get data from Database and put into Table
            List<EnrollmentRecord> list = adminRepository.findEnrollments(searchText, batchFilter);
            tblEnrollments.getItems().setAll(list);
        } catch (SQLException e) {
            showError("Could not load enrollment data", e);
        }
    }


    private Optional<Course> openEnrollmentDialog(EnrollmentRecord student, List<Course> courses) {
        Dialog<Course> dialog = new Dialog<>();
        dialog.setTitle("Enroll Student");

        ButtonType addBtnType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addBtnType, ButtonType.CANCEL);

        // Simple ComboBox for courses
        ComboBox<Course> courseBox = new ComboBox<>();
        courseBox.getItems().setAll(courses);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.add(new Label("Student: " + student.getStudentName()), 0, 0);
        grid.add(new Label("Select Course:"), 0, 1);
        grid.add(courseBox, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Convert the "Add" button click into a Course object
        dialog.setResultConverter(btn -> {
            if (btn == addBtnType) return courseBox.getValue();
            return null;
        });

        return dialog.showAndWait();
    }


    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showError(String msg, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(msg);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
}