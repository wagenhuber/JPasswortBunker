package jpasswortbunker.mgm.view;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javafx.util.Callback;

import javafx.util.Duration;
import jpasswortbunker.mgm.presenter.EntryProperty;
import jpasswortbunker.mgm.presenter.PresenterMain;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;
//http://code.makery.ch/blog/javafx-dialogs-official/ -> ConfirmDialog

public class MainInterfaceController implements Initializable {

    @FXML
    private ResourceBundle bundle;
    private Locale locale;

    @FXML
    private JFXButton btn_finance, btn_social, btn_email, btn_network, btn_settings, btn_newEntry, btn_recycle, btn_settings_timeoutClipboard, btn_settings_numberBackupEntriesOk;

    @FXML
    private ImageView btn_logo;

    @FXML
    private AnchorPane pane_entrys, pane_settings, pane_recycle;

    @FXML
    private JFXTreeTableView<EntryProperty> treeView;

    @FXML
    private JFXTreeTableView<EntryProperty> tableView_recylce;

    @FXML
    private JFXTextField textField_Search, textField_settings_timeoutClipboard, textField_settings_backupEntries, textField_settings_lengthRandomPasswords,textField_settings_saveStatus;

    @FXML
    private AnchorPane mainAnchorPane;

    @FXML
    private Menu menu_File, menu_Edit, menu_Help;

    @FXML
    private MenuItem menuItem_NewMasterpassword, menuItem_NewEntry, menuItem_Help, menuItem_About;

    private static Stage stageMainInterfaceController, stageSetMasterPassword, stageLogin, stageNewEntry;
    private ContextMenu contextMenu;


    private PresenterMain presenter = new PresenterMain(this);




    public MainInterfaceController() throws NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, SQLException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
    }



    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            //Ruft Methode auf und ruft jeweiliges Fenster auf
            checkIfMasterPasswortExistsInDB();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //bekommt die Stage der Main methode um diese später sichtbar zu schalten
        stageMainInterfaceController = Testklasse.getPrimaryStage();
    }

    public void updateView() {
        fillTreeView();
        fillRecycleTable();
        stageMainInterfaceController.show();
    }

    //Hinzugefügt von Wagenhuber: Wird nach dem Hinzufügen / Updaten eines neuen / bestehenden Entries ausgeführt um die View zu aktualisieren
    public void updateView2() {
        pane_settings.setVisible(false);
        pane_entrys.setVisible(true);
        pane_recycle.setVisible(false);
        textField_Search.clear();
        textField_Search.setVisible(true);
        stageMainInterfaceController.show();
        treeView.setPredicate(new Predicate<TreeItem<EntryProperty>>() {
            @Override
            public boolean test(TreeItem<EntryProperty> entryTreeItem) {
                Boolean flag = entryTreeItem.getValue().categoryIDProperty().getValue().equals(presenter.getCategoryChoosenForLastNewEntry());
                return flag;
            }
        });

    }


    public void fillTreeView() {

        //Spalte Title
        JFXTreeTableColumn<EntryProperty, String> titleName = new JFXTreeTableColumn<>("Title");
        titleName.setPrefWidth(120);
        titleName.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<EntryProperty, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<EntryProperty, String> param) {
                return param.getValue().getValue().titleProperty();
            }
        });

        //Spalte Username
        JFXTreeTableColumn<EntryProperty, String> usernameCol = new JFXTreeTableColumn<>("Username");
        usernameCol.setPrefWidth(200);
        usernameCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<EntryProperty, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<EntryProperty, String> param) {
                return param.getValue().getValue().usernameProperty();
            }
        });

        //Spalte URL
        JFXTreeTableColumn<EntryProperty, String> urlCol = new JFXTreeTableColumn<>("URL");
        urlCol.setPrefWidth(190);
        urlCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<EntryProperty, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<EntryProperty, String> param) {
                return param.getValue().getValue().urlProperty();
            }
        });

        //Spalte Description
        JFXTreeTableColumn<EntryProperty, String> desCol = new JFXTreeTableColumn<>("Description");
        desCol.setPrefWidth(180);
        desCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<EntryProperty, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<EntryProperty, String> param) {
                return param.getValue().getValue().descriptionProperty();
            }
        });

        //Inhalte werden in die Tabelle geschrieben
        final TreeItem<EntryProperty> root = new RecursiveTreeItem<EntryProperty>(presenter.getEntryPropertiesList(), RecursiveTreeObject::getChildren);
        treeView.getColumns().setAll(titleName, usernameCol, urlCol, desCol);
        treeView.setRoot(root);
        treeView.sort();
        treeView.setShowRoot(false);
        //ruft Methode auf und baut ContextMenu zusammen
        buildContextMenu();


        //Eventhandling für die Elemente
        treeView.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent ee) {
                if (ee.isPrimaryButtonDown() && ee.getClickCount() == 2) {

                    try {
                        editEntryScene();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                if (ee.isSecondaryButtonDown()) {
                    contextMenu.show(treeView, ee.getScreenX(), ee.getScreenY());
                }
            }
        });
    }

    /**
     * public void searchFunction()
     * Suchfunktion in Suchleiste Suche nach: Title und Username
     */
    public void searchFunction() {
        textField_Search.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                treeView.setPredicate(new Predicate<TreeItem<EntryProperty>>() {
                    @Override
                    public boolean test(TreeItem<EntryProperty> entryTreeItem) {
                        Boolean flag = entryTreeItem.getValue().titleProperty().getValue().toLowerCase().contains(newValue.toLowerCase()) ||
                                entryTreeItem.getValue().usernameProperty().getValue().toLowerCase().contains(newValue.toLowerCase());
                        return flag;
                    }
                });
            }
        });
    }

    /**
     * Ruft Methode in Model auf um zu überprüfen, ob Masterpasswort gesetzt wurde
     * Return Value:
     * true -> LoginScreen
     * false -> SetMasterPassword
     */
    private void checkIfMasterPasswortExistsInDB() throws IOException, SQLException {
        if (presenter.checkIfMasterPasswortExistsInDB()) {
            System.out.println("gesetzt");
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LoginScreen.fxml"));
            Parent parent = fxmlLoader.load();
            LoginScreenController loginScreenController = fxmlLoader.<LoginScreenController>getController();
            loginScreenController.setPresenter(presenter);
            this.stageLogin = new Stage();
            stageLogin.setTitle("LoginScreen");
            stageLogin.setScene(new Scene(parent, 500, 400));
            stageLogin.setAlwaysOnTop(true);
            stageLogin.setResizable(false);
            stageLogin.show();
        } else {
            System.out.println("Nicht gesetzt");
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SetMasterPassword.fxml"));
            Parent parent = fxmlLoader.load();
            SetMasterPasswordController loginScreenController = fxmlLoader.<SetMasterPasswordController>getController();
            loginScreenController.setPresenter(presenter);
            this.stageLogin = new Stage();
            stageLogin.setTitle("SetMasterPassword");
            stageLogin.setScene(new Scene(parent, 500, 400));
            stageLogin.setAlwaysOnTop(true);
            stageLogin.setResizable(false);
            stageLogin.show();
        }
    }

    /**
     * Button erstellt neues Fenster um einen neuen Eintrag zu erstellen
     */
    public void btn_newEntry() throws IOException, SQLException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("NewEntry.fxml"));
        Parent parent = fxmlLoader.load();
        NewEntryController newEntryController = fxmlLoader.<NewEntryController>getController();
        newEntryController.setPresenter(presenter);
        newEntryController.fillComboBox();
        this.stageNewEntry = new Stage();
        stageNewEntry.setTitle("New Entry");
        stageNewEntry.setScene(new Scene(parent, 400, 400));
        stageNewEntry.setAlwaysOnTop(true);
        stageNewEntry.setResizable(false);
        stageNewEntry.show();
    }


    //Button Logo zeit alle Einträge an und setzt Suchfilter bzw Kategorie zurück
    public void btn_logo(MouseEvent mouseEvent) {
        pane_settings.setVisible(false);
        pane_entrys.setVisible(true);
        pane_recycle.setVisible(false);
        textField_Search.clear();
        textField_Search.setVisible(true);
        treeView.setPredicate(new Predicate<TreeItem<EntryProperty>>() {
            @Override
            public boolean test(TreeItem<EntryProperty> entryTreeItem) {
                return true;
            }
        });
    }

    //Button Kategorie_Finanzen
    public void btn_finance(ActionEvent actionEvent) {

        //Auskommentiert durch Wagenhuber am 24.3.18 bzgl. Ansicht aktualisiert nicht
        //updateView();
        pane_settings.setVisible(false);
        pane_entrys.setVisible(true);
        pane_recycle.setVisible(false);
        textField_Search.clear();
        textField_Search.setVisible(true);
        treeView.setPredicate(new Predicate<TreeItem<EntryProperty>>() {
            @Override
            public boolean test(TreeItem<EntryProperty> entryTreeItem) {
                Boolean flag = entryTreeItem.getValue().categoryIDProperty().getValue().equals(1);
                return flag;
            }
        });
    }

    //Button Kategorie_Social
    public void btn_social(ActionEvent actionEvent) {
        pane_settings.setVisible(false);
        pane_entrys.setVisible(true);
        pane_recycle.setVisible(false);
        textField_Search.clear();
        textField_Search.setVisible(true);
        treeView.setPredicate(new Predicate<TreeItem<EntryProperty>>() {
            @Override
            public boolean test(TreeItem<EntryProperty> entryTreeItem) {
                Boolean flag = entryTreeItem.getValue().categoryIDProperty().getValue().equals(2);
                return flag;
            }
        });
    }


    //Button Kategorie_E-Mail
    public void btn_email(ActionEvent actionEvent) {
        pane_settings.setVisible(false);
        pane_entrys.setVisible(true);
        pane_recycle.setVisible(false);
        textField_Search.clear();
        textField_Search.setVisible(true);
        treeView.setPredicate(new Predicate<TreeItem<EntryProperty>>() {
            @Override
            public boolean test(TreeItem<EntryProperty> entryTreeItem) {
                Boolean flag = entryTreeItem.getValue().categoryIDProperty().getValue().equals(3);
                return flag;
            }
        });
    }


    /**
     * public void btn_network(ActionEvent actionEvent)
     * Wechselt zur Network Kategorie
     */
    public void btn_network(ActionEvent actionEvent) {
        pane_settings.setVisible(false);
        pane_entrys.setVisible(true);
        pane_recycle.setVisible(false);
        textField_Search.clear();
        textField_Search.setVisible(true);
        treeView.setPredicate(new Predicate<TreeItem<EntryProperty>>() {
            @Override
            public boolean test(TreeItem<EntryProperty> entryTreeItem) {
                Boolean flag = entryTreeItem.getValue().categoryIDProperty().getValue().equals(4);
                return flag;
            }
        });
    }


    /**
     * public void btn_recycle(ActionEvent actionEvent)
     * Wechselt zur Recycle Pane um sich die gelöschten Einträge anzeigen zu lassen
     */
    public void btn_recycle(ActionEvent actionEvent) {
        pane_settings.setVisible(false);
        pane_entrys.setVisible(false);
        pane_recycle.setVisible(true);
        textField_Search.clear();
        textField_Search.setVisible(true);

    }


    //Button für die Einstellungen
    public void btn_settings(ActionEvent actionEvent) {
        pane_settings.setVisible(true);
        pane_entrys.setVisible(false);
        pane_recycle.setVisible(false);
        textField_Search.clear();
        textField_Search.setVisible(false);

        //Hinzugefügt durch Wagenhuber: Textfelder für Settings
        textField_settings_backupEntries.setText(presenter.getTextField_settings_numberBackupEntries());
        textField_settings_lengthRandomPasswords.setText(presenter.getTextField_settings_lengthRandomPasswords());
        textField_settings_timeoutClipboard.setText(presenter.getTextField_settings_timeoutClipboard());
    }

    /**
     * public void btn_newMasterPassword()
     * Ruft einen neuen Dialog auf, um das MasterPasswort zu ändern
     */
    public void btn_newMasterPassword() {
        ChangePasswordDialog changePasswordDialog = new ChangePasswordDialog(presenter);
    }

    public void btn_lang_en(ActionEvent actionEvent) {
        System.out.println("Englisch");
        loadLang("en");
    }

    public void btn_lang_de(ActionEvent actionEvent) {
        System.out.println("Deutsch");
        loadLang("de");
    }

    /**
     * private void loadLang(String lang)
     * Bekommt als Paramter das jeweilige Landeskuerzel
     * setzt die jeweiligen Felder dann neu mit der neuen Sprache
     */
    private void loadLang(String lang) {
        locale = new Locale(lang);
        bundle = ResourceBundle.getBundle("jpasswortbunker.mgm.view.bundles.LangBundle", locale);
        btn_finance.setText(bundle.getString("button.finance"));
        btn_social.setText(bundle.getString("button.social"));
        btn_email.setText(bundle.getString("button.email"));
        btn_network.setText(bundle.getString("button.network"));
        btn_recycle.setText(bundle.getString("button.recycle"));
        btn_settings.setText(bundle.getString("button.settings"));
        btn_newEntry.setText(bundle.getString("button.newEntry"));
        textField_Search.setPromptText(bundle.getString("promptText.search"));
        menu_File.setText(bundle.getString("menu.file"));
        menu_Edit.setText(bundle.getString("menu.edit"));
        menu_Help.setText(bundle.getString("menu.help"));
        menuItem_NewMasterpassword.setText(bundle.getString("menuItem.newMasterPassword"));
        menuItem_NewEntry.setText(bundle.getString("menuItem.newEntry"));
        menuItem_Help.setText(bundle.getString("menuItem.help"));
        menuItem_About.setText(bundle.getString("menuItem.about"));
    }

    /**
     * private void buildContextMenu()
     * Baut Context Menu zusammen
     */
    private void buildContextMenu() {
        //ToDo eventuell noch mal anpassen wenn Zeit
        contextMenu = null;
        contextMenu = new ContextMenu();
        MenuItem item1 = new MenuItem("Delete");
        item1.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation Dialog");
                alert.setHeaderText("Delete Confirmation");
                alert.setContentText("Are you realy sure, that you want delete the Entry ");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    // ... user chose OK
                    try {
                        presenter.removeEntry(treeView.getSelectionModel().getSelectedItem().getValue());
                    } catch (IllegalBlockSizeException e) {
                        e.printStackTrace();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (BadPaddingException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else {
                    // ... user chose CANCEL or closed the dialog

                }
            }
        });
        contextMenu.getItems().add(item1);
        MenuItem item2 = new MenuItem("Edit");
        item2.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                try {
                    editEntryScene();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        contextMenu.getItems().add(item2);
        MenuItem item3 = new MenuItem("Copy Password");
        item3.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString(treeView.getSelectionModel().getSelectedItem().getValue().getPassword());
                clipboard.setContent(content);
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(Integer.parseInt(presenter.getTextField_settings_timeoutClipboard())), ev -> {
                    clipboard.clear();
                }));
                timeline.play();
            }
        });
        contextMenu.getItems().add(item3);
    }

    private void editEntryScene() throws SQLException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("EditEntry.fxml"));
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //übergibt an EditEntryController den ausgewählten Eintrag
        EditEntryController editEntryController = loader.getController();
        //Ausgewähltes Element treeView.getSelectionModel().getSelectedItem()
        editEntryController.setEntry(treeView.getSelectionModel().getSelectedItem());
        editEntryController.setPresenter(presenter);
        editEntryController.fillComboBox();
        editEntryController.fillComboBoxhistorie();


        Parent parentEditEntry = loader.getRoot();
        Stage stageEditEntry = new Stage();
        Scene sceneEditentry = new Scene(parentEditEntry, 420, 420);
        stageEditEntry.setTitle("Edit your EntryProperty");
        stageEditEntry.setScene(sceneEditentry);
        stageEditEntry.setResizable(false);
        stageEditEntry.show();
        stageEditEntry.setResizable(false);
        stageEditEntry.getIcons().add(new Image(String.valueOf(this.getClass().getResource("images/logo.png"))));
    }



    public void fillRecycleTable() {

        //Spalte Title
        JFXTreeTableColumn<EntryProperty, String> titleName = new JFXTreeTableColumn<>("Title");
        titleName.setPrefWidth(100);
        titleName.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<EntryProperty, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<EntryProperty, String> param) {
                return param.getValue().getValue().titleProperty();
            }
        });

        //Spalte Username
        JFXTreeTableColumn<EntryProperty, String> usernameCol = new JFXTreeTableColumn<>("Username");
        usernameCol.setPrefWidth(100);
        usernameCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<EntryProperty, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<EntryProperty, String> param) {
                return param.getValue().getValue().usernameProperty();
            }
        });

        //Spalte URL
        JFXTreeTableColumn<EntryProperty, String> urlCol = new JFXTreeTableColumn<>("URL");
        urlCol.setPrefWidth(150);
        urlCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<EntryProperty, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<EntryProperty, String> param) {
                return param.getValue().getValue().urlProperty();
            }
        });

        //Spalte Description
        JFXTreeTableColumn<EntryProperty, String> desCol = new JFXTreeTableColumn<>("Description");
        desCol.setPrefWidth(150);
        desCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<EntryProperty, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<EntryProperty, String> param) {
                return param.getValue().getValue().descriptionProperty();
            }
        });

        //Inhalte werden in die Tabelle geschrieben
        final TreeItem<EntryProperty> root1 = new RecursiveTreeItem<EntryProperty>(presenter.getEntryPropertiesListRecycle(), RecursiveTreeObject::getChildren);
        tableView_recylce.getColumns().setAll(titleName, usernameCol, urlCol, desCol);
        tableView_recylce.setRoot(root1);
        tableView_recylce.setShowRoot(false);
        tableView_recylce.sort();


        //ruft Methode auf und baut ContextMenu zusammen
        buildContextMenu();


        //Eventhandling für die Elemente
        tableView_recylce.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent ee) {
                if (ee.isPrimaryButtonDown() && ee.getClickCount() == 2) {

                    try {
                        editEntryScene();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                if (ee.isSecondaryButtonDown()) {
                    contextMenu.show(treeView, ee.getScreenX(), ee.getScreenY());
                }
            }
        });
    }


    //Suchfunktion in Suchleiste Suche nach: Title und Username
    public void searchFunction1() {
        textField_Search.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                tableView_recylce.setPredicate(new Predicate<TreeItem<EntryProperty>>() {
                    @Override
                    public boolean test(TreeItem<EntryProperty> entryTreeItem) {
                        Boolean flag = entryTreeItem.getValue().titleProperty().getValue().toLowerCase().contains(newValue.toLowerCase()) ||
                                entryTreeItem.getValue().usernameProperty().getValue().toLowerCase().contains(newValue.toLowerCase());
                        return flag;
                    }
                });
            }
        });
    }


// Folgende Methoden hinzugefügt von Wagenhuber:



    public void btn_settings_setNumberBackupEntries(ActionEvent actionEvent) {
       presenter.setTextField_settings_numberBackupEntries(textField_settings_backupEntries.getText());
       updateSaveStatus();
    }


    public void btn_settings_lengthRandomPasswords(ActionEvent actionEvent) {
        presenter.setTextField_settings_lengthRandomPasswords(textField_settings_lengthRandomPasswords.getText());
        updateSaveStatus();
    }


    public void btn_settings_timeoutClipboard(ActionEvent actionEvent) {
        presenter.setTextField_settings_timeoutClipboard(textField_settings_timeoutClipboard.getText());
        updateSaveStatus();
    }


    private void updateSaveStatus() {
        boolean status = presenter.isTextField_settings_saveStatusBoolean();
        if (status) {
            textField_settings_saveStatus.setText("Success!");
        } else {
            textField_settings_saveStatus.setText("Error!");
        }

    }

}
