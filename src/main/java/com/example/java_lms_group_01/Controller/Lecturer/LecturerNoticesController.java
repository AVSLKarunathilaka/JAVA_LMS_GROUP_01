package com.example.java_lms_group_01.Controller.Lecturer;

import com.example.java_lms_group_01.Repository.NoticeRepository;
import com.example.java_lms_group_01.model.Notice;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.util.List;

/**
 * Shows all notices to the lecturer and supports a simple title/content search.
 */
public class LecturerNoticesController {

    @FXML
    private TextField txtSearch;
    @FXML
    private TableView<Notice> tblNotices;
    @FXML
    private TableColumn<Notice, String> colNoticeId;
    @FXML
    private TableColumn<Notice, String> colTitle;
    @FXML
    private TableColumn<Notice, String> colContent;
    @FXML
    private TableColumn<Notice, String> colDate;
    @FXML
    private TableColumn<Notice, String> colCreatedBy;

    private final NoticeRepository noticeRepository = new NoticeRepository();

    @FXML
    public void initialize() {
        colNoticeId.setCellValueFactory(d -> d.getValue().noticeIdProperty());
        colTitle.setCellValueFactory(d -> d.getValue().titleProperty());
        colContent.setCellValueFactory(d -> d.getValue().contentProperty());
        colDate.setCellValueFactory(d -> d.getValue().publishDateProperty());
        colCreatedBy.setCellValueFactory(d -> d.getValue().createdByProperty());
        loadNotices(null);
    }

    @FXML
    private void searchNotices() {
        loadNotices(txtSearch.getText());
    }

    @FXML
    private void refreshNotices() {
        txtSearch.clear();
        loadNotices(null);
    }

    private void loadNotices(String keyword) {
        try {
            List<Notice> notices;
            if (keyword == null || keyword.isBlank()) {
                notices = noticeRepository.findAll();
            } else {
                notices = noticeRepository.findByKeyword(keyword);
            }
            tblNotices.getItems().setAll(notices);
        } catch (SQLException e) {
            showError("Failed to load notices.", e);
        }
    }

    private void showError(String message, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Database Error");
        alert.setHeaderText(null);
        alert.setContentText(message + "\n" + e.getMessage());
        alert.showAndWait();
    }
}
