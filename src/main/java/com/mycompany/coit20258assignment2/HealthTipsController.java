package com.mycompany.coit20258assignment2;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import java.util.List;

/** Controller that displays simple self-care / health tips and handles back navigation. */
public class HealthTipsController {

    @FXML private TextArea tipsArea; // multi-line text area to show the tips
    @FXML private Label message;     // small status/description label under/above the tips

    // Static list of tips shown to all users (can be extended later or made dynamic).
    private static final List<String> TIPS = List.of(
        "Drink water regularly to stay hydrated.",
        "Stand up and stretch every hour.",
        "Follow your medication schedule.",
        "Wash hands frequently to reduce infection risk.",
        "Aim for 7–8 hours of sleep."
    );

    /** Called automatically after FXML is loaded; populates the tips and info message. */
    @FXML
    public void initialize(){
        tipsArea.setText(String.join("\n\n", TIPS)); // put each tip on its own paragraph for readability
        message.setText("Self-care tips.");          // short descriptor for the section
    }

    /** Navigate back to the appropriate dashboard based on the current user's role. */
    @FXML
    public void onBack() {
        if (Session.isPatient()) {
            SceneNavigator.getInstance().goToPatientDashboard();
        } else if (Session.isDoctor()) {
            SceneNavigator.getInstance().goToDoctorDashboard();
        } else if (Session.isAdmin()) {
            SceneNavigator.getInstance().goToAdminDashboard();
        }
        // If no role is set, deliberately do nothing; caller may decide a fallback if needed.
    }
}
