/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rollcall;

import java.sql.*;
import java.util.Date;
import java.time.LocalDateTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
/**
 *
 * @author Han
 */
public class CloseCourse {
    //Connection conn = mysqlconnect.ConnecrDb();
    public void closeCourse () throws ParseException,SQLException{
        Connection conn = mysqlconnect.ConnecrDb();
        
        //取得目前的日期和時間
        LocalDateTime time = LocalDateTime.now();
        //轉換格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String nowTime = df.format(time);
        
        //開始比較時間大小
        String sql = "SELECT course_code,time FROM opcourse";
        PreparedStatement ptmt = conn.prepareStatement(sql);
        ResultSet rs = ptmt.executeQuery();
        while (rs.next()){
            try { 
                String deadlineTime = rs.getString("time");
                Date date1 = sdf.parse(nowTime);
                Date date2 = sdf.parse(deadlineTime);

                //如果現在時間超過截止時間
                if (date1.compareTo(date2) > 0 || date1.compareTo(date2) == 0){
                    toHistory(rs.getString("course_code"));
                    deletCourse(rs.getString("course_code"));
                    //System.out.println("Date1 時間在 Date2 之後");
                }
            } catch (ParseException ex) {
                System.out.println("格式不正确");
            }catch (Exception e) {
                e.printStackTrace();
            }
            
        }
    }
    
    //將過期開課資訊移至資料庫history
    public void toHistory(String code) throws SQLException{
        Connection conn1 = mysqlconnect.ConnecrDb();
        Connection conn2 = mysqlconnect.ConnecrDb();
        
        String sql = "SELECT course_name,professor,course_code,class,date_time,time FROM opcourse where course_code=?";
        PreparedStatement ptmt = conn1.prepareStatement(sql);
        ptmt.setString(1, code);
        
        ResultSet rs = ptmt.executeQuery();
        while (rs.next()){
            
            //將已過期的開課紀錄放到資料庫history裡
            String insert = "INSERT INTO history (course_name,professor,course_code,class,date_time,time)values(?,?,?,?,?,?)";
            PreparedStatement ps = conn2.prepareStatement(insert);
            ps.setString(1, rs.getString("course_name"));
            ps.setString(2, rs.getString("professor"));
            ps.setString(3, rs.getString("course_code"));
            ps.setString(4, rs.getString("class"));
            ps.setString(5, rs.getString("date_time"));
            ps.setString(6, rs.getString("time"));
            ps.executeUpdate();
            System.out.println("已轉移成功");
        }
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
}
