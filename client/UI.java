package client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import message.Card;
import message.Like;
import message.LoginRequest;
import message.Message;
import message.RegistRequest;
import message.SearchRequest;
import message.SendShare;
import message.User;

public class UI extends JFrame
{
	private static final long serialVersionUID = 1L;
	private Connector connect;
	
	
	//��½���ǳ���ע�ᣬ�û��б�������ߡ�
	protected boolean logedin=false;
	protected JPanel jpUser;
	protected JButton jbtLogin;
	protected JButton jbtRegist;
	private JFrame jftemp=null;
	
	protected JPanel jpUserInfo;
	protected JButton jbtLogout;
	protected User myself=new User(null,null);
	protected String userPass=null;
	protected JLabel jlbUserNo;
	protected JLabel jlbUserName;
	
	protected ArrayList<User> onlineUser;
	protected DefaultListModel<String> users=new DefaultListModel<String>(); 
	protected JList<String> jlstUser=new JList<String>(users);
	
	//ǰ�������ˣ����䰴ť���ұߵ�һ��	
	protected JPanel jpIcons;
	protected JButton jbtBack;
	protected JButton jbtForward;
	protected JButton jbtMail;
	protected LinkedList<Card> mailBox=new LinkedList<Card>();
	protected JLabel jlbBack;
	protected JLabel jlbForward;
	private SceneQueue scene=new SceneQueue(this);
	protected String word=null;
	protected boolean cbaidu=true,cbing=true,cyoudao=true;
	
	//��������򣬲�ѯ����ѡ���ұߵڶ���
	protected JTextField jtfWord;
	protected JButton jbtSearch;
	protected JCheckBox jchkBaidu;
	protected JCheckBox jchkYoudao;
	protected JCheckBox jchkBing;
	
	//��ѯ������ұߵ�������boolean��ʾ���ڵȴ��������ظ���ѯ��Ϣ����ʱ��ť����ס
	protected JPanel jpMUI;
	protected boolean locked=false;
	protected ResPanel rpBaidu;
	protected ResPanel rpBing;
	protected ResPanel rpYoudao;
	private short provider_selected;
	private String meaning_selected=null;
	
	//�ұ��·������Բ�ȥ�Ĳ��֡�
	protected JPanel right_down=new JPanel();
	protected JPanel right_down_normal;
	
	protected JPanel right_down_edit=new JPanel();
	private JCheckBox jchkSTA=new JCheckBox("���͸����������û�");
	private JLabel jlbEdit=new JLabel("<html>���������ۣ��������ѡ��Ҫ���͵����û�����ʾ����סctrl���ɶ�ѡ����"
											+ "���ߵ��������ѡ����͸�ȫ�������û���������ķ��ؼ����ء�</html>");
	private JTextArea jtaComment;
	
	protected JPanel right_down_preview=new JPanel();
	private JButton jbtOK=new JButton("ȷ��");
	
	protected JPanel right_down_read=new JPanel();
	private JPanel jpCard=new JPanel();
	private JButton jbtNext=new JButton("��");
	
	private boolean changing=false;
	//��ߣ��ұߵĿɿ����
	protected JPanel jpLeft;
	protected JPanel right;
	
	//�������
	protected class ResPanel extends JPanel
	{
		private static final long serialVersionUID = 1L;
		static final short BAIDU=0;
		static final short BING=1;
		static final short YOUDAO=2;
		short pcode;
		boolean selected=true;
		boolean liked=false;
		JPanel jpInteract;
		JLabel jlbProvider;
		JTextArea jtaRslt;
		JButton jbtShare;
		ImageIcon likeIcon;
		JButton jbtLike;
		JLabel jlbLike;
		int liked_count=0;
		public ResPanel(short code)
		{
			pcode=code;
			if(code==ResPanel.BAIDU)
				jlbProvider=new JLabel("�ٶȷ���");				
			else if(code==ResPanel.BING)
				jlbProvider=new JLabel("��Ӧ����");
			else if(code==ResPanel.YOUDAO)
				jlbProvider=new JLabel("�е��ʵ�");
			setLayout(new BorderLayout(5,5));
			add(jlbProvider,BorderLayout.NORTH);
			jtaRslt=new JTextArea();
			jtaRslt.setLineWrap(true);
			jtaRslt.setWrapStyleWord(true);
			jtaRslt.setEditable(false);
			add(new JScrollPane(jtaRslt),BorderLayout.CENTER);
			jpInteract=new JPanel();
			jbtShare=new JButton(getSmall("share.png"));
			likeIcon=getSmall("like.png");
			jbtLike=new JButton(likeIcon);
			jlbLike=new JLabel(likeIcon);
			jbtShare.setToolTipText("����");
			jbtLike.setToolTipText("��!");
			jpInteract.add(jbtShare);			
			jpInteract.add(jbtLike);
			jpInteract.setLayout(new FlowLayout(FlowLayout.RIGHT));
			add(jpInteract,BorderLayout.SOUTH);
			jbtLike.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					if(word==null)return;
					if(locked)
					{
						JFrame temp=new JFrame();
						temp.setTitle("��ʾ��Ϣ");
						temp.setSize(350,100);
						temp.setLocationRelativeTo(null);
						temp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
						temp.setVisible(true);
						Label lab=new Label("���緱æ�����Ժ�����");
						lab.setAlignment(Label.CENTER);
						lab.setFont(new Font("΢���ź�",18,Font.PLAIN));
						temp.add(lab);
						return;
					}
					if(!connect.connection_successful)
					{
						JFrame temp=new JFrame();
						temp.setTitle("��ʾ��Ϣ");
						temp.setSize(350,100);
						temp.setLocationRelativeTo(null);
						temp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
						temp.setVisible(true);
						Label lab=new Label("����������Ժ�����");
						lab.setAlignment(Label.CENTER);
						lab.setFont(new Font("΢���ź�",18,Font.PLAIN));
						temp.add(lab);
						return;
					}
					if(!logedin)
					{
						JFrame temp=new JFrame();
						temp.setTitle("��ʾ��Ϣ");
						temp.setSize(350,100);
						temp.setLocationRelativeTo(null);
						temp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
						temp.setVisible(true);
						Label lab=new Label("Ҫ��ʹ�ô˹��ܣ����ȵ�¼");
						lab.setAlignment(Label.CENTER);
						lab.setFont(new Font("΢���ź�",18,Font.PLAIN));
						temp.add(lab);
						return;
					}
					Message<Like> msg=new Message<Like>();
					msg.type=Message.LIKE;
					msg.content=new Like();
					msg.content.provider=pcode;
					msg.content.word=word;
					try
					{
						connect.toServer.writeObject(msg);
					} 
					catch (IOException e1){}
					liked_count++;
					liked=true;
					jlbLike.setText("��("+String.valueOf(liked_count)+")");
					jpInteract.removeAll();
					jpInteract.add(jbtShare);			
					jpInteract.add(jlbLike);
					repaint_jpMUI();		
				}
			});
			jbtShare.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					if(word==null)return;
					if(locked)
					{
						JFrame temp=new JFrame();
						temp.setTitle("��ʾ��Ϣ");
						temp.setSize(350,100);
						temp.setLocationRelativeTo(null);
						temp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
						temp.setVisible(true);
						Label lab=new Label("���緱æ�����Ժ�����");
						lab.setAlignment(Label.CENTER);
						lab.setFont(new Font("΢���ź�",18,Font.PLAIN));
						temp.add(lab);
						return;
					}
					if(!connect.connection_successful)
					{
						JFrame temp=new JFrame();
						temp.setTitle("��ʾ��Ϣ");
						temp.setSize(350,100);
						temp.setLocationRelativeTo(null);
						temp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
						temp.setVisible(true);
						Label lab=new Label("����������Ժ�����");
						lab.setAlignment(Label.CENTER);
						lab.setFont(new Font("΢���ź�",18,Font.PLAIN));
						temp.add(lab);
						return;
					}
					if(!logedin)
					{
						JFrame temp=new JFrame();
						temp.setTitle("��ʾ��Ϣ");
						temp.setSize(350,100);
						temp.setLocationRelativeTo(null);
						temp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
						temp.setVisible(true);
						Label lab=new Label("Ҫ��ʹ�ô˹��ܣ����ȵ�¼");
						lab.setAlignment(Label.CENTER);
						lab.setFont(new Font("΢���ź�",18,Font.PLAIN));
						temp.add(lab);
						return;
					}
					provider_selected=pcode;
					meaning_selected=jtaRslt.getText();
					changing=true;
					right_down_preview.removeAll();
					right_down_preview.add(Card.generatePreview(word,jtaRslt.getText(),pcode),BorderLayout.CENTER);
					right_down.removeAll();
					right_down.add(right_down_edit,BorderLayout.CENTER);
					right_down.updateUI();
				}
			});
		}
		public void setResult(Boolean selected,String result,boolean liked,int liked_count)
		{
			this.selected=selected;
			jtaRslt.setText(result);
			this.liked_count=liked_count;
			this.liked=liked;
			if(liked)
			{
				jpInteract.removeAll();
				jpInteract.add(jbtShare);
				jpInteract.add(jlbLike);
				jlbLike.setText("��("+liked_count+")");
			}
			else 
			{
				jpInteract.removeAll();
				jpInteract.add(jbtShare);
				jpInteract.add(jbtLike);
				jbtLike.setText("��("+liked_count+")");
			}
			jpInteract.updateUI();
		}
	}
	private ImageIcon getSmall(String p)
	{
		return getSmall(p,30,20);
	}
	private ImageIcon getSmall(String p,int w,int h)
	{
		Image image=null;
		try
		{
			image = ImageIO.read(this.getClass().getResourceAsStream("/images/"+p));
		} 
		catch (IOException e){}
		Image smallImage = image.getScaledInstance(w,h,Image.SCALE_SMOOTH);
		return new ImageIcon(smallImage);
	}	
	public UI(String ip)
	{	
		connect=new Connector(this,ip);
		jbtLogin=new JButton("��½");
		jbtRegist=new JButton("ע��");
		jpUser=new JPanel();
		jpUser.setLayout(new GridLayout(2, 1));
		jpUser.add(jbtRegist);		
		jpUser.add(jbtLogin);
		jpUserInfo=new JPanel();
		jbtLogout=new JButton("ע��");
		jlbUserNo=new JLabel();
		jlbUserName=new JLabel();
		jpUserInfo.setLayout(new GridLayout(3,1));
		jpUserInfo.add(jlbUserNo);
		jpUserInfo.add(jlbUserName);
		JPanel temp1=new JPanel();
		temp1.setLayout(new FlowLayout(FlowLayout.CENTER));
		temp1.add(jbtLogout);
		jpUserInfo.add(temp1);
		JPanel left=new JPanel();
		left.setLayout(new BorderLayout(5,10));
		jpLeft=new JPanel();
		jpLeft.setLayout(new BorderLayout(5,0));
		jpLeft.add(jpUser,BorderLayout.CENTER);
		left.add(jpLeft,BorderLayout.NORTH);
		JPanel left_down=new JPanel();
		left_down.setLayout(new BorderLayout(0,0));
		JLabel jlbList=new JLabel("��ǰ�����û�");
		jlbList.setHorizontalAlignment(SwingConstants.CENTER);
		left_down.add(jlbList,BorderLayout.NORTH);
		left_down.add(new JScrollPane(jlstUser),BorderLayout.CENTER);
		left.add(left_down,BorderLayout.CENTER);
		jlstUser.setFixedCellWidth(220);
		jlstUser.setFixedCellHeight(20);
		add(left,BorderLayout.WEST);
		jpIcons=new JPanel();
		jpIcons.setLayout(new FlowLayout(FlowLayout.LEFT,20,5));
		jbtBack=new JButton(getSmall("back.png",32,32));
		jbtBack.setToolTipText("����");
		jlbBack=new JLabel(getSmall("back.png",32,32));
		jbtForward=new JButton(getSmall("forward.png",32,32));
		jbtForward.setToolTipText("ǰ��");
		jlbForward=new JLabel(getSmall("forward.png",32,32));
		jbtMail=new JButton(getSmall("Mail.png",32,32));
		jbtMail.setToolTipText("����δ���ĵ��ʿ�");
		jbtSearch=new JButton(getSmall("search.png",25,25));
		jbtSearch.setToolTipText("����");
		jpIcons.add(jlbBack);
		jpIcons.add(jlbForward);
		right=new JPanel();
		right.setLayout(new BorderLayout(5,5));
		right.add(jpIcons,BorderLayout.NORTH);
		JPanel jpChoise=new JPanel();
		jpChoise.setLayout(new GridLayout(1,3));
		jchkBaidu=new JCheckBox("�ٶ�");
		jchkYoudao=new JCheckBox("�е�");
		jchkBing=new JCheckBox("��Ӧ");
		jpChoise.add(jchkBaidu);
		jpChoise.add(jchkYoudao);
		jpChoise.add(jchkBing);
		jchkBaidu.setHorizontalAlignment(SwingConstants.CENTER);
		jchkYoudao.setHorizontalAlignment(SwingConstants.CENTER);
		jchkBing.setHorizontalAlignment(SwingConstants.CENTER);
		JPanel input=new JPanel();
		jtfWord=new JTextField(10);
		input.setLayout(new BorderLayout(5,0));
		JLabel jlbInput=new JLabel("Input: ");
		input.add(jlbInput,BorderLayout.WEST);
		input.add(jtfWord,BorderLayout.CENTER);
		input.add(jbtSearch,BorderLayout.EAST);
		input.add(jpChoise,BorderLayout.SOUTH);
		jpMUI=new JPanel();
		rpBaidu=new ResPanel(ResPanel.BAIDU);
		rpBing=new ResPanel(ResPanel.BING);
		rpYoudao=new ResPanel(ResPanel.YOUDAO);
		jpMUI.setLayout(new GridLayout(3,1));
		jpMUI.add(rpBaidu);
		jpMUI.add(rpBing);
		jpMUI.add(rpYoudao);
		right_down_normal=new JPanel();
		right_down_normal.setLayout(new BorderLayout(5,0));
		right_down_edit.setLayout(new BorderLayout(5,0));
		right_down_preview.setLayout(new BorderLayout(0,0));
		right_down_preview.setBorder(BorderFactory.createTitledBorder("CARD"));
		right_down_normal.add(input,BorderLayout.NORTH);
		right_down_normal.add(jpMUI,BorderLayout.CENTER);
		right_down_read.setLayout(new BorderLayout(5,0));
		right_down_read.add(jpCard,BorderLayout.CENTER);
		JPanel temp2=new JPanel();
		temp2.setLayout(new FlowLayout(FlowLayout.CENTER));
		temp2.add(jbtNext);
		right_down_read.add(temp2,BorderLayout.SOUTH);
		jtaComment=new JTextArea();
		jtaComment.setLineWrap(true);
		jtaComment.setWrapStyleWord(true);
		jtaComment.setEditable(true);
		JScrollPane js=new JScrollPane(jtaComment);
		js.setSize(550,200);
		jtaComment.setBorder(BorderFactory.createTitledBorder("�������������ۣ�"));
		JPanel jptemp=new JPanel();
		jptemp.setLayout(new BorderLayout(5,0));
		jptemp.add(jlbEdit,BorderLayout.NORTH);
		jptemp.add(jchkSTA,BorderLayout.SOUTH);
		JPanel jpt=new JPanel();
		jpt.setLayout(new GridLayout(2,1));
		jpt.add(right_down_preview);
		jpt.add(js);
		right_down_edit.add(jptemp,BorderLayout.NORTH);
		right_down_edit.add(jpt,BorderLayout.CENTER);
		JPanel temp3=new JPanel();
		temp3.setLayout(new FlowLayout(FlowLayout.CENTER));
		temp3.add(jbtOK);
		right_down_edit.add(temp3,BorderLayout.SOUTH);
		jpCard.setBorder(BorderFactory.createTitledBorder("CARD"));
		jpCard.setLayout(new BorderLayout(0,0));
		right_down.setLayout(new BorderLayout(0,0));
		right_down.add(right_down_normal,BorderLayout.CENTER);
		right.add(jpIcons,BorderLayout.NORTH);
		right.add(right_down,BorderLayout.CENTER);
		add(right,BorderLayout.CENTER);
		init_ActionListeners();
		new Thread(connect).start();
	}
	protected void repaint_jpIcons()
	{
		jpIcons.removeAll();
		if(!scene.is_start() || changing)jpIcons.add(jbtBack);
		else jpIcons.add(jlbBack);
		if(scene.is_end())jpIcons.add(jlbForward);
		else jpIcons.add(jbtForward);
		if(!mailBox.isEmpty())
		{
			jpIcons.add(jbtMail);
			jbtMail.setText(String.valueOf(mailBox.size()));
		}
		jpIcons.updateUI();
	}
	protected void repaint_jpMUI()
	{
		jpMUI.removeAll();
		ResPanel[] temp=new ResPanel[3];
		int c=0;
		if(rpBaidu.selected){temp[c]=rpBaidu;c++;}
		if(rpBing.selected){temp[c]=rpBing;c++;}
		if(rpYoudao.selected){temp[c]=rpYoudao;c++;}
		for(int i=c-1;i>0;i--)
			for(int j=0;j<i;j++)
				if(temp[j+1].liked_count>temp[j].liked_count)
				{
					ResPanel m=temp[j+1];
					temp[j+1]=temp[j];
					temp[j]=m;
				}
		for(int i=0;i<c;i++)
			jpMUI.add(temp[i]);
		jpMUI.updateUI();
	}
	private void init_ActionListeners()
	{
		addWindowListener(new WindowListener()
		{
			public void windowOpened(WindowEvent e){}
			public void windowIconified(WindowEvent e){}
			public void windowDeiconified(WindowEvent e){}
			public void windowDeactivated(WindowEvent e){}
			public void windowClosing(WindowEvent e)
			{
				if(!mailBox.isEmpty())
				{
					int temp=JOptionPane.showConfirmDialog(null,"����"+mailBox.size()+"��δ�����ʿ����˳��Ļ����ʿ��ᶪʧ��ȷ���˳���", 
						"��ʾ��Ϣ",JOptionPane.YES_NO_OPTION);
					if(temp==JOptionPane.YES_OPTION)
					{
						if(connect.connection_successful)
						{
							Message<Object> msg=new Message<Object>();
							msg.type=Message.EXIT;
							try
							{
								connect.toServer.writeObject(msg);
							} 
							catch (IOException e1){}
						}
						connect.run=false;
						System.exit(0);
					}
				}
				else
				{
					if(connect.connection_successful)
					{
						Message<Object> msg=new Message<Object>();
						msg.type=Message.EXIT;
						try
						{
							connect.toServer.writeObject(msg);
						} 
						catch (IOException e1){}
					}
					connect.run=false;
					System.exit(0);
				}
			}
			public void windowClosed(WindowEvent e){}
			public void windowActivated(WindowEvent e){}
		});
		jbtLogin.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(jftemp!=null)jftemp.dispose();
				jftemp=new JFrame();
				jftemp.setLayout(new GridLayout(3,1));
				jftemp.setTitle("��½");
				jftemp.setSize(250,150);
				jftemp.setLocationRelativeTo(null);
				jftemp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				jftemp.setVisible(true);
				JPanel jpNo=new JPanel();
				jpNo.setLayout(new BorderLayout(5,0));
				JPanel jpPas=new JPanel();
				JTextField jtfNo=new JTextField();
				jpNo.add(new JLabel("�˺�: "),BorderLayout.WEST);
				jpNo.add(jtfNo,BorderLayout.CENTER);
				jpPas.setLayout(new BorderLayout(5,0));
				JPasswordField jtfPass=new JPasswordField();
				jpPas.add(new JLabel("����: "),BorderLayout.WEST);
				jpPas.add(jtfPass,BorderLayout.CENTER);
				JButton jbtConfirm=new JButton("ȷ��");
				jbtConfirm.setHorizontalAlignment(SwingConstants.CENTER);
				JPanel temp2=new JPanel();
				temp2.setLayout(new FlowLayout(FlowLayout.CENTER));
				temp2.add(jbtConfirm);
				jftemp.add(jpNo);
				jftemp.add(jpPas);
				jftemp.add(temp2);
				jtfPass.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						if(jtfNo.getText()==null || jtfPass.getPassword().length==0)return;
						if(!connect.connection_successful)
						{
							JFrame temp=new JFrame();
							temp.setTitle("��ʾ��Ϣ");
							temp.setSize(350,100);
							temp.setLocationRelativeTo(null);
							temp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
							temp.setVisible(true);
							Label lab=new Label("����������Ժ�����");
							lab.setAlignment(Label.CENTER);
							lab.setFont(new Font("΢���ź�",18,Font.PLAIN));
							temp.add(lab);
							return;
						}
						myself.UserID=jtfNo.getText();
						userPass=new String(jtfPass.getPassword());
						jftemp.dispose();jftemp=null;
						jpLeft.removeAll();
						jpLeft.add(new JLabel("�����У����Ժ�"),BorderLayout.CENTER);
						jpLeft.updateUI();
						Message<LoginRequest> msg=new Message<LoginRequest>();
						msg.type=Message.LOGIN;
						msg.content=new LoginRequest(myself.UserID,userPass);
						try
						{
							connect.toServer.writeObject(msg);
						} 
						catch (IOException e1){}
					}
				});
				jtfNo.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						if(jtfNo.getText()==null || jtfPass.getPassword().length==0)return;
						if(!connect.connection_successful)
						{
							JFrame temp=new JFrame();
							temp.setTitle("��ʾ��Ϣ");
							temp.setSize(350,100);
							temp.setLocationRelativeTo(null);
							temp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
							temp.setVisible(true);
							Label lab=new Label("����������Ժ�����");
							lab.setAlignment(Label.CENTER);
							lab.setFont(new Font("΢���ź�",18,Font.PLAIN));
							temp.add(lab);
							return;
						}
						myself.UserID=jtfNo.getText();
						userPass=new String(jtfPass.getPassword());
						jftemp.dispose();jftemp=null;
						jpLeft.removeAll();
						jpLeft.add(new JLabel("�����У����Ժ�"),BorderLayout.CENTER);
						jpLeft.updateUI();
						Message<LoginRequest> msg=new Message<LoginRequest>();
						msg.type=Message.LOGIN;
						msg.content=new LoginRequest(myself.UserID,userPass);
						try
						{
							connect.toServer.writeObject(msg);
						} 
						catch (IOException e1){}
					}
				});
				jbtConfirm.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						if(jtfNo.getText()==null || jtfPass.getPassword().length==0)return;
						if(!connect.connection_successful)
						{
							JFrame temp=new JFrame();
							temp.setTitle("��ʾ��Ϣ");
							temp.setSize(350,100);
							temp.setLocationRelativeTo(null);
							temp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
							temp.setVisible(true);
							Label lab=new Label("����������Ժ�����");
							lab.setAlignment(Label.CENTER);
							lab.setFont(new Font("΢���ź�",18,Font.PLAIN));
							temp.add(lab);
							return;
						}
						myself.UserID=jtfNo.getText();
						userPass=new String(jtfPass.getPassword());
						jftemp.dispose();jftemp=null;
						jpLeft.removeAll();
						jpLeft.add(new JLabel("�����У����Ժ�"),BorderLayout.CENTER);
						jpLeft.updateUI();
						Message<LoginRequest> msg=new Message<LoginRequest>();
						msg.type=Message.LOGIN;
						msg.content=new LoginRequest(myself.UserID,userPass);
						try
						{
							connect.toServer.writeObject(msg);
						} 
						catch (IOException e1){}
					}
				});
			}
		});
		jbtRegist.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(jftemp!=null)jftemp.dispose();
				jftemp=new JFrame();
				jftemp.setLayout(new GridLayout(5,1));
				jftemp.setTitle("ע��");
				jftemp.setSize(250,250);
				jftemp.setLocationRelativeTo(null);
				jftemp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				jftemp.setVisible(true);
				JPanel jpNo=new JPanel();
				jpNo.setLayout(new BorderLayout(5,0));
				JPanel jpPas1=new JPanel();
				JTextField jtfNo=new JTextField();
				jpNo.add(new JLabel("�����˺�: "),BorderLayout.WEST);
				jpNo.add(jtfNo,BorderLayout.CENTER);
				jpPas1.setLayout(new BorderLayout(5,0));
				JPasswordField jtfPass1=new JPasswordField();
				jpPas1.add(new JLabel("��������: "),BorderLayout.WEST);
				jpPas1.add(jtfPass1,BorderLayout.CENTER);
				JPanel jpPas2=new JPanel();
				jpPas2.setLayout(new BorderLayout(5,0));
				JPasswordField jtfPass2=new JPasswordField();
				jpPas2.add(new JLabel("ȷ������: "),BorderLayout.WEST);
				jpPas2.add(jtfPass2,BorderLayout.CENTER);
				JButton jbtConfirm=new JButton("ȷ��");
				jbtConfirm.setHorizontalAlignment(SwingConstants.CENTER);
				JPanel jpName=new JPanel();
				jpName.setLayout(new BorderLayout(5,0));
				jpName.add(new JLabel("��������: "),BorderLayout.WEST);
				JTextField jtfName=new JTextField();
				jpName.add(jtfName,BorderLayout.CENTER);
				JPanel temp2=new JPanel();
				temp2.setLayout(new FlowLayout(FlowLayout.CENTER));
				temp2.add(jbtConfirm);
				jftemp.add(jpNo);
				jftemp.add(jpName);
				jftemp.add(jpPas1);
				jftemp.add(jpPas2);
				jftemp.add(temp2);
				jtfNo.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						if(jtfNo.getText()==null || jtfPass1.getPassword().length==0 
								|| jtfPass2.getPassword().length==0)return;
						userPass=new String(jtfPass1.getPassword());
						if(userPass.compareTo(new String(jtfPass2.getPassword()))!=0)
						{
							JFrame temp=new JFrame();
							temp.setTitle("��ʾ��Ϣ");
							temp.setSize(350,100);
							temp.setLocationRelativeTo(null);
							temp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
							temp.setVisible(true);
							Label lab=new Label("����ȷ�ϴ�����������������");
							lab.setAlignment(Label.CENTER);
							lab.setFont(new Font("΢���ź�",18,Font.PLAIN));
							temp.add(lab);
							return;
						}
						if(!connect.connection_successful)
						{
							JFrame temp=new JFrame();
							temp.setTitle("��ʾ��Ϣ");
							temp.setSize(350,100);
							temp.setLocationRelativeTo(null);
							temp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
							temp.setVisible(true);
							Label lab=new Label("����������Ժ�����");
							lab.setAlignment(Label.CENTER);
							lab.setFont(new Font("΢���ź�",18,Font.PLAIN));
							temp.add(lab);
							return;
						}
						myself.UserID=jtfNo.getText();
						myself.UserName=jtfName.getText();
						jftemp.dispose();jftemp=null;
						jpLeft.removeAll();
						jpLeft.add(new JLabel("�����У����Ժ�"),BorderLayout.CENTER);
						jpLeft.updateUI();
						Message<RegistRequest> msg=new Message<RegistRequest>();
						msg.type=Message.REGIST;
						msg.content=new RegistRequest(myself.UserID,myself.UserName,userPass);
						try
						{
							connect.toServer.writeObject(msg);
						} 
						catch (IOException e1){}
					}
				});
				jtfPass1.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						if(jtfNo.getText()==null || jtfPass1.getPassword().length==0 
								|| jtfPass2.getPassword().length==0)return;
						userPass=new String(jtfPass1.getPassword());
						if(userPass.compareTo(new String(jtfPass2.getPassword()))!=0)
						{
							JFrame temp=new JFrame();
							temp.setTitle("��ʾ��Ϣ");
							temp.setSize(350,100);
							temp.setLocationRelativeTo(null);
							temp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
							temp.setVisible(true);
							Label lab=new Label("����ȷ�ϴ�����������������");
							lab.setAlignment(Label.CENTER);
							lab.setFont(new Font("΢���ź�",18,Font.PLAIN));
							temp.add(lab);
							return;
						}
						if(!connect.connection_successful)
						{
							JFrame temp=new JFrame();
							temp.setTitle("��ʾ��Ϣ");
							temp.setSize(350,100);
							temp.setLocationRelativeTo(null);
							temp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
							temp.setVisible(true);
							Label lab=new Label("����������Ժ�����");
							lab.setAlignment(Label.CENTER);
							lab.setFont(new Font("΢���ź�",18,Font.PLAIN));
							temp.add(lab);
							return;
						}
						myself.UserID=jtfNo.getText();
						myself.UserName=jtfName.getText();
						jftemp.dispose();jftemp=null;
						jpLeft.removeAll();
						jpLeft.add(new JLabel("�����У����Ժ�"),BorderLayout.CENTER);
						jpLeft.updateUI();
						Message<RegistRequest> msg=new Message<RegistRequest>();
						msg.type=Message.REGIST;
						msg.content=new RegistRequest(myself.UserID,myself.UserName,userPass);
						try
						{
							connect.toServer.writeObject(msg);
						} 
						catch (IOException e1){}
					}
				});
				jtfPass2.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						if(jtfNo.getText()==null || jtfPass1.getPassword().length==0 
								|| jtfPass2.getPassword().length==0)return;
						userPass=new String(jtfPass1.getPassword());
						if(userPass.compareTo(new String(jtfPass2.getPassword()))!=0)
						{
							JFrame temp=new JFrame();
							temp.setTitle("��ʾ��Ϣ");
							temp.setSize(350,100);
							temp.setLocationRelativeTo(null);
							temp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
							temp.setVisible(true);
							Label lab=new Label("����ȷ�ϴ�����������������");
							lab.setAlignment(Label.CENTER);
							lab.setFont(new Font("΢���ź�",18,Font.PLAIN));
							temp.add(lab);
							return;
						}
						if(!connect.connection_successful)
						{
							JFrame temp=new JFrame();
							temp.setTitle("��ʾ��Ϣ");
							temp.setSize(350,100);
							temp.setLocationRelativeTo(null);
							temp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
							temp.setVisible(true);
							Label lab=new Label("����������Ժ�����");
							lab.setAlignment(Label.CENTER);
							lab.setFont(new Font("΢���ź�",18,Font.PLAIN));
							temp.add(lab);
							return;
						}
						myself.UserID=jtfNo.getText();
						myself.UserName=jtfName.getText();
						jftemp.dispose();jftemp=null;
						jpLeft.removeAll();
						jpLeft.add(new JLabel("�����У����Ժ�"),BorderLayout.CENTER);
						jpLeft.updateUI();
						Message<RegistRequest> msg=new Message<RegistRequest>();
						msg.type=Message.REGIST;
						msg.content=new RegistRequest(myself.UserID,myself.UserName,userPass);
						try
						{
							connect.toServer.writeObject(msg);
						} 
						catch (IOException e1){}
					}
				});
				jbtConfirm.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						if(jtfNo.getText()==null || jtfPass1.getPassword().length==0 
								|| jtfPass2.getPassword().length==0)return;
						userPass=new String(jtfPass1.getPassword());
						if(userPass.compareTo(new String(jtfPass2.getPassword()))!=0)
						{
							JFrame temp=new JFrame();
							temp.setTitle("��ʾ��Ϣ");
							temp.setSize(350,100);
							temp.setLocationRelativeTo(null);
							temp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
							temp.setVisible(true);
							Label lab=new Label("����ȷ�ϴ�����������������");
							lab.setAlignment(Label.CENTER);
							lab.setFont(new Font("΢���ź�",18,Font.PLAIN));
							temp.add(lab);
							return;
						}
						if(!connect.connection_successful)
						{
							JFrame temp=new JFrame();
							temp.setTitle("��ʾ��Ϣ");
							temp.setSize(350,100);
							temp.setLocationRelativeTo(null);
							temp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
							temp.setVisible(true);
							Label lab=new Label("����������Ժ�����");
							lab.setAlignment(Label.CENTER);
							lab.setFont(new Font("΢���ź�",18,Font.PLAIN));
							temp.add(lab);
							return;
						}
						myself.UserID=jtfNo.getText();
						myself.UserName=jtfName.getText();
						jftemp.dispose();jftemp=null;
						jpLeft.removeAll();
						jpLeft.add(new JLabel("�����У����Ժ�"),BorderLayout.CENTER);
						jpLeft.updateUI();
						Message<RegistRequest> msg=new Message<RegistRequest>();
						msg.type=Message.REGIST;
						msg.content=new RegistRequest(myself.UserID,myself.UserName,userPass);
						try
						{
							connect.toServer.writeObject(msg);
						} 
						catch (IOException e1){}
					}
				});
			}
		});
		jbtLogout.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(connect.connection_successful)
				{
					Message<Object> msg=new Message<Object>();
					msg.type=Message.LOGOUT;
					try
					{
						connect.toServer.writeObject(msg);
					} 
					catch (IOException e1){}
				}
				if(!mailBox.isEmpty())
				{
					int temp=JOptionPane.showConfirmDialog(null,"����"+mailBox.size()+"��δ�����ʿ����ǳ��Ļ����ʿ��ᶪʧ��ȷ���ǳ���", 
						"��ʾ��Ϣ",JOptionPane.YES_NO_OPTION);
					if(temp==JOptionPane.NO_OPTION)return;
					mailBox.clear();
				}
				logedin=false;
				jpLeft.removeAll();
				jpLeft.add(jpUser);
				jpLeft.updateUI();
				users.removeAllElements();
				if(changing)
				{
					right_down.removeAll();
					right_down.add(right_down_normal,BorderLayout.CENTER);
					right_down.updateUI();
					changing=false;
					repaint_jpIcons();
				}
			}
		});	
		jbtBack.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(changing)
				{
					right_down.removeAll();
					right_down.add(right_down_normal,BorderLayout.CENTER);
					right_down.updateUI();
					changing=false;
					repaint_jpIcons();
					return;
				}
				else if(locked)return;
				ResultInfo temp=scene.back();
				word=temp.word;
				jtfWord.setText(word);
				jchkBaidu.setSelected(temp.baidu);
				jchkBing.setSelected(temp.bing);
				jchkYoudao.setSelected(temp.youdao);
				rpBaidu.setResult(temp.baidu,temp.Baidu_Content,temp.Baidu_Liked,temp.Baidu_Liked_Count);
				rpBing.setResult(temp.bing,temp.Bing_Content,temp.Bing_Liked,temp.Bing_Liked_Count);
				rpYoudao.setResult(temp.youdao,temp.Youdao_Content,temp.Youdao_Liked,temp.Youdao_Liked_Count);
				repaint_jpMUI();
				repaint_jpIcons();
			}
		});
		jbtForward.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(locked)return;
				ResultInfo temp=scene.forward();
				word=temp.word;
				cbaidu=temp.baidu;
				cbing=temp.bing;
				cyoudao=temp.youdao;
				jtfWord.setText(word);
				jchkBaidu.setSelected(temp.baidu);
				jchkBing.setSelected(temp.bing);
				jchkYoudao.setSelected(temp.youdao);
				rpBaidu.setResult(temp.baidu,temp.Baidu_Content,temp.Baidu_Liked,temp.Baidu_Liked_Count);
				rpBing.setResult(temp.bing,temp.Bing_Content,temp.Bing_Liked,temp.Bing_Liked_Count);
				rpYoudao.setResult(temp.youdao,temp.Youdao_Content,temp.Youdao_Liked,temp.Youdao_Liked_Count);
				repaint_jpMUI();
				repaint_jpIcons();
			}
		});
		jbtMail.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				changing=true;
				Card c=mailBox.getFirst();
				right_down.removeAll();
				right_down.add(right_down_read,BorderLayout.CENTER);
				jpCard.removeAll();
				jpCard.add(c,BorderLayout.CENTER);
				right_down.updateUI();
				jpCard.updateUI();
				repaint_jpIcons();
			}
		});
		jbtNext.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				mailBox.removeFirst();
				if(mailBox.isEmpty())
				{
					right_down.removeAll();
					right_down.add(right_down_normal,BorderLayout.CENTER);
					right_down.updateUI();
					changing=false;
					repaint_jpIcons();
					return;
				}
				Card c=mailBox.getFirst();
				jpCard.removeAll();
				jpCard.add(c,BorderLayout.CENTER);
				repaint_jpIcons();
				jpCard.updateUI();
			}
		});
		jbtOK.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(!connect.connection_successful)
				{
					JFrame temp=new JFrame();
					temp.setTitle("��ʾ��Ϣ");
					temp.setSize(350,100);
					temp.setLocationRelativeTo(null);
					temp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					temp.setVisible(true);
					Label lab=new Label("����������Ժ�����");
					lab.setAlignment(Label.CENTER);
					lab.setFont(new Font("΢���ź�",18,Font.PLAIN));
					temp.add(lab);
					return;
				}
				right_down.removeAll();
				right_down.add(right_down_normal,BorderLayout.CENTER);
				right_down.updateUI();
				changing=false;
				repaint_jpIcons();
				Message<SendShare> msg=new Message<SendShare>();
				msg.type=Message.SHARE;
				msg.content=new SendShare(jchkSTA.isSelected());
				if(!msg.content.sendToAll)
				{
					int []a=jlstUser.getSelectedIndices();
					for(int i=0;i<a.length;i++)
						msg.content.list.add(onlineUser.get(a[i]));
				}
				msg.content.card=new Card(myself.UserID,myself.UserName,word,
						meaning_selected,provider_selected,jtaComment.getText());
				new Thread(new Sender(msg,connect.toServer)).start();
			}
		});
		jbtSearch.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(jtfWord.getText()==null)return;
				if(locked)
				{
					JFrame temp=new JFrame();
					temp.setTitle("��ʾ��Ϣ");
					temp.setSize(350,100);
					temp.setLocationRelativeTo(null);
					temp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					temp.setVisible(true);
					Label lab=new Label("���緱æ�����Ժ�����");
					lab.setAlignment(Label.CENTER);
					lab.setFont(new Font("΢���ź�",18,Font.PLAIN));
					temp.add(lab);
					return;
				}
				if(!connect.connection_successful)
				{
					JFrame temp=new JFrame();
					temp.setTitle("��ʾ��Ϣ");
					temp.setSize(350,100);
					temp.setLocationRelativeTo(null);
					temp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					temp.setVisible(true);
					Label lab=new Label("����������Ժ�����");
					lab.setAlignment(Label.CENTER);
					lab.setFont(new Font("΢���ź�",18,Font.PLAIN));
					temp.add(lab);
					return;
				}
				locked=true;
				scene.move_on();
				repaint_jpIcons();
				Message<SearchRequest> msg=new Message<SearchRequest>();
				msg.type=Message.SEARCH;
				msg.content=new SearchRequest();
				msg.content.word=jtfWord.getText();
				word=jtfWord.getText();
				cbaidu=jchkBaidu.isSelected();
				cbing=jchkBing.isSelected();
				cyoudao=jchkYoudao.isSelected();
				msg.content.Baidu=jchkBaidu.isSelected();
				msg.content.Bing=jchkBing.isSelected();
				msg.content.Youdao=jchkYoudao.isSelected();
				msg.content.check();
				try
				{
					connect.toServer.writeObject(msg);
				} 
				catch (IOException e1){}
			}
		});
		jtfWord.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(jtfWord.getText()==null)return;
				if(jtfWord.getText().equals(word) && cbaidu==jchkBaidu.isSelected() 
						&& cbing==jchkBing.isSelected() && cyoudao==jchkYoudao.isSelected())return;
				if(locked)return;
				if(!connect.connection_successful)return;
				locked=true;
				scene.move_on();
				repaint_jpIcons();
				Message<SearchRequest> msg=new Message<SearchRequest>();
				msg.type=Message.SEARCH;
				msg.content=new SearchRequest();
				msg.content.word=jtfWord.getText();
				word=jtfWord.getText();
				cbaidu=jchkBaidu.isSelected();
				cbing=jchkBing.isSelected();
				cyoudao=jchkYoudao.isSelected();
				msg.content.Baidu=jchkBaidu.isSelected();
				msg.content.Bing=jchkBing.isSelected();
				msg.content.Youdao=jchkYoudao.isSelected();
				msg.content.check();
				try
				{
					connect.toServer.writeObject(msg);
				} 
				catch (IOException e1){}
			}
		});
	}
}