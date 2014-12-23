package server;
import message.*;
public class Login {
	public User user;
	public LoginResponse return_ans = new LoginResponse();
	Login(LoginRequest msg,String ip,int port){
		System.out.println("start login!");
		Connectdb conn = new Connectdb();
		ip = ip.substring(1,ip.length());
		String username = conn.login(msg.userID,msg.userPassword,ip,port);
		if (username.equals("")){
			System.out.println("login failed!");
			return_ans.identifier = false;
		}else{
			System.out.println("login success!");
			return_ans.identifier = true;
			return_ans.userName = username;
		}
		conn.disconnect();
		
		if (return_ans.identifier == false) return;
		
		user = new User(msg.userID,return_ans.userName);
		Mailbox.lock.lock();;
		Mailbox.userlist.list.add(user);
		Mailbox.userlist_updated_time_add();
		Mailbox.lock.unlock();
	}
}
