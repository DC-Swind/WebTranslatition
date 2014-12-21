package tools;

public class GetTranslationReturn {
	public String Chinese1;
	public String Chinese2;
	public String Chinese3;
	public String Phonetic1;
	public String Phonetic2;
	public String Phonetic3;
	public int Like1;
	public int Like2;
	public int Like3;
	public boolean Liked1;
	public boolean Liked2;
	public boolean Liked3;
	public GetTranslationReturn(String c1,String c2,String c3,String p1,String p2,String p3,int l1,int l2,int l3){
		Chinese1 = c1;
		Chinese2 = c2;
		Chinese3 = c3;
		Phonetic1 = p1;
		Phonetic2 = p2;
		Phonetic3 = p3;
		Like1 = l1;
		Like2 = l2;
		Like3 = l3;
	}
	public void addinfo(int ld1,int ld2,int ld3){
		if (ld1 == 0) Liked1 = false; else Liked1 = true;
		if (ld2 == 0) Liked2 = false; else Liked2 = true;
		if (ld3 == 0) Liked3 = false; else Liked3 = true;
	}
}
