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
public class ThHomeFXMLController implements Initializable {
    
    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement pst = null;
    
    @FXML
    public Button logout;
    public Label name;
    public Button OpenCourse;
    public Button opRecord;
    public Button Search;
    
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
    //教師開課按鈕
    private void OpenButtonAction(ActionEvent event) throws SQLException,IOException{       
        Stage open = null; 
        Parent root;
        open=(Stage) OpenCourse.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("/rollcall/fxml/ThOpCourseFXML.fxml"));
        Scene sceneLogin = new Scene(root);
        open.setScene(sceneLogin);
        open.setTitle("教師開課");
        open.show();
    }
    
     @FXML
    //開課紀錄按鈕
    private void opRecordButtonAction(ActionEvent event) throws SQLException,IOException{       
        Stage opCourse = null; 
        Parent root;
        opCourse=(Stage) opRecord.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("/rollcall/fxml/ThOpRecordFXML.fxml"));
        Scene sceneLogin = new Scene(root);
        opCourse.setScene(sceneLogin);
        opCourse.setTitle("開課紀錄");
        opCourse.show();
    }
    
     @FXML
    //快速查詢按鈕
    private void SearchButtonAction(ActionEvent event) throws SQLException,IOException{       
        Stage search = null; 
        Parent root;
        search=(Stage) Search.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("/rollcall/fxml/ThSearchFXML.fxml"));
        Scene sceneLogin = new Scene(root);
        search.setScene(sceneLogin);
        search.setTitle("快速查詢");
        search.show();
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
        
        ///將本窗口保存到map中
        StageManager.CONTROLLER.put("ThHomeFXMLController", this);
        //System.out.println(StageManager.Data.get("UserAccount"));
        String UserName = StageManager.Data.get("UserName");
        name.setText(UserName);
    }
    
  
}    


