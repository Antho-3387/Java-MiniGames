package demo.teste.moreless.controller;

import demo.teste.moreless.dao.DatabaseManager;
import demo.teste.moreless.model.Produit;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    @FXML private TableView<Produit> productTable;
    @FXML private TableColumn<Produit, Integer> colId;
    @FXML private TableColumn<Produit, String>  colNom;
    @FXML private TableColumn<Produit, Double>  colPrix;
    @FXML private TableColumn<Produit, String>  colCategorie;
    @FXML private TableColumn<Produit, String>  colImagePath;

    @FXML private TextField nomField;
    @FXML private TextField prixField;
    @FXML private TextField categorieField;
    @FXML private TextField imagePathField;

    @FXML private Button editBtn;
    @FXML private Button deleteBtn;
    @FXML private Label  statusLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        colCategorie.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        colImagePath.setCellValueFactory(new PropertyValueFactory<>("imagePath"));

        refreshTable();

        productTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            boolean has = sel != null;
            editBtn.setDisable(!has);
            deleteBtn.setDisable(!has);
            if (has) {
                nomField.setText(sel.getNom());
                prixField.setText(String.valueOf(sel.getPrix()));
                categorieField.setText(sel.getCategorie() != null ? sel.getCategorie() : "");
                imagePathField.setText(sel.getImagePath() != null ? sel.getImagePath() : "");
            }
        });
    }

    @FXML
    public void handleAdd() {
        Produit p = buildFromForm();
        if (p == null) return;
        DatabaseManager.insert(p);
        refreshTable();
        clearForm();
        statusLabel.setText("Produit ajouté.");
    }

    @FXML
    public void handleEdit() {
        Produit selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        Produit p = buildFromForm();
        if (p == null) return;
        p.setId(selected.getId());
        DatabaseManager.update(p);
        refreshTable();
        clearForm();
        statusLabel.setText("Produit modifié.");
    }

    @FXML
    public void handleDelete() {
        Produit selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        DatabaseManager.delete(selected.getId());
        refreshTable();
        clearForm();
        statusLabel.setText("Produit supprimé.");
    }

    @FXML
    public void handleBrowse() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choisir une image");
        fc.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp", "*.webp")
        );
        File file = fc.showOpenDialog(imagePathField.getScene().getWindow());
        if (file != null) {
            imagePathField.setText(file.getPath());
            statusLabel.setText("Image sélectionnée.");
        }
    }

    @FXML
    public void handleClose() {
        Stage stage = (Stage) productTable.getScene().getWindow();
        stage.close();
    }

    private Produit buildFromForm() {
        String nom = nomField.getText().trim();
        String prixStr = prixField.getText().trim().replace(",", ".");
        String categorie = categorieField.getText().trim();
        String imagePath = imagePathField.getText().trim();

        if (nom.isEmpty() || prixStr.isEmpty()) {
            statusLabel.setText("Nom et prix obligatoires.");
            return null;
        }
        double prix;
        try {
            prix = Double.parseDouble(prixStr);
        } catch (NumberFormatException e) {
            statusLabel.setText("Prix invalide.");
            return null;
        }
        return new Produit(0, nom, prix, imagePath.isEmpty() ? null : imagePath, categorie);
    }

    private void clearForm() {
        nomField.clear();
        prixField.clear();
        categorieField.clear();
        imagePathField.clear();
        productTable.getSelectionModel().clearSelection();
        editBtn.setDisable(true);
        deleteBtn.setDisable(true);
    }

    private void refreshTable() {
        productTable.getItems().setAll(DatabaseManager.findAll());
    }
}