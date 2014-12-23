package message;

import java.io.Serializable;
import java.util.ArrayList;

public class UserList implements Serializable
{
	private static final long serialVersionUID = 1L;
	public ArrayList<User> list;
	public UserList(){list=new ArrayList<User>();} 
}