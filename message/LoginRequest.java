package message;

import java.io.Serializable;

public class LoginRequest implements Serializable
{
	private static final long serialVersionUID = 1L;
	public String userID=null;
	public String userPassword=null;
	public LoginRequest(String ID,String Pass){userID=ID;userPassword=Pass;}
}