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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.*;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;


import rollcall.StageManager;
import rollcall.CloseCourse;

/**
 *
 * @author Han
 */
public class StCourseFXMLController implements Initializable {
    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement pst = null;
    //private String tranDataToHome;
    
    @FXML
    public Button logout;
    public Label name;
    public GridPane buttonGrid;
    public Button back;
    
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
    //回首頁按鈕
    private void backButtonAction(ActionEvent event) throws SQLException,IOException{       
        Stage sthome = null; 
        Parent root;
        sthome=(Stage) back.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("/rollcall/fxml/StHomeFXML.fxml"));
        Scene sceneLogin = new Scene(root);
        sthome.setScene(sceneLogin);
        sthome.setTitle("Welcome");
        sthome.show();
    }
    
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
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
        StageManager.CONTROLLER.put("StCourseFXMLController", this);
        String UserName = StageManager.Data.get("UserName");
        String UserAccount = StageManager.Data.get("UserAccount");
        name.setText(UserName);
        
        int countCourse = 0;

        try { 
            Connection conn = mysqlconnect.ConnecrDb();
            
            //讀取該學生這學期所選的課程
            String sql1 = "SELECT course_name,course_code FROM stucourse where stu_id=?";
            PreparedStatement ptmt1 = conn.prepareStatement(sql1);
            ptmt1.setString(1, UserAccount);
            ResultSet rs1 = ptmt1.executeQuery();
            List<String> StuCoursecode = new ArrayList<String>();
            List<String> StuCoursename = new ArrayList<String>();
            while(rs1.next()){
                StuCoursecode.add(rs1.getString("course_code"));
                StuCoursename.add(rs1.getString("course_name"));
            }
            
            //讀取教師正在開放的課程
            String sql2 = "SELECT course_name,course_code FROM opcourse ";
            PreparedStatement ptmt2 = conn.prepareStatement(sql2);
            ResultSet rs2 = ptmt2.executeQuery();
            List<String> OpCoursecode = new ArrayList<String>();
            List<String> OpCoursename = new ArrayList<String>();
            while(rs2.next()){
                OpCoursecode.add(rs2.getString("course_code"));
                OpCoursename.add(rs2.getString("course_name"));
            }
            
            //找出學生有選的課和正在開課中的課(課程代碼)
            Collection notexistsCode = new ArrayList<String>(OpCoursecode);
            Collection existsCode = new ArrayList<String>(OpCoursecode);
            notexistsCode.removeAll(StuCoursecode);
            existsCode.removeAll(notexistsCode);
            List<String> showCode = new ArrayList<String>(existsCode);
            //找出學生有選的課和正在開課中的課(課程名稱)
            Collection notexistsName = new ArrayList<String>(OpCoursename);
            Collection existsName = new ArrayList<String>(OpCoursename);
            notexistsName.removeAll(StuCoursename);
            existsName.removeAll(notexistsName);
            List<String> showName = new ArrayList<String>(existsName);
            //System.out.println(showCode);
            
            
            //插入Button
            for(int i = 0;i<showCode.size();i++){
                String CourseCode = showCode.get(i);
                String CourseName = showName.get(i);
                addProtoButton(CourseName, countCourse, CourseCode);
                countCourse++;
            }
        }
         catch (SQLException e){
             e.printStackTrace();
        }
    }
    
    private void addProtoButton(String simpleName, int order, String code) throws SQLException{
        String name = simpleName;
        String coursecode = code;
        
        Connection conn = mysqlconnect.ConnecrDb();
        String sql = "SELECT class,date_time FROM opcourse where course_code=?";
        PreparedStatement ptmt = conn.prepareStatement(sql);
        ptmt.setString(1, coursecode);
        ResultSet rs = ptmt.executeQuery();
        while (rs.next()){
            StageManager.OpCouresStartTime.put("CourseStartTime", rs.getString("date_time"));
        }
        
        Button button = new Button(name);
        button.setPrefSize(187, 40);
        button.getStyleClass().add("btn-basic");
        button.setStyle("-fx-pref-height: 40px;;");
        button.setId("button_" + order + 1);
        //System.out.println("button_" + order + 1);
        button.setOnAction(event -> {
            try {
                //TODO 按鈕事件
                StageManager.CourseCode.put("CourseCode", coursecode);
                StartRollcall(button);
            } catch (SQLException ex) {
                Logger.getLogger(StCourseFXMLController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(StCourseFXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        buttonGrid.add(button , 1, order + 1);
    }
    
    @FXML
    //"座位點名"按鈕
    private void StartRollcall(Button start) throws SQLException,IOException{       
        Stage startRollcall = null; 
        Parent root;
        startRollcall=(Stage) start.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("/rollcall/fxml/StWriteFXML.fxml"));
        Scene sceneLogin = new Scene(root);
        startRollcall.setScene(sceneLogin);
        startRollcall.setTitle("座位點名");
        startRollcall.show();
    }
}    


