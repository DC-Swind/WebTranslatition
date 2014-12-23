package message;

import java.io.Serializable;

public class User implements Serializable
{
	private static final long serialVersionUID = 1L;
	public String UserID=null;
	public String UserName=null;
	public User(String id,String name)
	{
		UserID=id;
		UserName=name;
	}
}