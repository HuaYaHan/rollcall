/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rollcall;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.*;
import java.text.ParseException;

import rollcall.StageManager;
import rollcall.CloseCourse;

/**
 *
 * @author Han
 */
public class StHomeFXMLController implements Initializable {
    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement pst = null;
    //private String tranDataToHome;
    
    @FXML
    public Button logout;
    public Label name;
    public Button course;
    public Button RCRecord;
    
    @FXML
    //登出按鈕
    private void logoutButtonAction(ActionEvent event) throws SQLException,IOException{
        //登出帳號，回到登入頁面
        Stage login = null; 
        Parent root;
        login=(Stage) logout.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("/rollcall/fxml/LoginFXML.fxml"));
        Scene sceneLogin = new Scene(root);
        login.setScene(sceneLogin);
        login.setTitle("登入");
        login.show();
    }
    
    @FXML
    //"線上點名"按鈕
    private void MyCourseButtonAction(ActionEvent event) throws SQLException,IOException{
        //進入我的課程頁面
        Stage mycourse = null; 
        Parent root;
        mycourse=(Stage) course.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("/rollcall/fxml/StCourseFXML.fxml"));
        Scene sceneLogin = new Scene(root);
        mycourse.setScene(sceneLogin);
        mycourse.setTitle("線上點名");
        mycourse.show();
    }
    
    @FXML
    //"點名紀錄"按鈕
    private void RCRecordButtonAction(ActionEvent event) throws SQLException,IOException{
        //進入學生點名紀錄頁面
        Stage record = null; 
        Parent root;
        record=(Stage) RCRecord.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("/rollcall/fxml/StRCRecordFXML.fxml"));
        Scene sceneLogin = new Scene(root);
        record.setScene(sceneLogin);
        record.setTitle("點名紀錄");
        record.show();
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //檢查是否有點名已結束，進行資料轉移
        CloseCourse close = new CloseCourse();
        try { 
            close.closeCourse();
        } catch (ParseException ex) {
            System.out.println("格式不正确1");
        } catch (SQLException ex) {
            System.out.println("格式不正确2");
        }
        
        //將本窗口保存到map中
        StageManager.CONTROLLER.put("StHomeFXMLController", this);
        String UserName = StageManager.Data.get("UserName");
        name.setText(UserName);
    }
}    


