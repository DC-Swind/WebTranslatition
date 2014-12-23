package message;

import java.io.Serializable;

public class Message<T> implements Serializable
{
	private static final long serialVersionUID = 1L;
	public static final short EXIT=0;
	public static final short LOGIN=1;
	public static final short REGIST=2;
	public static final short SEARCH=3;
	public static final short LOGOUT=7;
	public static final short USER_LIST=4;
	public static final short SHARE=5;
	public static final short LIKE=6;
	public short type=-1;
	public T content;
	public Message(){}
	public Message(T buf){content=buf;}
}