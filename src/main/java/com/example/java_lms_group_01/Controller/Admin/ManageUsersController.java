package com.example.java_lms_group_01.Controller.Admin;

import com.example.java_lms_group_01.Repository.AdminRepository;
import com.example.java_lms_group_01.model.UserRecord;
import com.example.java_lms_group_01.model.users.UserRole;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class ManageUsersController implements Initializable {

    // Tabs for each role
    @FXML private TabPane tabUsers;
    @FXML private Tab tabAdmins;
    @FXML private Tab tabLecturers;
    @FXML private Tab tabStudents;
    @FXML private Tab tabTechnicalOfficers;

    // Tables for each role
    @FXML private TableView<UserRecord> tblAdmins;
    @FXML private TableView<UserRecord> tblLecturers;
    @FXML private TableView<UserRecord> tblStudents;
    @FXML private TableView<UserRecord> tblTechnicalOfficers;

    @FXML private TableColumn<UserRecord, String> adminId;
    @FXML private TableColumn<UserRecord, String> adminFirstName;
    @FXML private TableColumn<UserRecord, String> adminLastName;
    @FXML private TableColumn<UserRecord, String> adminEmail;
    @FXML private TableColumn<UserRecord, String> adminPhone;
    @FXML private TableColumn<UserRecord, String> adminGender;
    @FXML private TableColumn<UserRecord, String> adminDeptId;
    @FXML private TableColumn<UserRecord, String> adminAccessLevel;

    @FXML private TableColumn<UserRecord, String> lecId;
    @FXML private TableColumn<UserRecord, String> lecFirstName;
    @FXML private TableColumn<UserRecord, String> lecLastName;
    @FXML private TableColumn<UserRecord, String> lecEmail;
    @FXML private TableColumn<UserRecord, String> lecPhone;
    @FXML private TableColumn<UserRecord, String> lecGender;
    @FXML private TableColumn<UserRecord, String> lecRegNo;
    @FXML private TableColumn<UserRecord, String> lecDeptId;
    @FXML private TableColumn<UserRecord, String> lecPosition;

    @FXML private TableColumn<UserRecord, String> stuId;
    @FXML private TableColumn<UserRecord, String> stuFirstName;
    @FXML private TableColumn<UserRecord, String> stuLastName;
    @FXML private TableColumn<UserRecord, String> stuEmail;
    @FXML private TableColumn<UserRecord, String> stuPhone;
    @FXML private TableColumn<UserRecord, String> stuGender;
    @FXML private TableColumn<UserRecord, String> stuRegNo;
    @FXML private TableColumn<UserRecord, String> stuDeptId;
    @FXML private TableColumn<UserRecord, String> stuBatchId;
    @FXML private TableColumn<UserRecord, String> stuStatus;

    @FXML private TableColumn<UserRecord, String> toId;
    @FXML private TableColumn<UserRecord, String> toFirstName;
    @FXML private TableColumn<UserRecord, String> toLastName;
    @FXML private TableColumn<UserRecord, String> toEmail;
    @FXML private TableColumn<UserRecord, String> toPhone;
    @FXML private TableColumn<UserRecord, String> toGender;
    @FXML private TableColumn<UserRecord, String> toDeptId;
    @FXML private TableColumn<UserRecord, String> toPosition;
    @FXML private TableColumn<UserRecord, String> toLab;
    @FXML private TableColumn<UserRecord, String> toShift;

    private final AdminRepository adminRepository = new AdminRepository();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Bind columns first, then load data from the database.
        setupTableColumns();
        refreshAllTables();
    }

    @FXML
    private void btnOnActionAdd(ActionEvent event) {
        // Open the form in add mode.
        saveUser(false);
    }

    @FXML
    private void btnOnActionEdit(ActionEvent event) {
        // Open the form in edit mode.
        saveUser(true);
    }

    @FXML
    private void btnOnActionDelete(ActionEvent event) {
        // Delete the selected row from the active tab.
        UserRole role = getActiveRole();
        if (role == null) {
            showInfo("Select a tab first.");
            return;
        }

        UserRecord selected = getSelectedRow(role);
        if (selected == null) {
            showInfo("Select a row first.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("Delete " + role.getValue());
        confirm.setContentText("Delete " + selected.getRegistrationNo() + "?");

        Optional<ButtonType> answer = confirm.showAndWait();
        if (answer.isEmpty() || answer.get() != ButtonType.OK) {
            return;
        }

        try {
            if (deleteUser(role, selected.getUserId())) {
                refreshAllTables();
                showInfo(role.getValue() + " deleted successfully.");
            }
        } catch (SQLException e) {
            showError("Delete failed", e);
        }
    }

    @FXML
    private void btnOnActionRefresh(ActionEvent event) {
        // Reload all tables.
        refreshAllTables();
    }

    private void saveUser(boolean edit) {
        // This method is used for both add and edit.
        UserRole role = getActiveRole();
        if (role == null) {
            showInfo("Select a tab first.");
            return;
        }

        UserRecord current = null;
        if (edit) {
            current = getSelectedRow(role);
            if (current == null) {
                showInfo("Select a row first.");
                return;
            }
        }

        try {
            UserRecord row = openDialog(role, current);
            if (row == null) {
                return;
            }

            boolean ok = edit ? updateUser(role, row) : addUser(role, row);
            if (ok) {
                refreshAllTables();
                showInfo(role.getValue() + (edit ? " updated successfully." : " added successfully."));
            }
        } catch (IllegalArgumentException e) {
            showInfo(e.getMessage());
        } catch (SQLException e) {
            showError("Save failed", e);
        }
    }

    private void setupTableColumns() {
        // Admin table
        adminId.setCellValueFactory(c -> new SimpleStringProperty(text(c.getValue().getUserId())));
        adminFirstName.setCellValueFactory(c -> new SimpleStringProperty(text(c.getValue().getFirstName())));
        adminLastName.setCellValueFactory(c -> new SimpleStringProperty(text(c.getValue().getLastName())));
        adminEmail.setCellValueFactory(c -> new SimpleStringProperty(text(c.getValue().getEmail())));
        adminPhone.setCellValueFactory(c -> new SimpleStringProperty(text(c.getValue().getPhoneNumber())));
        adminGender.setCellValueFactory(c -> new SimpleStringProperty(text(c.getValue().getGender())));
        adminDeptId.setCellValueFactory(c -> new SimpleStringProperty(text(c.getValue().getAddress())));
        adminAccessLevel.setCellValueFactory(c -> new SimpleStringProperty(text(c.getValue().getRegistrationNo())));

        // Lecturer table
        lecId.setCellValueFactory(c -> new SimpleStringProperty(text(c.getValue().getUserId())));
        lecFirstName.setCellValueFactory(c -> new SimpleStringProperty(text(c.getValue().getFirstName())));
        lecLastName.setCellValueFactory(c -> new SimpleStringProperty(text(c.getValue().getLastName())));
        lecEmail.setCellValueFactory(c -> new SimpleStringProperty(text(c.getValue().getEmail())));
        lecPhone.setCellValueFactory(c -> new SimpleStringProperty(text(c.getValue().getPhoneNumber())));
        lecGender.setCellValueFactory(c -> new SimpleStringProperty(text(c.getValue().getGender())));
        lecRegNo.setCellValueFactory(c -> new SimpleStringProperty(text(c.getValue().getRegistrationNo())));
        lecDeptId.setCellValueFactory(c -> new SimpleStringProperty(text(c.getValue().getDepartment())));
        lecPosition.setCellValueFactory(c -> new SimpleStringProperty(text(c.getValue().getPosition())));

        // Student table
        stuId.setCellValueFactory(c -> new SimpleStringProperty(text(c.getValue().getUserId())));
        stuFirstName.setCellValueFactory(c -> new SimpleStringProperty(text(c.getValue().getFirstName())));
        stuLastName.setCellValueFactory(c -> new SimpleStringProperty(text(c.getValue().getLastName())));
        stuEmail.setCellValueFactory(c -> new SimpleStringProperty(text(c.getValue().getEmail())));
        stuPhone.setCellValueFactory(c -> new SimpleStringProperty(text(c.getValue().getPhoneNumber())));
        stuGender.setCellValueFactory(c -> new SimpleStringProperty(text(c.getValue().getGender())));
        stuRegNo.setCellValueFactory(c -> new SimpleStringProperty(text(c.getValue().getRegistrationNo())));
        stuDeptId.setCellValueFactory(c -> new SimpleStringProperty(text(c.getValue().getDepartment())));
        stuBatchId.setCellValueFactory(c -> new SimpleStringProperty(text(c.getValue().getBatch())));
        stuStatus.setCellValueFactory(c -> new SimpleStringProperty(text(c.getValue().getStatus())));

        // Technical officer table
        toId.setCellValueFactory(c -> new SimpleStringProperty(text(c.getValue().getUserId())));
        toFirstName.setCellValueFactory(c -> new SimpleStringProperty(text(c.getValue().getFirstName())));
        toLastName.setCellValueFactory(c -> new SimpleStringProperty(text(c.getValue().getLastName())));
        toEmail.setCellValueFactory(c -> new SimpleStringProperty(text(c.getValue().getEmail())));
        toPhone.setCellValueFactory(c -> new SimpleStringProperty(text(c.getValue().getPhoneNumber())));
        toGender.setCellValueFactory(c -> new SimpleStringProperty(text(c.getValue().getGender())));
        toDeptId.setCellValueFactory(c -> new SimpleStringProperty(text(c.getValue().getAddress())));
        toPosition.setCellValueFactory(c -> new SimpleStringProperty(text(c.getValue().getRegistrationNo())));
        toLab.setCellValueFactory(c -> new SimpleStringProperty(text(c.getValue().getDateOfBirth())));
        toShift.setCellValueFactory(c -> new SimpleStringProperty(text(c.getValue().getRole())));
    }

    private UserRecord openDialog(UserRole role, UserRecord existing) {
        // Create a simple dialog for adding or editing one user.
        boolean edit = existing != null;
        Dialog<UserRecord> dialog = new Dialog<>();
        dialog.setTitle((edit ? "Edit " : "Add ") + role.getValue());
        dialog.setHeaderText(edit ? "Update selected record." : "Enter new record details.");

        ButtonType saveButton = new ButtonType(edit ? "Update" : "Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        // Common fields
        TextField firstName = new TextField();
        TextField lastName = new TextField();
        TextField email = new TextField();
        TextField registrationNo = new TextField();
        PasswordField password = new PasswordField();
        TextField address = new TextField();
        TextField phone = new TextField();
        DatePicker dob = new DatePicker();
        ComboBox<String> gender = new ComboBox<>();
        TextField imagePath = new TextField();

        // Extra fields
        TextField department = new TextField();
        TextField batch = new TextField();
        TextField gpa = new TextField();
        ComboBox<String> status = new ComboBox<>();
        TextField position = new TextField();

        // Small fixed choices for dropdowns.
        gender.getItems().addAll("Male", "Female", "Other");
        status.getItems().addAll("proper", "repeat");

        if (existing != null) {
            // Fill fields when editing.
            firstName.setText(text(existing.getFirstName()));
            lastName.setText(text(existing.getLastName()));
            email.setText(text(existing.getEmail()));
            registrationNo.setText(text(existing.getRegistrationNo()));
            address.setText(text(existing.getAddress()));
            phone.setText(text(existing.getPhoneNumber()));
            dob.setValue(existing.getDateOfBirth());
            gender.setValue(text(existing.getGender()));
            imagePath.setText(text(existing.getProfileImagePath()));
            department.setText(text(existing.getDepartment()));
            batch.setText(text(existing.getBatch()));
            gpa.setText(existing.getGpa() == null ? "" : existing.getGpa().toString());
            status.setValue(text(existing.getStatus()).isBlank() ? "proper" : existing.getStatus());
            position.setText(text(existing.getPosition()));
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        int row = 0;
        // Add form controls one line at a time.
        row = addRow(grid, "First Name:", firstName, row);
        row = addRow(grid, "Last Name:", lastName, row);
        row = addRow(grid, "Email:", email, row);
        row = addRow(grid, "Registration No:", registrationNo, row);
        row = addRow(grid, edit ? "New Password (optional):" : "Password:", password, row);
        row = addRow(grid, "Address:", address, row);
        row = addRow(grid, "Phone:", phone, row);
        row = addRow(grid, "Date of Birth:", dob, row);
        row = addRow(grid, "Gender:", gender, row);
        row = addRow(grid, "Profile Image Path:", imagePath, row);

        if (role == UserRole.LECTURER || role == UserRole.STUDENT || role == UserRole.TECHNICAL_OFFICER) {
            row = addRow(grid, "Department:", department, row);
        }
        if (role == UserRole.LECTURER) {
            row = addRow(grid, "Position:", position, row);
        }
        if (role == UserRole.STUDENT) {
            row = addRow(grid, "Batch:", batch, row);
            row = addRow(grid, "GPA:", gpa, row);
            row = addRow(grid, "Status:", status, row);
        }

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(button -> button == saveButton
                ? createUser(role, existing, edit, firstName, lastName, email, registrationNo, password, address, phone, dob, gender, imagePath, department, batch, gpa, status, position)
                : null);

        return dialog.showAndWait().orElse(null);
    }

    private UserRecord createUser(
            UserRole role,
            UserRecord existing,
            boolean edit,
            TextField firstName,
            TextField lastName,
            TextField email,
            TextField registrationNo,
            PasswordField password,
            TextField address,
            TextField phone,
            DatePicker dob,
            ComboBox<String> gender,
            TextField imagePath,
            TextField department,
            TextField batch,
            TextField gpa,
            ComboBox<String> status,
            TextField position
    ) {
        // Read the form values and create the object to save.
        String regNo = required(registrationNo, "Registration No");
        String pwd = edit ? password.getText() : required(password, "Password");
        String userId = edit ? existing.getUserId() : regNo;

        if (role == UserRole.ADMIN) {
            return new UserRecord(
                    userId, required(firstName, "First Name"), required(lastName, "Last Name"),
                    required(email, "Email"), text(address), text(phone), dob.getValue(), gender.getValue(),
                    UserRole.ADMIN.getValue(), regNo, pwd, null, null, null, null, null, text(imagePath)
            );
        }

        if (role == UserRole.LECTURER) {
            return new UserRecord(
                    userId, required(firstName, "First Name"), required(lastName, "Last Name"),
                    required(email, "Email"), text(address), text(phone), dob.getValue(), gender.getValue(),
                    UserRole.LECTURER.getValue(), regNo, pwd, required(department, "Department"),
                    null, null, null, required(position, "Position"), text(imagePath)
            );
        }

        if (role == UserRole.STUDENT) {
            return new UserRecord(
                    userId, required(firstName, "First Name"), required(lastName, "Last Name"),
                    required(email, "Email"), text(address), text(phone), dob.getValue(), gender.getValue(),
                    UserRole.STUDENT.getValue(), regNo, pwd, required(department, "Department"),
                    required(batch, "Batch"), parseGpa(gpa), requiredStatus(status), null, text(imagePath)
            );
        }

        return new UserRecord(
                userId, required(firstName, "First Name"), required(lastName, "Last Name"),
                required(email, "Email"), text(address), text(phone), dob.getValue(), gender.getValue(),
                UserRole.TECHNICAL_OFFICER.getValue(), regNo, pwd, required(department, "Department"),
                null, null, null, null, text(imagePath)
        );
    }

    private int addRow(GridPane grid, String label, Node field, int row) {
        // Helper for one label + one control.
        grid.add(new Label(label), 0, row);
        grid.add(field, 1, row);
        return row + 1;
    }

    private void refreshAllTables() {
        // Load the latest records into each table.
        try {
            tblAdmins.getItems().setAll(adminRepository.findAdmins());
            tblLecturers.getItems().setAll(adminRepository.findLecturers());
            tblStudents.getItems().setAll(adminRepository.findStudents());
            tblTechnicalOfficers.getItems().setAll(adminRepository.findTechnicalOfficers());
        } catch (SQLException e) {
            showError("Load failed", e);
        }
    }

    private UserRole getActiveRole() {
        // Figure out which tab is selected now.
        Tab selectedTab = tabUsers.getSelectionModel().getSelectedItem();
        if (selectedTab == tabAdmins) return UserRole.ADMIN;
        if (selectedTab == tabLecturers) return UserRole.LECTURER;
        if (selectedTab == tabStudents) return UserRole.STUDENT;
        if (selectedTab == tabTechnicalOfficers) return UserRole.TECHNICAL_OFFICER;
        return null;
    }

    private UserRecord getSelectedRow(UserRole role) {
        // Get the selected row from the correct table.
        if (role == UserRole.ADMIN) return tblAdmins.getSelectionModel().getSelectedItem();
        if (role == UserRole.LECTURER) return tblLecturers.getSelectionModel().getSelectedItem();
        if (role == UserRole.STUDENT) return tblStudents.getSelectionModel().getSelectedItem();
        return tblTechnicalOfficers.getSelectionModel().getSelectedItem();
    }

    private boolean addUser(UserRole role, UserRecord row) throws SQLException {
        // Save a new user based on the role.
        if (role == UserRole.ADMIN) return adminRepository.createAdmin(row);
        if (role == UserRole.LECTURER) return adminRepository.createLecturer(row);
        if (role == UserRole.STUDENT) return adminRepository.createStudent(row);
        return adminRepository.createTechnicalOfficer(row);
    }

    private boolean updateUser(UserRole role, UserRecord row) throws SQLException {
        // Update an existing user based on the role.
        if (role == UserRole.ADMIN) return adminRepository.updateAdmin(row);
        if (role == UserRole.LECTURER) return adminRepository.updateLecturer(row);
        if (role == UserRole.STUDENT) return adminRepository.updateStudent(row);
        return adminRepository.updateTechnicalOfficer(row);
    }

    private boolean deleteUser(UserRole role, String userId) throws SQLException {
        // Delete an existing user based on the role.
        if (role == UserRole.ADMIN) return adminRepository.deleteAdmin(userId);
        if (role == UserRole.LECTURER) return adminRepository.deleteLecturer(userId);
        if (role == UserRole.STUDENT) return adminRepository.deleteStudent(userId);
        return adminRepository.deleteTechnicalOfficer(userId);
    }

    private String required(TextField field, String name) {
        // Basic required-field check.
        String value = text(field);
        if (value.isBlank()) {
            throw new IllegalArgumentException(name + " is required.");
        }
        return value;
    }

    private String requiredStatus(ComboBox<String> comboBox) {
        // Student status must be selected.
        String value = comboBox.getValue();
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Status is required.");
        }
        return value;
    }

    private Double parseGpa(TextField field) {
        // GPA is optional, but must be numeric if entered.
        String value = text(field);
        if (value.isBlank()) {
            return null;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("GPA must be a number.");
        }
    }

    private String text(Object value) {
        // Convert null to empty text.
        return value == null ? "" : value.toString().trim();
    }

    private void showInfo(String message) {
        // Simple info popup.
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message, Exception e) {
        // Simple error popup.
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Error");
        alert.setContentText(message + "\n" + e.getMessage());
        alert.showAndWait();
    }
}
