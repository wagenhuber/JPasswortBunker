package jpasswortbunker.mgm.view;

import com.jfoenix.controls.*;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import jpasswortbunker.mgm.model.Entry;
import jpasswortbunker.mgm.presenter.EntryProperty;
import jpasswortbunker.mgm.presenter.PresenterMain;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.sql.Array;
import java.sql.SQLException;
import java.util.*;

public class EditEntryController {

    @FXML
    private JFXTextField textFieldTitle, textFieldUsername, textFieldURL, textFieldPassword1, textFieldPassword2;

    @FXML
    private JFXPasswordField passwordField1, passwordField2;

    @FXML
    private JFXTextArea textAreaDescription;

    @FXML
    private Label labelErrorMessage;

    @FXML
    private JFXButton btn_save, btn_eye, btn_copyPassword, btn_restore;

    @FXML
    public JFXComboBox<Label> comboBox = new JFXComboBox<Label>();

    @FXML
    public JFXComboBox<Label> comboBoxHistorie = new JFXComboBox<Label>();



    public EntryProperty entryProperty;
    private PresenterMain presenter;

    @FXML
    public void initialize(){
        btn_eye.setTooltip(new Tooltip("Show Password"));
        btn_copyPassword.setTooltip(new Tooltip("Copy Password to Clipboard"));
        textFieldPassword1.setManaged(false);
        textFieldPassword1.setVisible(false);
        textFieldPassword2.setManaged(false);
        textFieldPassword2.setVisible(false);
    }

    /**
     * public void btn_save(ActionEvent actionEvent)
     * Ruft changeEntry() auf und schließt bei erfolgreicher Eingabe das Fenster
     */
    public void btn_save(ActionEvent actionEvent) throws IllegalBlockSizeException, SQLException, InvalidKeyException, BadPaddingException, UnsupportedEncodingException {

        if (changeEntry()) {
            Stage stage = (Stage) btn_save.getScene().getWindow();
            stage.close();
        }
    }

    public void btn_restore(ActionEvent actionEvent) throws  IllegalBlockSizeException, SQLException,InvalidKeyException,BadPaddingException,UnsupportedEncodingException {

        if (changeEntry()) {
            Stage stage = (Stage) btn_restore.getScene().getWindow();
            stage.close();
        }
    }
    /**
     * public void btn_eyeIcon(ActionEvent actionEvent)
     * Zeit das Passwort im Klartext an, indem das PasswortFeld ausgeblendet wird und Textfeld eingeblendet
     * Felder sind Bidirektional miteinander verbunden
     */
    public void btn_eyeIcon(ActionEvent actionEvent){
        textFieldPassword1.textProperty().bindBidirectional(passwordField1.textProperty());
        textFieldPassword2.textProperty().bindBidirectional(passwordField2.textProperty());
        if (passwordField1.isVisible()) {
            textFieldPassword1.setManaged(true);
            textFieldPassword1.setVisible(true);
            passwordField1.setManaged(false);
            passwordField1.setVisible(false);
            textFieldPassword2.setManaged(true);
            textFieldPassword2.setVisible(true);
            passwordField2.setManaged(false);
            passwordField2.setVisible(false);
        } else {
            textFieldPassword1.setManaged(false);
            textFieldPassword1.setVisible(false);
            passwordField1.setManaged(true);
            passwordField1.setVisible(true);
            textFieldPassword2.setManaged(false);
            textFieldPassword2.setVisible(false);
            passwordField2.setManaged(true);
            passwordField2.setVisible(true);
        }
    }

    /**
     * public void btn_copyPasswordToClipboard(ActionEvent actionEvent)
     * kopiert das Passwort in die System Zwischenablage und löscht dieses nach x Sekunden
     * Zeit wird aus DB abgefragt
     */
    public void btn_copyPasswordToClipboard(ActionEvent actionEvent) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(passwordField1.getText());
        clipboard.setContent(content);
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(Integer.parseInt(presenter.getTextField_settings_timeoutClipboard())), ev -> {
            clipboard.clear();
        }));
        timeline.play();
    }


    /**
     * public void setEntry(TreeItem<EntryProperty> selectedEntry)
     * Übergebenes Element wird in die jeweiligen Felder geschrieben
     */
    public void setEntry(TreeItem<EntryProperty> selectedEntry) {
        this.entryProperty = selectedEntry.getValue();

        textFieldTitle.setText(entryProperty.getTitle());
        textFieldUsername.setText(entryProperty.getUsername());
        passwordField1.setText(entryProperty.getPassword());
        passwordField2.setText(entryProperty.getPassword());
        textFieldURL.setText(entryProperty.getUrl());
        textAreaDescription.setText(entryProperty.getDescription());
    }

    /**
     * public Boolean changeEntry()
     * Ändert den bestehenden Eintrag, überprüft ob Felder geändert wurden oder nicht
     * @return
     * false -> keiner Änderung
     * true -> mindestens ein Feld wurde geändert
     */
    public Boolean changeEntry() throws SQLException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException, UnsupportedEncodingException {

        if (entryProperty.getTitle().equals(textFieldTitle.getText()) &&
                entryProperty.getUsername().equals(textFieldUsername.getText()) &&
                entryProperty.getPassword().equals(passwordField1.getText()) &&
                entryProperty.getPassword().equals(passwordField2.getText()) &&
                entryProperty.getUrl().equals(textFieldURL.getText()) &&
                entryProperty.getDescription().equals(textAreaDescription.getText()) &&
                entryProperty.getCategoryID() == (comboBox.getSelectionModel().getSelectedIndex()+1) ) {
        } else {
            if (equalsPassword()) {
                this.entryProperty.setTitle(textFieldTitle.getText());
                this.entryProperty.setUsername((textFieldUsername.getText()));
                this.entryProperty.setPassword(passwordField1.getText());
                this.entryProperty.setUrl(textFieldURL.getText());
                this.entryProperty.setDescription(textAreaDescription.getText());
                this.entryProperty.setCategoryID((comboBox.getSelectionModel().getSelectedIndex()+1));
                presenter.updateEntry(entryProperty);

            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * private boolean equalsPassword()
     * Überprüft ob die eingegebenen Passwörter identsich sind
     * @return
     * true -> Passwörter sind gleich
     * false -> Passwörter sind nicht gleich -> Labelmeldung wird angezeigt
     */
    private boolean equalsPassword() {
        if (passwordField1.getText().equals(passwordField2.getText())) {
            return true;
        }
        labelErrorMessage.setText("Password not Equals");
        return false;
    }

    /**
     *  public void fillComboBox()
     * Füllt auswahl Combobox mit den Kategorien aus der DB
     */
    public void fillComboBox() throws SQLException {
        ArrayList<String> categoryList = (ArrayList<String>) presenter.getCategoryListFromDB();
        for (int i = 1; i < categoryList.size(); i++) {
            comboBox.getItems().add(new Label(categoryList.get(i)));
        }
        comboBox.setPromptText("Select categorie");
        comboBox.getSelectionModel().select(entryProperty.getCategoryID()-1);
    }


    /**
     * public void setPresenter(PresenterMain presenter)
     * Bekommt den Presenter übergeben
     */
    public void setPresenter(PresenterMain presenter) throws SQLException {
        this.presenter = presenter;
    }



    public void fillComboBoxhistorie() throws SQLException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        ArrayList<Entry> entrieHistroy = presenter.getEntrysFromRecycleBinForEntryID(entryProperty.getEntryID().toString());

        comboBoxHistorie.getItems().add(new Label("Current Entry"));

        for (Entry entry : entrieHistroy) {
            comboBoxHistorie.getItems().add(new Label(presenter.timestampToTime(entry.getTimestamp())));
            System.out.println(entry.getTimestamp());

        }

        comboBoxHistorie.setPromptText("Historie");
        comboBoxHistorie.getSelectionModel().select(entryProperty.getCategoryID()-1);

        comboBoxHistorie.valueProperty().addListener(new ChangeListener<Label>() {
            @Override
            public void changed(ObservableValue<? extends Label> observable, Label oldValue, Label newValue) {

                if (comboBoxHistorie.getSelectionModel().getSelectedIndex() == 0) {
                    btn_save.setVisible(true);
                    btn_restore.setVisible(false);
                } else {
                    btn_save.setVisible(false);
                    btn_restore.setVisible(true);
                    Entry selectedEntry = entrieHistroy.get(comboBoxHistorie.getSelectionModel().getSelectedIndex()-1);
                    textFieldTitle.setText(selectedEntry.getTitle());
                    textAreaDescription.setText(selectedEntry.getDescription());
                    textFieldUsername.setText(selectedEntry.getUsername());
                    textFieldPassword1.setText(selectedEntry.getPassword());
                    textFieldPassword2.setText(selectedEntry.getPassword());
                    textFieldURL.setText(selectedEntry.getUrl());


                }
            }
        });

        }




    }



