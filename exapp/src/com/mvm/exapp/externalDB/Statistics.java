

package com.mvm.exapp.externalDB;


import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.mvm.exapp.model.ExappAppointment;
import com.mvm.exapp.model.ExappDiscussion;
import com.mvm.exapp.model.ExappDiscussionBoard;
import com.mvm.exapp.model.ExappGroup;
import com.mvm.exapp.model.ExappUser;
import com.mvm.exapp.model.statistics.StatisticsAppointment;
import com.mvm.exapp.model.statistics.StatisticsSubject;
import com.mvm.exapp.model.statistics.StatisticsUser;

public class Statistics extends AsyncTask<Object, Void, Object>{
	
	private static Statistics instance;
	private  ProgressDialog dialog;
    
	   // private  Activity activity;
	    private  Context context;
	   //
	    public Statistics(Context activity) {
	    	//this.activity = activity;
	        this.context = activity;
	 //       dialog = new ProgressDialog(context);
	    }
	    @Override
	    protected void onPreExecute() {
//	        this.dialog.setMessage("Loading your data");
//	        this.dialog.show();
	    }
	    @Override
		protected void onPostExecute(Object result) {
//			
//			if (dialog.isShowing()) {
//	            dialog.dismiss();
//	        }
			

		}
	public static Statistics getInstance(Context activity) {
		//if (instance==null)
			instance = new Statistics(activity);
		return instance;
	}
	
	public static String DriverName = "com.mysql.jdbc.Driver";
	
	//Main database:
	public static String urlDB = "jdbc:mysql://sql3.freemysqlhosting.net:3306/sql340113";	//?connectTimeout=10000";
	public static String userDB = "[main database name]";
	public static String passwordDB = "[password]";
	
	//Secondary Database. Only stores images.
	public static String urlFilesDB = "jdbc:mysql://sql3.freemysqlhosting.net:3306/sql345669";	//?connectTimeout=10000";
	public static String userFilesDB = "[image database name]";
	public static String passwordFilesDB = "[password]";
	
	/*
	//Statistics
	public abstract ArrayList<String> preferedSubjects();	//DONE
	public abstract ArrayList<String> preferedSubjectsByUser(String user);
	 */
	
	// For the application
	@Override
	protected Object doInBackground(Object... params) {
		switch (params[0].toString()){
		case "preferedSubjects": return this.preferedSubjects();
		case "preferedSubjectsByUser": return this.preferedSubjectsByUser(params[1].toString());
		
		
		case "getMeetingsByGroup": return this.getMeetingsByGroup(params[1].toString());
		case "getMeetingsByGroupByUser": return this.getMeetingsByGroupByUser(params[1].toString(), params[2].toString());
		
		case "getAllDiscussions": return this.getAllDiscussions();
		case "getDiscussionsByGroup": return this.getDiscussionsByGroup(params[1].toString());
		case "getDiscussionsByUserPerGroup": return this.getDiscussionsByUserPerGroup(params[1].toString(), params[2].toString());
		//case "getDiscussionsByUser": return this.getDiscussionsByUser(params[1].toString());
		
		case "peopleWhoAttendedMoreAppointments": return this.peopleWhoAttendedMoreAppointments();
		
		case "appointmentsAttendedByUser": return this.appointmentsAttendedByUser(params[1].toString());
		case "appointmentsWithMoreParticipants": return this.appointmentsWithMoreParticipants();
		
		case "groupsWithMoreMembers": return this.groupsWithMoreMembers();
		case "groupsIFounded": return this.groupsIFounded(params[1].toString());
		}
		return null;
	}
	
	public ArrayList<StatisticsSubject> preferedSubjects(){
		/* Returns a list of StatisticsSubject. Have a look to the class, is kinda self explanatory.
		 * Basically, each instance contains the name of a subject, a list of the NAMES of all the groups that 
		 * study this particular subject, and also, the total number of people studying this subject.
		 * Note this number is just the sum of the members of each group.
		 * That means 
		 */
		Connection con = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs1 = null;
		ArrayList<StatisticsSubject> l = new ArrayList<StatisticsSubject>();

		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			/*
			ps = con.prepareStatement("SELECT subject, COUNT(*) FROM `Group` GROUP BY subject");
			rs1= ps.executeQuery();
			while (rs1.next()) {
				String groupID= rs1.getString("groupID");
				String subject= rs1.getString("subject");
				if (!rs1.wasNull()){
					l.add(new StatisticsSubject(subject));
				}
			*/
			ps = con.prepareStatement("SELECT g.groupID,COUNT(DISTINCT userID), subject FROM `LinksGroupsUsers` l, `Group` g WHERE "
					+ "g.groupID=l.groupID GROUP BY groupID ORDER BY COUNT(*) DESC");
			rs1 = ps.executeQuery();
			while (rs1.next()) {
				String groupID= rs1.getString(1);
				int membersOfGroup = rs1.getInt(2);
				if (!rs1.wasNull()){

					String subject = rs1.getString("subject");
					ps2 = con.prepareStatement("SELECT COUNT(DISTINCT userID) FROM `LinksGroupsUsers` "
							+ "WHERE groupID IN (SELECT groupID FROM `Group` WHERE subject=?) ORDER BY count(DISTINCT userID) DESC");
					ps2.setString(1, subject);
					ResultSet rs2 = ps2.executeQuery();
					//groups.add(g);
					if (rs2.next()){
						int studentsOfSubject = rs2.getInt(1);
						if (!l.contains(new StatisticsSubject(subject,0)))
							l.add(new StatisticsSubject(subject, studentsOfSubject));
						int n = l.indexOf(new StatisticsSubject(subject,0));
						//if (l.get(n).getNumMembers()!=studentsOfSubject)	//This log should never appear!!!
							//Log.v("preferedSubjects", "Subject: "+l.get(n).getSubject()+" || students: DB:"+studentsOfSubject+" vs stored: "+l.get(n).getNumMembers());
						/*if (l.get(n).getNumMembers()!=studentsOfSubject)
							l.get(n).setNumMembers(l.get(n).getNumMembers() + studentsOfSubject);*/
						l.get(n).getListGroups().add(groupID);

					}
				}
			}
			Collections.sort(l, new Comparator<StatisticsSubject>() {
				/*@Override
				public int compare(Object s1, Object s2) {
					//StatisticsSubject s1 = (StatisticsSubject) o1;
					//StatisticsSubject s2 = (StatisticsSubject) o2;
					return Integer.valueOf(s2.getNumMembers()).compareTo(Integer.valueOf(s1.getNumMembers()));
				}*/

				@Override
				public int compare(StatisticsSubject s1, StatisticsSubject s2) {
					return Integer.valueOf(s2.getNumMembers()).compareTo(Integer.valueOf(s1.getNumMembers()));
				}
			});
			
			return l;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
				if (ps != null)
					ps.close();
				if (ps2 != null)
					ps2.close();
			} catch (Exception e1) {
			}
		}
		return l;
	}
	
	public ArrayList<StatisticsSubject> preferedSubjectsByUser(String user){
		Connection con = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs1 = null;
		ArrayList<StatisticsSubject> l = new ArrayList<StatisticsSubject>();

		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);			
			ps = con.prepareStatement("SELECT g.groupID, subject FROM `LinksGroupsUsers` l, `Group` g WHERE g.groupID=l.groupID AND userID=?");
			ps.setString(1, user);
			rs1 = ps.executeQuery();
			while (rs1.next()) {
				String groupID= rs1.getString(1);
				if (!rs1.wasNull()){

					String subject = rs1.getString("subject");
					ps2 = con.prepareStatement("SELECT COUNT(DISTINCT userID) FROM `LinksGroupsUsers` WHERE groupID IN "
							+ "(SELECT groupID FROM `Group` WHERE subject IN (SELECT subject FROM `Group` WHERE subject=?)) ORDER BY count(*) DESC");
					ps2.setString(1, subject);
					ResultSet rs2 = ps2.executeQuery();
					if (rs2.next()){
						int studentsOfSubject = rs2.getInt(1);
						if (!l.contains(new StatisticsSubject(subject,0)))
							l.add(new StatisticsSubject(subject, studentsOfSubject));
						int n = l.indexOf(new StatisticsSubject(subject,0));
						//if (l.get(n).getNumMembers()!=studentsOfSubject)	//This log should never appear!!!
							//Log.v("preferedSubjectsByUser", "Subject: "+l.get(n).getSubject()+", User: "+user+" || students: DB:"+studentsOfSubject+" vs stored: "+l.get(n).getNumMembers());
						/*if (l.get(n).getNumMembers()!=studentsOfSubject)
							l.get(n).setNumMembers(l.get(n).getNumMembers() + studentsOfSubject);*/
						l.get(n).getListGroups().add(groupID);

					}
				}
			}
			Collections.sort(l, new Comparator<StatisticsSubject>() {
				@Override
				public int compare(StatisticsSubject s1, StatisticsSubject s2) {
					return Integer.valueOf(s2.getNumMembers()).compareTo(Integer.valueOf(s1.getNumMembers()));
				}
			});
			
			return l;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
				if (ps != null)
					ps.close();
				if (ps2 != null)
					ps2.close();
			} catch (Exception e1) {
			}
		}
		return l;
	}
	
	public ArrayList<ExappDiscussionBoard> getAllDiscussions(){
		Connection con = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rsg = null;
		ArrayList<ExappDiscussionBoard> list = new ArrayList<ExappDiscussionBoard>();

		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			/*s = con.prepareStatement("SELECT discussionID, userID, title, body, time, date, groupID FROM `Discussion` d, `LinkGroupDiscussion` l "
					+ "WHERE d.discussionID=l.discussionID GROUP BY d.discussionID");*/
			ps = con.prepareStatement("SELECT groupID FROM `Group` WHERE 1 ORDER BY groupID ASC");
			rsg = ps.executeQuery();
			while (rsg.next()) {
				String group = rsg.getString("groupID");
				if (!rsg.wasNull()){
					//ExappDiscussionBoard edb = this.getDiscussionsByGroup(group);
					ExappDiscussionBoard edb = new ExappDiscussionBoard(group);
					ps2 = con.prepareStatement("SELECT discussionID, userID, title, body, time, date FROM `Discussion` WHERE discussionID IN "
							+ "(SELECT discussionID FROM `LinkGroupDiscussion` WHERE groupID=?) ORDER BY discussionID DESC");
					ps2.setString(1, group);
					ResultSet rs = ps2.executeQuery();
					while (rs.next()) {
						int id = rs.getInt("discussionID");
						if(!rs.wasNull()){
							String userID=rs.getString("userID");
							String title = rs.getString("title");
							String body = rs.getString("body");
							Time time = rs.getTime("time");
							Date date = rs.getDate("date");
							edb.getList().add(new ExappDiscussion(id, userID, title, body, time, date));
						}
					} // fin while
					if (edb != null)
						list.add(edb);
				}
			} // fin while
			return list;
		} catch (SQLException | ClassNotFoundException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
				if (ps != null)
					ps.close();
				if (ps2 != null)
					ps2.close();
			} catch (Exception e1) {
			}
		}
		return list;
	}
	
	public ArrayList<ExappAppointment> getMeetingsByGroup(String group){
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ExappAppointment> list = new ArrayList<ExappAppointment>();

		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			ps = con.prepareStatement("SELECT appointmentID, title, date, time, place, description FROM `Appointment` WHERE appointmentID IN "
					+ "(SELECT appointmentID FROM `LinksGroupsAppointments` WHERE groupID=?) ");
			ps.setString(1, group);
			rs = ps.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("appointmentID");
				if(!rs.wasNull()){
					String title = rs.getString("title");
					Time time = rs.getTime("time");
					Date date = rs.getDate("date");
					String place = rs.getString("place");
					String description = rs.getString("description");
					list.add(new ExappAppointment(id, title, place, date, time, description, group));
				}
			} // fin while
			return list;
		} catch (SQLException | ClassNotFoundException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
				if (ps != null)
					ps.close();
			} catch (Exception e1) {
			}
		}
		return list;
	}
	
	public ArrayList<ExappAppointment> getMeetingsByGroupByUser(String user, String group){
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ExappAppointment> list = new ArrayList<ExappAppointment>();

		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			ps = con.prepareStatement("SELECT appointmentID, title, date, time, place, description FROM `Appointment` WHERE "
					+ "appointmentID IN (SELECT appointmentID FROM `LinksGroupsAppointments` WHERE groupID=?) AND "
					+ "appointmentID IN (SELECT appointmentID FROM `LinksUsersAppointments` WHERE userID=? AND accepted=1)");
			ps.setString(1, group);
			ps.setString(2, user);
			rs = ps.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("appointmentID");
				if(!rs.wasNull()){
					String title = rs.getString("title");
					Time time = rs.getTime("time");
					Date date = rs.getDate("date");
					String place = rs.getString("place");
					String description = rs.getString("description");
					list.add(new ExappAppointment(id, title, place, date, time, description, group));
				}
			} // fin while
			return list;
		} catch (SQLException | ClassNotFoundException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
				if (ps != null)
					ps.close();
			} catch (Exception e1) {
			}
		}
		return list;
	}
	
	public ExappDiscussionBoard getDiscussionsByGroup(String group){
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ExappDiscussionBoard edb = new ExappDiscussionBoard(group);

		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			ps = con.prepareStatement("SELECT discussionID, userID, title, body, time, date FROM `Discussion` WHERE discussionID IN "
					+ "(SELECT discussionID FROM `LinkGroupDiscussion` WHERE groupID=?) ORDER BY discussionID DESC");
			ps.setString(1, group);
			rs = ps.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("discussionID");
				if(!rs.wasNull()){
					String userID=rs.getString("userID");
					String title = rs.getString("title");
					String body = rs.getString("body");
					Time time = rs.getTime("time");
					Date date = rs.getDate("date");
					edb.getList().add(new ExappDiscussion(id, userID, title, body, time, date));
				}
			} // fin while
			return edb;
		} catch (SQLException | ClassNotFoundException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
				if (ps != null)
					ps.close();
			} catch (Exception e1) {
			}
		}
		return edb;
	}
	
	public ExappDiscussionBoard getDiscussionsByUserPerGroup(String user, String group){
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ExappDiscussionBoard edb = new ExappDiscussionBoard(group);

		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			ps = con.prepareStatement("SELECT discussionID, title, body, time, date FROM `Discussion` WHERE discussionID IN "
					+ "(SELECT discussionID FROM `LinkGroupDiscussion` WHERE groupID=?) AND userID=? ORDER BY discussionID ASC");
			ps.setString(1, group);
			ps.setString(2, user);
			rs = ps.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("discussionID");
				if(!rs.wasNull()){
					String title = rs.getString("title");
					String body = rs.getString("body");
					Time time = rs.getTime("time");
					Date date = rs.getDate("date");
					edb.getList().add(new ExappDiscussion(id, user, title, body, time, date));
				}
			} // fin while
			return edb;
		} catch (SQLException | ClassNotFoundException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
				if (ps != null)
					ps.close();
			} catch (Exception e1) {
			}
		}
		return edb;
	}
	
	/*public ArrayList<ExappDiscussionBoard> getDiscussionsByUser(String user){
		return DBManager.getInstance().getAllDiscussionsByUser(user);
	}*/
	
	public ArrayList<StatisticsUser> peopleWhoAttendedMoreAppointments(){
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<StatisticsUser> list = new ArrayList<StatisticsUser>();

		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			ps = con.prepareStatement("SELECT userID, COUNT(*) FROM `LinksUsersAppointments` WHERE accepted=1 GROUP BY userID ORDER BY count(*) DESC");
			rs = ps.executeQuery();
			while (rs.next()) {
				String user = rs.getString("userID");
				if(!rs.wasNull()){
					int numAppointmentsAssisted = rs.getInt(2);
					list.add(new StatisticsUser(user, numAppointmentsAssisted));
				}
			} // fin while
			return list;
		} catch (SQLException | ClassNotFoundException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
				if (ps != null)
					ps.close();
			} catch (Exception e1) {
			}
		}
		return list;
	}
	
	public ArrayList<ExappAppointment> appointmentsAttendedByUser(String user){
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ExappAppointment> list = new ArrayList<ExappAppointment>();

		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			ps = con.prepareStatement("SELECT l.appointmentID, title, `date`, `time`, place, description, groupID FROM `LinksUsersAppointments` l INNER JOIN `Appointment` a INNER JOIN `LinksGroupsAppointments` l2 "
					+ "ON l.appointmentID=a.appointmentID AND l2.appointmentID=a.appointmentID WHERE accepted=1 AND userID=?");
			ps.setString(1, user);
			rs = ps.executeQuery();
			while (rs.next()) {
				int id = rs.getInt(1);
				if(!rs.wasNull()){
					String title = rs.getString("title");
					Date dateDB = rs.getDate("date");
					Time timeDB = rs.getTime("time");
					String place = rs.getString("place");
					String description = rs.getString("description");

					String group = rs.getString("groupID");

					list.add(new ExappAppointment(id, title, place, dateDB, timeDB, description, group));
				}
			} // fin while
			return list;
		} catch (SQLException | ClassNotFoundException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
				if (ps != null)
					ps.close();
			} catch (Exception e1) {
			}
		}
		return list;
	}
	
	public ArrayList<StatisticsAppointment> appointmentsWithMoreParticipants(){
		/* Returns the appointment, and the list of participants */
		Connection con = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		PreparedStatement ps3 = null;
		ResultSet rs1 = null;
		ArrayList<StatisticsAppointment> l = new ArrayList<StatisticsAppointment>();
		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);			
			ps = con.prepareStatement("SELECT appointmentID,COUNT(*) FROM `LinksUsersAppointments` WHERE accepted=1 GROUP BY appointmentID ORDER BY COUNT(*) DESC");
			rs1 = ps.executeQuery();
			while (rs1.next()) {
				int aID= rs1.getInt("appointmentID");
				int participants = rs1.getInt(2);	//number of participants in the appointment. Pointless with the code below ->ps3
				if (!rs1.wasNull()){
					ps2 = con.prepareStatement("SELECT title, `date`, `time`, place, description, groupID FROM `Appointment` a, `LinksGroupsAppointments` l "
							+ "WHERE a.appointmentID=l.appointmentID AND a.appointmentID=?");
					ps2.setInt(1, aID);
					ResultSet rs2 = ps2.executeQuery();
					if (rs2.next()) {
						String title= rs2.getString("title");
						if (!rs2.wasNull()){
							Date date= rs2.getDate("date");
							Time time= rs2.getTime("time");
							String place = rs2.getString("place");
							String description = rs2.getString("description");
							String group = rs2.getString("groupID");
							StatisticsAppointment sa = new StatisticsAppointment(group, new ExappAppointment(aID, title, place, date, time, description, group));
							
							ps3 = con.prepareStatement("SELECT userID FROM `LinksUsersAppointments` WHERE accepted=1 AND appointmentID=?");
							ps3.setInt(1, aID);
							ResultSet rs3 = ps3.executeQuery();
							while (rs3.next()){
								String user= rs3.getString("userID");
								sa.getListParticipants().add(user);
							}
							
							l.add(sa);
						}
					}
				}
			}
			return l;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
				if (ps != null)
					ps.close();
				if (ps2 != null)
					ps2.close();
				if (ps3 != null)
					ps3.close();
			} catch (Exception e1) {
			}
		}
		return l;
	}
	
	public ArrayList<ExappGroup> groupsWithMoreMembers(){
		/* Returns a list of GROUPS in order: The first element in the list is the group with highest number
		 * of members. 
		 * Also, for each group, the arraylist of members will be given */
		Connection con = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		PreparedStatement ps3 = null;
		ResultSet rs1 = null;
		ArrayList<ExappGroup> l = new ArrayList<ExappGroup>();

		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			ps = con.prepareStatement("SELECT groupID, COUNT(*) FROM `LinksGroupsUsers` GROUP BY groupID ORDER BY COUNT(*) DESC");
			rs1 = ps.executeQuery();
			while (rs1.next()) {
				String groupID= rs1.getString("groupID");
				int members = rs1.getInt(2);	//Pointless with the code bellow
				// the arraylist group.getMemebers() will contain all the memebers.
				if (!rs1.wasNull()){
					ps2 = con.prepareStatement("SELECT founder, subject, description FROM `Group` WHERE groupID=?");
					ps2.setString(1, groupID);
					ResultSet rs = ps2.executeQuery();
					if (rs.next()) {
						String founder= rs.getString("founder");
						if (!rs.wasNull()){
							String subject= rs.getString("subject");
							String desc= rs.getString("description");
							ExappGroup g = new ExappGroup(groupID, founder, subject, desc);
							
							ps3 = con.prepareStatement("SELECT userID FROM `LinksGroupsUsers` WHERE groupID=?");
							ps3.setString(1, groupID);
							ResultSet rs2 = ps3.executeQuery();
							while (rs2.next()){
								g.getMembers().add(rs2.getString("userID"));
							}
							
							l.add(g);
						}
					}
				}
			}
			return l;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
				if (ps != null)
					ps.close();
				if (ps2 != null)
					ps2.close();
				if (ps3 != null)
					ps3.close();
			} catch (Exception e1) {
			}
		}
		return l;
	}
	
	public int groupsIFounded(String user){
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int result = 0;

		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			ps = con.prepareStatement("SELECT COUNT(*) FROM `Group` WHERE founder=?");
			ps.setString(1, user);
			rs = ps.executeQuery();
			if (rs.next()) {
				int num = rs.getInt(1);
				if(!rs.wasNull()){
					result=num;
				}
			} // fin while
			return result;
		} catch (SQLException | ClassNotFoundException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
				if (ps != null)
					ps.close();
			} catch (Exception e1) {
			}
		}
		return result;
	}

}


