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
import datetimepicker.DateTimePicker;
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
import javafx.scene.control.ComboBox;
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
public class ThOpRecordFXMLController implements Initializable {
    
    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement pst = null;
    
    @FXML
    public Button logout;
    public Label name;
    public Button back;
    public Button OpenCourse;
    public Button Search;
    @FXML
    public TableView<Record> recordTable;
    public TableColumn<Record, String> courseCol, classroomCol, optimeCol, closetimeCol, statusCol, lookCol, deleteCol;

    
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
    //去"教師開課"頁面按鈕
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
        StageManager.CONTROLLER.put("ThOpRecordFXMLController", this);
        StageManager.LastPage.put("check", 0);
        String UserName = StageManager.Data.get("UserName");
        String UserAccount = StageManager.Data.get("UserAccount");
        name.setText(UserName);
        
        try { 
            Connection conn = mysqlconnect.ConnecrDb();
            //將課程資訊先寫入ArrayList
            List<Record> brList = new ArrayList<>();
            
            //讀取該教師"開放中"的課程
            String sql1 = "SELECT course_name,class,date_time,time,course_code FROM opcourse where professor=?";
            PreparedStatement ptmt1 = conn.prepareStatement(sql1);
            ptmt1.setString(1, UserAccount);
            
            ResultSet rs1 = ptmt1.executeQuery();
            
            while (rs1.next()){
                Record record = new Record(rs1.getString("course_name"), rs1.getString("class"), rs1.getString("date_time"), rs1.getString("time"), "開放中", rs1.getString("course_code"));
                brList.add(record);
            }
            
            //讀取該教師"已結束"的課程
            String sql2 = "SELECT course_name,class,date_time,time,course_code FROM history where professor=?";
            PreparedStatement ptmt2 = conn.prepareStatement(sql2);
            ptmt2.setString(1, UserAccount);
            
            ResultSet rs2 = ptmt2.executeQuery();
            while (rs2.next()){
                Record record = new Record(rs2.getString("course_name"), rs2.getString("class"), rs2.getString("date_time"), rs2.getString("time"), "已結束", rs2.getString("course_code"));
                brList.add(record);
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
        
        lookCol.setCellFactory((col) -> {
            TableCell<Record, String> cell = new TableCell<Record, String>() {

                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    this.setGraphic(null);

                    if (!empty) {
                        Button lookBtn = new Button("查看");
                        this.setGraphic(lookBtn);
                        lookBtn.setOnMouseClicked((me) -> {
                            try {
                                String CourseCode = this.getTableView().getItems().get(this.getIndex()).getCourseCode();
                                String CourseName = this.getTableView().getItems().get(this.getIndex()).getCourse();
                                String Classroom = this.getTableView().getItems().get(this.getIndex()).getClassroom();
                                String OpTime = this.getTableView().getItems().get(this.getIndex()).getOptime();
                                String CloseTime = this.getTableView().getItems().get(this.getIndex()).getClosetime();
                                
                                //把要查看的課程資料存入StageManager
                                StageManager.CourseName.put("check", CourseName);
                                StageManager.CourseCode.put("check", CourseCode);
                                StageManager.Classroom.put("check", Classroom);
                                StageManager.OpCouresStartTime.put("check", OpTime);
                                StageManager.CourseCloseTime.put("check", CloseTime);
                                CheckRollCall();
                            } catch (SQLException ex) {
                                Logger.getLogger(ThOpRecordFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IOException ex) {
                                Logger.getLogger(ThOpRecordFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        });
                    }
                }

            };
            return cell;
        });
        
        deleteCol.setCellFactory((col) -> {
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
                            Button delBtn = new Button("删除");
                            this.setGraphic(delBtn);
                            delBtn.setOnMouseClicked((me) -> {
                                Record clickedRe = this.getTableView().getItems().get(this.getIndex());
                                String coursecode = this.getTableView().getItems().get(this.getIndex()).getCourseCode();
                                try {
                                    deletCourse(coursecode);
                                    Reset();
                                    //System.out.println(coursecode);
                                } catch (SQLException ex) {
                                    Logger.getLogger(ThOpRecordFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (IOException ex) {
                                    Logger.getLogger(ThOpRecordFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            });
                        }
                        else{
                            Button delBtn = new Button("删除");
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
    public void deletCourse(String code) throws SQLException{
        Connection conn = mysqlconnect.ConnecrDb();

        String delete = "Delete FROM opcourse where course_code=?";
        PreparedStatement ps=conn.prepareStatement(delete); 
        ps.setString(1,code);
        ps.executeUpdate();
        System.out.println("已刪除成功");
    }
    
    @FXML
    //重置頁面
    private void Reset() throws SQLException,IOException{       
        Stage reset = null; 
        Parent root;
        reset=(Stage) back.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("/rollcall/fxml/ThOpRecordFXML.fxml"));
        Scene sceneLogin = new Scene(root);
        reset.setScene(sceneLogin);
        reset.setTitle("開課紀錄");
        reset.show();
    }
    
    @FXML
    //"座位點名"按鈕
    private void CheckRollCall() throws SQLException,IOException{       
        Stage checkRollcall = null; 
        Parent root;
        checkRollcall=(Stage) back.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("/rollcall/fxml/ThCheckFXML.fxml"));
        Scene sceneLogin = new Scene(root);
        checkRollcall.setScene(sceneLogin);
        checkRollcall.setTitle("座位點名單");
        checkRollcall.show();
    }
  
}    



//https://blog.csdn.net/MrChung2016/article/details/71774496 Table教學


