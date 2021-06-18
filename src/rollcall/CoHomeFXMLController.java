/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rollcall;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.stage.FileChooser;

import rollcall.StageManager;
import rollcall.CloseCourse;

/**
 *
 * @author Han
 */
public class CoHomeFXMLController implements Initializable {
    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement pst = null;
    String user;
    String thcourse;
    String stcourse;
    
    @FXML
    public Button logout;
    public Label name;
    //使用者資料
    public Button userCSV;
    public Button userInput;
    public Label userLabel;
    //教師課程資料
    public Button thCourseCSV;
    public Button thCourseInput;
    public Label thCourseLabel;
    //學生選課資料
    public Button stCourseCSV;
    public Button stCourseInput;
    public Label stCourseLabel;

    
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
        
        //上傳"使用者資料"按鈕(開啟檔案)
        EventHandler<ActionEvent> userButtonHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage fileStage = null; 
                FileChooser fileChooser = new FileChooser();
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV Files", "*.csv");
                fileChooser.getExtensionFilters().add(extFilter);
                File file = fileChooser.showOpenDialog(fileStage);
                if(file != null){
                    String path = file.getAbsolutePath();
                    user = path;
                    userLabel.setText(user);
                }else{
                    System.out.println("No Data");
                }
                
            }
        };
        userCSV.setOnAction(userButtonHandler);
        
        //上傳"教師課程資料"按鈕(開啟檔案)
        EventHandler<ActionEvent> thCourseButtonHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage fileStage = null; 
                FileChooser fileChooser = new FileChooser();
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV Files", "*.csv");
                fileChooser.getExtensionFilters().add(extFilter);
                File file = fileChooser.showOpenDialog(fileStage);
                if(file != null){
                    String path = file.getAbsolutePath();
                    thcourse = path;
                    thCourseLabel.setText(thcourse);
                }else{
                    System.out.println("No Data");
                }
                
            }
        };
        thCourseCSV.setOnAction(thCourseButtonHandler);
        
        //上傳"學生選課資料"按鈕(開啟檔案)
        EventHandler<ActionEvent> stCourseButtonHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage fileStage = null; 
                FileChooser fileChooser = new FileChooser();
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV Files", "*.csv");
                fileChooser.getExtensionFilters().add(extFilter);
                File file = fileChooser.showOpenDialog(fileStage);
                if(file != null){
                    String path = file.getAbsolutePath();
                    stcourse = path;
                    stCourseLabel.setText(stcourse);
                }else{
                    System.out.println("No Data");
                }
                
            }
        };
        stCourseCSV.setOnAction(stCourseButtonHandler);
        
        
        
        
        
        //"匯入使用者資料"按鈕(匯入檔案)
        EventHandler<ActionEvent> userInputButtonHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    BufferedReader br = new BufferedReader(new FileReader(user));
                    String line;
                    conn = mysqlconnect.ConnecrDb();
                    while((line = br.readLine()) != null){
                        line = line.replaceAll("\"", "");
                        String[] value = line.split(",");
                        String sql1 = "INSERT INTO users (authority,department,account,username,password)values('"+value[0]+"','"+value[1]+"','"+value[2]+"','"+value[3]+"','"+value[4]+"')";
                        PreparedStatement ps1 = conn.prepareStatement(sql1);
                        ps1.executeUpdate();
                    }
                    br.close();
                    TimeAlert("使用者資料匯入成功!");
                    Reset();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(CoHomeFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(CoHomeFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }catch (SQLException ex) {
                    Logger.getLogger(CoHomeFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        userInput.setOnAction(userInputButtonHandler);
        
        //"匯入教師課程資料"按鈕(匯入檔案)
        EventHandler<ActionEvent> thCourseInputButtonHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    BufferedReader br = new BufferedReader(new FileReader(thcourse));
                    String line1;
                    conn = mysqlconnect.ConnecrDb();
                    while((line1 = br.readLine()) != null){
                        line1 = line1.replaceAll("\"", "");
                        String[] value = line1.split(",");
                        String sql1 = "INSERT INTO professor (grade,course_code,course_name,account,professor)values('"+value[0]+"','"+value[1]+"','"+value[2]+"','"+value[3]+"','"+value[4]+"')";
                        PreparedStatement ps1 = conn.prepareStatement(sql1);
                        ps1.executeUpdate();
                    }
                    br.close();
                    TimeAlert("教師課程資料匯入成功!");
                    Reset();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(CoHomeFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(CoHomeFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }catch (SQLException ex) {
                    Logger.getLogger(CoHomeFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        thCourseInput.setOnAction(thCourseInputButtonHandler);
        
        //"匯入學生選課資料"按鈕(匯入檔案)
        EventHandler<ActionEvent> stCourseInputButtonHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    BufferedReader br = new BufferedReader(new FileReader(stcourse));
                    String line1;
                    conn = mysqlconnect.ConnecrDb();
                    while((line1 = br.readLine()) != null){
                        line1 = line1.replaceAll("\"", "");
                        String[] value = line1.split(",");
                        String sql1 = "INSERT INTO stucourse (stu_id,course_code,course_name)values('"+value[0]+"','"+value[1]+"','"+value[2]+"')";
                        PreparedStatement ps1 = conn.prepareStatement(sql1);
                        ps1.executeUpdate();
                    }
                    br.close();
                    TimeAlert("學生選課資料匯入成功!");
                    Reset();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(CoHomeFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(CoHomeFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }catch (SQLException ex) {
                    Logger.getLogger(CoHomeFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        stCourseInput.setOnAction(stCourseInputButtonHandler);
    }
    
    //彈跳警示視窗
    public void TimeAlert(String s){
        final Alert alert = new Alert(Alert.AlertType.INFORMATION); // 實體化Alert對話框物件，並直接在建構子設定對話框的訊息類型
        alert.setTitle("小提示"); //設定對話框視窗的標題列文字
        alert.setHeaderText(""); //設定對話框視窗裡的標頭文字。若設為空字串，則表示無標頭
        alert.setContentText(s); //設定對話框的訊息文字
        alert.showAndWait();
    }
    
    //重置頁面
    private void Reset() throws SQLException,IOException{       
        Stage cohome = null; 
        Parent root;
        cohome=(Stage) logout.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("/rollcall/fxml/CoHomeFXML.fxml"));
        Scene sceneLogin = new Scene(root);
        cohome.setScene(sceneLogin);
        cohome.setTitle("Welcome");
        cohome.show();
    }
}    


