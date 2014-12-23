package message;

import java.io.Serializable;

public class LoginResponse implements Serializable
{
	private static final long serialVersionUID = 1L;
	public boolean identifier;
	public String userName=null;
	public LoginResponse(){identifier=false;}
	public LoginResponse(String name){identifier=true;userName=name;}
}
