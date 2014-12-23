package message;

import java.io.Serializable;

public class RegistResponse implements Serializable
{
	private static final long serialVersionUID = 1L;
	public boolean identifier;
	public RegistResponse()
	{
		identifier=false;
	}
}