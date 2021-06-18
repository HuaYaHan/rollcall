/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rollcall;

import java.sql.*;
public class mysqlconnect {
    Connection conn = null;

    public static Connection ConnecrDb() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/user","root","test");
            
            return conn;
        } catch(Exception e){
            System.out.println(e);
            return null;
        }
    }
}