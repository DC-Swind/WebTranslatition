package message;

import java.io.Serializable;

public class SearchResponse implements Serializable
{
	private static final long serialVersionUID = 1L;
	public String Baidu_Meaning=null;
	public int Baidu_Liked_Count;
	public boolean Baidu_Liked;
	public boolean Baidu_Selected;
	public String Bing_Meaning=null;
	public int Bing_Liked_Count;
	public boolean Bing_Liked;
	public boolean Bing_Selected;
	public String Youdao_Meaning=null;
	public int Youdao_Liked_Count;
	public boolean Youdao_Liked;
	public boolean Youdao_Selected;
}