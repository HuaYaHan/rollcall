/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rollcall;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.io.IOException;
import java.net.URL;
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
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import rollcall.StageManager;

/**
 *
 * @author Han
 */

//教師開課
public class ThCheckFXMLController implements Initializable {
    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement pst = null;
    
    @FXML
    public Button logout;
    public Label name;
    public Button back;
    public Button absence;
    public Button last;
    public Button renew;
    @FXML
    public GridPane seatGrid;
    public GridPane LabelGrid1;
    public GridPane LabelGrid2;
    public GridPane LabelGrid3;


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
    //去"缺席名單"頁面按鈕
    private void AbsenceButtonAction(ActionEvent event) throws SQLException,IOException{   
        Stage abs= null; 
        Parent root;
        abs=(Stage) absence.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("/rollcall/fxml/ThAbsenceFXML.fxml"));
        Scene sceneLogin = new Scene(root);
        abs.setScene(sceneLogin);
        abs.setTitle("缺席名單");
        abs.show();
    }
    
    @FXML
    //"上一頁"按鈕
    private void lastButtonAction(ActionEvent event) throws SQLException,IOException{       
        //登出帳號，回到登入頁面
        Stage lastpage = null; 
        Parent root;
        lastpage=(Stage) last.getScene().getWindow();
        if(StageManager.LastPage.get("check") == 0){
            root = FXMLLoader.load(getClass().getResource("/rollcall/fxml/ThOpRecordFXML.fxml"));
            Scene sceneLogin = new Scene(root);
            lastpage.setScene(sceneLogin);
            lastpage.setTitle("開課紀錄");
            lastpage.show();
        }else if(StageManager.LastPage.get("check") == 1){
            root = FXMLLoader.load(getClass().getResource("/rollcall/fxml/ThSearchFXML.fxml"));
            Scene sceneLogin = new Scene(root);
            lastpage.setScene(sceneLogin);
            lastpage.setTitle("快速查詢");
            lastpage.show();
        }
    }
    
    @FXML
    //"刷新頁面"按鈕
    private void renewButtonAction(ActionEvent event) throws SQLException,IOException{       
        //登出帳號，回到登入頁面
        Stage re = null; 
        Parent root;
        re=(Stage) renew.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("/rollcall/fxml/ThCheckFXML.fxml"));
        Scene sceneLogin = new Scene(root);
        re.setScene(sceneLogin);
        re.setTitle("座位點名單");
        re.show();
        System.out.println("已刷新");
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
        StageManager.CONTROLLER.put("ThCheckFXMLController", this);
        String UserName = StageManager.Data.get("UserName");
        String UserAccount = StageManager.Data.get("UserAccount");
        name.setText(UserName);
        
        //讀取課程資料
        String CourseCode = StageManager.CourseCode.get("check");
        String CourseName = StageManager.CourseName.get("check");
        String Classroom = StageManager.Classroom.get("check");
        String OpTime = StageManager.OpCouresStartTime.get("check");
        String CloseTime = StageManager.CourseCloseTime.get("check");
        addCourseNameRoom("課程名稱: ", CourseName, 0, 0);
        addCourseNameRoom("教室: ", Classroom, 1, 0);
        addCourseTime("開放點名時間: ", OpTime, 0, 0);
        addCourseTime("點名結束時間: ", CloseTime, 0, 1);
        
        //先預設所有座位都是空的: StageManager的isTaken全設0
        for(int a = 1; a<=9; a++){
            for(int b = 1; b<=9; b++){
                int total = a*10+b;
                String buttonName = Integer.toString(total);
                StageManager.isTaken.put(buttonName, 0);
            }
        }
        
        //計算應到人數&實到人數
        int ExpectedAttendence = 0;
        int ActualAttendence = 0;
        
        //檢查最位表資料庫，如果該座位已有人，該座位的isTaken設1
        try {
            String CourseStartTime = StageManager.OpCouresStartTime.get("check");
            
            conn = mysqlconnect.ConnecrDb();
            String sql = "SELECT account,username,seat FROM rollcall where date_time=?";
            PreparedStatement ptmt1 = conn.prepareStatement(sql);
            ptmt1.setString(1, CourseStartTime);
            ResultSet rs1 = ptmt1.executeQuery();
            while (rs1.next()){
                String seatcode = rs1.getString("seat");
                char[] c = seatcode.toCharArray();
                int ones = Character.getNumericValue(c[1]);  
                int tens = Character.getNumericValue(c[0]);  
                addProtoLabel(rs1.getString("username"), rs1.getString("account"), ones, tens);
                StageManager.isTaken.put(seatcode, 1);
                ExpectedAttendence++;
            }
            addCourseSt("實到人數: ", String.valueOf(ExpectedAttendence), 1, 0);
        } catch (SQLException ex) {
            Logger.getLogger(StWriteFXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
        //System.out.println(ExpectedAttendence);
        
        //用來計算有選該課堂的人數，應到人數。
        try {
            String sq2 = "SELECT stu_id FROM stucourse where course_code=?";
            PreparedStatement ptmt2 = conn.prepareStatement(sq2);
            ptmt2.setString(1, CourseCode);
            ResultSet rs2 = ptmt2.executeQuery();
            while (rs2.next()){
                ActualAttendence++;
            }
            addCourseSt("應到人數: ", String.valueOf(ActualAttendence), 0, 0);
        }catch (SQLException ex) {
            Logger.getLogger(StWriteFXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //彈跳警示視窗
    public void TimeAlert(String s){
        final Alert alert = new Alert(AlertType.INFORMATION); // 實體化Alert對話框物件，並直接在建構子設定對話框的訊息類型
        alert.setTitle("小提示"); //設定對話框視窗的標題列文字
        alert.setHeaderText(""); //設定對話框視窗裡的標頭文字。若設為空字串，則表示無標頭
        alert.setContentText(s); //設定對話框的訊息文字
        alert.showAndWait();
    }
    
    //添加點名學生的座號姓名
    private void addProtoLabel(String simpleName, String simpleAccount, int ones, int tens){
        String name = simpleName;
        String account = simpleAccount;
        
        Label label = new Label(account+name);
        label.setWrapText(true);
        label.setPrefSize(64, 45);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setId("label_" + tens + ones);
        label.setStyle("-fx-background-color: lavender;");
        seatGrid.add(label , ones, tens);
    }
    
    //添加課程名稱和教室
    private void addCourseNameRoom(String title, String input, int ones, int tens){
        Label label = new Label(title+input);
        label.setWrapText(true);
        label.setPrefSize(200, 45);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setId("label_" + tens + ones);
        label.setStyle("-fx-font-weight: bold;");
        label.setTextFill(Color.web("#ffffff"));
        LabelGrid1.add(label , ones, tens);
    }
    
    //添加課程開放和關閉時間
    private void addCourseTime(String title, String input, int ones, int tens){
        Label label = new Label(title+input);
        label.setWrapText(true);
        label.setPrefSize(300, 45);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setId("label_" + tens + ones);
        label.setStyle("-fx-font-weight: bold;");
        label.setTextFill(Color.web("#ffffff"));
        LabelGrid2.add(label , ones, tens);
    }
    
    //添加課程人數
    private void addCourseSt(String title, String input, int ones, int tens){
        Label label = new Label(title+input);
        label.setWrapText(true);
        label.setPrefSize(200, 45);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setId("label_" + tens + ones);
        label.setStyle("-fx-font-weight: bold;");
        label.setTextFill(Color.web("#ffffff"));
        LabelGrid3.add(label , ones, tens);
    }
}    



//https://blog.csdn.net/MrChung2016/article/details/71774496 Table教學


