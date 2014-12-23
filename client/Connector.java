package client;

import java.awt.Font;
import java.awt.Label;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JFrame;

import message.LoginRequest;
import message.LoginResponse;
import message.Message;
import message.RecvShare;
import message.RegistResponse;
import message.SearchResponse;
import message.SendShare;
import message.User;
import message.UserList;

public class Connector implements Runnable
{
	private UI ui;
	private Socket connection;
	private ObjectInputStream fromServer;
	public ObjectOutputStream toServer;
	public boolean connection_successful;
	public boolean run=true;
	private String IP;
	public Connector(UI ui,String IP)
	{
		this.IP=IP;
		this.ui=ui;
	}
	public void set_stop()
	{
		run=false;
	}
	public void run()
	{
		//有断线重连
		while(run)
		{
			init_connection();
			try
			{
				receiver();
			} 
			catch (Exception e){}
		}
		try
		{
			fromServer.close();
			toServer.close();
			connection.close();
		} 
		catch (IOException e){}
	}
	private void init_connection()
	{
		ui.logedin=false;
		ui.jpLeft.removeAll();
		ui.jpLeft.add(ui.jpUser);
		ui.jpLeft.updateUI();
		if(ui.locked)ui.word=null;
		ui.locked=false;
		connection_successful=false;
		ui.setTitle("Client(offline)");
		while(run)
		{
			try
			{
				connection=new Socket(IP,18800);
				toServer=new ObjectOutputStream(connection.getOutputStream());
				fromServer=new ObjectInputStream(connection.getInputStream());
				if(ui.logedin)
				{
					Message<LoginRequest> temp=new Message<LoginRequest>();
					temp.type=Message.LOGIN;
					temp.content=new LoginRequest(ui.myself.UserID,ui.userPass);
					toServer.writeObject(temp);
				}
				break;
			}
			catch (Exception e)
			{
				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e1){}
			}
		}
		ui.setTitle("Client");
		connection_successful=true;
	}
	private void receiver() throws Exception
	{
		while(run)
		{
			@SuppressWarnings("unchecked")
			Message<Object> msg=(Message<Object>)fromServer.readObject();
			new Thread(new Handler(ui,msg)).start();
		}
	}	
}
class Handler implements Runnable
{
	UI ui;
	Message<Object> msg;
	public Handler(UI ui,Message<Object> msg)
	{
		this.ui=ui;
		this.msg=msg;
	}
	public void run()
	{
		switch (msg.type)
		{
			case Message.LOGIN:	
				handle_login((LoginResponse)msg.content);
				break;
			case Message.REGIST:	
				handle_regist((RegistResponse)msg.content);
				break;
			case Message.SEARCH:	
				handle_search((SearchResponse)msg.content);
				break;
			case Message.USER_LIST:	
				handle_user_list((UserList)msg.content);
				break;
			case Message.SHARE:			
				handle_share((RecvShare)msg.content);
				break;
			default:
				break;
		}
	}
	private void handle_share(RecvShare msg)
	{
		ui.mailBox.add(msg.card);
		ui.repaint_jpIcons();
	}
	private void handle_user_list(UserList msg)
	{
		ui.onlineUser=msg.list;
		ui.users.clear();
		for(User temp:msg.list)
		{
			ui.users.addElement(temp.UserID+"_"+temp.UserName);
		}
	}
	private void handle_search(SearchResponse msg)
	{
		ui.rpBaidu.setResult(msg.Baidu_Selected,(msg.Baidu_Selected&&msg.Baidu_Meaning.equals(""))?"Not Found":msg.Baidu_Meaning,msg.Baidu_Liked,msg.Baidu_Liked_Count);
		ui.rpBing.setResult(msg.Bing_Selected,(msg.Bing_Selected&&msg.Bing_Meaning.equals(""))?"Not Found":msg.Bing_Meaning,msg.Bing_Liked,msg.Bing_Liked_Count);
		ui.rpYoudao.setResult(msg.Youdao_Selected,(msg.Youdao_Selected&&msg.Youdao_Meaning.equals(""))?"Not Found":msg.Youdao_Meaning,msg.Youdao_Liked,msg.Youdao_Liked_Count);
		ui.repaint_jpMUI();
		ui.locked=false;
	}
	private void handle_regist(RegistResponse msg)
	{
		ui.logedin=msg.identifier;
		ui.jpLeft.removeAll();
		if(msg.identifier==false)
		{
			JFrame temp=new JFrame();
			temp.setTitle("提示信息");
			temp.setSize(350,100);
			temp.setLocationRelativeTo(null);
			temp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			temp.setVisible(true);
			Label lab=new Label("您输入的账号已存在，注册失败");
			lab.setAlignment(Label.CENTER);
			lab.setFont(new Font("微软雅黑",18,Font.PLAIN));
			temp.add(lab);
			ui.jpLeft.add(ui.jpUser);
		}
		else
		{
			ui.jlbUserName.setText("用户名："+ui.myself.UserName);
			ui.jlbUserNo.setText("账号："+ui.myself.UserID);
			ui.jpLeft.add(ui.jpUserInfo);
		}
		ui.jpLeft.updateUI();
	}
	private void handle_login(LoginResponse msg)
	{
		ui.logedin=msg.identifier;
		ui.jpLeft.removeAll();
		if(msg.identifier==false)
		{
			JFrame temp=new JFrame();
			temp.setTitle("提示信息");
			temp.setSize(350,100);
			temp.setLocationRelativeTo(null);
			temp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			temp.setVisible(true);
			Label lab=new Label("用户ID或密码错误，登录失败");
			lab.setAlignment(Label.CENTER);
			lab.setFont(new Font("微软雅黑",18,Font.PLAIN));
			temp.add(lab);
			ui.jpLeft.add(ui.jpUser);
		}
		else
		{
			ui.jlbUserName.setText("用户名："+msg.userName);
			ui.jlbUserNo.setText("账号："+ui.myself.UserID);
			ui.jpLeft.add(ui.jpUserInfo);
			ui.myself.UserName=msg.userName;
		}
		ui.jpLeft.updateUI();
	}
}
class Sender implements Runnable
{
	Message<SendShare> msg;
	ObjectOutputStream out;
	Sender(Message<SendShare> msg,ObjectOutputStream out)
	{
		this.msg=msg;
		this.out=out;
	}
	public void run()
	{
		try
		{
			out.writeObject(msg);
		} 
		catch (IOException e1){}
	}
}