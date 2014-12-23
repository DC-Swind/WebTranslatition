
package server;
import java.sql.*;

import tools.GetTranslationReturn;

public class Connectdb {
	private Connection conn = null;
	private Statement stmt;
	private PreparedStatement pstmt;
	public int userid;
	
	public Connectdb(){
        //mysql connect url
        String url = "jdbc:mysql://localhost:3306/lab2?useUnicode=true&characterEncoding=UTF8";
 
        try {
            // 动态加载mysql驱动
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("成功加载MySQL驱动程序");
            
            //connect to database
            conn = DriverManager.getConnection(url,"root","metis");
            System.out.println("connect 数据库成功");
            
            // Statement里面带有很多方法，比如executeUpdate可以实现插入，更新和删除等
            stmt = conn.createStatement();
            
            
        } catch (SQLException e) {
            System.out.println("MySQL操作错误");
            System.err.println("sql exception:" + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (conn == null) System.out.println("conn is null");
	}
	public void likeupdate(String uid,String word,int which){
		ResultSet rs = query("select TID from translation where English = '"+word+"'");
		int tid = -1;
		try{
			if (rs.next()) tid = rs.getInt(1); 
		}catch(Exception e){
			System.out.println(e.toString() + "Connectdb-->likeupdate()-1");
		}
		if (tid == -1) return;
		rs = query("select T"+which+" from likecheck where TID = '"+tid+"' and UID = '"+uid+"'");
		
		int haveRow = -1;
		int valid = 0;
		try{
			//do not have a row, so use insert
			if (!rs.next()) haveRow = 0; 
			//have a row , so use update
			else{
				haveRow = 1;
				valid = rs.getInt(1);
			}
		}catch(Exception e){
			System.out.println(e.toString() + "Connectdb-->likeupdate()-2");
		}
		if (haveRow == -1) return;
		if (haveRow == 0){
			update("insert into likecheck(TID,UID,T"+which+") values('"+tid+"','"+uid+"','1')");
			update("update translation set Like"+which+" = Like"+which+" + 1 where TID = '"+tid+"'");
		}
		if (haveRow == 1 && valid == 0){
			update("update likecheck set T"+which+" = '1' where TID = '"+tid+"' and UID = '"+uid+"' ");
			update("update translation set Like"+which+" = Like"+which+" + 1 where TID = '"+tid+"'");
		}
	}
	
	public int clearLoginStatus(){
		int code = -1;
		String sql = "update user set LoginStatus = '0'";
		code = update(sql);
		return code;
	}
	public int register(String uid,String user,String pass,String ip,int port){
		//int code = update("insert into user(UID,User,Pass,LoginStatus,IP,Port) values('"+uid+"','"+user+"','"+pass+"','1','"+ip+"','"+port+"')");
		int code = -1;
		String sql1 = "insert into user(UID,User,Pass,LoginStatus,IP,Port) values(?,?,?,'1',?,?)";
		try{
			
			pstmt = conn.prepareStatement(sql1);
			pstmt.setString(1, uid);
			pstmt.setString(2, user);
			pstmt.setString(3, pass);
			pstmt.setString(4, ip);
			pstmt.setInt(5, port);
			code = pstmt.executeUpdate();
		}catch(Exception e){
			if (conn == null) System.out.println("conn is null in login");
			if (pstmt == null) System.out.println("pstmt is null in login");
			System.out.println("Connectdb-register-1 "+e.toString());
		}
		return code;
	}
	public String login(String uid,String pass,String ip,int port){
		//ResultSet rs = query("select User from user where UID = '"+uid+"' AND Pass = '"+pass+"'");
		ResultSet rs = null;
		String sql1 = "select User,LoginStatus from user where UID = ? AND Pass = ?";
		try{
			pstmt = conn.prepareStatement(sql1);
			pstmt.setString(1, uid);
			pstmt.setString(2, pass);
			rs = pstmt.executeQuery();
		}catch(Exception e){
			System.out.println("Connectdb-login-1 "+e.toString());
		}
		
		String username = "";
		int loginstatus = -1;
		try{
			if (rs.next()){
				username = rs.getString(1);
				loginstatus = rs.getInt(2);
			}
		}catch(Exception e){
			username = "";
			//System.out.println();
		}
		//if no this user or already login return failed
		if (username.equals("") || loginstatus != 0) return "";
		
		
		//int code = update("UPDATE user SET LoginStatus = '1',IP = '"+ip+"',Port = '"+port+"' WHERE UID = '"+uid+"'");
		int code = -1;
		String sql2 = "UPDATE user SET LoginStatus = '1',IP = ?,Port = ? WHERE UID = ?";
		try{
			if (conn == null) System.out.println("conn is null in login");
			pstmt = conn.prepareStatement(sql2);
			if (pstmt == null) System.out.println("pstmt is null in login");
			pstmt.setString(1, ip);
			pstmt.setInt(2, port);
			pstmt.setString(3, uid);
			code = pstmt.executeUpdate();
		}catch(Exception e){
			System.out.println("Connectdb-login-1 "+e.toString());
		}
		
		if (code == -1) username = "";
		return username;
	}
	public int logout(String uid){
		//int code = update("UPDATE user SET LoginStatus = '0' WHERE UID = '"+uid+"'");
		int code = -1;
		String sql = "UPDATE user SET LoginStatus = '0' WHERE UID = ?";
		try{
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, uid);
			code = pstmt.executeUpdate();
		}catch(Exception e){
			System.out.println("Connectdb-logout "+e.toString());
		}
		return code;
	}
	public int ExistWord(String word){
		//ResultSet rs = query("select TID FROM translation WHERE English = '"+word+"'");
		ResultSet rs = null;
		String sql = "select TID FROM translation WHERE English = ?";
		try{
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, word);
			rs = pstmt.executeQuery();
		}catch(Exception e){
			System.out.println("Connectdb-ExistWord "+e.toString());
		}
		int tid = -1;
		try{
			if (rs.next()) tid = rs.getInt(1);
		}catch(Exception e){
			tid = -1;
		}
		return tid;
	}
	public int addtranslation(String word,String c1,String p1,String c2,String p2,String c3,String p3){
		//int code = update("INSERT INTO translation(English,Chinese1,Chinese2,Chinese3,Phonetic1,Phonetic2,Phonetic3,Valid) values('"+word+"','"+c1+"','"+c2+"','"+c3+"','"+p1+"','"+p2+"','"+p3+"','1')");
		int code = -1;
		String sql1 = "INSERT INTO translation(English,Chinese1,Chinese2,Chinese3,Phonetic1,Phonetic2,Phonetic3,Valid) values(?,?,?,?,?,?,?,?)";
		try{
			pstmt = conn.prepareStatement(sql1);
			pstmt.setString(1, word);
			pstmt.setString(2, c1);
			pstmt.setString(3, c2);
			pstmt.setString(4, c3);
			pstmt.setString(5, p1);
			pstmt.setString(6, p2);
			pstmt.setString(7, p3);
			pstmt.setInt(8, 1);
			code = pstmt.executeUpdate();
		}catch(Exception e){
			System.out.println("Connectdb-addtranslation "+e.toString());
		}
		
		return code;
	}
	public int updatetranslation(int tid,String c1,String p1,String c2,String p2,String c3,String p3){
		/*
		String sql = "UPDATE translation SET ";
		if (c1.equals("")){} else{ sql = sql + "Chinese1 = '"+c1+"', Phonetic1 = '"+p1+"',"; }
		if (c2.equals("")){} else{ sql = sql + "Chinese2 = '"+c2+"', Phonetic2 = '"+p2+"',"; }
		if (c3.equals("")){} else{ sql = sql + "Chinese3 = '"+c3+"', Phonetic3 = '"+p3+"',"; }
		sql = sql + "Valid = '1' WHERE TID = '"+tid+"'";
		int code = update(sql);
		*/
		int code = -1;
		String sql1 = "UPDATE translation SET Chinese1 = ? , Phonetic1 = ? WHERE TID='"+tid+"'";
		if (c1.equals("")){}else{
			try{
				pstmt = conn.prepareStatement(sql1);
				pstmt.setString(1, c1);
				pstmt.setString(2, p1);
				code = pstmt.executeUpdate();
			}catch(Exception e){
				System.out.println("Connectdb-updatetranslation-1 "+e.toString());
			}
		}
		String sql2 = "UPDATE translation SET Chinese2 = ? , Phonetic2 = ? WHERE TID='"+tid+"'";
		if (c2.equals("")){}else{
			try{
				pstmt = conn.prepareStatement(sql2);
				pstmt.setString(1, c2);
				pstmt.setString(2, p2);
				code = pstmt.executeUpdate();
			}catch(Exception e){
				System.out.println("Connectdb-updatetranslation-2 "+e.toString());
			}
		}
		String sql3 = "UPDATE translation SET Chinese3 = ? , Phonetic3 = ? WHERE TID='"+tid+"'";
		if (c3.equals("")){}else{
			try{
				pstmt = conn.prepareStatement(sql3);
				pstmt.setString(1, c3);
				pstmt.setString(2, p3);
				code = pstmt.executeUpdate();
			}catch(Exception e){
				System.out.println("Connectdb-updatetranslation-3 "+e.toString());
			}
		}
		return code;
	}
	public GetTranslationReturn gettranslation(int tid,String userid){
		String sql1 = "select Chinese1,Chinese2,Chinese3,Like1,Like2,Like3,Phonetic1,Phonetic2,Phonetic3 FROM translation WHERE TID = ?";
		//ResultSet rs = query("select Chinese1,Chinese2,Chinese3,Like1,Like2,Like3,Phonetic1,Phonetic2,Phonetic3 FROM translation WHERE TID = '"+tid+"'"); 
		ResultSet rs = null;
		try{
			pstmt = conn.prepareStatement(sql1);
			pstmt.setInt(1, tid);
			rs = pstmt.executeQuery();
		}catch(Exception e){
			System.out.println("Connectdb-gettranslation-1 "+e.toString());
		}
		GetTranslationReturn ans = null;
		
		try{
			if (rs.next())
				ans = new GetTranslationReturn(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(7),rs.getString(8),rs.getString(9),rs.getInt(4),rs.getInt(5),rs.getInt(6));
		}catch(Exception e){
			System.out.println(e.toString());
			return null;
		}	
		
		if (ans == null) return ans;
		if (userid == null){
			ans.addinfo(1, 1, 1);
			return ans;
		}
		
		String sql2 = "select T1,T2,T3 FROM likecheck WHERE TID = ? AND UID = ?";
		//rs = query("select T1,T2,T3 FROM likecheck WHERE TID = '"+tid+"' AND UID = '"+userid+"'");
		try{
			pstmt = conn.prepareStatement(sql2);
			pstmt.setInt(1, tid);
			pstmt.setString(2, userid);
			rs = pstmt.executeQuery();
		}catch(Exception e){
			System.out.println("Connectdb-gettranslation-2 "+e.toString());
		}
		
		try{
			if (rs.next()) ans.addinfo(rs.getInt(1), rs.getInt(2), rs.getInt(3));
			else ans.addinfo(0, 0, 0);
		}catch(Exception e){
			System.out.println(e.toString());
			return null;
		}	
		return ans;
	}
	public int update(String sql){
		int result = -1;
		
		try{
			result = stmt.executeUpdate(sql);
		}catch(Exception e){
			System.out.println("update failed!");
		}
		
		return result;
	}
	public ResultSet query(String sql){
		ResultSet rs = null;
		try{
			rs = stmt.executeQuery(sql);// executeQuery会返回结果的集合
		}catch(Exception e){
			System.out.println("query failed!--"+e.toString());
		}
		return rs;
	}
	
	public void disconnect(){
		//disconnect
		try{
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.out.println("MySQL操作错误");
            System.err.println("sql exception:" + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
