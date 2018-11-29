package sample.Model;

/**
 * Created by Adeoy3 Pc on 11/24/2018.
 */

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;



//This class create the model for the object
public class InventoryModel {

    public SimpleIntegerProperty carId = new SimpleIntegerProperty();
    public SimpleStringProperty name = new SimpleStringProperty();
    public SimpleStringProperty color = new SimpleStringProperty();
    public SimpleStringProperty price = new SimpleStringProperty();
    public SimpleStringProperty buyer = new SimpleStringProperty();


    public Integer getUserId() {
        return carId.get();
    }

    public String getName() {
        return name.get();
    }

    public String getColor() {
        return color.get();
    }

    public String getPrice() {
        return price.get();
    }
    public String getBuyer() {
        return buyer.get();
    }


}