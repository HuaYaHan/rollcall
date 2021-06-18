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
public class LoginFXMLController implements Initializable {
    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement pst = null;
    
    @FXML
    public TextField account;
    public TextField password;
    public Label errorHint;
    public Button login;
    public Button logout;
    public Label title;
    
    @FXML
    private void loginButtonAction(ActionEvent event) throws SQLException,IOException{
        conn = mysqlconnect.ConnecrDb();
        
        String ac = account.getText().toString();
        String pw = password.getText().toString();
        
        String sql="SELECT account,password,authority,username FROM users where account=? and password=?";
        PreparedStatement ptmt=conn.prepareStatement(sql);
        ptmt.setString(1, ac);
        ptmt.setString(2, pw);
        
        ResultSet rs=ptmt.executeQuery();
        
        //從登入使用者給出的賬號密碼來檢測查詢在資料庫表中是否存在相同的賬號密碼
        if(rs.next()){
            String au = rs.getString("authority");
            String name = rs.getString("username");
            
            //將使用者帳號和名字存入Data裡
            StageManager.Data.put("UserAccount", ac);
            StageManager.Data.put("UserName", name);
            
            //檢查權限，權限分為"老師(au=1)"、"學生(au=2)"、"管理員(au=3)"
            if(au.equals("1")){  
                //開啟教師頁面
                Stage teacher = null; 
                Parent rootTh;
                teacher=(Stage) login.getScene().getWindow();
                rootTh = FXMLLoader.load(getClass().getResource("/rollcall/fxml/ThHomeFXML.fxml"));
                Scene ThHome = new Scene(rootTh);
                teacher.setScene(ThHome);
                teacher.setTitle("Welcome");
                teacher.show();
                
                //開啟學生頁面
            }else if(au.equals("2")){
                Stage student = null; 
                Parent rootSt;
                student=(Stage) login.getScene().getWindow();
                rootSt = FXMLLoader.load(getClass().getResource("/rollcall/fxml/StHomeFXML.fxml"));
                Scene StHome = new Scene(rootSt);
                student.setScene(StHome);
                student.setTitle("Welcome");
                student.show();
                
                //開啟管理員頁面
            }else if(au.equals("3")){
                Stage controller = null; 
                Parent rootSt;
                controller=(Stage) login.getScene().getWindow();
                rootSt = FXMLLoader.load(getClass().getResource("/rollcall/fxml/CoHomeFXML.fxml"));
                Scene StHome = new Scene(rootSt);
                controller.setScene(StHome);
                controller.setTitle("Welcome");
                controller.show();
            }
        }else{
            System.out.println("姓名或密碼錯誤！");
            errorHint.setVisible(true);
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
        
        //將本窗口保存到map中
        StageManager.CONTROLLER.put("LoginControl", this);
        
        //將教師"快速查詢"的頁面重置
        StageManager.SearchCode.put("CourseCode", null);
    }

}    



