package com.mycompany.coit20258assignment2;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SceneNavigator {

    private static SceneNavigator instance;
    private Stage stage;
    private double width;
    private double height;

    private static final String VIEW_ROOT = "/com/mycompany/coit20258assignment2/view/";
    private static final String CSS_IN_VIEW = VIEW_ROOT + "styles.css";

    private SceneNavigator() {}

    public static SceneNavigator getInstance() {
        if (instance == null) instance = new SceneNavigator();
        return instance;
    }

    public void init(Stage stage, double width, double height) {
        this.stage = stage;
        this.width = width;
        this.height = height;
        this.stage.centerOnScreen();
    }

    private void attachStyles(Scene scene) {
        var url = getClass().getResource(CSS_IN_VIEW);
        if (url != null) scene.getStylesheets().add(url.toExternalForm());
    }

    private void showError(String fxml, Exception e) {
        e.printStackTrace();
        var a = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        a.setHeaderText("Failed to load: " + fxml);
        a.setContentText(String.valueOf(e));
        if (stage != null) a.initOwner(stage);
        a.initModality(Modality.WINDOW_MODAL);
        a.showAndWait();
    }

    private void go(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(VIEW_ROOT + fxmlFile));
            if (loader.getLocation() == null) throw new IllegalStateException("Missing FXML: " + VIEW_ROOT + fxmlFile);

            Parent root = loader.load();
            Scene scene = new Scene(root, width, height);
            attachStyles(scene);

            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            showError(fxmlFile, e);
        }
    }

    // ===== Routes =====
    public void goToLogin()                { go("Login.fxml"); }
    public void goToPatientDashboard()     { go("patient_dashboard.fxml"); }
    public void goToDoctorDashboard()      { go("doctor_dashboard.fxml"); }
    public void goToAdminDashboard()       { go("admin_dashboard.fxml"); }

    public void goToAppointmentForm()      { go("appointment_form.fxml"); }
    public void goToVitalsForm()           { go("vitals_form.fxml"); }
    public void goToPrescriptionForm()     { go("prescription_form.fxml"); }
    public void goToPrescriptionEditor()   { go("prescription_editor.fxml"); }
    public void goToPrescriptionReview()   { go("prescription_review.fxml"); }

    public void goToStaffBookingEditor()   { go("staff_booking_editor.fxml"); }
    public void goToReferralForm()         { go("referral_form.fxml"); }
    public void goToDiagnosisForm()        { go("diagnosis_form.fxml"); }
    public void goToMyDataView()           { go("my_data_view.fxml"); }
    public void goToDoctorUnavailability() { go("doctor_unavailability.fxml"); }


    public void goToDoctorDataView()       { go("doctor_data_view.fxml"); }


    public void goToAdminAppointments()    { go("admin_appointments.fxml"); }

    public void goToHealthTips()           { go("health_tips.fxml"); }
    public void goToSignup()               { go("signup.fxml"); }
    public void goToForgotPassword()       { go("forgot_password.fxml"); }
}
