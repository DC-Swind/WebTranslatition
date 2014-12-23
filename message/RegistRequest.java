package message;

import java.io.Serializable;

public class RegistRequest implements Serializable
{
	private static final long serialVersionUID = 1L;
	public String UID=null;
	public String UName=null;
	public String PassWord=null;
	public RegistRequest(String id,String name,String pw)
	{
		UID=id;UName=name;PassWord=pw;
	}
}