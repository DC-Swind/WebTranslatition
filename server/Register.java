package server;
import message.*;
public class Register {
	public User user;
	public RegistResponse return_ans = new RegistResponse(); 
	
	Register(RegistRequest msg,String ip,int port){
		System.out.println("start register!");
		Connectdb conn = new Connectdb();
		ip = ip.substring(1,ip.length());
		int code = conn.register(msg.UID,msg.UName,msg.PassWord,ip,port);
		if (code == -1){
			System.out.println("register failed!");
			return_ans.identifier = false;
		}else{
			System.out.println("register success!");
			return_ans.identifier = true;
		}
		conn.disconnect();
		
		if (return_ans.identifier == false) return;
		
		
		user = new User(msg.UID,msg.UName);
		Mailbox.lock.lock();
		Mailbox.userlist.list.add(user);
		Mailbox.userlist_updated_time_add();
		Mailbox.lock.unlock();
	}
}
