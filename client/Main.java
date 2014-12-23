package client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Main
{
	public static void main(String[] args)
	{
		JFrame temp=new JFrame();
		temp.setTitle("请输入服务器的IP");
		temp.setSize(350,130);
		temp.setLocationRelativeTo(null);
		temp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		temp.setVisible(true);
		JPanel jpC=new JPanel();
		JTextField jtfIP=new JTextField();
		jtfIP.setFont(new Font("Consolas",Font.BOLD,20));
		jtfIP.setHorizontalAlignment(JTextField.CENTER);
		temp.setLayout(new BorderLayout(5,5));
		temp.add(jpC,BorderLayout.CENTER);
		jpC.setLayout(new BorderLayout(5,5));
		jpC.add(jtfIP,BorderLayout.CENTER);
		JButton jbtOK=new JButton("确定");
		JPanel jp=new JPanel();
		jp.setLayout(new FlowLayout(FlowLayout.CENTER));
		jp.add(jbtOK);
		jpC.add(jp,BorderLayout.SOUTH);
		jpC.updateUI();
		jbtOK.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String ip=jtfIP.getText();
				if(!testIP(ip))return;
				UI ui=new UI(ip);
				ui.setSize(800,750);
				ui.setLocationRelativeTo(null);
				ui.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				ui.setVisible(true);
				temp.dispose();
			}
		});
		jtfIP.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String ip=jtfIP.getText();
				if(!testIP(ip))return;
				UI ui=new UI(ip);
				ui.setSize(800,750);
				ui.setLocationRelativeTo(null);
				ui.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				ui.setVisible(true);
				temp.dispose();
			}
		});
	}
	static private boolean testIP(String ip)
	{
		if(ip.equals("localhost"))return true;
		String []temp=ip.split("[.]");
		if(temp.length!=4)return false;
		for(int i=0;i<4;i++)
		{
			if(temp[i].length()>3)return false;
			for(int j=0;j<temp[i].length();j++)
				if(!Character.isDigit(temp[i].charAt(j)))
					return false;
			int a=Integer.valueOf((new String(temp[i]))).intValue();
			if(a<0 || a>255)return false;
		}
		return true;
	}
}