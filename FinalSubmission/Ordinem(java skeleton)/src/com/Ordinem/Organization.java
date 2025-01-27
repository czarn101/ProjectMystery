package com.Ordinem;


import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.text.ParseException;
//import java.util.Date;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
//import java.util.Scanner;
import java.util.*;
import java.io.*;

public class Organization {

    private String email;
    private String password;
    private int organID;
    private SQLConnector sql;

    public void setEmail(String _email,String _pass){
        this.email = _email;
        this.password = _pass;
    }

    public Organization() {
        this.sql = new SQLConnector("acsm_6cac058ec0c0df1", // database name
                "jdbc:mysql://us-cdbr-azure-west-b.cleardb.com:3306/acsm_6cac058ec0c0df1", //connection url
                "bdee9cb0c426b0", // username
                "624cb96a"); // password
    }


    public void printMenu(){
        int choice = 0;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while(true){
            try{
                /*
                System.out.println("----Login Menu----");
                System.out.println("1.Edit Organization");
                System.out.println("2.View My Events");
                System.out.println("3.Create New Event");
                System.out.println("4.Delete Organization");
                System.out.println("5.Log out");
                System.out.print("Enter your choice: ");

                choice =  Integer.parseInt(reader.readLine());
                */
                if(choice == 1){ //not done
                    //edit organization
                    //stop the loop
                    //editOrg(this.email,this.password);

                }
                else if(choice == 2){ //not dont
                    //view events
                    //stop loop
                    System.out.println();
                    printEventMenu();
                }
                else if(choice == 3){ //done
                    //add event
                    //stop loop
                    //addEvent(this.email,this.password);

                }
                else if(choice == 4){
                    //delete org
                    deleteOrg();
                }
                else if(choice == 5){
                    this.sql.close();
                    System.exit(0);
                    break;
                }
                else{
                    System.out.println("Input must be 1,2 or 3\n");
                }
            }catch(Exception e){
                System.out.println("Input must be 1,2 or 3\n");
            }
        }
    }

    public List<String> getTable(String _email, String _password){
        ArrayList<String> org = new ArrayList<String>();

        try{
            this.sql.pst = this.sql.mysql.prepareStatement("SELECT orgName,email,password FROM organizations WHERE email=? AND password=?");
            this.sql.pst.setString(1,_email);
            this.sql.pst.setString(2,_password);
            if(this.sql.runSelect()){
                String[] cols = {"orgName","email","password"};
                //String orgToEdit = this.sql.getColumns(cols);
                org = this.sql.getArrayCol(cols);
                //System.out.println(orgToEdit);
            }
        }catch (Exception e){
            e.printStackTrace();

        }
        return org;
    }

    public boolean editOrg(String _email, String _password,int _indexofCombo, String _NameNew, String _EmailNew, String _PassNew){
        //BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        ArrayList<String> orgNameByType = new ArrayList<String>();
        ArrayList<String> orgEmailByType = new ArrayList<String>();
        int orgID = 0;
        int orgType = 0;

        try{
            orgNameByType.clear();
            orgEmailByType.clear();
            //get the typeID to get all organizations with that type u are editing
            this.sql.pst = this.sql.mysql.prepareStatement("SELECT typeID FROM organizations WHERE email=? AND password=?");
            this.sql.pst.setString(1,_email);
            this.sql.pst.setString(2,_password);
            if(this.sql.runSelect()){
                String tempTypeID = this.sql.printEventData().trim();

                try{
                    orgType = Integer.parseInt(tempTypeID);
                }catch(NumberFormatException e){
                    e.printStackTrace();
                }
            }

            //get that unique primary key to always insert with that id
            this.sql.pst = this.sql.mysql.prepareStatement("SELECT orgID FROM organizations WHERE email=? AND password=?");
            this.sql.pst.setString(1,_email);
            this.sql.pst.setString(2,_password);
            if(this.sql.runSelect()){
                String tempID = this.sql.printEventData().trim();

                try{
                    orgID = Integer.parseInt(tempID);
                }catch(NumberFormatException e){
                    e.printStackTrace();
                }
            }
            //get the organization names by type so u can compare names
            this.sql.pst = this.sql.mysql.prepareStatement("SELECT orgName FROM organizations WHERE typeID = ?");
            this.sql.pst.setInt(1,orgType);
            if(this.sql.runSelect()){
                while(this.sql.data.next()){
                    String orgNameType = this.sql.data.getString(1).toLowerCase();
                    orgNameByType.add(orgNameType);
                }
            }

            this.sql.pst = this.sql.mysql.prepareStatement("SELECT email FROM organizations WHERE typeID = ?");
            this.sql.pst.setInt(1,orgType);
            if(this.sql.runSelect()){
                while(this.sql.data.next()){
                    String orgEmailType = this.sql.data.getString(1).toLowerCase();
                    orgEmailByType.add(orgEmailType);
                }
            }
        }catch(SQLException e){
            System.out.println("SQL went wrong");
        }catch(Exception e){
            e.printStackTrace();
        }


        try{
            if(_indexofCombo == 1){
                //edit name
                if(orgNameByType.contains(_NameNew.toLowerCase())){
                    System.out.println("The name you entered already exists.\n");
                    return false;
                }
                else{
                    //does not contain
                    orgNameByType.add(_NameNew);
                    //edit the database
                    String insert_sql_events = "UPDATE organizations SET orgName = ? WHERE orgID = ?";
                    this.sql.pst = this.sql.mysql.prepareStatement(insert_sql_events);
                    this.sql.pst.setString(1,_NameNew);
                    this.sql.pst.setInt(2,orgID);
                    if(this.sql.runUpdate()){
                        System.out.println("Successfully changed organization name!\n");
                        return true;
                    }

                }

            }else if(_indexofCombo == 2){
                //edit email
                if(orgEmailByType.contains(_EmailNew.toLowerCase())){
                    System.out.println("The email you entered already exists.\n");
                    return false;
                }
                else{
                    //does not contain
                    orgEmailByType.add(_EmailNew);
                    //edit the database
                    String insert_sql_events = "UPDATE organizations SET email = ? WHERE orgID = ?";
                    this.sql.pst = this.sql.mysql.prepareStatement(insert_sql_events);
                    this.sql.pst.setString(1,_EmailNew);
                    this.sql.pst.setInt(2,orgID);
                    if(this.sql.runUpdate()){
                        System.out.println("Successfully changed organization email!\n");
                        return true;
                    }
                }

            }else if(_indexofCombo == 3){
                //edit pass

                String insert_sql_events = "UPDATE organizations SET password = ? WHERE orgID = ?";
                this.sql.pst = this.sql.mysql.prepareStatement(insert_sql_events);
                this.sql.pst.setString(1,_PassNew);
                this.sql.pst.setInt(2,orgID);
                if(this.sql.runUpdate()){
                    System.out.println("Successfully changed organization password!\n");
                    return true;
                }
            }
            else{
                System.out.println("Input must be between 1 and 5!\n");
                return false;
            }
        }catch(SQLException e){
            System.out.println("SQL went wrong!\n");
        }catch(Exception e){
            System.out.println("Input must be an integer!\n");
        }
        return true;

    }

    public void editLive(String _newVal, int _eventID) throws ParseException{
        try{

            String insert_sql_events = "UPDATE events SET isLive = ? WHERE eventID = ?";
            this.sql.pst = this.sql.mysql.prepareStatement(insert_sql_events);
            this.sql.pst.setString(1,_newVal);
            this.sql.pst.setInt(2,_eventID);
            if(this.sql.runUpdate()){
                //System.out.println("Successfully started the event!\n");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void addEvent(String _email, String _password,String _eventName,String _eventDesc, String _eventDate,String _startTime, String _location, int _points, String _isLive) throws ParseException {

        try{
            this.sql.pst = this.sql.mysql.prepareStatement("SELECT orgID FROM organizations WHERE email=? AND password=?");
            this.sql.pst.setString(1,_email);
            this.sql.pst.setString(2,_password);
            this.sql.runSelect();
            int orgID = 0;
            if(this.sql.runSelect()){
                String temp = this.sql.printEventData().trim();
                try{
                    orgID = Integer.parseInt(temp);
                }catch(NumberFormatException e){
                    System.out.println("OrgID must be a number!");
                }

            }

            String insert_sql_events = "INSERT INTO events (eventName,description,hostOrgID,eventDate,startTime,location,pointsForAttending,isLive) " + "VALUES (?,?,?,?,?,?,?,?)";
            this.sql.pst = this.sql.mysql.prepareStatement(insert_sql_events);
            this.sql.pst.setString(1,_eventName);
            this.sql.pst.setString(2,_eventDesc);
            this.sql.pst.setInt(3,orgID);
            this.sql.pst.setString(4,_eventDate);
            this.sql.pst.setString(5,_startTime);
            this.sql.pst.setString(6,_location);
            this.sql.pst.setInt(7,_points);
            this.sql.pst.setString(8, _isLive);

            if(this.sql.runUpdate()){
                AlertBox.display("Success","Successfully Created An Event!");
                //System.out.println("Successfully added an event!\n");
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void deleteOrg(){
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        //display all organizations
        String choice = "";
        int ID = 0;
        boolean isActive = true;
        boolean isOrgID = true;
        try{
            this.sql.pst = this.sql.mysql.prepareStatement("SELECT orgID,orgName,email FROM organizations");
            if(this.sql.runSelect()){
                String[] cols = {"orgID","orgName","email"};
                String orgs = this.sql.getColumns(cols);
                System.out.println(orgs);
            }
        }catch(SQLException e){
            System.out.println("SQL went wrong.");
        }
        catch (Exception e){
            e.printStackTrace();
        }

        while(true){
            try {
                System.out.print("Enter organization ID(type 'goback' to return): ");
                String tempID = reader.readLine();
                if (tempID.toLowerCase().trim().equals("goback")) {
                    break;
                }else{
                    ID = Integer.parseInt(tempID);
                    this.sql.pst = this.sql.mysql.prepareStatement("SELECT orgID,orgName,email FROM organizations where orgID = ?");
                    this.sql.pst.setInt(1,ID);
                    //this.sql.runSelect();
                    if(this.sql.runSelect()){
                        if(this.sql.data.next() == false){
                            System.out.println("Organization not found. Try again\n");
                        }else{
                            String[] cols = {"orgID","orgName","email"};
                            String theOrg2Delete = this.sql.getColumns(cols);
                            System.out.println("Would you like to delete this organization?(y/n): ");
                            choice = reader.readLine();
                            if(choice.toLowerCase().trim().equals("y")){
                                //delete the org
                                this.sql.pst = this.sql.mysql.prepareStatement("DELETE FROM organizations where orgID = ?");
                                this.sql.pst.setInt(1,ID);
                                if(this.sql.runUpdate()){
                                    System.out.println("Successfully deleted organization!");
                                    isActive = false;
                                    break;
                                }
                            }
                            else if(choice.toLowerCase().trim().equals("n")){
                                isActive = false;
                            }
                            else{
                                System.out.println("Input must be either 'y' or 'n'\n");
                            }
                        }
                    }
                    break;
                }
            }catch(NumberFormatException e){
                System.out.println("Input must be an integer\n");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void printEventMenu(){
        int viewChoice = 0;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while(true){
            try{
                System.out.println("----View My Events----");
                System.out.println("1.View All Events");
                System.out.println("2.View Past Events");
                System.out.println("3.View Future Events");
                System.out.println("4.Go Back");
                System.out.print("Enter your choice: ");
                viewChoice = Integer.parseInt(reader.readLine());

                if(viewChoice == 1){
                    //view all events
                    //System.out.println("email: " + this.email + "\npass: " + this.password);
                    getAllEvents(this.email,this.password);
                    //break;
                }
                else if(viewChoice == 2){
                    //view past
                    break;
                }
                else if(viewChoice == 3){
                    //view future
                    break;
                }
                else if(viewChoice == 4){
                    //go back
                    System.out.println();
                    break;
                }
                else{
                    System.out.println("Input must be 1,2 or 3");
                }
            }catch(Exception e){
                System.out.println("Input must be 1,2 or 3\n");
            }
        }
    }

    public List<String> getAllEvents(String _email, String _password){
        ArrayList<String> events = new ArrayList<String>();

        try {
            this.sql.pst = this.sql.mysql.prepareStatement("SELECT orgID FROM organizations WHERE email=? AND password=?");
            this.sql.pst.setString(1,_email);
            this.sql.pst.setString(2,_password);
            //this.sql.runSelect();
            if(this.sql.runSelect()){
                //int orgID = 0;
                //orgID = this.sql.printData();
                String[] cols = {"orgID"};
                String temp = this.sql.getSeperateCol(cols).trim();
                //System.out.println(temp);
                int orgID = Integer.parseInt(temp);
                //System.out.println(Integer.toString(orgID));
                this.organID = orgID;
                this.sql.pst = this.sql.mysql.prepareStatement("SELECT * FROM events WHERE hostOrgID=?");
                this.sql.pst.setInt(1,orgID);
                this.sql.runSelect();
                String[] list = {"eventID","eventName","eventDate","startTime","location","pointsForAttending","isLive"};
                //String colums = this.sql.getColumns(list);
                //System.out.println(colums);

                events = this.sql.getArrayCol(list);
                /*
                for(int i = 0; i < events.size(); ++i){
                    System.out.println(i + "." + events.get(i));
                }
                */

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return events;
    }

    public List<String> getCheckIn(int _eventID){
        ArrayList<String> checkInTable = new ArrayList<String>();

        try{
            this.sql.pst = this.sql.mysql.prepareStatement("SELECT studentID,checkInTime FROM checkins WHERE eventID = ?");
            this.sql.pst.setInt(1,_eventID);

            if(this.sql.runSelect()){
                String[] cols = {"studentID","checkInTime"};
                //String orgToEdit = this.sql.getColumns(cols);
                checkInTable = this.sql.getArrayCol(cols);
                //System.out.println(checkInTable);
            }
        }catch (Exception e){
            e.printStackTrace();

        }
        return checkInTable;


    }

    public void insertID(int _studID, int _eventID){
        ArrayList<String> studentIDs = new ArrayList<String>();
        try{
            //make the checkinID

            this.sql.pst = this.sql.mysql.prepareStatement("SELECT studentID FROM checkins WHERE eventID = ?");
            this.sql.pst.setInt(1,_eventID);

            if(this.sql.runSelect()){
                String[] cols = {"studentID"};
                //String orgToEdit = this.sql.getColumns(cols);
                studentIDs = this.sql.getArrayCol(cols);
                //System.out.println(checkInTable);
            }

            if(studentIDs.contains(Integer.toString(_studID))){
                AlertBox.display("Error!","Student " + _studID + "Is ALREADY Checked In.");
            }

            String temp = Integer.toString(_studID) + Integer.toString(_eventID);

            int _checkinID = Integer.parseInt(temp);
            String insert_sql_events = "INSERT INTO checkins (checkinID,studentID,eventID) " + "VALUES (?,?,?)";
            this.sql.pst = this.sql.mysql.prepareStatement(insert_sql_events);
            this.sql.pst.setInt(1,_checkinID);
            this.sql.pst.setInt(2,_studID);
            this.sql.pst.setInt(3,_eventID);

            if(this.sql.runUpdate()){
                AlertBox.display("Success","Successfully Checked In Student" + _studID + ".");
                //System.out.println("Successfully checked in" + _studID + "!");
            }else{
                AlertBox.display("Error!","Student " + _studID + "Is Not In The Database");
            }

        }catch(Exception e){
            e.printStackTrace();

        }

    }
}

