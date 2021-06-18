/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rollcall;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import datetimepicker.DateTimePicker;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Date;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import rollcall.StageManager;
import rollcall.CloseCourse;

/**
 *
 * @author Han
 */

//教師開課
public class ThOpCourseFXMLController implements Initializable {
    
    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement pst = null;
    
    @FXML
    public Button logout;
    public Label name;
    public Button back;
    public Button opRecord;
    public Button Search;
    public ComboBox course;
    public ComboBox classroom;
    public DateTimePicker deadline;
    
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
    //去"開課紀錄"頁面按鈕
    private void opRecordButtonAction(ActionEvent event) throws SQLException,IOException{       
        Stage record = null; 
        Parent root;
        record=(Stage) opRecord.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("/rollcall/fxml/ThOpRecordFXML.fxml"));
        Scene sceneLogin = new Scene(root);
        record.setScene(sceneLogin);
        record.setTitle("開課紀錄");
        record.show();
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
    
    @FXML
    //開放點名按鈕
    private void startButtonAction(ActionEvent event) throws SQLException,IOException{       
        //教師所選擇的課程名稱、教室、點名截止時間
        String coursenameSelect = (String)course.getValue();
        String classroomSelect = (String)classroom.getValue();
        String deadlineSelect = deadline.getDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        //教師所開課程的課程代碼
        CourseCode();
        String coursecodeSelect = StageManager.CourseCode.get("CourseCode");
        String professorSelect = StageManager.Data.get("UserAccount"); 
        
        //取得目前的日期和時間
        LocalDateTime time = LocalDateTime.now();
        //轉換格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String nowTime = df.format(time);
        
        //檢查開課時間是否錯誤
        try {
            Date date1 = sdf.parse(nowTime);
            Date date2 = sdf.parse(deadlineSelect);
            
            look:  //標籤，以方便跳出迴圈
            if (coursenameSelect == null || classroomSelect == null || deadlineSelect == null ){
                TimeAlert("請確認是否完整填寫"); 
            }
            //如果現在時間超過截止時間
            else if (date1.compareTo(date2) > 0 || date1.compareTo(date2) == 0){
                TimeAlert("截止時間有誤!"); 
            }
            else{
               //檢查該課程是否正在開放點名
                conn = mysqlconnect.ConnecrDb();
                String opCourse="SELECT course_code FROM opcourse";
                PreparedStatement ptmt = conn.prepareStatement(opCourse);
                ResultSet rs = ptmt.executeQuery();
                while (rs.next()){
                    String hasOp = rs.getString("course_code");
                    if(hasOp.equals(coursecodeSelect)){
                        TimeAlert("本課程已開放點名中!");
                        break look; //跳出迴圈
                    }
                }
                
                //將教師開課資訊寫入資料庫
                String insert = "INSERT INTO opcourse (course_name,professor,course_code,class,time)values(?,?,?,?,?)";
                PreparedStatement ps=conn.prepareStatement(insert);
                ps.setString(1, coursenameSelect);
                ps.setString(2, professorSelect);
                ps.setString(3, coursecodeSelect);
                ps.setString(4, classroomSelect);
                ps.setString(5, deadlineSelect);
                ps.executeUpdate();
                               
                //開課成功後跳回首頁
                TimeAlert("開放成功!");
                Stage thhome = null; 
                Parent root;
                thhome=(Stage) back.getScene().getWindow();
                root = FXMLLoader.load(getClass().getResource("/rollcall/fxml/ThHomeFXML.fxml"));
                Scene sceneLogin = new Scene(root);
                thhome.setScene(sceneLogin);
                thhome.setTitle("Welcome");
                thhome.show();
            }
        } catch (ParseException ex) {
            System.out.println("格式不正确");
        }catch (Exception e) {
            e.printStackTrace();
        }
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
        StageManager.CONTROLLER.put("ThOpCourseFXMLController", this);
        String UserName = StageManager.Data.get("UserName");
        String UserAccount = StageManager.Data.get("UserAccount");
        name.setText(UserName);
        
        try { 
            Connection conn = mysqlconnect.ConnecrDb();
            
            //讀取該教師負責的課程
            String sql1 = "SELECT account,course_name FROM professor where account=?";
            PreparedStatement ptmt1 = conn.prepareStatement(sql1);
            ptmt1.setString(1, UserAccount);
        
            ResultSet rs1 = ptmt1.executeQuery();
            while (rs1.next()){
                String course = rs1.getString("course_name");
                Course(course);
             }
 
            //讀取所有教室
            String sql2="SELECT all_class FROM class";
            PreparedStatement ptmt2 = conn.prepareStatement(sql2);
            ResultSet rs2 = ptmt2.executeQuery();
            while (rs2.next()){
                String classroom = rs2.getString("all_class");
                ClassRoom(classroom);
             }
         }
         catch (SQLException e){
             e.printStackTrace();
         }
    }
    
    //將該教師開的課程名稱放入course combobox
    public void Course(String cn){
        course.getItems().addAll(cn);
        course.setEditable(true); 
    }
    
    //將該教師的開課教室放入classroom combobox
    public void ClassRoom(String cr){
        classroom.getItems().addAll(cr);
        classroom.setEditable(true); 
    }
    
    //教師所開課程的課程代碼
    public void CourseCode() throws SQLException{
        Connection conn = mysqlconnect.ConnecrDb();
        
        String sql = "SELECT course_name,course_code FROM professor where course_name=?";
        PreparedStatement ptmt = conn.prepareStatement(sql);
        ptmt.setString(1, (String)course.getValue());
        
        ResultSet rs = ptmt.executeQuery();
        while (rs.next()){
            String coursecode = rs.getString("course_code");
            StageManager.CourseCode.put("CourseCode", coursecode);
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
  
}    


