package controller;

import db.DBConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import tm.ToDoTM;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class HomeFormController {
    public Label lblmessage;
    public Label lblUId;
    public Pane subroot;
    public TextField txtTaskName;
    public Button btnAddToList;
    public AnchorPane root;
    public ListView<ToDoTM> lstToDoList;
    public TextField txtSelectedText;
    public Button btnDelete;
    public Button btnUpdate;
    public String id;
    public void initialize(){
        lblUId.setText(LoginFormController.entereduserid);
        lblmessage.setText("Hi "+LoginFormController.enteredusername+" Welcome to To Do List");

        txtSelectedText.setDisable(true);
        btnDelete.setDisable(true);
        btnUpdate.setDisable(true);
        //btnAddToList.setDisable(true);
        subroot.setVisible(false);

        loadlist();

        lstToDoList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToDoTM>() {
            @Override
            public void changed(ObservableValue<? extends ToDoTM> observable, ToDoTM oldValue, ToDoTM newValue) {
                txtSelectedText.setDisable(false);
                btnDelete.setDisable(false);
                btnUpdate.setDisable(false);

                txtSelectedText.requestFocus();

                subroot.setVisible(false);


                if(newValue == null){
                    return;
                }

                txtSelectedText.setText(newValue.getDescription());
                id = newValue.getId();

            }
        });
    }

    public void btnAddNewToDoOnAction(ActionEvent actionEvent) {
        subroot.setVisible(true);
        txtTaskName.requestFocus();

        txtSelectedText.setDisable(true);
        btnDelete.setDisable(true);
        btnUpdate.setDisable(true);
    }

    public void lblTaskNameTextChange(InputMethodEvent inputMethodEvent) {

    }

    public void btnLogOutOnAction(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to Log Out..?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();

        if(buttonType.get().equals(ButtonType.YES)){
            Parent parent = FXMLLoader.load(this.getClass().getResource("../view/LoginForm.fxml"));
            Scene scene = new Scene(parent);

            Stage primaryStage = (Stage) this.root.getScene().getWindow();
            primaryStage.setScene(scene);
            primaryStage.setTitle("Login Form");
            primaryStage.centerOnScreen();
        }else{

        }
    }

    public void btnAddToListOnAction(ActionEvent actionEvent) {
        String id = autoGenerate();
        String description = txtTaskName.getText();
        String user_id = lblUId.getText();
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("insert into todo values (?,?,?)");

            preparedStatement.setObject(1,id);
            preparedStatement.setObject(2,description);
            preparedStatement.setObject(3,user_id);

            int i = preparedStatement.executeUpdate();

            System.out.println(i);

            subroot.setVisible(false);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        loadlist();
    }

    public String autoGenerate(){
        Connection connection = DBConnection.getInstance().getConnection();
        String newId = "";

        try {
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("select * from todo order by id desc limit 1");
            boolean isExist = resultSet.next();

            if(isExist){
                String oldId = resultSet.getString(1);
                oldId = oldId.substring(1,oldId.length());
                int intId = Integer.parseInt(oldId);
                intId++;
                if(intId<10){
                    newId = "T00"+intId;
                }
                else if(intId<100){
                    newId = "T0"+intId;
                }
                else {
                    newId = "T"+intId;
                }
            }
            else{
                newId = "T001";
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return newId;
    }

    public void loadlist(){
        ObservableList<ToDoTM> todo = lstToDoList.getItems();

        todo.clear();
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from todo where user_id = ?");
            preparedStatement.setObject(1,LoginFormController.entereduserid);

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                String id = resultSet.getString(1);
                String description = resultSet.getString(2);
                String user_id = resultSet.getString(3);
                ToDoTM todotm = new ToDoTM(id,description,user_id);
                todo.add(todotm);

            }
            lstToDoList.refresh();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void btnUpdateOnAction(ActionEvent actionEvent) {
        String description = txtSelectedText.getText();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("update todo set description = ? where id = ?");

            preparedStatement.setObject(1,description);
            preparedStatement.setObject(2,id);
            preparedStatement.executeUpdate();
            loadlist();
            txtSelectedText.clear();
            txtSelectedText.setDisable(true);
            btnUpdate.setDisable(true);
            btnDelete.setDisable(true);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
        //Connection connection = DBConnection.getInstance().getConnection();


            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to delete this To Do..?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> buttonType = alert.showAndWait();

            if(buttonType.get().equals(ButtonType.YES)){
                Connection connection = DBConnection.getInstance().getConnection();
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement("delete from todo where id = ?");
                    preparedStatement.setObject(1,id);
                    preparedStatement.executeUpdate();

                    loadlist();

                    txtSelectedText.clear();
                    txtSelectedText.setDisable(true);
                    btnDelete.setDisable(true);
                    btnUpdate.setDisable(true);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }



    }
}
