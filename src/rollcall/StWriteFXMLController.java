/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rollcall;

import java.io.*;
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
import javafx.event.EventHandler;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;

import rollcall.StageManager;

/**
 *
 * @author Han
 */

//教師開課
public class StWriteFXMLController implements Initializable {
    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement pst = null;
    
    @FXML
    public Button logout;
    public Label name;
    public Button back;
    public Button insertImage;
    public Button last;
    public Label imageLabel;
    @FXML
    public GridPane seatGrid;
    public GridPane inputButton;
    public String s;


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
        Stage sthome = null; 
        Parent root;
        sthome=(Stage) back.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("/rollcall/fxml/StHomeFXML.fxml"));
        Scene sceneLogin = new Scene(root);
        sthome.setScene(sceneLogin);
        sthome.setTitle("Welcome");
        sthome.show();
    }
    
    @FXML
    //"上一頁"按鈕
    private void lastButtonAction(ActionEvent event) throws SQLException,IOException{       
        //登出帳號，回到登入頁面
        Stage lastpage = null; 
        Parent root;
        lastpage=(Stage) last.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("/rollcall/fxml/StCourseFXML.fxml"));
        Scene sceneLogin = new Scene(root);
        lastpage.setScene(sceneLogin);
        lastpage.setTitle("線上點名");
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
        StageManager.CONTROLLER.put("StWriteFXMLController", this);
        String UserName = StageManager.Data.get("UserName");
        String UserAccount = StageManager.Data.get("UserAccount");
        name.setText(UserName);
        
        
        //"上傳圖檔"按鈕(開啟檔案)
        EventHandler<ActionEvent> buttonHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage fileStage = null; 
                FileChooser fileChooser = new FileChooser();
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("All Images", "*.*");
                fileChooser.getExtensionFilters().add(extFilter);
                File file = fileChooser.showOpenDialog(fileStage);
                if(file != null){
                    String path = file.getAbsolutePath();
                    s = path;
                    imageLabel.setText(s);
                }else{
                    System.out.println("No Data");
                }
                
            }
        };
        insertImage.setOnAction(buttonHandler);
        
        
        //先預設所有座位都是空的: StageManager的isTaken全設0
        for(int a = 1; a<=9; a++){
            for(int b = 1; b<=9; b++){
                int total = a*10+b;
                String buttonName = Integer.toString(total);
                StageManager.isTaken.put(buttonName, 0);
            }
        }
        
        //檢查最位表資料庫，如果該座位已有人，該座位的isTaken設1
        try {
            String CourseStartTime = StageManager.OpCouresStartTime.get("CourseStartTime");
            
            conn = mysqlconnect.ConnecrDb();
            String sql = "SELECT account,username,seat FROM rollcall where date_time=?";
            PreparedStatement ptmt = conn.prepareStatement(sql);
            ptmt.setString(1, CourseStartTime);
            ResultSet rs1 = ptmt.executeQuery();
            while (rs1.next()){
                String seatcode = rs1.getString("seat");
                char[] c = seatcode.toCharArray();
                int ones = Character.getNumericValue(c[1]);  
                int tens = Character.getNumericValue(c[0]);  
                addProtoLabel(rs1.getString("username"), rs1.getString("account"), ones, tens);
                StageManager.isTaken.put(seatcode, 1);
            }   
        } catch (SQLException ex) {
            Logger.getLogger(StWriteFXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //如果是空座位(isTaken=0)的話，則加入按鈕
        for(int i = 1; i<=9; i++){
            for(int j = 1; j<=9; j++){
                int total = i*10+j;
                String buttonName = Integer.toString(total);
                try {
                    int taken = StageManager.isTaken.get(buttonName);
                    if(taken == 0){
                        addProtoButton(buttonName,UserName, UserAccount, j, i);
                        //如果座位已添加按鈕，則設isTaken=2，防止重複添加!!!!!!!!!!!!!!!
                        StageManager.isTaken.put(buttonName, 2);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(StWriteFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
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
    
    //添加座位表按鈕
    private void addProtoButton(String simpleName, String stuName, String stuAccount, int ones, int tens) throws SQLException{
        String seatcode = simpleName;
        String userName = stuName;
        String userAccount = stuAccount;
        String coursecode = StageManager.CourseCode.get("CourseCode");
        String CourseStartTime = StageManager.OpCouresStartTime.get("CourseStartTime");
        
        Button button = new Button(seatcode);
        button.setPrefSize(62, 43);
        button.setId("button_" + tens + ones);
        button.getStyleClass().add("setButtonImage");
        button.setOnAction(event -> {
            //讀取該教師"開放中"的課程            
            try {
                int check = 0;
                String sql1 = "SELECT course_code FROM rollcall where account=? and date_time=?";
                PreparedStatement ptmt1 = conn.prepareStatement(sql1);
                ptmt1.setString(1, userAccount);
                ptmt1.setString(2, CourseStartTime);
                ResultSet rs1 = ptmt1.executeQuery();
                while (rs1.next()){
                    check++;
                }
                
                if(check>0){
                    TimeAlert("不可重複點名!");
                }
                else{
                    String sql2 = "SELECT course_code,date_time FROM opcourse where course_code=?";
                    PreparedStatement ptmt2 = conn.prepareStatement(sql2);
                    ptmt2.setString(1, coursecode);
                    ResultSet rs2 = ptmt2.executeQuery();
                    while (rs2.next()){
                        String starttime = rs2.getString("date_time");
                        try {
                            writein(userAccount, userName, coursecode, seatcode, starttime);
                        } catch (IOException ex) {
                            Logger.getLogger(StWriteFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                            System.out.println("格式錯誤B");
                        }
                    
                        //點名成功後跳回首頁
                        TimeAlert("點名成功!");
                        try {
                            Reset();
                        } catch (IOException ex) {
                            Logger.getLogger(StWriteFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }  
            } catch (SQLException ex) {
                System.out.println("格式錯誤A");
            }
        });
        seatGrid.add(button , ones, tens);
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
    
    //將學生選擇的座位資訊寫入資料庫
    public void writein(String userAccount, String userName, String courseCode, String Seat, String startTime) throws SQLException, IOException{
        Connection conn = mysqlconnect.ConnecrDb();
        String insert = "INSERT INTO rollcall (account,username,course_code,seat,date_time)values(?,?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(insert);
        ps.setString(1, userAccount);
        ps.setString(2, userName);
        ps.setString(3, courseCode);
        ps.setString(4, Seat);
        ps.setString(5, startTime);
        ps.executeUpdate();
        
        if(s != null){
            InputStream is = new FileInputStream(new File(s));
            String update = "update rollcall set imageData=? where account=? and date_time=?";
            PreparedStatement ps2 = conn.prepareStatement(update);
            ps2.setBinaryStream(1, is, (int) s.length());
            ps2.setString(2, userAccount);
            ps2.setString(3, startTime);
            ps2.executeUpdate();
            System.out.println("成功");
        }
    }
    
    //回首頁
    private void Reset() throws SQLException,IOException{       
        Stage sthome = null; 
        Parent root;
        sthome=(Stage) back.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("/rollcall/fxml/StHomeFXML.fxml"));
        Scene sceneLogin = new Scene(root);
        sthome.setScene(sceneLogin);
        sthome.setTitle("Welcome");
        sthome.show();
    }
}    



//https://blog.csdn.net/MrChung2016/article/details/71774496 Table教學


