package com.example.java_lms_group_01.Controller.Admin;

import com.example.java_lms_group_01.Repository.AdminRepository;
import com.example.java_lms_group_01.model.Notice;
import com.example.java_lms_group_01.session.LoggedInAdmin;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ManageNoticesController implements Initializable {

    @FXML private TableView<Notice> tblNotices;
    @FXML private TableColumn<Notice, Number> colNoticeId;
    @FXML private TableColumn<Notice, String> colTitle;
    @FXML private TableColumn<Notice, String> colDate;
    @FXML private TableColumn<Notice, String> colAuthor;
    @FXML private TextField txtSearchNotice;

    private final AdminRepository adminRepository = new AdminRepository();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 1. Setup Table Columns
        setupColumns();

        // 2. Load all notices initially
        refreshTable("");

        // 3. Search Listener: Update table as user types
        txtSearchNotice.textProperty().addListener((obs, oldVal, newVal) -> refreshTable(newVal));
    }

    private void setupColumns() {
        colNoticeId.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getNoticeId()));
        colTitle.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTitle()));
        colDate.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getPublishDate())));
        colAuthor.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCreatedBy()));
    }

    // Add New Notice
    @FXML
    void btnOnActionAddNewNotice(ActionEvent event) {
        Notice newNotice = showNoticeDialog(null); // Passing null means "New"

        if (newNotice != null) {
            try {
                if (adminRepository.saveNotice(newNotice)) {
                    refreshTable("");
                    showInfo("Notice saved successfully!");
                }
            } catch (SQLException e) {
                showError("Could not save notice", e);
            }
        }
    }

    // Update / View Selected Notice
    @FXML
    void btnOnActionViewNotice(ActionEvent event) {
        Notice selected = tblNotices.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showInfo("Please select a notice from the table.");
            return;
        }

        Notice updatedNotice = showNoticeDialog(selected);

        if (updatedNotice != null) {
            try {
                if (adminRepository.updateNotice(updatedNotice)) {
                    refreshTable("");
                    showInfo("Notice updated successfully!");
                }
            } catch (SQLException e) {
                showError("Update failed", e);
            }
        }
    }

    // Delete Selected Notice
    @FXML
    void btnOnActionDeleteNotice(ActionEvent event) {
        Notice selected = tblNotices.getSelectionModel().getSelectedItem();

        if (selected == null) return;

        // Confirmation Pop-up
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete this notice?", ButtonType.YES, ButtonType.NO);
        if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            try {
                adminRepository.deleteNoticeById(selected.getNoticeId());
                refreshTable("");
            } catch (SQLException e) {
                showError("Delete failed", e);
            }
        }
    }

    // Loads notices into the table from the Database
    private void refreshTable(String search) {
        try {
            List<Notice> notices;
            if (search == null || search.isEmpty()) {
                notices = adminRepository.findAllNotices();
            } else {
                notices = adminRepository.findNoticesByKeyword(search.trim());
            }
            tblNotices.getItems().setAll(notices);
        } catch (SQLException e) {
            showError("Database Error", e);
        }
    }

    // Opens a pop-up window to enter/edit notice data
    private Notice showNoticeDialog(Notice existing) {
        Dialog<Notice> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Create Notice" : "Edit Notice");

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        // UI components for the dialog
        TextField titleField = new TextField();
        TextArea contentArea = new TextArea();
        DatePicker datePicker = new DatePicker(LocalDate.now());

        // Fill data if we are editing
        if (existing != null) {
            titleField.setText(existing.getTitle());
            contentArea.setText(existing.getContent());
            datePicker.setValue(existing.getPublishDate());
        }

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Content:"), 0, 1);
        grid.add(contentArea, 1, 1);
        grid.add(new Label("Date:"), 0, 2);
        grid.add(datePicker, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Convert button click into a Notice object
        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                if (titleField.getText().isEmpty()) return null;

                int id = (existing == null) ? 0 : existing.getNoticeId();
                return new Notice(
                        id,
                        titleField.getText(),
                        contentArea.getText(),
                        datePicker.getValue(),
                        LoggedInAdmin.getRegistrationNo() // Auto-fill author
                );
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }


    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showError(String title, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(title);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
}
