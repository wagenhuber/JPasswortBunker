package jpasswortbunker.mgm.presenter;

import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import jpasswortbunker.mgm.model.Entry;
import jpasswortbunker.mgm.model.ModelMain;
import jpasswortbunker.mgm.view.MainInterfaceController;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public final class PresenterMain {

    private MainInterfaceController controller;
    private ModelMain model;
    public ObservableList<EntryProperty> entryPropertiesList = FXCollections.observableArrayList();
    public ObservableList<EntryProperty> entryPropertiesListRecycle = FXCollections.observableArrayList();


    public PresenterMain(MainInterfaceController controller) throws NoSuchPaddingException, BadPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, SQLException, NoSuchAlgorithmException, InvalidKeyException {
        this.controller = controller;
        model = new ModelMain();
        model.initMasterPassword("test");
        model.initEncryptionService();
    }

    public ObservableList<EntryProperty> getEntryPropertiesList() {
        return entryPropertiesList;
    }

    public ObservableList<EntryProperty> getEntryPropertiesListRecycle() {
        return entryPropertiesListRecycle;
    }

    public PresenterMain getPresenter() {
        return this;
    }

    //Schreibt die Liste der Arraylist aus Model in die Observable List im Presenter
    public void writeToObservableList() throws SQLException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException, UnsupportedEncodingException {
        model.FillEntryListFromDb();
        for (jpasswortbunker.mgm.model.Entry entry : model.getEntryListEntrysTable()) {
            entryPropertiesList.add(new EntryProperty(entry.getDbID(), entry.getEntryID(), entry.getTitle(),
                    entry.getUsername(), entry.getPassword(), entry.getUrl(), entry.getDescription(), entry.getCategoryID()));
        }
    }
    //TODO: 14.03.2018  anpassen für das Schreiben in den Mülleimer
    //Schreibt die Liste der Arraylist aus Model in die Observable List im Presenter
    public void writeToObservableListrecycle() throws SQLException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException, UnsupportedEncodingException {
        model.FillEntryListFromRecycleBin();
        for (jpasswortbunker.mgm.model.Entry entry : model.getEntryListRecycleBinTable()) {
            entryPropertiesListRecycle.add(new EntryProperty(entry.getDbID(), entry.getEntryID(), entry.getTitle(),
                    entry.getUsername(), entry.getPassword(), entry.getUrl(), entry.getDescription(), entry.getCategoryID()));
        }
    }



    public boolean checkSetMasterpassword() {
        return true;
        // TODO: 06.03.2018 Methode von Model aufrufen
    }


    public boolean checkIfMasterPasswordIsCorrect() throws UnsupportedEncodingException, SQLException {
        return model.checkIfMasterPasswordIsCorrect();
    }

    public void newEntry(String title, String username, String password, String description, String url, int categoryID) throws SQLException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException, UnsupportedEncodingException {
        model.newEntry(title, username, password, description, url, categoryID);
        /**
         * Parameter werden an Model übergeben und Entry in Model erstellt
         * Den eben erstellten Eintrag wieder holen und diesen in die ObservableList schreiben
         */
        Entry entry = model.getEntryListEntrysTable().get(model.getEntryListEntrysTable().size() - 1);
        entryPropertiesList.add(new EntryProperty(entry.getDbID(), entry.getEntryID(), entry.getTitle(),
                entry.getUsername(), entry.getPassword(), entry.getUrl(), entry.getDescription(), entry.getCategoryID()));
        controller.fillTreeView();
    }


    public void removeEntry(EntryProperty entry) throws IllegalBlockSizeException, SQLException, BadPaddingException, InvalidKeyException, UnsupportedEncodingException {
        model.removeEntry(entry.getEntryID().toString());
        entryPropertiesList.remove(entry);
        controller.fillTreeView();
    }

    public void updateEntry(EntryProperty entry) throws IllegalBlockSizeException, SQLException, BadPaddingException, InvalidKeyException, UnsupportedEncodingException {
        model.updateEntry(entry.getEntryID().toString(), entry.getTitle(), entry.getUsername(), entry.getPassword(), entry.getPassword(), entry.getDescription(), entry.getCategoryID());
        controller.fillTreeView();
    }



    public void test() {
        System.out.println("testMethode Presenter");
    }

}
