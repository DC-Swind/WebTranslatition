package client;

public class SceneQueue
{
	//���е���ֹ�㣬cur=-1��ʾ����û�����õ����У�end=-1��ʾ�գ�endָ���βһ����Ч��Ԫ�ء�
	int begin=0,cur=-1,end=-1;
	ResultInfo[] queue=new ResultInfo[10];
	UI temp;
	public SceneQueue(UI ui)
	{
		temp=ui;
		for(int i=0;i<10;i++)
		{
			queue[i]=new ResultInfo();
		}
	}
	boolean is_start(){return begin==cur||end==-1;}
	boolean is_end(){return cur==end||end==-1||cur==-1;}
	void move_on()
	{
		if(end==-1){end=0;save_current_scene(0);return;}
		if(cur!=-1){end=cur;cur=-1;return;}
		end=(end+1)%10;
		if(end==begin)begin=(begin+1)%10;
		save_current_scene(end);
	}
	ResultInfo back()
	{
		if(cur==-1)
		{
			cur=end;
			end=(end+1)%10;
			if(end==begin)begin=(begin+1)%10;
			save_current_scene(end);
			return queue[cur];
		}
		cur=(cur-1)%10;
		return queue[cur];
	}
	ResultInfo forward()
	{
		cur=(cur+1)%10;
		return queue[cur];
	}
	private void save_current_scene(int loc)
	{
		queue[loc].word=temp.word;
		queue[loc].baidu=temp.rpBaidu.selected;
		queue[loc].youdao=temp.rpYoudao.selected;
		queue[loc].bing=temp.rpBing.selected;
		queue[loc].Baidu_Content=temp.rpBaidu.jtaRslt.getText();
		queue[loc].Baidu_Liked=temp.rpBaidu.liked;
		queue[loc].Baidu_Liked_Count=temp.rpBaidu.liked_count;
		queue[loc].Bing_Content=temp.rpBing.jtaRslt.getText();
		queue[loc].Bing_Liked=temp.rpBing.liked;
		queue[loc].Bing_Liked_Count=temp.rpBing.liked_count;
		queue[loc].Youdao_Content=temp.rpYoudao.jtaRslt.getText();
		queue[loc].Youdao_Liked=temp.rpYoudao.liked;
		queue[loc].Youdao_Liked_Count=temp.rpYoudao.liked_count;
	}
}
//�ұ߷��������ֳ�
class ResultInfo
{
	String word;
	boolean baidu,youdao,bing;
	boolean Baidu_Liked;
	String Baidu_Content;
	int Baidu_Liked_Count;
	boolean Bing_Liked;
	String Bing_Content;
	int Bing_Liked_Count;
	boolean Youdao_Liked;
	String Youdao_Content;
	int Youdao_Liked_Count;
}