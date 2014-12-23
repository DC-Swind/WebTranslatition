package server;

import message.SendShare;
import message.User;

public class Share {

	Share(SendShare sendshare,User user){
		if (sendshare.sendToAll){
			Mailbox.lock.lock();
			Mailbox.alladdCard(sendshare.card);
			Mailbox.lock.unlock();
		}else{
			Mailbox.lock.lock();
			Mailbox.someaddCard(sendshare.list, sendshare.card);
			Mailbox.lock.unlock();
		}
	}
}
