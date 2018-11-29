package sample;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.Connect.ConnectionClass;
import sample.Model.InventoryModel;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public Button closeDialog;
    private int dataId;

    //Referencing FXML table
    public TableColumn<InventoryModel, String> nameColumn;
    public TableColumn<InventoryModel, String> priceColumn;
    public TableColumn<InventoryModel, String> colorColumn;
    public TableView<InventoryModel> inventoryTable;
    public TableView<InventoryModel> soldTable;

    //Creating observable list for inventory and sales table
    public static ObservableList<InventoryModel> data = FXCollections.observableArrayList();
    public static ObservableList<InventoryModel> buyerdata = FXCollections.observableArrayList();
    public Button add_new;
    public TableColumn<InventoryModel, String> soldNameColumn1;
    public TableColumn<InventoryModel, String> soldPriceColumn1;
    public TableColumn<InventoryModel, String> soldColorColumn1;
    public TableColumn<InventoryModel, String> soldBuyerColumn;

    //creating instances of the controlls  used
    public Button save_inventory;
    public Button clear_inventory;
    public TextField nameField;
    public TextField priceFiled;
    public TextField colorField;
    public Label warningLable;
    public Label inventoryDetailName;
    public Label inventoryDetailPrice;
    public Label inventoryDetailColor;
    public Button confirmSales;
    public HBox buyersDetailHolder;
    public Label pickItenError;
    public TextField customerNameHolder;
    public Label customernNameError;
    public TextField searchBar;
    public Button updateButton;
    public Button saveButton;

//Instanciating connection to mysql database
    ConnectionClass connectionClass = new ConnectionClass();



    ///This is the method that gets called by default after FXML declaration
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //initializing the inventory table column
        nameColumn.setCellValueFactory(
                new PropertyValueFactory<InventoryModel, String>("name"));
        priceColumn.setCellValueFactory(
                new PropertyValueFactory<InventoryModel, String>("price"));
        colorColumn.setCellValueFactory(
                new PropertyValueFactory<InventoryModel, String>("color"));


        //initializing the sales table column
        soldNameColumn1.setCellValueFactory(
                new PropertyValueFactory<InventoryModel, String>("name"));
        soldPriceColumn1.setCellValueFactory(
                new PropertyValueFactory<InventoryModel, String>("price"));
        soldColorColumn1.setCellValueFactory(
                new PropertyValueFactory<InventoryModel, String>("color"));
        soldBuyerColumn.setCellValueFactory(
                new PropertyValueFactory<InventoryModel, String>("buyer"));

        // calling the method that build inventory table data
        buildData();

        //calling the method that build sales table data
        buildData2();

        //calling the method that handles the table row click
        handleCellFactory();


        //passing data to the observablr list
        ObservableList data = inventoryTable.getItems();
        ObservableList data2 = soldTable.getItems();

        //Method that adds right click to the table( Edit and delete) Menu
        addContextMenu();

        //Handling of the search bar textfield on the Inventory table
        searchBar.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (oldValue != null && (newValue.length() < oldValue.length())) {
                inventoryTable.setItems(data);
            }
            String value = newValue.toLowerCase();
            ObservableList<InventoryModel> subentries = FXCollections.observableArrayList();

            long count = inventoryTable.getColumns().stream().count();
            for (int i = 0; i < inventoryTable.getItems().size(); i++) {
                for (int j = 0; j < count; j++) {
                    String entry = "" + inventoryTable.getColumns().get(j).getCellData(i);
                    if (entry.toLowerCase().contains(value)) {
                        subentries.add(inventoryTable.getItems().get(i));
                        break;
                    }
                }
            }
            inventoryTable.setItems(subentries);
        });


        //Handling of the search bar textfield on the Sales table
        searchBar.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (oldValue != null && (newValue.length() < oldValue.length())) {
                soldTable.setItems(data2);
            }
            String value = newValue.toLowerCase();
            ObservableList<InventoryModel> subentries = FXCollections.observableArrayList();

            long count = soldTable.getColumns().stream().count();
            for (int i = 0; i < soldTable.getItems().size(); i++) {
                for (int j = 0; j < count; j++) {
                    String entry = "" + soldTable.getColumns().get(j).getCellData(i);
                    if (entry.toLowerCase().contains(value)) {
                        subentries.add(soldTable.getItems().get(i));
                        break;
                    }
                }
            }
            soldTable.setItems(subentries);
        });


    }

    Stage stage = new Stage();


    public void cancelStage(ActionEvent actionEvent) {
        stage = new Stage();
        stage.close();
    }


    //Method that saves the input inventory to the database
    public void saveInventory(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {

        if (nameField.getText().trim().equals("")) {
            nameField.setStyle("#43454");
            warningLable.setVisible(true);
        } else if (priceFiled.getText().trim().equals("")) {
            priceFiled.setStyle("#23452");
            warningLable.setVisible(true);

        } else if (colorField.getText().trim().equals("#45345")) {
            colorField.setStyle("#89087");
            warningLable.setVisible(true);

        } else {
            ConnectionClass connectionClass = new ConnectionClass();
            Connection connection = connectionClass.getConnection();

            String sql = "INSERT INTO iv2 (name,price,color) VALUES('" + nameField.getText().toString() + "','" + priceFiled.getText().toString() + "','" + colorField.getText().toString() + "')";
            // System.out.println("Name: "+namefield.getText().toString() );
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            // controller = new Controller();

//        stage.close();
            doClear();
            refreshTable();
            warningLable.setVisible(false);
        }
    }

    public void buildData() {
        try {
            Connection connection = connectionClass.getConnection();//Make connection to the database
            String SQL = "Select * from iv2 Order By id"; //Select query from database
            ResultSet rs = connection.createStatement().executeQuery(SQL); //Initiating Quey
            while (rs.next()) {
                InventoryModel cm = new InventoryModel(); //Initializing the inventory Model class
                cm.name.set(rs.getString("name")); //Get the name row
                cm.price.set(rs.getString("price"));//Get the price row
                cm.color.set(rs.getString("color")); //Get the color row
                cm.carId.set(Integer.valueOf(rs.getString("id"))); //Get each

                data.add(cm);
            }
            inventoryTable.setItems(data); //Add data to table

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
    }

    public void buildData2() {
        try {
            Connection connection = connectionClass.getConnection();
            String SQL = "Select * from soldtable Order By id";
            ResultSet rs = connection.createStatement().executeQuery(SQL);
            while (rs.next()) {
                InventoryModel cm = new InventoryModel();
                cm.name.set(rs.getString("name"));
                cm.price.set(rs.getString("price"));
                cm.color.set(rs.getString("color"));
                cm.buyer.set(rs.getString("buyer"));

                buyerdata.add(cm);
            }
            soldTable.setItems(buyerdata);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
    }

    private void printRow(InventoryModel item) {
        System.out.println("After Clicked: " + item.getName());

        inventoryDetailName.setText("" + item.getName());
        inventoryDetailPrice.setText("" + item.getPrice());
        inventoryDetailColor.setText("" + item.getColor());
    }

    public void handleCellFactory() {
        inventoryTable.setRowFactory(tv -> {
            TableRow<InventoryModel> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY
                        && event.getClickCount() == 2) {


                    InventoryModel clickedRow = row.getItem();
                    // InventoryDetailController inventoryDetailController = new InventoryDetailController();

                    printRow(clickedRow);
                }
            });
            return row;
        });
    }

    //Method that refresh table when an item is added or deleted
    public void refreshTable() {
        data.clear();
        buildData();

    }

    //Method that refresh sales table
    public void refreshSoldTable() {
        data.clear();
        buildData2();

    }

    //Method that calls method that reset and clears all the necessary field
    public void clearField(ActionEvent event) {
        doClear();
    }


    ////Method that reset and clears all the necessary field
    public void doClear() {
        if (nameField.getText() != null && colorField.getText() != null & priceFiled.getText() != null) {
            nameField.setText("");
            colorField.setText("");
            priceFiled.setText("");
            warningLable.setVisible(false);

        }
        if(updateButton.isVisible())
        {
            updateButton.setVisible(false);
            saveButton.setVisible(true);
        }
    }

    public void doClearBuyer() {
        if (customerNameHolder.getText().trim().equals("")) {
            customerNameHolder.setText("");

        }
    }

    public void clearInventoryDetail(ActionEvent event) {
        inventoryDetailName.setText("");
        inventoryDetailPrice.setText("");
        inventoryDetailColor.setText("");
        buyersDetailHolder.setVisible(false);
        customernNameError.setVisible(false);
    }

    public void handleSell(ActionEvent event) {
        if (inventoryDetailColor.getText().trim().equals("") || inventoryDetailName.getText().trim().equals("")
                || inventoryDetailPrice.getText().trim().equals("")) {
            pickItenError.setVisible(true);
        } else {
            buyersDetailHolder.setVisible(true);
            confirmSales.setDisable(false);
            pickItenError.setVisible(false);
        }
    }


    //Method that handles and populate sold inventory table
    public void confirmSales(ActionEvent event) throws SQLException, ClassNotFoundException {
        if (customerNameHolder.getText().trim().equals("")) {
            customernNameError.setVisible(true);
        } else {
            Connection connection = connectionClass.getConnection();

            String sql = "INSERT INTO soldtable (name,price,color,buyer) VALUES('" + inventoryDetailName.getText().toString() + "','" + inventoryDetailPrice.getText().toString() + "','" + inventoryDetailColor.getText().toString() + "','" + customerNameHolder.getText().toString() + "')";
            // System.out.println("Name: "+namefield.getText().toString() );
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            doClearBuyer();
            buyerdata.clear();
            buildData2();

        }
    }

    //Method that adds the context menu to table
    public void addContextMenu(){
        ContextMenu cm = new ContextMenu(); //initialize context menu
        MenuItem update = new MenuItem("Edit"); //create context menu item Edit
        cm.getItems().add(update); //Add context menu item Edit
        MenuItem delete = new MenuItem("Delete"); //create context menu item Delete
        cm.getItems().add(delete); //Add context menu item Delete


        //Handle Event to table to show the context menu
        inventoryTable.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                if(t.getButton() == MouseButton.SECONDARY) {
                    cm.show(inventoryTable, t.getScreenX(), t.getScreenY());
                }
            }
        });

        //Method that handles the update button when clicked
        update.setOnAction((ActionEvent event) -> {
            InventoryModel item = inventoryTable.getSelectionModel().getSelectedItem();

            updateButton.setVisible(true);
            saveButton.setVisible(false);
            System.out.println("Selected item: " + item.getName());

            nameField.setText(item.getName());
            priceFiled.setText(item.getPrice());
            colorField.setText(item.getColor());
            dataId = item.getUserId();
            System.out.println("Id ni: "+dataId);
        });

        //Method that deletes the item
        delete.setOnAction((ActionEvent event) -> {
            InventoryModel item = inventoryTable.getSelectionModel().getSelectedItem();

            Connection connection = null;

            try {
                connection = connectionClass.getConnection();
                PreparedStatement st = connection.prepareStatement("DELETE FROM iv2 WHERE id = ?");
                st.setInt(1,item.getUserId());
                st.executeUpdate();
                refreshTable();
                dialog(Alert.AlertType.CONFIRMATION,item.getName()+" has been deleted");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        });

    }

    public void update(ActionEvent event) {

        Connection connection = null;
        try {
            connection = connectionClass.getConnection();
            PreparedStatement st = connection.prepareStatement("UPDATE iv2 SET name = ?, price = ?, color = ? WHERE id = ?");
            st.setString(1, nameField.getText());
            st.setString(2, priceFiled.getText());
            st.setString(3, colorField.getText());
            st.setString(4, ""+dataId);
            st.executeUpdate();

            dialog(Alert.AlertType.INFORMATION,"Updated successfully");
            nameField.setText("");
            priceFiled.setText("");
            colorField.setText("");
            updateButton.setVisible(false);
            saveButton.setVisible(true);

            refreshTable();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    //Dialog method ..It shows dialog and accepts the dialog type and Message the dialog should show
    private void dialog(Alert.AlertType alertType,String s){
        Alert alert = new Alert(alertType,s);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle("Info");
        alert.showAndWait();
    }
}
