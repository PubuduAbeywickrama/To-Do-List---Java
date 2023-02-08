package controller;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFormController {
    public AnchorPane root;
    public PasswordField txtPassword;
    public TextField txtUserName;

    public static String enteredusername;
    public static String entereduserid;

    public void CreateNewAccountOnAction(ActionEvent actionEvent) throws IOException {
        Parent parent = FXMLLoader.load(this.getClass().getResource("../view/NewAccountForm.fxml"));
        Scene scene = new Scene(parent);

        Stage primaryStage = (Stage) this.root.getScene().getWindow();

        primaryStage.setScene(scene);
        primaryStage.setTitle("Create New Account");
        primaryStage.centerOnScreen();
    }

    public void btnLoginOnAction(ActionEvent actionEvent) {
        String username = txtUserName.getText();
        String password = txtPassword.getText();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from user where user_name = ? and password = ?");

            preparedStatement.setObject(1,username);
            preparedStatement.setObject(2,password);

            ResultSet resultSet = preparedStatement.executeQuery();
            boolean isExist = resultSet.next();

            if(isExist){
                enteredusername = resultSet.getString(2);
                entereduserid = resultSet.getString(1);
                Parent parent = FXMLLoader.load(this.getClass().getResource("../view/HomeForm.fxml"));
                Scene scene = new Scene(parent);

                Stage primaryStage = (Stage) this.root.getScene().getWindow();
                primaryStage.setScene(scene);
                primaryStage.setTitle("To DO Form");
                primaryStage.centerOnScreen();
            }
            else{
                new Alert(Alert.AlertType.ERROR,"Invalid User name or Password").showAndWait();
                txtPassword.clear();
                txtUserName.clear();
                txtUserName.requestFocus();
            }
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }
    }
}
