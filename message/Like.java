package message;

import java.io.Serializable;

public class Like implements Serializable
{
	public static final short BAIDU=0;
	public static final short BING=1;
	public static final short YOUDAO=2;
	private static final long serialVersionUID = 1L;
	public short provider;
	public String word=null;
}