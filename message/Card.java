package message;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Card extends JPanel implements Serializable
{
	private static final long serialVersionUID = 1L;
	public static final short BAIDU=0;
	public static final short BING=1;
	public static final short YOUDAO=2;
	public transient BufferedImage content;
	public transient User sender;
	private static Font fWord=new Font("Serif",Font.BOLD,25);
	private static Font fProvider=new Font("微软雅黑",Font.ITALIC,12);
	private static Font fMeaning=new Font("微软雅黑",Font.PLAIN,15);
	private static Font fComment=new Font("楷体",Font.BOLD,19);
	private static Font fSender=new Font("楷体",Font.ITALIC,19);
	public Card(String senderID,String senderName,String word,String meaning,short provider,String comment)
	{
		sender=new User(senderID,senderName);
		content=new BufferedImage(550,550,BufferedImage.TYPE_INT_ARGB);
		Graphics2D temp=content.createGraphics();
		temp.setColor((new Color(229,187,129)).brighter());
		temp.fillRect(0,0,550,550);
		String p=null;
		int stater=25;
		if(provider==BAIDU)p="百度翻译";
		else if(provider==BING)p="必应翻译";
		else if(provider==YOUDAO)p="有道词典";
		temp.setFont(fProvider);temp.setColor(Color.BLUE);
		temp.drawString("以下内容由 "+p+" 提供",5,stater);stater+=12;
		temp.setColor(Color.BLACK);temp.drawLine(3,stater,500,stater);
		temp.setFont(fWord);temp.setColor(Color.BLACK);stater+=30;
		temp.drawString(word,5,stater);
		temp.setFont(fMeaning);temp.setColor(Color.BLACK);stater+=30;
		String []sp_meaning=meaning.split("\n");
		for(int i=0;i<sp_meaning.length;i++)
			stater=drawMeaning(sp_meaning[i],temp,stater);
		stater+=2;
		temp.setColor(Color.BLACK);temp.drawLine(3,stater,500,stater);
		temp.setFont(fSender);temp.setColor(Color.RED);stater+=40;
		temp.drawString(senderName+" 评论道：",5,stater);
		temp.setFont(fComment);temp.setColor(Color.BLACK);stater+=40;
		if(comment==null)
			comment="("+senderName+"好懒，发了分享却不评论)";
		String []sp_comment=comment.split("\n");
		for(int i=0;i<sp_comment.length;i++)
			stater=drawComment(sp_comment[i],temp,stater);
		temp.setFont(new Font("楷体",Font.ITALIC,16));temp.setColor(Color.BLACK);stater+=5;
		temp.drawString("发送者："+senderName,320,stater);stater+=25;
		temp.drawString("ID："+senderID,320,stater);
	}
	public static Card generatePreview(String word,String meaning,short provider)
	{
		return new Card(word,meaning,provider);
	}
	private Card(String word,String meaning,short provider)
	{
		content=new BufferedImage(550,300,BufferedImage.TYPE_INT_ARGB);
		Graphics2D temp=content.createGraphics();
		temp.setColor((new Color(229,187,129)).brighter());
		temp.fillRect(0,0,550,550);
		String p=null;
		int stater=25;
		if(provider==BAIDU)p="百度翻译";
		else if(provider==BING)p="必应翻译";
		else if(provider==YOUDAO)p="有道词典";
		temp.setFont(fProvider);temp.setColor(Color.BLUE);
		temp.drawString("以下内容由 "+p+" 提供",5,stater);stater+=12;
		temp.setColor(Color.BLACK);temp.drawLine(3,stater,500,stater);
		temp.setFont(fWord);temp.setColor(Color.BLACK);stater+=30;
		temp.drawString(word,5,stater);
		temp.setFont(fMeaning);temp.setColor(Color.BLACK);stater+=30;
		String []sp_meaning=meaning.split("\n");
		for(int i=0;i<sp_meaning.length;i++)
			stater=drawMeaning(sp_meaning[i],temp,stater);
		stater+=2;
		temp.setColor(Color.BLACK);temp.drawLine(3,stater,500,stater);
	}
	static private int drawMeaning(String s,Graphics2D g,int y)
	{
		final int cap=33;
		int n=s.length()/cap;
		for(int i=0;i<n;i++)
		{
			g.drawString(s.substring(i*cap,(i+1)*cap),5,y);
			y+=20;
		}
		if(s.length()%cap!=0)
		{
			g.drawString(s.substring(n*cap),5,y);
			y+=20;
		}
		return y;
	}
	static private int drawComment(String s,Graphics2D g,int y)
	{
		if(s==null)return y;
		final int cap=25;		
		s="    "+s;
		int n=(s.length()-2)/cap;
		g.drawString(s.substring(0,(2+cap)>s.length()?s.length():(2+cap)),1,y);
		y+=23;
		for(int i=1;i<n;i++)
		{
			g.drawString(s.substring(2+i*cap,2+(i+1)*cap),5,y);
			y+=23;
		}
		if((s.length()-2)%cap!=0 && n>0)
		{
			g.drawString(s.substring(n*cap),5,y);
			y+=23;
		}
		return y;
	}
	private void writeObject(ObjectOutputStream out) throws IOException 
	{
		out.defaultWriteObject();
		out.writeObject(sender);
		ImageIO.write(content,"JPEG",out);
	}
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException 
	{
		in.defaultReadObject();
		sender=(User)in.readObject();
		content=ImageIO.read(in);
	} 
	public void paintComponent(Graphics g)
	{
		g.drawImage(content,0,0,null);   
	}
	public static void main(String[] args)
	{
		JFrame ui=new JFrame();
		Card c=new Card("12345","HAOTO","Fuck","收大额奥斯迪骄傲诶如啊是大搜\n丢澳网城建司阿斯顿哦啊是简单\n的撒旦几哦啊死绝地哦啊是简单计算机代价滴噢撒娇道圣诞节撒旦教爱神的箭死哦啊的见奥斯丁见",BAIDU,"收大额奥斯迪骄傲诶如啊是大搜丢\n澳网城建司阿斯顿哦啊是简单的撒旦几哦啊死绝地哦啊是简单计撒大声\n地算机代价滴噢撒娇道圣诞节撒旦教爱神的箭死哦啊的见奥斯丁见");
		JPanel p=new JPanel();
		p.setLayout(new BorderLayout(5,5));
		p.setBorder(BorderFactory.createTitledBorder("CARD"));
		p.add(c,BorderLayout.CENTER);
		ui.add(p);
		ui.setSize(670,670);
		ui.setLocationRelativeTo(null);
		ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ui.setVisible(true);
	}
}