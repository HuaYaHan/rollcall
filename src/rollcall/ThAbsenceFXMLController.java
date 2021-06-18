/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rollcall;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.*;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.cell.PropertyValueFactory;

import rollcall.StageManager;

/**
 *
 * @author Han
 */

//教師開課
public class ThAbsenceFXMLController implements Initializable {
    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement pst = null;
    
    @FXML
    public Button logout;
    public Label name;
    public Button back;
    public Button last;
    public Label CourseTime;
    @FXML
    public TableView<Absence> table;
    public TableColumn<Absence, String> countCol, stuidCol, nameCol, departmentCol;
    

    @FXML
    //"登出"按鈕
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
    //回"首頁"按鈕
    private void backButtonAction(ActionEvent event) throws SQLException,IOException{   
        StageManager.SearchCode.put("CourseCode", null);
        Stage thhome = null; 
        Parent root;
        thhome=(Stage) back.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("/rollcall/fxml/ThHomeFXML.fxml"));
        Scene sceneLogin = new Scene(root);
        thhome.setScene(sceneLogin);
        thhome.setTitle("Welcome");
        thhome.show();
    }
    
    @FXML
    //"上一頁"按鈕
    private void lastButtonAction(ActionEvent event) throws SQLException,IOException{       
        //登出帳號，回到登入頁面
        Stage lastpage = null; 
        Parent root;
        lastpage=(Stage) last.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("/rollcall/fxml/ThCheckFXML.fxml"));
        Scene sceneLogin = new Scene(root);
        lastpage.setScene(sceneLogin);
        lastpage.setTitle("座位點名單");
        lastpage.show();
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
        StageManager.CONTROLLER.put("ThAbsenceFXMLController", this);
        String UserName = StageManager.Data.get("UserName");
        String UserAccount = StageManager.Data.get("UserAccount");
        name.setText(UserName);
        
        //讀取課程資料
        String CourseCode = StageManager.CourseCode.get("check");
        String CourseName = StageManager.CourseName.get("check");
        String Classroom = StageManager.Classroom.get("check");
        String OpTime = StageManager.OpCouresStartTime.get("check");
        String CloseTime = StageManager.CourseCloseTime.get("check");
        CourseTime.setText(CourseName+" ("+CloseTime+")");
        
        //先預設有修這門課的學生為0
        try {
            conn = mysqlconnect.ConnecrDb();
            String sql0 = "SELECT account FROM users where authority=2";
            PreparedStatement ptmt0 = conn.prepareStatement(sql0);
            ResultSet rs0 = ptmt0.executeQuery();
            while (rs0.next()){
                StageManager.check.put(rs0.getString("account"), 0);
            }
        } catch (SQLException ex) {
            Logger.getLogger(StWriteFXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //已點完名的同學設為1
        try {
            conn = mysqlconnect.ConnecrDb();
            String sql1 = "SELECT account FROM rollcall where course_code=? and date_time=?";
            PreparedStatement ptmt1 = conn.prepareStatement(sql1);
            ptmt1.setString(1, CourseCode);
            ptmt1.setString(2, OpTime);
            ResultSet rs1 = ptmt1.executeQuery();
            while (rs1.next()){
                StageManager.check.put(rs1.getString("account"), 1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(StWriteFXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //將缺席的人的ID記起來
        try {
            conn = mysqlconnect.ConnecrDb();
            String sql2 = "SELECT stu_id FROM stucourse where course_code=?";
            PreparedStatement ptmt2 = conn.prepareStatement(sql2);
            ptmt2.setString(1, CourseCode);
            ResultSet rs2 = ptmt2.executeQuery();
            while (rs2.next()){
                String StuID = rs2.getString("stu_id");
                if(StageManager.check.get(StuID) != 1){
                    StageManager.AbsenceID.put(rs2.getString("stu_id"), rs2.getString("stu_id"));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(StWriteFXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //計算項次
        int count = 1; 
        
        //把缺席的學生資訊放入列表
        try {
            List<Absence> brList = new ArrayList<>();
            conn = mysqlconnect.ConnecrDb();
            String sql3 = "SELECT department,account,username FROM users where authority=2";
            PreparedStatement ptmt3 = conn.prepareStatement(sql3);
            ResultSet rs3 = ptmt3.executeQuery();
            while (rs3.next()){
                String AbsenceID = StageManager.AbsenceID.get(rs3.getString("account"));
                if(rs3.getString("account").equals(AbsenceID)){
                    Absence absence = new Absence(count, rs3.getString("account"), rs3.getString("username"), rs3.getString("department"));
                    brList.add(absence);
                    count++;
                }
            }
            //把ArrayList裡所有課程資訊一次寫入TableView裡
            this.showAbsenceTable(this.getAbsenceData(brList));
        } catch (SQLException ex) {
            Logger.getLogger(StWriteFXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //獲得Absence(缺席的學生)的資料
    public ObservableList<Absence> getAbsenceData(List<Absence> brList) {
        ObservableList<Absence> AbsenceList = FXCollections.observableArrayList(brList);
        return AbsenceList;
    }
    
    //顯示缺席表單
    public void showAbsenceTable(ObservableList<Absence> AbsenceList) throws SQLException{
        countCol.setCellValueFactory(new PropertyValueFactory<>("count"));
        stuidCol.setCellValueFactory(new PropertyValueFactory<>("stu_id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("stu_name"));
        departmentCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        table.setItems(AbsenceList);        
    }
    
    //彈跳警示視窗
    public void TimeAlert(String s){
        final Alert alert = new Alert(AlertType.INFORMATION); // 實體化Alert對話框物件，並直接在建構子設定對話框的訊息類型
        alert.setTitle("小提示"); //設定對話框視窗的標題列文字
        alert.setHeaderText(""); //設定對話框視窗裡的標頭文字。若設為空字串，則表示無標頭
        alert.setContentText(s); //設定對話框的訊息文字
        alert.showAndWait();
    }
}    



//https://blog.csdn.net/MrChung2016/article/details/71774496 Table教學


