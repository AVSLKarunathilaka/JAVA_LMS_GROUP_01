package com.example.java_lms_group_01.Controller.Admin;

import com.example.java_lms_group_01.model.Course;
import com.example.java_lms_group_01.model.CourseType;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class CourseFormController {

    @FXML private TextField txtCourseCode;
    @FXML private TextField txtCredit;
    @FXML private TextField txtDepartment;
    @FXML private TextField txtLecturerRegNo;
    @FXML private TextField txtName;
    @FXML private TextField txtSemester;
    @FXML private ComboBox<CourseType> cmbCourseType;

    // Prepares the form for adding a NEW course.
    public void setupForCreate() {
        // Clear all fields just in case
        clearFields();

        // Ensure ID field is enabled and fill the dropdown
        txtCourseCode.setDisable(false);
        cmbCourseType.getItems().setAll(CourseType.values());
        cmbCourseType.setValue(CourseType.THEORY); // Default value
    }

    // Prepares the form for EDITING an existing course.
    public void setupForEdit(Course course) {
        // First, do the basic setup
        setupForCreate();

        // Fill the text fields with the existing course data
        txtCourseCode.setText(course.getCourseCode());
        txtCourseCode.setDisable(true); // Don't allow changing the ID during edit

        txtName.setText(course.getName());
        txtCredit.setText(String.valueOf(course.getCredit()));
        txtLecturerRegNo.setText(course.getLecturerRegistrationNo());
        txtDepartment.setText(course.getDepartment());
        txtSemester.setText(course.getSemester());
        cmbCourseType.setValue(course.getCourseTypeEnum());
    }


    // create a Course object.
    public Course buildCourse() {
        // Get values from text fields (and trim extra spaces)
        String code = txtCourseCode.getText().trim();
        String name = txtName.getText().trim();
        String dept = txtDepartment.getText().trim();
        String sem = txtSemester.getText().trim();
        String lecturer = txtLecturerRegNo.getText().trim();

        // Simple Validation: Check if fields are empty
        if (code.isEmpty() || name.isEmpty() || dept.isEmpty() || sem.isEmpty()) {
            throw new IllegalArgumentException("Please fill in all required fields.");
        }

        if (cmbCourseType.getValue() == null) {
            throw new IllegalArgumentException("Please select a course type.");
        }

        // Number Validation: Convert credit text to a number
        int creditValue;
        try {
            creditValue = Integer.parseInt(txtCredit.getText().trim());
            if (creditValue <= 0) {
                throw new Exception(); // Go to catch block
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Credit must be a positive number.");
        }

        // Return the new Course object
        return new Course(
                code,
                name,
                lecturer.isEmpty() ? null : lecturer,
                dept,
                sem,
                creditValue,
                cmbCourseType.getValue()
        );
    }


    private void clearFields() {
        txtCourseCode.clear();
        txtName.clear();
        txtCredit.clear();
        txtLecturerRegNo.clear();
        txtDepartment.clear();
        txtSemester.clear();
    }
}