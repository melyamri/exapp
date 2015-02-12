

package com.mvm.exapp.externalDB;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.mvm.exapp.model.ExappAppointment;
import com.mvm.exapp.model.ExappAppointmentState;
import com.mvm.exapp.model.ExappDiscussion;
import com.mvm.exapp.model.ExappDiscussionBoard;
import com.mvm.exapp.model.ExappGroup;
import com.mvm.exapp.model.ExappUser;

import android.graphics.Bitmap;
import android.os.AsyncTask;


public abstract class DBInterface extends AsyncTask<Object, Void, Object>{
	public static String DriverName = "com.mysql.jdbc.Driver";
	
	//Main database:
	public static String urlDB = "jdbc:mysql://sql3.freemysqlhosting.net:3306/sql340113";	//?connectTimeout=10000";
	public static String userDB = "[main database name]";
	public static String passwordDB = "[password]";
	
	//Secondary Database. Only stores images.
	public static String urlFilesDB = "jdbc:mysql://sql3.freemysqlhosting.net:3306/sql345669";	//?connectTimeout=10000";
	public static String userFilesDB = "[image database name]";
	public static String passwordFilesDB = "[password]";

	
	public Date getDate() {
		Connection con = null;
		Statement s = null;
		ResultSet rs = null;
		Date fecha = null;

		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			s = con.createStatement();
			rs = s.executeQuery("SELECT sysdate() FROM DUAL");
			if (rs.next()) {
				fecha = rs.getDate(1);
			} // fin while
			return fecha;
		} catch (SQLException | ClassNotFoundException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
				if (s != null)
					s.close();
			} catch (Exception e1) {
			}
		}
		return fecha;
	}
	
	//Image methods (images are stored in a different database)
	public abstract int uploadNewPicture(Bitmap bm);	//DONE
	public abstract Bitmap getPicture(int imgID);	//DONE
	
	//login
	public abstract ExappUser login(String user, String password); //DONE
	public abstract ExappUser getProfile(String user);	//DONE
	public abstract ExappUser insertOrUpdateUser(ExappUser u, boolean newImage, Bitmap bm); //DONE
	
	// Checkers
	public abstract boolean checkUserExists(String u);	//DONE
	public abstract boolean checkGroupExists(String g);	//DONE
	public abstract boolean checkAppointmentExists(int a);	//DONE
	public abstract boolean checkUserBelongsToGroup(String user, String group);	//DONE
	
	// Global methods
	public abstract ArrayList<String> getAllUsers();	//DONE
	public abstract ArrayList<String> getAllSubjects();	//DONE
	public abstract ArrayList<ExappGroup> getAllGroups();	//DONE
	
	// Global "getters"
	public abstract ArrayList<ExappGroup> getAllGroupsByUser(String user);	//DONE
	public abstract ArrayList<ExappAppointment> getAllAppointmentsByUser(String user); 	//DONE
	public abstract int getAllDiscussionsByUser(String user);	//DONE
	public abstract ArrayList<ExappGroup> getGroupsBySubject(String subject);	//DONE
	public abstract ArrayList<ExappGroup> getInvitations(String user);	//DONE
	
	// Appointments related methods
	public abstract  String getGroupOfAppointment(int appointmentID);
	public abstract boolean setAppointment(ExappAppointment a);	//DONE
	public abstract boolean acceptAppointment(int appointmentID, String userID, boolean accept);	//DONE
	public abstract boolean deleteAppointment(int appointment);		//DONE
	public abstract ExappAppointment getAppointmentById(int id);	//DONE
	public abstract ArrayList<ExappAppointmentState> getUsersAppointmentState(int appointment); 	//DONE
	public abstract ArrayList<ExappAppointment> getFutureAppointments(String user, Date date);	//DONE
	public abstract boolean hasUserAcceptedAppointment(String user, int appointmentID);	//DONE
		
	// Groups related methods
	public abstract String getFounder(String group);	//DONE
	public abstract ExappGroup getGroupById(String group);	//DONE
	public abstract boolean joinGroup(String user, String group);	//DONE
	public abstract boolean quitGroup(String user, String group);	//DONE
	public abstract String createOrUpdateGroupByUser (ExappUser u, ExappGroup group);	//DONE
	public abstract boolean deleteGroup(String user, String group);		//DONE
	public abstract ArrayList<ExappAppointment> getAllAppointmentsByGroup(String group);	//DONE
	public abstract ArrayList<String> getAllMembers(String group);		//DONE
	
	// Discussions related methods
	public abstract boolean insertDiscussion(String groupID, ExappDiscussion d);	//DONE
	public abstract boolean deleteDiscussion(int id);	//DONE
	public abstract ExappDiscussionBoard getExappDiscussion(String group);	//DONE
	
	//Statistics
	public abstract ArrayList<ExappAppointment> getAttendedAppointments(String user,String group);	//DONE
	public abstract ArrayList<ExappDiscussion> getAllDiscussionsWrittenByUser(String user);	//DONE
	public abstract ArrayList<ExappDiscussion> getAllDiscussionsWrittenByUserAndGroup(String user, String group);	//DONE
	
}
