package message;

import java.io.Serializable;
import java.util.ArrayList;

public class SendShare implements Serializable
{
	private static final long serialVersionUID = 1L;
	public boolean sendToAll;
	public ArrayList<User> list;
	public Card card;
	public SendShare(boolean all)
	{
		sendToAll=all;
		if(!sendToAll)list=new ArrayList<User>();
	}
}