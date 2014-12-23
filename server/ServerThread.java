package server;
import message.*;

import java.io.*;
import java.net.*;
import java.util.concurrent.locks.*;


public class ServerThread implements Runnable{
	public Socket socket;
	private ThreadPool threadpoolforsend;
	public User user = null;
	public Lock lock = new ReentrantLock();
	public ObjectInputStream inputFromClient;
	public ObjectOutputStream outputToClient;
	public ServerThreadForSend sendthread = null;
	
	ServerThread(Socket socket,ThreadPool threadpoolforsend){
		System.out.println("A new thread is created!");
		this.socket = socket;
		this.threadpoolforsend = threadpoolforsend;
        
		try{
			inputFromClient = new ObjectInputStream(socket.getInputStream());
			outputToClient = new ObjectOutputStream(socket.getOutputStream());
		}catch(Exception e){
			System.out.println(e.toString() + "...Create IO Stream failed! from ServerThread.java");
		}
		
	}
	public void run(){

        while (true){
        	Object msg = null;
        	Object rtmsg = null;
        	try{
        		msg = inputFromClient.readObject();
        		
        		//this can remove
        		System.out.println("Receive a Message From Client: ");
        		
        		rtmsg = analysis(msg);

        	}catch(Exception e){
        		System.out.println(e.toString() + "A connection with client disconnect! from ServerThread.java");
        		break;
        	}
        	
        	//the following 3 sentence can remove
        	if (msg == null) System.out.println("msg is null");
        	if (lock == null) System.out.println("lock is null");	
        	if (outputToClient == null) System.out.println("outputstream is null");
        	
        	if (rtmsg != null){
        		try{
        			lock.lock();	
        			outputToClient.writeObject(rtmsg);
        			lock.unlock();
        		}catch(Exception e){
        			System.out.println(e.toString() + "Ouput to Client error! from ServerThread.java");
        			lock.unlock();
        			break;
        		}
        	}
        }
        
        stop();
	}
	private void startsendthread(){
		sendthread = new ServerThreadForSend(user,outputToClient,lock);
		this.threadpoolforsend.add(sendthread);
	}
	private void stop(){
		if (sendthread != null){
			System.out.println("send thread is not stop,start stop");
			sendthread.running = false;
			sendthread.stop();
		}
		if (user == null){
			//un login
		}else{
			Logout logout = new Logout(user);
		}
	}
	private Object analysis(Object msg){ 
		if (msg == null){
			System.out.println("receive a null msg");
			return null;
		}
		Message<?> message = (Message<?>) msg; 
		
		if (message.type == message.REGIST){
			Register register = new Register(((Message<RegistRequest>)msg).content,socket.getInetAddress().toString(),socket.getPort());
			Message<RegistResponse> response = new Message<RegistResponse>(register.return_ans);
			if (register.return_ans.identifier){
				user = register.user;
				startsendthread();
			}
			response.type = message.REGIST;
			return response;
			
		}else if (message.type == message.LOGIN){
			Login login = new Login(((Message<LoginRequest>)msg).content,socket.getInetAddress().toString(),socket.getPort());
			Message<LoginResponse> response = new Message<LoginResponse>(login.return_ans);
			if (login.return_ans.identifier){
				user = login.user;
				startsendthread();
			}
			
			response.type = message.LOGIN;
			return response;
			
		}else if (message.type == message.LOGOUT){
			stop();
		}else if (message.type == message.LIKE){
			LikeCheck like = new LikeCheck(((Message<Like>)msg).content,user);
			//likecheck
			return null;
		}else if (message.type == message.SEARCH){
			Translate translate = new Translate(((Message<SearchRequest>) msg).content,user);
			Message<SearchResponse> response= new Message<SearchResponse>(translate.return_ans);
			response.type = message.SEARCH;
			return response;
			
		}else if (message.type == message.SHARE){
			System.out.println("receive a share");
			Share share = new Share(((Message<SendShare>)msg).content,user);
			
			return null;
		}
		/*
		else if (message.type == message.USER_LIST){
			Userlist userlist = new Userlist();
		}
		*/
		return null;
	} 
}
