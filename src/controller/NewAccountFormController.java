package controller;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.sql.*;

public class NewAccountFormController {
    public TextField txtUserName;
    public TextField txtEMail;
    public TextField txtPassword;
    public TextField txtConfirmPassword;
    public AnchorPane root;
    public Button btnRegister;
    public Label lblUID;
    public Label lblPasswordError;

    public void initialize(){
        txtUserName.setDisable(true);
        txtEMail.setDisable(true);
        txtPassword.setDisable(true);
        txtConfirmPassword.setDisable(true);
        btnRegister.setDisable(true);
        lblPasswordError.setVisible(false);
    }
    public void btnAddNewUserOnAction(ActionEvent actionEvent) {
        txtUserName.setDisable(false);
        txtEMail.setDisable(false);
        txtPassword.setDisable(false);
        txtConfirmPassword.setDisable(false);
        btnRegister.setDisable(false);
        txtUserName.requestFocus();

        autoGenerateID();


    }
    public void autoGenerateID(){
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select  id from user order by id desc limit 1");

            boolean isExist = resultSet.next();

            if(isExist){
                String oldId = resultSet.getString(1);
                int length = oldId.length();

                String id = oldId.substring(1,length);

                int intID = Integer.parseInt(id);
                intID = intID+1;

                if(intID<10){
                    lblUID.setText("U00"+intID);
                }
                else if(intID<100){
                    lblUID.setText("U0"+intID);
                }
                else {
                    lblUID.setText("U"+intID);
                }
            }
            else {
                lblUID.setText("U001");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void btnRegisterOnAction(ActionEvent actionEvent) {
        String newPassword = txtPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        if(newPassword.equals(confirmPassword)){
            txtPassword.setStyle("-fx-border-color: green");
            txtConfirmPassword.setStyle("-fx-border-color: green");
            lblPasswordError.setVisible(false);
            register();
        }else{
            txtPassword.setStyle("-fx-border-color: red");
            txtConfirmPassword.setStyle("-fx-border-color: red");
            txtConfirmPassword.clear();
            lblPasswordError.setVisible(true);
            txtPassword.requestFocus();
        }
    }
    public void register(){
        String id = lblUID.getText();
        String username = txtUserName.getText();
        String email = txtEMail.getText();
        String password = txtConfirmPassword.getText();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedstatement  = connection.prepareStatement("insert into user values (?,?,?,?)");

            preparedstatement.setObject(1,id);
            preparedstatement.setObject(2,username);
            preparedstatement.setObject(3,password);
            preparedstatement.setObject(4,email);

            int i = preparedstatement.executeUpdate();

            if(i != 0){
                new Alert(Alert.AlertType.CONFIRMATION,"Success..").showAndWait();

                Parent parent = FXMLLoader.load(this.getClass().getResource("../view/LoginForm.fxml"));
                Scene scene = new Scene(parent);

                Stage primaryStage = (Stage) this.root.getScene().getWindow();
                primaryStage.setScene(scene);
                primaryStage.setTitle("Login Form");
                primaryStage.centerOnScreen();
            }
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }

    }
}
