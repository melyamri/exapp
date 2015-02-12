
package com.mvm.exapp.externalDB;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;

import com.mvm.exapp.model.ExappAppointment;
import com.mvm.exapp.model.ExappAppointmentState;
import com.mvm.exapp.model.ExappDiscussionBoard;
import com.mvm.exapp.model.ExappDiscussion;
import com.mvm.exapp.model.ExappGroup;
import com.mvm.exapp.model.ExappUser;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;


public class DBManager extends DBInterface{
	
	private  static DBManager instance;
	private  ProgressDialog dialog;
    
   // private  Activity activity;
    private  Context context;
   
    public DBManager(Context activity) {
    	//this.activity = activity;
       // this.context = activity;
       
    }
    @Override
    protected void onPreExecute() {
//    	if(context != null){
//    	 dialog = new ProgressDialog(context);
//        this.dialog.setMessage("Loading your data");
//        this.dialog.show();
//    	}
    }
    @Override
	protected void onPostExecute(Object result) {
//		if(context != null && dialog != null){
//		if (dialog.isShowing() ) {
//            dialog.dismiss();
//        }
//		}

	}

	public static DBManager getInstance(Context activity) {
		//if (instance==null)
		
			instance = new DBManager(activity);
		return instance;
	}
	
	//Image methods (images are stored in a different database)
	public int uploadNewPicture(Bitmap bm){
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int imageID=-1;
		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlFilesDB, userFilesDB, passwordFilesDB);
			ps = con.prepareStatement("INSERT INTO image (picture, height, width) "
					+ "VALUES (?,?,?)");
			/*Bitmap bmp;
			bmp = BitmapFactory.decodeStream(new FileInputStream(img), null, null);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);*/
			//byteArray = stream.toByteArray();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.PNG, 100, bos);
			byte[] bArray = bos.toByteArray();
			ps.setBytes(1, bArray);
			ps.setInt(2, bm.getHeight());
			ps.setInt(3, bm.getWidth());
			
			int numCambios = ps.executeUpdate();
			rs = ps.getGeneratedKeys();
			if (numCambios == 1) {
				if (rs.next()) {
					int num = rs.getInt(1);
					if (!rs.wasNull()) {
						imageID = num;
					}
				}
			}
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
		return imageID;
	}
	
	public Bitmap getPicture(int imgID){
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		int height, width;
		Blob blob;
		Bitmap img = null;

		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlFilesDB, userFilesDB, passwordFilesDB);
			ps = con.prepareStatement("SELECT picture, height, width FROM Image WHERE imageID=?");
			ps.setInt(1, imgID);
			rs = ps.executeQuery();
			if (rs.next()) {
				height = rs.getInt("height");
				width = rs.getInt("width");
				blob = rs.getBlob("picture");
				if (!rs.wasNull()){
					ByteArrayInputStream imageStream = new ByteArrayInputStream(blob.getBytes(1, (int) blob.length()));
					img= BitmapFactory.decodeStream(imageStream);
				}
			} 
			return img;
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
		return img;
	}
	
	
	//login
	public ExappUser login(String user, String password){
		ExappUser u = null;
		
		if (checkUserExists(user)){
			Connection con = null;
			PreparedStatement ps = null;
			ResultSet rs = null;	
			try {
				Class.forName(DriverName);
				con = DriverManager.getConnection(urlDB, userDB, passwordDB);
				
				ps = con.prepareStatement("SELECT password, name, birthday, imageID, isRWTHstudent, description FROM User WHERE userID=?");
				ps.setString(1, user);
				rs = ps.executeQuery();
				if (rs.next()) {
					byte[] bytes = rs.getBytes("password");
					String passwordDB = new String(bytes, java.nio.charset.Charset.forName("UTF-8"));
					if (!rs.wasNull()){
						String name = rs.getString("name");
						Date birthday = rs.getDate("birthday");
						int imageID = rs.getInt("imageID");
						boolean isRWTHstudent = rs.getBoolean("isRWTHstudent");
						String description = rs.getString("description");
						if (password.equals(passwordDB))
							u = new ExappUser(user, passwordDB, name, birthday, imageID, isRWTHstudent, description);
						}
					}
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
		}//end check user
		return u;
	}
	
	public ExappUser getProfile(String user){
		ExappUser u = null;
		if (checkUserExists(user)){
			Log.i("asdf", "entra");
			Connection con = null;
			PreparedStatement ps = null;
			ResultSet rs = null;	
			try {
				Class.forName(DriverName);
				con = DriverManager.getConnection(urlDB, userDB, passwordDB);
				
				ps = con.prepareStatement("SELECT name, imageID, isRWTHstudent, description, birthday FROM User WHERE userID=?");
				ps.setString(1, user);
				rs = ps.executeQuery();
				if (rs.next()) {
					String name = rs.getString("name");
					Date birthday = rs.getDate("birthday");
					int imageID = rs.getInt("imageID");
					boolean isRWTHstudent = rs.getBoolean("isRWTHstudent");
					String description = rs.getString("description");
					u = new ExappUser(user, "", name, birthday, imageID, isRWTHstudent, description);
				}
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
		}//end check user
		return u;
	}
	
	public ExappUser insertOrUpdateUser(ExappUser u, boolean newImage, Bitmap bm) {
		Connection con1 = null;
		Connection con2 = null;
		PreparedStatement ps = null;
		PreparedStatement ps_insert = null;
		PreparedStatement ps2 = null;
		ExappUser exappUser = null;	
		
		//if (u.getPassword().length()<8){
		boolean b = checkUserExists(u.getUserID());
		try {
			Class.forName(DriverName);
			con2 = DriverManager.getConnection(urlFilesDB, userFilesDB, passwordFilesDB);
			con1 = DriverManager.getConnection(urlDB, userDB, passwordDB);
			
			if (!b){	//new User
				u.setImageID(-1);
				Log.v("existencia", "New user");
				ps = con2.prepareStatement("INSERT INTO `Image` (picture, height, width) "
						+ "VALUES (?,?,?)");
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				bm.compress(Bitmap.CompressFormat.PNG, 100, bos);
				byte[] bArray = bos.toByteArray();
				ps.setBytes(1, bArray);
				ps.setInt(2, bm.getHeight());
				ps.setInt(3, bm.getWidth());
				
				int numCambios = ps.executeUpdate();
				ResultSet rs = ps.getGeneratedKeys();
				if (numCambios == 1) {
					if (rs.next()) {
						int num = rs.getInt(1);
						if (!rs.wasNull()) {
							u.setImageID(num);
							Log.v("existencia", "New image inserted with id: "+num+". User's imageID assigned: "+u.getImageID());
							
							ps_insert = con1.prepareStatement("INSERT INTO `User` (userID, password, name, birthday, imageID, isRWTHstudent, description) "
									+ "VALUES (?,?,?,?,?,?,?)");
							ps_insert.setString(1, u.getUserID());
							ps_insert.setBytes(2, u.getPassword().getBytes(java.nio.charset.Charset.forName("UTF-8")));
							ps_insert.setString(3, u.getName());
							ps_insert.setDate(4, u.getBirthday());
							ps_insert.setInt(5, u.getImageID());
							ps_insert.setBoolean(6, u.getIsRWTHstudent());
							ps_insert.setString(7, u.getDescription());
							int numRows = ps_insert.executeUpdate();
							if (numRows == 1) {
								exappUser = u;
							}
						}
					}
				}
			}
			else{	//User exists, therefore, Update user
				Log.v("existencia", "Updating user");
				if (newImage){
					Log.v("existencia", "Image changed, deleting imageID: "+u.getImageID());
					ps = con2.prepareStatement("DELETE FROM `Image` WHERE imageID=?");
					ps.setInt(1, u.getImageID());
					int deleted = ps.executeUpdate();
					
					ps_insert = con2.prepareStatement("INSERT INTO `Image` (picture, height, width) "
							+ "VALUES (?,?,?)");
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					bm.compress(Bitmap.CompressFormat.PNG, 100, bos);
					byte[] bArray = bos.toByteArray();
					ps_insert.setBytes(1, bArray);
					ps_insert.setInt(2, bm.getHeight());
					ps_insert.setInt(3, bm.getWidth());
					
					int numCambios = ps_insert.executeUpdate();
					ResultSet rs = ps_insert.getGeneratedKeys();
					if (numCambios == 1) {
						if (rs.next()) {
							int num = rs.getInt(1);
							if (!rs.wasNull()) {
								u.setImageID(num);
							}
						}
					}
					if (deleted==1)
						Log.v("existencia", "Deleted previous image, new image assigned: "+u.getImageID());
				}
				ps2 = con1.prepareStatement("UPDATE `User` SET `password`=?,`name`=?,`birthday`=?,`imageID`=?,`description`=? WHERE userID=?");
				ps2.setBytes(1, u.getPassword().getBytes(java.nio.charset.Charset.forName("UTF-8")));
				ps2.setString(2, u.getName());
				ps2.setDate(3, u.getBirthday());
				ps2.setInt(4, u.getImageID());
				ps2.setString(5, u.getDescription());
				ps2.setString(6, u.getUserID());
				int numRows = ps2.executeUpdate();
				if (numRows == 1) {
					exappUser = u;
					Log.v("existencia", "User personal details updated");
				}
			}
		return exappUser;
		} catch (SQLException | ClassNotFoundException e1) {
			e1.printStackTrace();
		} finally {
			try {

				if (con1 != null)
					con1.close();
				if (con2 != null)
					con2.close();

				if (ps != null)
					ps.close();
				if (ps2 != null)
					ps2.close();
				if (ps_insert != null)
					ps_insert.close();
			} catch (Exception e1) {
			}
		}
		//}// end check password is less than 8 characters
		return exappUser;
	}

	
	// Checkers
	public boolean checkUserExists(String u){
		Connection con = null;
		PreparedStatement ps_check = null;
		ResultSet rs_check = null;	
		boolean b = false;
		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			
			ps_check = con.prepareStatement("SELECT userID FROM User WHERE userID=?");
			ps_check.setString(1, u);
			rs_check = ps_check.executeQuery();
			if (rs_check.next()) {
				String userID = rs_check.getString("userID");
				if (!rs_check.wasNull()){
						b = true;
					}
			return b;
			}
			} catch (SQLException | ClassNotFoundException e1) {
				e1.printStackTrace();
			} finally {
				try {
					if (con != null)
						con.close();
					if (ps_check != null)
						ps_check.close();
				} catch (Exception e1) {
				}
			}
			return b;
	}
	
	public boolean checkGroupExists(String g){
		Connection con = null;
		PreparedStatement ps_check = null;
		ResultSet rs_check = null;	
		boolean b = false;
		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			
			ps_check = con.prepareStatement("SELECT groupID FROM `Group` WHERE groupID=?");
			ps_check.setString(1, g);
			rs_check = ps_check.executeQuery();
			if (rs_check.next()) {
				if (!rs_check.wasNull())
					b = true;
				}
			return b;
			} catch (SQLException | ClassNotFoundException e1) {
				e1.printStackTrace();
			} finally {
				try {
					if (con != null)
						con.close();
					if (ps_check != null)
						ps_check.close();
				} catch (Exception e1) {
				}
			}
			return b;
	}
	
	public boolean checkAppointmentExists(int a){
		Connection con = null;
		PreparedStatement ps_check = null;
		ResultSet rs_check = null;	
		boolean b = false;
		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			
			ps_check = con.prepareStatement("SELECT date, place, description FROM `Appointment` WHERE appointmentID=?");
			ps_check.setInt(1, a);
			rs_check = ps_check.executeQuery();
			if (rs_check.next()) {
				Date date = rs_check.getDate("date");
				if (!rs_check.wasNull()){
					String place = rs_check.getString("place");
					String description = rs_check.getString("description");
					b = true;
				}
			}
			return b;
			} catch (SQLException | ClassNotFoundException e1) {
				e1.printStackTrace();
			} finally {
				try {
					if (con != null)
						con.close();
					if (ps_check != null)
						ps_check.close();
				} catch (Exception e1) {
				}
			}
			return b;
	}
	
	public boolean checkUserBelongsToGroup(String user, String group){
		Connection con = null;
		PreparedStatement ps_check = null;
		ResultSet rs_check = null;	
		boolean b = false;
		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			
			ps_check = con.prepareStatement("SELECT * FROM `LinksGroupsUsers` WHERE groupID=? AND userID=?");
			ps_check.setString(1, group);
			ps_check.setString(2, user);
			rs_check = ps_check.executeQuery();
			if (rs_check.next()) {
				if (!rs_check.wasNull())
					b = true;
				}
			return b;
			} catch (SQLException | ClassNotFoundException e1) {
				e1.printStackTrace();
			} finally {
				try {
					if (con != null)
						con.close();
					if (ps_check != null)
						ps_check.close();
				} catch (Exception e1) {
				}
			}
			return b;
	}
	
	// Global methods
	public ArrayList<String> getAllUsers(){
		Connection con = null;
		Statement s = null;
		ResultSet rs = null;
		ArrayList<String> list = new ArrayList<String>();

		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			s = con.createStatement();
			rs = s.executeQuery("SELECT userID FROM User WHERE 1");
			while (rs.next()) {
				list.add(rs.getString("userID"));
			} // fin while
			return list;
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
		return list;
	}
	
	public ArrayList<String> getAllSubjects(){
		Connection con = null;
		Statement s = null;
		ResultSet rs = null;
		ArrayList<String> list = new ArrayList<String>();

		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			s = con.createStatement();
			rs = s.executeQuery("SELECT subject FROM `Group` WHERE 1");
			while (rs.next()) {
				String subject = rs.getString("subject");
				if (!rs.wasNull()){
					if (!list.contains(subject))
						list.add(subject);
				}
				
			} // fin while
			return list;
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
		return list;
	}
	
	public ArrayList<ExappGroup> getAllGroups(){
		Connection con = null;
		Statement s = null;
		ResultSet rs = null;
		ArrayList<ExappGroup> list = new ArrayList<ExappGroup>();
		//ArrayList<String> list = new ArrayList<String>();

		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			s = con.createStatement();
			rs = s.executeQuery("SELECT groupID, founder, subject, description FROM `Group` WHERE 1");
			while (rs.next()) {
				String groupID= rs.getString("groupID");
				if (!rs.wasNull()){
					String founder= rs.getString("founder");
					String subject = rs.getString("subject");
					String desc= rs.getString("description");
					list.add(new ExappGroup(groupID, founder, subject, desc));
					//list.add(groupID);
				}
			}
			return list;
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
		return list;
	}
	
	
	// Global "getters"
	public ArrayList<ExappGroup> getAllGroupsByUser(String user) {
		Connection con = null;
		PreparedStatement ps_LinksGroupsUsers = null;
		PreparedStatement ps_Groups = null;
		ResultSet rs_LinkGroupsUsers = null;
		ResultSet rs_Groups = null;
		
		ArrayList<ExappGroup> list = new ArrayList<ExappGroup>();

		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			ps_LinksGroupsUsers = con.prepareStatement("SELECT groupID FROM `LinksGroupsUsers` WHERE userID=?");
			ps_LinksGroupsUsers.setString(1, user);
			rs_LinkGroupsUsers = ps_LinksGroupsUsers.executeQuery();
			while (rs_LinkGroupsUsers.next()) {
				String groupID= rs_LinkGroupsUsers.getString("groupID");
				 if (!rs_LinkGroupsUsers.wasNull()){
					ps_Groups=con.prepareStatement("SELECT founder, subject, description FROM `Group` WHERE groupID=?");
					ps_Groups.setString(1, groupID);
					rs_Groups = ps_Groups.executeQuery();
					if (rs_Groups.next()){
						String founder = rs_Groups.getString("founder");
						String subject = rs_Groups.getString("subject");
						String description = rs_Groups.getString("description");
						if (!rs_Groups.wasNull()){
							list.add(new ExappGroup(groupID, founder, subject, description));
						}
					}
				}
			}
			return list;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
				if (ps_LinksGroupsUsers != null)
					ps_LinksGroupsUsers.close();
				if (ps_Groups != null)
					ps_Groups.close();
			} catch (Exception e1) {
			}
		}
		return list;
	}
	
	public ArrayList<ExappAppointment> getAllAppointmentsByUser(String user) {
		Connection con = null;
		PreparedStatement ps_LinksUsersAppointments = null;
		ResultSet rs_LinksUsersAppointments = null;
		
		ArrayList<ExappAppointment> list = new ArrayList<ExappAppointment>();

		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			//ps_LinksUsersAppointments = con.prepareStatement("SELECT appointmentID FROM `LinksUsersAppointments` WHERE userID=?");
			ps_LinksUsersAppointments = con.prepareStatement("SELECT a.appointmentID, date, title, time, place, description, groupID, accepted "
					+ "FROM `Appointment`a, `LinksGroupsAppointments` l, `LinksUsersAppointments` u "
					+ "WHERE a.appointmentID IN (SELECT appointmentID FROM `LinksUsersAppointments` WHERE userID=?) "
					+ "AND a.appointmentID=l.appointmentID AND a.appointmentID=u.appointmentID GROUP BY a.appointmentID");
			ps_LinksUsersAppointments.setString(1, user);
			rs_LinksUsersAppointments = ps_LinksUsersAppointments.executeQuery();
			while (rs_LinksUsersAppointments.next()) {
				int appointmentID= rs_LinksUsersAppointments.getInt("appointmentID");
				if (!rs_LinksUsersAppointments.wasNull()){
						String title = rs_LinksUsersAppointments.getString("title");
						Date date = rs_LinksUsersAppointments.getDate("date");
						Time time = rs_LinksUsersAppointments.getTime("time");
						String place = rs_LinksUsersAppointments.getString("place");
						String description = rs_LinksUsersAppointments.getString("description");
						String group = rs_LinksUsersAppointments.getString("groupID");
						boolean state = rs_LinksUsersAppointments.getBoolean("accepted");
						ExappAppointment appointment = new ExappAppointment(appointmentID, title, place, date, time, description, group);
						appointment.setHasCurrentUserAccepted(state);
						list.add(appointment);
					}
				}
			return list;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException  e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
				if (ps_LinksUsersAppointments != null)
					ps_LinksUsersAppointments.close();
			} catch (Exception e1) {
			}
		}
		return list;
	}
	
	public int getAllDiscussionsByUser(String user){
		Connection con = null;
		PreparedStatement ps_groups = null;
		PreparedStatement ps_discussions = null;
		ResultSet rs_groups = null;
		ResultSet rs_discussions = null;
		
		int resp = 0;

		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			
//			 "SELECT d.discussionID, userID, title, body, time, date, groupID FROM `Discussion` d, `LinkGroupDiscussion` l WHERE d.discussionID "
//			 + "IN (SELECT discussionID FROM `LinkGroupDiscussion` WHERE groupID "
//			 + "IN (SELECT groupID FROM `LinksGroupsUsers` WHERE userID=?)) AND l.discussionID=d.discussionID ORDER BY discussionID ASC"
			 
			ps_groups = con.prepareStatement("SELECT COUNT(*) FROM `Discussion` WHERE d.discussionID "
					+ "IN (SELECT discussionID FROM `LinkGroupDiscussion` WHERE groupID "
					+ "IN (SELECT groupID FROM `LinksGroupsUsers` WHERE userID=?)) ORDER BY discussionID ASC");
			ps_groups.setString(1, user);
			rs_groups = ps_groups.executeQuery();
			while (rs_groups.next()) {
				int num = rs_discussions.getInt(1);
				if (!rs_groups.wasNull()){
					resp = num;
				}
			}
			return resp;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
				if (ps_groups != null)
					ps_groups.close();
				if (ps_discussions != null)
					ps_discussions.close();
			} catch (Exception e1) {
			}
		}
		return resp;
	}
	
	public ArrayList<ExappGroup> getGroupsBySubject(String subject){
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ExappGroup> list = new ArrayList<ExappGroup>();
		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			ps = con.prepareStatement("SELECT groupID, founder, description FROM `Group` WHERE subject=?");
			ps.setString(1, subject);
			rs = ps.executeQuery();
			while (rs.next()) {
				String groupID= rs.getString("groupID");
				if (!rs.wasNull()){
					String founder= rs.getString("founder");
					String desc= rs.getString("description");
					list.add(new ExappGroup(groupID, founder, subject, desc));
				}
			}
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
	
	public ArrayList<ExappGroup> getInvitations(String user){
		Connection con = null;
		PreparedStatement ps = null;
		PreparedStatement ps_group = null;
		ResultSet rs = null;
		ArrayList<ExappGroup> list = new ArrayList<ExappGroup>();
		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			ps = con.prepareStatement("SELECT groupID FROM `LinkGroupInvitations` WHERE userID=?");
			ps.setString(1, user);
			rs = ps.executeQuery();
			while (rs.next()) {
				String groupID= rs.getString("groupID");
				if (!rs.wasNull()){
					ps_group = con.prepareStatement("SELECT founder, subject, description FROM `Group` WHERE groupID=?");
					ps_group.setString(1, groupID);
					ResultSet rs_group = ps_group.executeQuery();
					if (rs_group.next()){
						String founder= rs_group.getString("founder");
						String subject= rs_group.getString("subject");
						String desc= rs_group.getString("description");
						list.add(new ExappGroup(groupID, founder, subject, desc));
					}
				}
			}
			return list;
		} catch (SQLException | ClassNotFoundException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
				if (ps != null)
					ps.close();
				if (ps_group != null)
					ps_group.close();
			} catch (Exception e1) {
			}
		}
		return list;
	}
	
	// Appointments related methods
	public String getGroupOfAppointment(int appointmentID){
		Connection con = null;
		PreparedStatement ps_check = null;
		ResultSet rs_check = null;	
		String g = "";
		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			
			ps_check = con.prepareStatement("SELECT groupID FROM `LinksGroupsAppointments` WHERE appointmentID=?");
			ps_check.setInt(1, appointmentID);
			rs_check = ps_check.executeQuery();
			if (rs_check.next()) {
				String groupID = rs_check.getString("groupID");
				if (!rs_check.wasNull()){
						g = groupID;
					}
				}
			return g;
			} catch (SQLException | ClassNotFoundException e1) {
				e1.printStackTrace();
			} finally {
				try {
					if (con != null)
						con.close();
					if (ps_check != null)
						ps_check.close();
				} catch (Exception e1) {
				}
			}
			return g;
	}
	
	public boolean setAppointment(ExappAppointment a) {
		Connection con = null;
		PreparedStatement ps_insert = null;
		PreparedStatement ps_link = null;
		PreparedStatement ps_members = null;
		PreparedStatement ps_invitation = null;
		ResultSet rs = null;
		ResultSet rs_member = null;
		boolean b = false;

		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			ps_insert = con.prepareStatement("INSERT INTO `Appointment` (title, place, date, time, description) "
					+ "VALUES (?,?,?,?,?)");
			ps_insert.setString(1, a.getTitle());
			ps_insert.setString(2, a.getPlace());
			ps_insert.setDate(3, a.getDate());
			ps_insert.setTime(4, a.getTime());
			ps_insert.setString(5, a.getDescription());
			int numRows1 = ps_insert.executeUpdate();
			rs = ps_insert.getGeneratedKeys();
			if (numRows1 == 1) {
				// Everything ok. 
				//Code below only sets the correct id to our appointment object (because appointmentID is an A_I value in the database)
				if (rs.next()) {
					int id = rs.getInt(1);
					if (!rs.wasNull()) {
						a.setId(id);
					}
				}
			}
			
			ps_link = con.prepareStatement("INSERT INTO `LinksGroupsAppointments` (groupID, appointmentID) "
					+ "VALUES (?,?)");
			ps_link.setString(1, a.getGroup());
			ps_link.setInt(2, a.getId());
			int numRows2 = ps_link.executeUpdate();
						
			ps_members = con.prepareStatement("SELECT userID FROM `LinksGroupsUsers` WHERE groupID=? ");
			ps_members.setString(1, a.getGroup());
			rs_member = ps_members.executeQuery();
			while (rs_member.next()) {
				String userID= rs_member.getString("userID");
				if (!rs_member.wasNull()){
					ps_invitation = con.prepareStatement("INSERT INTO `LinksUsersAppointments` (userID, appointmentID, accepted) "
						+ "VALUES (?,?,?)");
					ps_invitation.setString(1, userID);
					ps_invitation.setInt(2, a.getId());
					ps_invitation.setBoolean(3, false);
					int num = ps_invitation.executeUpdate();
				}
			}
			
			if ((numRows1 == 1)&&(numRows2==1)) {
				b = true;
			}
			return b;
		} catch (SQLException | ClassNotFoundException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
				if (ps_insert != null)
					ps_insert.close();
				if (ps_members != null)
					ps_members.close();
				if (ps_link != null)
					ps_link.close();
				if (ps_invitation != null)
					ps_invitation.close();
			} catch (Exception e1) {
			}
		}
		return b;
	}
	
	public boolean acceptAppointment(int appointmentID, String userID, boolean accept) {
		Connection con = null;
		PreparedStatement ps_insert = null;
		PreparedStatement ps_invitations = null;
		ResultSet rs = null;
		boolean b = false;

		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			ps_insert = con.prepareStatement("UPDATE `LinksUsersAppointments` SET `accepted`=? WHERE `userID`=? AND `appointmentID`=?");
			ps_insert.setBoolean(1, accept);
			ps_insert.setString(2, userID);
			ps_insert.setInt(3, appointmentID);
			int numCambios = ps_insert.executeUpdate();
			
			if (numCambios == 1) {
				b = true;
			}
			return b;
		} catch (SQLException | ClassNotFoundException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
				if (ps_insert != null)
					ps_insert.close();
				if (ps_invitations != null)
					ps_invitations.close();
			} catch (Exception e1) {
			}
		}
		return b;
	}
	
	public boolean deleteAppointment(int appointment){
		Connection con = null;
		PreparedStatement ps = null;		
		boolean b = false;
		
		try{
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			//Delete appointment
			ps = con.prepareStatement("DELETE FROM `Appointment` WHERE appointmentID=?");
			ps.setInt(1, appointment);
			int l1 = ps.executeUpdate();
			
			//Delete from LinksGroupAppointments
			ps = con.prepareStatement("DELETE FROM `LinksUsersAppointments` WHERE appointmentID=?");
			ps.setInt(1, appointment);
			int l2 = ps.executeUpdate();
			
			//Delete from LinksGroupAppointments
			ps = con.prepareStatement("DELETE FROM `LinksGroupsAppointments` WHERE appointmentID=?");
			ps.setInt(1, appointment);
			int l3 = ps.executeUpdate();
			
			if (l1==1)
				b = true;
			return b;
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
			} catch (Exception e1) {
			}
		}
		return b;
	}
	
	public ExappAppointment getAppointmentById(int id){
		Connection con = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		
		ExappAppointment a  = null;
	
		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			ps = con.prepareStatement("SELECT title, `date`, `time`, place, description FROM `Appointment` WHERE appointmentID=?");
			ps.setInt(1, id);
			rs1 = ps.executeQuery();
			if (rs1.next()) {
				String title= rs1.getString("title");
				if (!rs1.wasNull()){
					Date date= rs1.getDate("date");
					Time time= rs1.getTime("time");
					String place = rs1.getString("place");
					String description = rs1.getString("description");
					ps2 = con.prepareStatement("SELECT groupID FROM `LinksGroupsAppointments` WHERE appointmentID=?");
					ps2.setInt(1, id);
					rs2 = ps2.executeQuery();
					if (rs2.next()){
						String group = rs2.getString("groupID");
						if (!rs2.wasNull())
							a = new ExappAppointment(id, title, place, date, time, description, group);
					}
				}
			}
			return a;
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
		return a;
	}
	
	public ArrayList<ExappAppointmentState> getUsersAppointmentState(int appointment){
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		
		ArrayList<ExappAppointmentState> list = new ArrayList<ExappAppointmentState>();
	
		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			ps = con.prepareStatement("SELECT groupID FROM `LinksGroupsAppointments` WHERE appointmentID=?");
			ps.setInt(1, appointment);
			rs1 = ps.executeQuery();
			if (rs1.next()) {
				String group= rs1.getString("groupID");
				if (!rs1.wasNull()){
					ps = con.prepareStatement("SELECT userID FROM `LinksGroupsUsers` WHERE groupID=?");
					ps.setString(1, group);
					rs2 = ps.executeQuery();
					while (rs2.next()) {
						String user= rs2.getString("userID");
						if (!rs2.wasNull()){
							ps = con.prepareStatement("SELECT accepted FROM `LinksUsersAppointments` WHERE appointmentID=? AND userID=?");
							ps.setInt(1, appointment);
							ps.setString(2, user);
							rs3 = ps.executeQuery();
							if (rs3.next()) {
								boolean b= rs3.getBoolean("accepted");
								if (!rs3.wasNull()){
									list.add(new ExappAppointmentState(user, b));
								}
							}
						}
					}
				}
			}
			return list;
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
			} catch (Exception e1) {
			}
		}
		return list;
	}
	
	public ArrayList<ExappAppointment> getFutureAppointments(String user, Date date){
		Connection con = null;
		PreparedStatement ps_LinksUsersAppointments = null;
		PreparedStatement ps_Appointment = null;
		ResultSet rs_LinksUsersAppointments = null;
		ResultSet rs_Appointment = null;
		
		ArrayList<ExappAppointment> list = new ArrayList<ExappAppointment>();

		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			ps_LinksUsersAppointments = con.prepareStatement("SELECT appointmentID FROM `LinksUsersAppointments` WHERE userID=?");
			ps_LinksUsersAppointments.setString(1, user);
			rs_LinksUsersAppointments = ps_LinksUsersAppointments.executeQuery();
			while (rs_LinksUsersAppointments.next()) {
				int appointmentID= rs_LinksUsersAppointments.getInt("appointmentID");
				if (!rs_LinksUsersAppointments.wasNull()){
					ps_Appointment=con.prepareStatement("SELECT title, `date`, `time`, place, description FROM `Appointment` WHERE appointmentID=? ORDER BY date, time ASC");
					ps_Appointment.setInt(1, appointmentID);
					rs_Appointment = ps_Appointment.executeQuery();
					if (rs_Appointment.next()){
						String title = rs_Appointment.getString("title");
						Date dateDB = rs_Appointment.getDate("date");
						Time timeDB = rs_Appointment.getTime("time");
						String place = rs_Appointment.getString("place");
						String description = rs_Appointment.getString("description");
						if (!rs_Appointment.wasNull()){
							String group = getGroupOfAppointment(appointmentID);
							Calendar calendarD=Calendar.getInstance();
							calendarD.setTime(dateDB);
							Calendar calendarT=Calendar.getInstance();
							calendarT.setTime(timeDB);				
							calendarD.set(Calendar.HOUR_OF_DAY, calendarT.get(Calendar.HOUR_OF_DAY));
							calendarD.set(Calendar.MINUTE, calendarT.get(Calendar.MINUTE));
							Date d = new Date(calendarD.getTime().getTime());	//calendar.getTime() returns a java.util.Date. Then, java.util.Date.getTime() returns a java.sql.Date
							if (d.after(date)){
								ExappAppointment appointment = new ExappAppointment(appointmentID, title, place, dateDB, timeDB, description, group);
								list.add(appointment);
							}
						}
					}
				}
			}
			return list;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
				if (ps_LinksUsersAppointments != null)
					ps_LinksUsersAppointments.close();
				if (ps_Appointment != null)
					ps_Appointment.close();
			} catch (Exception e1) {
			}
		}
		return list;
	}
	
	public boolean hasUserAcceptedAppointment(String user, int appointmentID){
		Connection con = null;
		PreparedStatement ps_check = null;
		ResultSet rs_check = null;
		boolean b = false;
		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			ps_check = con.prepareStatement("SELECT accepted FROM `LinksUsersAppointments` WHERE userID=? AND appointmentID=?");
			ps_check.setString(1, user);
			ps_check.setInt(2, appointmentID);
			rs_check = ps_check.executeQuery();
			if (rs_check.next()) {
				boolean accepted = rs_check.getBoolean("accepted");
				if (!rs_check.wasNull()){
						b = accepted;
					}
				}
			return b;
			} catch (SQLException | ClassNotFoundException e1) {
				e1.printStackTrace();
			} finally {
				try {
					if (con != null)
						con.close();
					if (ps_check != null)
						ps_check.close();
				} catch (Exception e1) {
				}
			}
			return b;
	}
	
	// Groups related methods
	public String getFounder(String group){
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String s = "";

		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			ps = con.prepareStatement("SELECT founder FROM `Group` WHERE groupID=?");
			ps.setString(1, group);
			rs = ps.executeQuery();
			if (rs.next()) {
				String founder= rs.getString("founder");
				if (!rs.wasNull()){
					s=founder;
				}
			}
			return s;
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
			} catch (Exception e1) {
			}
		}
		return s;
	}
	
	public ExappGroup getGroupById(String group){
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		ExappGroup g  = null;

		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			ps = con.prepareStatement("SELECT founder, subject, description FROM `Group` WHERE groupID=?");
			ps.setString(1, group);
			rs = ps.executeQuery();
			if (rs.next()) {
				String founder= rs.getString("founder");
				if (!rs.wasNull()){
				String subject= rs.getString("subject");
				String desc= rs.getString("description");
				g = new ExappGroup(group, founder, subject, desc);
				}
			}
			return g;
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
			} catch (Exception e1) {
			}
		}
		return g;
	}
	
	public boolean joinGroup(String user, String group){
		Connection con = null;
		PreparedStatement ps_insert = null;
		PreparedStatement ps_remove = null;
		PreparedStatement ps_appointments = null;
		PreparedStatement ps_insertAppointment = null;
		ResultSet rs= null;	
		ResultSet rs_appointments = null;	
		boolean b = false;
		
		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			ps_insert = con.prepareStatement("INSERT INTO `LinksGroupsUsers` (userID, groupID) "
					+ "VALUES (?,?)");
			ps_insert.setString(1, user);
			ps_insert.setString(2, group);
			int numRows = ps_insert.executeUpdate();
			if (numRows == 1) {
				b=true;
			}
			
			ps_remove = con.prepareStatement("DELETE FROM `LinkGroupInvitations` WHERE userID=? AND groupID=?");
			ps_remove.setString(1, user);
			ps_remove.setString(2, group);
			int num = ps_remove.executeUpdate();
			
			ps_appointments = con.prepareStatement("SELECT appointmentID FROM `LinksGroupsAppointments` WHERE groupID=? ");
			ps_appointments.setString(1, group);
			rs_appointments = ps_appointments.executeQuery();
			while (rs_appointments.next()) {
				int appointment= rs_appointments.getInt("appointmentID");
				if (!rs_appointments.wasNull()){
					ps_insertAppointment = con.prepareStatement("INSERT INTO `LinksUsersAppointments` (userID, appointmentID, accepted) "
						+ "VALUES (?,?,?)");
					ps_insertAppointment.setString(1, user);
					ps_insertAppointment.setInt(2, appointment);
					ps_insertAppointment.setBoolean(3, false);
					int num2 = ps_insertAppointment.executeUpdate();
				}
			}
			
			return b;
		} catch (SQLException | ClassNotFoundException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
				if (ps_insert != null)
					ps_insert.close();
				if (ps_remove != null)
					ps_remove.close();
				if (ps_appointments != null)
					ps_appointments.close();
				if (ps_insertAppointment != null)
					ps_insertAppointment.close();
			} catch (Exception e1) {
			}
		}
		return b;
	}
	
	public boolean quitGroup(String user, String group){
		Connection con = null;
		PreparedStatement ps = null;
		PreparedStatement ps_appointments = null;
		PreparedStatement ps_deleteAppointment = null;
		ResultSet rs_appointments = null;
		boolean b = false;

		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			ps = con.prepareStatement("DELETE FROM `LinksGroupsUsers` WHERE groupID=? AND userID=?");
			ps.setString(1, group);
			ps.setString(2, user);
			int num = ps.executeUpdate();
			if (num==1) {
				b = true;
			}
			
			ps_appointments = con.prepareStatement("SELECT appointmentID FROM `LinksGroupsAppointments` WHERE groupID=? ");
			ps_appointments.setString(1, group);
			rs_appointments = ps_appointments.executeQuery();
			while (rs_appointments.next()) {
				int appointment= rs_appointments.getInt("appointmentID");
				if (!rs_appointments.wasNull()){
					ps_deleteAppointment = con.prepareStatement("DELETE FROM `LinksUsersAppointments` WHERE userID=? AND appointmentID=?");
					ps_deleteAppointment.setString(1, user);
					ps_deleteAppointment.setInt(2, appointment);
					int num2 = ps_deleteAppointment.executeUpdate();
				}
			}
			
			return b;
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
				if (ps_appointments != null)
					ps_appointments.close();
				if (ps_deleteAppointment != null)
					ps_deleteAppointment.close();
			} catch (Exception e1) {
			}
		}
		return b;
	}
	
	public String createOrUpdateGroupByUser (ExappUser u, ExappGroup g) {
		Connection con = null;
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		PreparedStatement ps_invitations = null;
		ResultSet rs = null;
		ResultSet rs_user = null;
		
		String response = "";
		boolean owner = false;
		boolean exists = false; //checkGroupExists(g.getGroupID());
		
		try {		
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			ps = con.prepareStatement("SELECT `founder` FROM `Group` WHERE `groupID`=?");
			ps.setString(1, g.getGroupID());
			rs_user = ps.executeQuery();
			if (rs_user.next()){
				exists = true;
				if (!rs_user.wasNull()){
					String founder = rs_user.getString("founder");
					if (u.getUserID().equals(founder))
						owner = true;
				}
			}
			
			if (exists){	//The group exists
				if (owner){	//Only the owner can make changes
					ps1 = con.prepareStatement("UPDATE `Group` SET `subject`=?,`description`=? WHERE `groupID`=?");
					ps1.setString(3, g.getGroupID());
					ps1.setString(1, g.getSubject());
					ps1.setString(2, g.getDescription());
					int numRows = ps1.executeUpdate();
					if (numRows == 1)

						response = "Data of the group changed by group's owner! \n";
				}//endif owner
				else
					response = "Warning!, user is not the owner of the group (main group's data has not been changed) \n";
				
				for (String member : g.getMembers()){
					response +="User: '"+member+"' ";
					ps_invitations = con.prepareStatement("SELECT groupID FROM `LinksGroupsUsers` WHERE `userID`=? AND `groupID`=?");
					ps_invitations.setString(1, member);
					ps_invitations.setString(2, g.getGroupID());
					rs = ps_invitations.executeQuery();
					boolean isAlreadyInGroup = false;
					if (rs.next()) {
						String groupID= rs.getString("groupID");
						if (!rs.wasNull()){
							isAlreadyInGroup = true;
							response +="is already in the group! \n";
						}
					}
					if (!isAlreadyInGroup){
						ps_invitations = con.prepareStatement("SELECT groupID FROM `LinkGroupInvitations` WHERE `userID`=? AND `groupID`=?");

						ps_invitations.setString(1, member);
						ps_invitations.setString(2, g.getGroupID());
						rs = ps_invitations.executeQuery();

						boolean isAlreadyInvited = false;

						if (rs.next()) {
							String groupID= rs.getString("groupID");
							if (!rs.wasNull()){

								isAlreadyInvited = true;
								response +="was already invited! \n";

							}
						}
						if (!isAlreadyInvited){
							ps_invitations = con.prepareStatement("INSERT INTO `LinkGroupInvitations` (userID, groupID) "
									+ "VALUES (?,?)");
							ps_invitations.setString(1, member);
							ps_invitations.setString(2, g.getGroupID());
							int num = ps_invitations.executeUpdate();
							response += "has just been invited! \n";
						}
					}
				}
			}//endif exists
			
			else {	//The group doesn't exist

				ps1 = con.prepareStatement("INSERT INTO `Group` (groupID, founder, subject, description) "
						+ "VALUES (?,?,?,?)");
				ps1.setString(1, g.getGroupID());
				ps1.setString(2, u.getUserID());
				ps1.setString(3, g.getSubject());
				ps1.setString(4, g.getDescription());
	
				int numRows = ps1.executeUpdate();
				response = "Group created! \n";
				
				for (String member : g.getMembers()){
					ps_invitations = con.prepareStatement("INSERT INTO `LinkGroupInvitations` (userID, groupID) "
							+ "VALUES (?,?)");
					ps_invitations.setString(1, member);
					ps_invitations.setString(2, g.getGroupID());
					int num = ps_invitations.executeUpdate();
					response += "User '"+member+"' has been invited! \n";
				}

				if ((numRows == 1) && (this.joinGroup(u.getUserID(), g.getGroupID()))) {
					response += "Succesful!";
				}
			}
			return response;
		} catch (SQLException | ClassNotFoundException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
				if (ps != null)
					ps.close();
				if (ps1 != null)
					ps1.close();
				if (ps_invitations!=null)
					ps_invitations.close();
			} catch (Exception e1) {
			}
		}
		return response;
	}
	
	public boolean deleteGroup(String user, String group){
		Connection con = null;
		PreparedStatement ps = null;
		PreparedStatement psCheck = null;
		PreparedStatement psLimpieza = null;
		ResultSet rs = null;
		boolean b = false;

		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			ps = con.prepareStatement("SELECT founder FROM `Group` WHERE groupID=?");
			ps.setString(1, group);
			rs = ps.executeQuery();
			if (rs.next()) {
				String founder = rs.getString("founder");
				if(!rs.wasNull()){
					if (user.equals(founder)){
						ps = con.prepareStatement("DELETE FROM `Group` WHERE groupID=?");
						ps.setString(1, group);
						int num = ps.executeUpdate();
						if (num==1)
							b = true;
						
						//Delete  pending invitations to the group
						psLimpieza = con.prepareStatement("DELETE FROM `LinkGroupInvitations` WHERE groupID=?");
						psLimpieza.setString(1, group);
						int j = psLimpieza.executeUpdate();
						
						//Delete  members to the group
						psLimpieza = con.prepareStatement("DELETE FROM `LinksGroupsUsers` WHERE groupID=?");
						psLimpieza.setString(1, group);
						int k = psLimpieza.executeUpdate();
						
						//Delete related appointments of the group
						psCheck = con.prepareStatement("SELECT appointmentID FROM `LinksGroupsAppointments` WHERE groupID=?");
						psCheck.setString(1, group);
						ResultSet rs2 = psCheck.executeQuery();
						while(rs2.next()){
							int a = rs2.getInt("appointmentID");
							if (!rs2.wasNull()){
								this.deleteAppointment(a);
							}
						}
					}//endif user.equals(founder)
				}
			}
			return b;
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
				if (psCheck != null)
					psCheck.close();
				if (psLimpieza != null)
					psLimpieza.close();
			} catch (Exception e1) {
			}
		}
		return b;
	}

	public ArrayList<ExappAppointment> getAllAppointmentsByGroup(String group){
		Connection con = null;
		PreparedStatement ps = null;
		PreparedStatement ps_Appointment = null;
		ResultSet rs = null;	
		ResultSet rs_Appointment = null;
		ArrayList<ExappAppointment> list = new ArrayList<ExappAppointment>();
		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			
			ps = con.prepareStatement("SELECT appointmentID FROM `LinksGroupsAppointments` WHERE groupID=?");
			ps.setString(1, group);
			rs = ps.executeQuery();
			while (rs.next()) {
				int appointmentID = rs.getInt("appointmentID");
				if (!rs.wasNull()){
					ps_Appointment = con.prepareStatement("SELECT title, date, time, place, description FROM Appointment WHERE appointmentID=?");
					ps_Appointment.setInt(1, appointmentID);
					rs_Appointment = ps_Appointment.executeQuery();
					if (rs_Appointment.next()){
						String title = rs_Appointment.getString("title");
						Date date = rs_Appointment.getDate("date");
						Time time = rs_Appointment.getTime("time");
						String place = rs_Appointment.getString("place");
						String description = rs_Appointment.getString("description");
						list.add(new ExappAppointment(appointmentID, title, place, date, time, description, group));
					}
				}
			}
			return list;
			} catch (SQLException | ClassNotFoundException e1) {
				e1.printStackTrace();
			} finally {
				try {
					if (con != null)
						con.close();
					if (ps != null)
						ps.close();
					if (ps_Appointment != null)
						ps_Appointment.close();
				} catch (Exception e1) {
				}
			}
			return list;
	}
	
	public ArrayList<String> getAllMembers(String group){
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		ArrayList<String> list = new ArrayList<String>();
	
		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			ps = con.prepareStatement("SELECT userID FROM `LinksGroupsUsers` WHERE groupID=?");
			ps.setString(1, group);
			rs = ps.executeQuery();
			while (rs.next()) {
				String user= rs.getString("userID");
				if (!rs.wasNull()){
					list.add(user);
				}
			}
			return list;
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
			} catch (Exception e1) {
			}
		}
		return list;
	}
	
	
	// Discussions related methods
	public boolean insertDiscussion(String groupID, ExappDiscussion d){
		Connection con = null;
		PreparedStatement ps_insert = null;
		PreparedStatement ps_insert2 = null;
		ResultSet rs = null;
		boolean b = false;					
		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			ps_insert = con.prepareStatement("INSERT INTO `Discussion`(`userID`, `title`, `body`, `time`, `date`) "
					+ "VALUES (?,?,?,?,?)");
			ps_insert.setString(1, d.getUserID());
			ps_insert.setString(2, d.getTitle());
			ps_insert.setString(3, d.getBody());
			ps_insert.setTime(4, d.getTime());
			ps_insert.setDate(5, d.getDate());
			int numRows = ps_insert.executeUpdate();
			rs = ps_insert.getGeneratedKeys();
			
			if (numRows == 1) {
				if (rs.next()) {
					int id = rs.getInt(1);
					if (!rs.wasNull()) {
						d.setDiscussionID(id);
						ps_insert2 = con.prepareStatement("INSERT INTO `LinkGroupDiscussion`(`groupID`, `discussionID`) "
								+ "VALUES (?,?)");
						ps_insert2.setString(1, groupID);
						ps_insert2.setInt(2, id);
						int numRows2 = ps_insert2.executeUpdate();
						if (numRows2 == 1)
							b=true;
					}
				}
			}
		return b;
		} catch (SQLException | ClassNotFoundException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
				if (ps_insert != null)
					ps_insert.close();
				if (ps_insert2 != null)
					ps_insert2.close();
			} catch (Exception e1) {
			}
		}
		return b;
	}
	
	public boolean deleteDiscussion(int id){
		Connection con = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		boolean b = false;
		
		try{
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			//Delete Discussion
			ps = con.prepareStatement("DELETE FROM `Discussion` WHERE discussionID=?");
			ps.setInt(1, id);
			int l1 = ps.executeUpdate();
			//Delete from LinkGroupDiscussion
			ps2 = con.prepareStatement("DELETE FROM `LinkGroupDiscussion` WHERE discussionID=?");
			ps2.setInt(1, id);
			int l2 = ps2.executeUpdate();
			
			if ((l1==1) && (l2==1))
				b = true;
			return b;
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
		return b;
	}
	
	public ExappDiscussionBoard getExappDiscussion(String group){
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;	
		ExappDiscussionBoard d = new ExappDiscussionBoard(group);
		
		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			
			ps=con.prepareStatement("SELECT discussionID, userID, title, body, time, date FROM `Discussion` WHERE discussionID "
					+ "IN (SELECT discussionID FROM `LinkGroupDiscussion` WHERE groupID=?) ORDER BY discussionID DESC");
			//most modern 
			ps.setString(1, group);
			rs = ps.executeQuery();
			while (rs.next()){
				int discussionID=rs.getInt("discussionID");
				if(!rs.wasNull()){
					String userID=rs.getString("userID");
					String title = rs.getString("title");
					String body = rs.getString("body");
					Time time = rs.getTime("time");
					Date date = rs.getDate("date");
					d.getList().add(new ExappDiscussion(discussionID, userID, title, body, time, date));
				}
			}
			return d;
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
			return d;
	}
	
	// Statistics
	public ArrayList<ExappAppointment> getAttendedAppointments(String user,String groupID){
		Connection con = null;
		PreparedStatement ps_LinksUsersAppointments = null;
		PreparedStatement ps_Appointment = null;
		ResultSet rs_LinksUsersAppointments = null;
		ResultSet rs_Appointment = null;
		
		ArrayList<ExappAppointment> list = new ArrayList<ExappAppointment>();

		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			ps_LinksUsersAppointments = con.prepareStatement("SELECT appointmentID FROM `LinksUsersAppointments` WHERE "
					+ "userID=? AND appointmentID IN (SELECT appointmentID FROM `LinksGroupsAppointments` WHERE groupID=?) AND accepted='1'");
			ps_LinksUsersAppointments.setString(1, user);
			ps_LinksUsersAppointments.setString(2, groupID);
			rs_LinksUsersAppointments = ps_LinksUsersAppointments.executeQuery();
			while (rs_LinksUsersAppointments.next()) {
				int appointmentID= rs_LinksUsersAppointments.getInt("appointmentID");
				if (!rs_LinksUsersAppointments.wasNull()){
					ps_Appointment=con.prepareStatement("SELECT date, title, time, place, description FROM `Appointment` WHERE appointmentID=? ORDER BY date, time ASC");
					ps_Appointment.setInt(1, appointmentID);
					rs_Appointment = ps_Appointment.executeQuery();
					if (rs_Appointment.next()){
						String title = rs_Appointment.getString("title");
						Date dateDB = rs_Appointment.getDate("date");
						Time timeDB = rs_Appointment.getTime("time");
						String place = rs_Appointment.getString("place");
						String description = rs_Appointment.getString("description");
						if (!rs_Appointment.wasNull()){
							String group = DBManager.getInstance(context).getGroupOfAppointment(appointmentID);
							ExappAppointment appointment = new ExappAppointment(appointmentID, title, place, dateDB, timeDB, description, group);
							list.add(appointment);
						}
					}
				}
			}
			return list;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
				if (ps_LinksUsersAppointments != null)
					ps_LinksUsersAppointments.close();
				if (ps_Appointment != null)
					ps_Appointment.close();
			} catch (Exception e1) {
			}
		}
		return list;
	}
	
	
	public ArrayList<ExappDiscussion> getAllDiscussionsWrittenByUser(String user){
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;	
		ArrayList<ExappDiscussion> list = new ArrayList<ExappDiscussion>();
		
		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			
			ps=con.prepareStatement("SELECT discussionID, title, body, time, date FROM `Discussion` WHERE userID=?");
			ps.setString(1, user);
			rs = ps.executeQuery();
			if (rs.next()){
				int id=rs.getInt("discussionID");
				if(!rs.wasNull()){
					String title = rs.getString("title");
					String body = rs.getString("body");
					Time time = rs.getTime("time");
					Date date = rs.getDate("date");
					list.add(new ExappDiscussion(id, user, title, body, time, date));
				}
			}
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
	
	public ArrayList<ExappDiscussion> getAllDiscussionsWrittenByUserAndGroup(String user, String group){
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;	
		ArrayList<ExappDiscussion> list = new ArrayList<ExappDiscussion>();
		
		try {
			Class.forName(DriverName);
			con = DriverManager.getConnection(urlDB, userDB, passwordDB);
			
			ps=con.prepareStatement("SELECT discussionID, title, body, time, date FROM `Discussion` WHERE userID=? "
					+ "AND discussionID IN (SELECT discussionID FROM `LinkGroupDiscussion` WHERE groupID=?)");
			ps.setString(1, user);
			ps.setString(2, group);
			rs = ps.executeQuery();
			if (rs.next()){
				int id=rs.getInt("discussionID");
				if(!rs.wasNull()){
					String title = rs.getString("title");
					String body = rs.getString("body");
					Time time = rs.getTime("time");
					Date date = rs.getDate("date");
					list.add(new ExappDiscussion(id, user, title, body, time, date));
				}
			}
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
	
	// For the application
	@Override
	protected Object doInBackground(Object... params) {
		
		switch (params[0].toString()){
		//Image methods (images are stored in a different database)
		case "uploadNewPicture": return this.uploadNewPicture((Bitmap) params[1]);
		case "getPicture": return this.getPicture((int) params[1]);
		
		//login
		case "login": return this.login(params[1].toString(), params[2].toString());
		case "getProfile": return this.getProfile(params[1].toString());
		case "insertUser": return this.insertOrUpdateUser((ExappUser)params[1], (boolean) params[2], (Bitmap) params[3]);
		
		// Checkers
		case "checkUserExists": return this.checkUserExists(params[1].toString());
		case "checkGroupExists": return this.checkGroupExists(params[1].toString());
		case "checkAppointmentExists": return this.checkAppointmentExists((int)params[1]);
		case "checkUserBelongsToGroup": return this.checkUserBelongsToGroup(params[1].toString(), params[2].toString());
		
		// Global methods
		case "getAllUsers": return this.getAllUsers();
		case "getAllSubjects": return this.getAllSubjects();
		case "getAllGroupsInDB": return this.getAllGroups();
		
		// Global "getters"
		case "getAllGroups": return this.getAllGroupsByUser(params[1].toString());
		case "getAllAppointmentsByUser": return this.getAllAppointmentsByUser(params[1].toString());
		case "getAllDiscussionsByUser": return this.getAllDiscussionsByUser(params[1].toString());
		case "getGroupsBySubject": return this.getGroupsBySubject(params[1].toString());
		case "getInvitations": return this.getInvitations(params[1].toString());
		
		// Appointments related methods
		case "getGroupOfAppointment": return this.getGroupOfAppointment((int)params[1]);
		case "setAppointment": return this.setAppointment((ExappAppointment)params[1]);
		case "acceptAppointment": return this.acceptAppointment((int) params[1], params[2].toString(), (boolean) params[3]);
		case "deleteAppointment": return this.deleteAppointment((int) params[1]);
		case "getAppointmentById": return this.getAppointmentById((int)params[1]);
		case "getUsersAppointmentState": return this.getUsersAppointmentState((int) params[1]);
		case "getFutureAppointments": return this.getFutureAppointments(params[1].toString(), (Date)params[2]);
		case "hasUserAcceptedAppointment": return this.hasUserAcceptedAppointment(params[1].toString(), (int)params[2]);
		
		// Groups related methods
		case "getFounder": return this.getFounder(params[1].toString());
		case "getGroupById": return this.getGroupById(params[1].toString());
		case "joinGroup": return this.joinGroup(params[1].toString(), params[2].toString());
		case "quitGroup": return this.quitGroup(params[1].toString(), params[2].toString());
		case "changeOrAddGroup": return createOrUpdateGroupByUser((ExappUser)params[1], (ExappGroup)params[2]);
		case "deleteGroup": return this.deleteGroup(params[1].toString(), params[2].toString());
		case "getAllAppointmentsByGroup": return this.getAllAppointmentsByGroup(params[1].toString());
		case "getAllMembers": return this.getAllMembers(params[1].toString());
		
		// Discussions related methods
		case "insertDiscussion": return this.insertDiscussion(params[1].toString(), (ExappDiscussion)params[2]);
		case "deleteDiscussion": return this.deleteDiscussion((int)params[1]);
		case "getExappDiscussion":return this.getExappDiscussion(params[1].toString());
		
		//Statistics
		case "getAttendedAppointments": return this.getAttendedAppointments(params[1].toString(), params[2].toString());
		case"getAllDiscussionsWrittenByUser": return this.getAllDiscussionsWrittenByUser(params[1].toString());
		case "getAllDiscussionsWrittenByUserAndGroup": return this.getAllDiscussionsWrittenByUserAndGroup(params[1].toString(), params[2].toString());
		}
		return null;
	}

}

