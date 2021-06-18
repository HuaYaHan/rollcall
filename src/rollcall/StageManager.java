/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rollcall;


import java.util.HashMap;
import java.util.Map;
import javafx.stage.Stage;

/**
 *
 * @author Han
 */
public class StageManager {
    public static Map<String, String> Data=new HashMap<String, String>();
    public static Map<String, Object> CONTROLLER=new HashMap<String, Object>();
    
    public static Map<String, String> CourseName=new HashMap<String, String>();
    public static Map<String, String> CourseCode=new HashMap<String, String>();
    public static Map<String, String> Classroom=new HashMap<String, String>();
    public static Map<String, String> OpCouresStartTime=new HashMap<String, String>();
    public static Map<String, String> CourseCloseTime=new HashMap<String, String>();
    
    //StRCRecordFXMLController
    public static Map<String, Integer> isTaken=new HashMap<String, Integer>();
    public static Map<String, String> StHasTaken=new HashMap<String, String>();
    
    //ThCheckFXMLController  ThOpRecord=0  ThSearch=1
    public static Map<String, Integer> LastPage=new HashMap<String, Integer>();
    
    //ThAbsenceFXMLController
    public static Map<String, Integer> check=new HashMap<String, Integer>();
    public static Map<String, String> AbsenceID=new HashMap<String, String>();
    
    //ThSearchFXMLController
    public static Map<String, String> SearchCode=new HashMap<String, String>();
}
