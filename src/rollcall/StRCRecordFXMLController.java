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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.*;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

import rollcall.StageManager;
import rollcall.CloseCourse;
import rollcall.Record;

/**
 *
 * @author Han
 */

//教師開課
public class StRCRecordFXMLController implements Initializable {
    
    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement pst = null;
    
    @FXML
    public Button logout;
    public Label name;
    public Button back;
    public Button course;

    @FXML
    public TableView<Record> recordTable;
    public TableColumn<Record, String> courseCol, classroomCol, optimeCol, closetimeCol, statusCol, rollcallCol, reviseCol;

    
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
        StageManager.CONTROLLER.put("StRCRecordFXMLController", this);
        String UserName = StageManager.Data.get("UserName");
        String UserAccount = StageManager.Data.get("UserAccount");
        name.setText(UserName);
        
        try { 
            Connection conn = mysqlconnect.ConnecrDb();
            //將課程資訊先寫入ArrayList
            List<Record> brList = new ArrayList<>();
            
            //讀取學生有選的課，並將course code放入StageManager裡
            String sql = "SELECT course_code FROM stucourse where stu_id=?";
            PreparedStatement ptmt1 = conn.prepareStatement(sql);
            ptmt1.setString(1, UserAccount);
            ResultSet rs1 = ptmt1.executeQuery();
            while (rs1.next()){
                String CourseCode = rs1.getString("course_code");
                StageManager.CourseCode.put(CourseCode, CourseCode);
            }
            
            //檢查學生是否已點名
            String sq2 = "SELECT date_time FROM rollcall where account=?";
            PreparedStatement ptmt2 = conn.prepareStatement(sq2);
            ptmt2.setString(1, UserAccount);
            ResultSet rs2 = ptmt2.executeQuery();
            while (rs2.next()){
                String hasTaken = rs2.getString("date_time");
                StageManager.StHasTaken.put(hasTaken, hasTaken);
            }
            
            //讀取"開放中"的課程
            String sql11 = "SELECT course_name,class,date_time,time,course_code FROM opcourse";
            PreparedStatement ptmt11 = conn.prepareStatement(sql11);
            ResultSet rs11 = ptmt11.executeQuery();
            while (rs11.next()){
                String course1 = StageManager.CourseCode.get(rs11.getString("course_code"));
                if(rs11.getString("course_code").equals(course1)){
                    Record record = new Record(rs11.getString("course_name"), rs11.getString("class"), rs11.getString("date_time"), rs11.getString("time"), "開放中", rs11.getString("course_code"));
                    brList.add(record);
                } 
            }
            
            //讀取"已結束"的課程
            String sql12 = "SELECT course_name,class,date_time,time,course_code FROM history";
            PreparedStatement ptmt12 = conn.prepareStatement(sql12);
            ResultSet rs12 = ptmt12.executeQuery();
            while (rs12.next()){
                String course2 = StageManager.CourseCode.get(rs12.getString("course_code"));
                if(rs12.getString("course_code").equals(course2)){
                    Record record = new Record(rs12.getString("course_name"), rs12.getString("class"), rs12.getString("date_time"), rs12.getString("time"), "已結束", rs12.getString("course_code"));
                    brList.add(record);
                }
            }
            //把ArrayList裡所有課程資訊一次寫入TableView裡
            this.showRecordTable(this.getRecordData(brList));
         }
         catch (SQLException e){
             e.printStackTrace();
         }
        
    }
    
    
    //獲得Record(開課紀錄)的資料
    public ObservableList<Record> getRecordData(List<Record> brList) {
        ObservableList<Record> RecordList = FXCollections.observableArrayList(brList);
        return RecordList;
    }
    
    
    //顯示開課紀錄表單
    public void showRecordTable(ObservableList<Record> RecordList) throws SQLException{
        
        courseCol.setCellValueFactory(new PropertyValueFactory<>("course"));
        classroomCol.setCellValueFactory(new PropertyValueFactory<>("classroom"));
        optimeCol.setCellValueFactory(new PropertyValueFactory<>("optime"));
        closetimeCol.setCellValueFactory(new PropertyValueFactory<>("closetime"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        rollcallCol.setCellFactory((col) -> {
            TableCell<Record, String> cell = new TableCell<Record, String>() {

                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    this.setGraphic(null);

                    if (!empty) {
                        String courseCodeDate = this.getTableView().getItems().get(this.getIndex()).getOptime();
                        String StHasTaken = StageManager.StHasTaken.get(courseCodeDate);
                        if (courseCodeDate.equals(StHasTaken)) {
                            this.setText("已點名");
                            //this.getStyleClass().add("mark");
                        }
                        else{
                            this.setText("未點名");
                            this.getStyleClass().add("redMark"); 
                        }
                    }
                }

            };
            return cell;
        });
        
        reviseCol.setCellFactory((col) -> {
            TableCell<Record, String> cell = new TableCell<Record, String>() {

                @Override
                public void updateItem(String item, boolean empty){
                    super.updateItem(item, empty);
                    this.setText(null);
                    this.setGraphic(null);

                    if (!empty) {
                        String status = this.getTableView().getItems().get(this.getIndex()).getStatus();
                        
                        //如果課程正在"開放中"，教師可選擇是否刪除。若"已結束"，則不可刪除
                        if(status == "開放中"){
                            Button reviseBtn = new Button("修改");
                            this.setGraphic(reviseBtn);
                            reviseBtn.setOnMouseClicked((me) -> {
                                Record clickedRe = this.getTableView().getItems().get(this.getIndex());
                                String classroom = this.getTableView().getItems().get(this.getIndex()).getClassroom();
                                String coursecode = this.getTableView().getItems().get(this.getIndex()).getCourseCode();
                                String coursestarttime = this.getTableView().getItems().get(this.getIndex()).getOptime();
                                String UserAccount = StageManager.Data.get("UserAccount");
                                String hasTaken = classroom + coursestarttime;
                                try {
                                    StageManager.OpCouresStartTime.put("CourseStartTime", coursestarttime);
                                    StageManager.CourseCode.put("CourseCode", coursecode);
                                    deletCourse(coursecode,coursestarttime,UserAccount);
                                    StageManager.StHasTaken.put(hasTaken, "0");
                                    //System.out.println(StageManager.StHasTaken.get(hasTaken));
                                    TimeAlert("已刪除點名紀錄，請重新填寫!");
                                    StartRollcall();
                                } catch (SQLException ex) {
                                    Logger.getLogger(StRCRecordFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (IOException ex) {
                                    Logger.getLogger(StRCRecordFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            });
                        }
                        else{
                            Button delBtn = new Button("修改");
                            this.setGraphic(delBtn);
                            delBtn.setDisable(true);
                        }
                    }
                }
            };
            return cell;
        });
        
        recordTable.setItems(RecordList);
        
    }
    
    
    //彈跳警示視窗
    public void TimeAlert(String s){
        final Alert alert = new Alert(AlertType.INFORMATION); // 實體化Alert對話框物件，並直接在建構子設定對話框的訊息類型
        alert.setTitle("小提示"); //設定對話框視窗的標題列文字
        alert.setHeaderText(""); //設定對話框視窗裡的標頭文字。若設為空字串，則表示無標頭
        alert.setContentText(s); //設定對話框的訊息文字
        alert.showAndWait();
    }
    
    //刪除資料庫opcourse裡的資料
    public void deletCourse(String code, String coursestarttime, String UserAccount) throws SQLException{
        Connection conn = mysqlconnect.ConnecrDb();

        String delete = "Delete FROM rollcall where course_code=? and date_time=? and account=?";
        PreparedStatement ps=conn.prepareStatement(delete); 
        ps.setString(1,code);
        ps.setString(2,coursestarttime);
        ps.setString(3,UserAccount);
        ps.executeUpdate();
        System.out.println("已刪除成功");
    }
    
    @FXML
    //"線上點名"按鈕
    private void StartRollcall() throws SQLException,IOException{       
        Stage mycourse = null; 
        Parent root;
        mycourse=(Stage) back.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("/rollcall/fxml/StWriteFXML.fxml"));
        Scene sceneLogin = new Scene(root);
        mycourse.setScene(sceneLogin);
        mycourse.setTitle("座位點名");
        mycourse.show();
    }
  
}    



//https://blog.csdn.net/MrChung2016/article/details/71774496 Table教學


