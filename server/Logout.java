package server;

import message.User;

public class Logout {
	Logout(User user){
		System.out.println("start logout!");
		Connectdb conn = new Connectdb();
		
		int code = conn.logout(user.UserID);
		if (code == -1){
			System.out.println("logout failed!");
		}else{
			Mailbox.lock.lock();
			Mailbox.userlist.list.remove(user);
			Mailbox.userlist_updated_time_add();
			Mailbox.lock.unlock();
			System.out.println("logout success!");
		}

		conn.disconnect();
	}
}
