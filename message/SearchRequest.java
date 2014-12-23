package message;

import java.io.Serializable;

public class SearchRequest implements Serializable
{
	private static final long serialVersionUID = 1L;
	public boolean Baidu;
	public boolean Bing;
	public boolean Youdao;
	public String word=null;
	public SearchRequest()
	{
		Baidu=false;
		Bing=false;
		Youdao=false;
	}
	public void check()
	{
		if(!Baidu && !Bing && !Youdao)
		{
			Baidu=true;
			Bing=true;
			Youdao=true;
		}
	}
}