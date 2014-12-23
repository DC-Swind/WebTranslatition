package server;
import message.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tools.GetTranslationReturn;

public class Translate {
	private User user = null;
	private SearchRequest msg = null;
	public SearchResponse return_ans = new SearchResponse();
	
	Translate(SearchRequest msg,User user){
		this.user = user;
		this.msg = msg;
		int tid = ExistDB(msg.word);
		
		//can search from db
		
		if (tid == -1){
			GetFromInet(msg,tid);
				
		}else{
			GetFromDB(tid);
		}
		
		
		
		//can not search from db
		/*
		GetFromInet(msg,tid);
		tid = ExistDB(msg.word); 
		GetFromDB(tid); 
		*/
		 
		
	}


	private void GetFromDB(int tid){
		Connectdb conn = new Connectdb();
		String userid = null;
		if (user != null) userid = user.UserID;
		GetTranslationReturn ans = conn.gettranslation(tid,userid);
		if (ans == null){
			System.out.println("get translate from db error!");
		}else{
			//return_ans.Baidu_Phonetic = ans.Phonetic1;
			return_ans.Baidu_Meaning = ans.Phonetic1 + ans.Chinese1;
			return_ans.Baidu_Liked_Count = ans.Like1;
			return_ans.Baidu_Liked = ans.Liked1;
			return_ans.Baidu_Selected = msg.Baidu;
			//return_ans.Youdao_Phonetic = ans.Phonetic2;
			return_ans.Youdao_Meaning = ans.Phonetic2 + ans.Chinese2;
			return_ans.Youdao_Liked_Count = ans.Like2;
			return_ans.Youdao_Liked = ans.Liked2;
			return_ans.Youdao_Selected = msg.Youdao;
			//return_ans.Bing_Phonetic = ans.Phonetic3;
			return_ans.Bing_Meaning = ans.Phonetic3 + ans.Chinese3;
			return_ans.Bing_Liked_Count = ans.Like3;
			return_ans.Bing_Liked = ans.Liked3;
			return_ans.Bing_Selected = msg.Bing;
			System.out.println("translate from db success!");
		}
		conn.disconnect();
	}
	private void GetFromInet(SearchRequest msg,int tid){
		String c1 = "";
		String c2 = "";
		String c3 = "";
		String p1 = "";
		String p2 = "";
		String p3 = "";
		
		
		String[] a1,a2,a3;
		//if (msg.Baidu){
		if (true){
			a1 = dicFromBaidu(msg.word);
			//return_ans.Baidu_Phonetic = a1[0];
			return_ans.Baidu_Meaning = a1[0] + a1[1];
			return_ans.Baidu_Liked_Count = 0;
			if (user != null) return_ans.Baidu_Liked = false; else return_ans.Baidu_Liked = true;
			return_ans.Baidu_Selected = msg.Baidu;
			c1 = a1[1];
			p1 = a1[0]; 
			//System.out.println("Baidu:");
			//System.out.println(p1 + c1);
		}
		//if (msg.Youdao){
		if (true){
			a2 = dicFromYoudao(msg.word);
			//return_ans.Youdao_Phonetic = a2[0];
			return_ans.Youdao_Meaning =a2[0] + a2[1];
			return_ans.Youdao_Liked_Count = 0;
			if (user != null) return_ans.Youdao_Liked = false; else return_ans.Youdao_Liked = true;
			return_ans.Youdao_Selected = msg.Youdao;
			c2 = a2[1];
			p2 = a2[0];
			//System.out.println("Youdao:");
			//System.out.println(p2 + c2);
		}
		//if (msg.Bing){
		if (true){
			a3 = dicFromBing(msg.word);
			//return_ans.Bing_Phonetic = a3[0];
			return_ans.Bing_Meaning = a3[0] + a3[1];
			return_ans.Bing_Liked_Count = 0;
			if (user != null) return_ans.Bing_Liked = false; else return_ans.Bing_Liked = true;
			return_ans.Bing_Selected = msg.Bing;
			c3 = a3[1];
			p3 = a3[0];
			//System.out.println("Bing:");
			//System.out.println(p3 + c3);
		}
		
		if (tid == -1) AddTranslation(msg.word,c1,p1,c2,p2,c3,p3);
		else UpdateTranslation(tid,c1,p1,c2,p2,c3,p3);
		
	}
	private void UpdateTranslation(int tid,String c1,String p1,String c2,String p2,String c3,String p3){
		Connectdb conn = new Connectdb();
		int code = conn.updatetranslation(tid,c1,p1,c2,p2,c3,p3);
		if (code == -1) System.out.println("Translate::updatetranslation error");
		conn.disconnect();
	}
	private void AddTranslation(String word,String c1,String p1,String c2,String p2,String c3,String p3){
		Connectdb conn = new Connectdb();
		int code = conn.addtranslation(word,c1,p1,c2,p2,c3,p3);
		if (code == -1) System.out.println("Translate::addtranslation error");
		conn.disconnect();
	}
	private int ExistDB(String word){
		Connectdb conn = new Connectdb();
		int tid = conn.ExistWord(word);
		conn.disconnect();
		return tid;
	}
	/*
	private String[] dicFromJinshan(String english){
		String strurl = "http://dict-co.iciba.com/api/dictionary.php?key=B28960CB73E4B04BA8BBBDF412F68CE5&w="+english;
        String getContent = "";
        try {  
            URL url = new URL(strurl);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(),"utf8"));  
            String s = "";  
            StringBuffer sb = new StringBuffer("");  
            while ((s = br.readLine()) != null) {  
                sb.append(s);  
            }  
            br.close();  
            getContent = sb.toString();  
        } catch (Exception e) {  
            System.out.println("error open url:" + strurl);  
        }
        String[] rs = new String[2];
        Pattern pattern = Pattern.compile("<ps>(.*?)</ps>");   
        Matcher matcher = pattern.matcher(getContent);
        String phonetic = "";
        if (matcher.find()) phonetic = matcher.group(1);
            
        pattern = Pattern.compile("<pos>(.*?)</pos>.*?<acceptation>(.*?)</acceptation>");   
        matcher = pattern.matcher(getContent);
        String ans = "";
        while (matcher.find()) {
            ans = ans + matcher.group(1) + matcher.group(2);  
        }
        phonetic.replace('\'', '*');
        ans.replace('\"', '*');
        rs[0] = phonetic; rs[1] = ans;
        return rs;
	}
	*/
	private String[] dicFromBaidu(String english){
		String strurl = "http://fanyi.baidu.com/v2transapi?from=en&to=zh&transtype=json&query="+english;
        String getContent = "";
        try {  
            URL url = new URL(strurl);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(),"utf8"));  
            String s = "";  
            StringBuffer sb = new StringBuffer("");  
            while ((s = br.readLine()) != null) {  
                sb.append(s);  
            }  
            br.close();  
            getContent = sb.toString();  
        } catch (Exception e) {  
            System.out.println("error open url:" + strurl);  
        }
        String[] rs = new String[2];
        Pattern pattern = Pattern.compile("\"simple_means\".*?\"ph_en\":\"(.*?)\",\"ph_am\":\"(.*?)\"");   
        Matcher matcher = pattern.matcher(getContent);
        String phonetic = "";
        if (matcher.find()) phonetic = "√¿“Ù[" + matcher.group(1) + "]”¢“Ù[" + matcher.group(2) + "]\n";
        phonetic = UnicodeToString(phonetic);
        
        pattern = Pattern.compile("\"part\":\"(.*?)\",\"means\":\\[\"(.*?)\"\\]");   
        matcher = pattern.matcher(getContent);
        String ans = "";
        while (matcher.find()) {
            ans = ans + matcher.group(1) + matcher.group(2) + "\n";  
        }
        ans = UnicodeToString(ans);
        String[] anss = ans.split("\",\"");
        ans = "";
        for (int i=0; i<anss.length; i++) ans += anss[i];

        rs[0] = phonetic; rs[1] = ans;
        return rs;
	}
	private String[] dicFromYoudao(String english){
		String strurl = "http://fanyi.youdao.com/openapi.do?keyfrom=SwindBlog&key=811594467&type=data&doctype=json&version=1.1&q="+english;
        String getContent = "";
        try {  
            URL url = new URL(strurl);  
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(),"utf8")); 
            String s = "";  
            StringBuffer sb = new StringBuffer("");  
            while ((s = br.readLine()) != null) { 
                sb.append(s);  
            }  
            br.close();  
            getContent = sb.toString();  
        } catch (Exception e) {  
            System.out.println("error open url:" + strurl);  
        }


        String[] rs = new String[2];
        String ans = "";
        String phonetic = "";
        Pattern pattern = Pattern.compile("\"translation\":\\[\"(.*?)\"\\].*?\"us-phonetic\":\"(.*?)\".*?\"uk-phonetic\":\"(.*?)\".*?explains\":\\[\"(.*?)\"\\]");
        Matcher matcher = pattern.matcher(getContent);
        if (matcher.find()) {
            ans = ans + matcher.group(1) +"\n"+ matcher.group(4);  
            phonetic = "√¿“Ù[" + matcher.group(2) + "]”¢“Ù[" + matcher.group(3) + "]\n";
        }
        String[] anss = ans.split("\",\"");
        ans = "";
        for (int i=0; i<anss.length; i++)if (!anss[i].equals("")) ans += anss[i] + "\n";
        ans = UnicodeToString(ans);
        rs[0] = phonetic; rs[1] = ans;
        return rs;
	}
    private String[] dicFromBing(String english){        
        String strurl = "http://cn.bing.com/dict/search?q="+english;
        String getContent = "";
        try {  
            URL url = new URL(strurl);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(),"utf8"));  
            String s = "";  
            StringBuffer sb = new StringBuffer("");  
            while ((s = br.readLine()) != null) {  
                sb.append(s);  
            }  
            br.close();  
            getContent = sb.toString();  
        } catch (Exception e) {  
            System.out.println("error open url:" + strurl);  
        }
        
        Pattern pattern = Pattern.compile("<div class=\"hd_prUS\">√¿&#160;(.*?)</div>.*?<div class=\"hd_pr\">”¢&#160;(.*?)</div>");   
        Matcher matcher = pattern.matcher(getContent);

        String phonetic = "";
        if (matcher.find()) phonetic = "√¿“Ù" + matcher.group(1)+"”¢“Ù" + matcher.group(2)+"\n";
        
        String mean = "";
        //pattern = Pattern.compile("<li><span class=\"pos\">(.*?)</span><span class=\"def\"><span>(.*?)</span></span></li>");
        pattern = Pattern.compile("<li><span class=\"pos\">(.*?)</span><span class=\"def\">(.*?)</span></li>");  
        matcher = pattern.matcher(getContent);
        
        while (matcher.find()) {
            mean = mean + matcher.group(1);
            Pattern pattern2 = Pattern.compile("<span>(.*?)</span>");
            Matcher matcher2 = pattern2.matcher(matcher.group(2));
            while(matcher2.find()){
            	mean = mean + matcher2.group(1) + " ";
            } 
            
            mean = mean +"\n";  
        }
        String[] rs = new String[2];
        rs[0] = WebUnicodeToString(phonetic); rs[1] = mean;
        return rs;
    }
    /*
	private String translateFromYoudao(String english){
        String strurl = "http://fanyi.youdao.com/translate?smartresult=dict&smartresult=rule&smartresult=ugc&sessionFrom=http://www.baidu.com/s&doctype=json&"
        +"i="+english+"&keyfrom=fanyi.web&type=AUTO&typoResult=true&ue=UTF-8&xmlVersion=1.6";
        String getContent = "";
        try {  
            URL url = new URL(strurl);  
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(),"utf8"));  
            String s = "";  
            StringBuffer sb = new StringBuffer("");  
            while ((s = br.readLine()) != null) {  
                sb.append(s);  
            }  
            br.close();  
            getContent = sb.toString();  
        } catch (Exception e) {  
            System.out.println("error open url:" + strurl);  
        }

        Pattern pattern = Pattern.compile("\"tgt\":\"(.*?)\"}");   
        Matcher matcher = pattern.matcher(getContent);

        String ans = "";
        while (matcher.find()) {
            ans = ans + matcher.group(1);  
        }

        return UnicodeToString(ans);
    }
    */
    private static String WebUnicodeToString(String str) {
        Pattern pattern = Pattern.compile("(&#([0-9]*))");   
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 10);
            str = str.replace(matcher.group(1), ch + "");   
        }
        return str;
    }
    private static String UnicodeToString(String str) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");   
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch + "");   
        }
        return str;
    }
    /*
    private String translateFromBaidu(String english){        
        String strurl = "http://openapi.baidu.com/public/2.0/bmt/translate?client_id=LGmgctDZlz7GSiWiGSifBVKj&from=en&to=zh&q="+english+"";
        String getContent = "";
        try {  
            URL url = new URL(strurl);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(),"utf8"));  
            String s = "";  
            StringBuffer sb = new StringBuffer("");  
            while ((s = br.readLine()) != null) {  
                sb.append(s);  
            }  
            br.close();  
            getContent = sb.toString();  
        } catch (Exception e) {  
            System.out.println("error open url:" + strurl);  
        }
   
        int index = getContent.indexOf("\"dst\":");
        String ans = getContent.substring(index+7,getContent.length()-4);
        return UnicodeToString(ans);
    }
    private String translateFromBing(String english){        
        String strurl = "http://api.microsofttranslator.com/v2/ajax.svc/TranslateArray2?appId=%22TWgU6WmTpxwcgaREh0cqiado4XNTOKm_55SdbHiGqKwjQj68NgR5fjcwAaCVds9gE%22&"+
            "texts=[%22"+english+"%22]&from=%22%22&to=%22zh-CHS%22&options={}&oncomplete=onComplete_0&onerror=onError_0";
        String getContent = "";
        try {  
            URL url = new URL(strurl);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(),"utf8"));  
            String s = "";  
            StringBuffer sb = new StringBuffer("");  
            while ((s = br.readLine()) != null) {  
                sb.append(s);  
            }  
            br.close();  
            getContent = sb.toString();  
        } catch (Exception e) {  
            System.out.println("error open url:" + strurl);  
        }
        
        Pattern pattern = Pattern.compile("\"TranslatedText\":\"(.*?)\"");   
        Matcher matcher = pattern.matcher(getContent);

        String ans = "";
        while (matcher.find()) {
            ans = ans + matcher.group(1);  
        }
        return ans;
    }
    */
}
