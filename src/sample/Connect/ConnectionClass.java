package sample.Connect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Adeoy3 Pc on 11/24/2018.
 */


//Class that creates connection to database
public class ConnectionClass
{
    public  Connection connection;
    public Connection getConnection() throws ClassNotFoundException, SQLException {
        String dbName = "projector";
        String userName ="root";
        String password="";

        Class.forName("com.mysql.jdbc.Driver");

        //connecting  to database
         connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+dbName, userName,password);
        return connection;
    }
}
