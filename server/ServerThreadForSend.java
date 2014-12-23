package server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.locks.*;

import message.Card;
import message.Message;
import message.RecvShare;
import message.User;
import message.UserList;

public class ServerThreadForSend implements Runnable{
	public User user;
	public ObjectOutputStream outputToClient;
	public boolean running = true;
	public Lock lock;
	public Mailbox mailbox;
	
	ServerThreadForSend(User user,ObjectOutputStream output,Lock lock){
		this.outputToClient = output;
		this.lock = lock;
		this.user = user;
		mailbox = new Mailbox();
	}
	
	public void run() {
		Mailbox.lock.lock();
		mailbox.getamailindex(user);
		Mailbox.lock.unlock();
		if (mailbox.mymailboxindex == -1){
			System.out.println("get mailbox failed!");
			stop();
		}
		while(running){
			Mailbox.lock.lock();
			//check userlist
			if (mailbox.userlist_checked_time != Mailbox.userlist_updated_time){
				mailbox.userlist_checked_time = Mailbox.userlist_updated_time;
				try{
					sendUserlist();
				}catch(Exception e){
					System.out.println("send User list failed! from ServerThreadForSend.java");
					Mailbox.lock.unlock();
					lock.unlock();
					break;
				}
			}
			Mailbox.lock.unlock();
			
			Mailbox.lock.lock();
			//check card
			if (Mailbox.sharecard[mailbox.mymailboxindex]){
				try{
					sendCard(Mailbox.getCard(mailbox.mymailboxindex));
				}catch(Exception e){
					System.out.println("send card failed! from ServerThreadForSend.java");
					Mailbox.lock.unlock();
					lock.unlock();
					break;
				}
			} 
			
			Mailbox.lock.unlock();
		}
		stop();
		System.out.println("send thread reback");
	}
	private void sendCard(Card card) throws IOException {
		RecvShare recvshare = new RecvShare();
		recvshare.card = card;
		Message<RecvShare> msg = new Message<RecvShare>(recvshare);
		msg.type = Message.SHARE;	

		lock.lock();
		outputToClient.writeObject(msg);
		lock.unlock();
	}

	public void stop(){
		//reback mailbox
		Mailbox.lock.lock();
		mailbox.rebackmailbox();
		Mailbox.lock.unlock();
		running = false;
	}
	private void sendUserlist() throws IOException{
		lock.lock();
		Message<UserList> msg = new Message<UserList>(Mailbox.userlist);
		msg.type = Message.USER_LIST;
		outputToClient.reset();
		outputToClient.writeObject(msg);
		
		lock.unlock();
	}
	
}
