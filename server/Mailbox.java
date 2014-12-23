package server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import message.Card;
import message.User;
import message.UserList;

public class Mailbox {
	public int userlist_checked_time;
	public int mymailboxindex = -1;
	public static int userlist_updated_time = 0;
	public static UserList userlist = new UserList();
	
	public static boolean[] sharecard = new boolean[ThreadPool.MAX_THREADS];
	public static Object[] cardlist = new Object[ThreadPool.MAX_THREADS];
	public static String[] mailhost = new String[ThreadPool.MAX_THREADS];
	
	public static Lock lock = new ReentrantLock();
	
	Mailbox(){
		userlist_checked_time = -1;
	}
	/*注意同步问题*/
	public static void alladdCard(Card card){
		for(int i=0; i<ThreadPool.MAX_THREADS; i++)
			if (mailhost[i] != null) addCard(mailhost[i],card);
	}
	public static void someaddCard(ArrayList<User> userlist,Card card){
		Iterator<User> it = userlist.iterator();
        while(it.hasNext()) addCard(it.next().UserID,card);
	}
	public static void addCard(String userid,Card card){
		int mailboxindex = -1;
		for (int i=0; i<ThreadPool.MAX_THREADS; i++) if (mailhost[i] != null && mailhost[i].equals(userid)){
			mailboxindex = i;
			break;
		}
		if (mailboxindex == -1) return;
		
		if (cardlist[mailboxindex] == null) cardlist[mailboxindex] = new ArrayList<Card>();
		
		@SuppressWarnings({ "unchecked" })
		ArrayList<Card> list = (ArrayList<Card>)cardlist[mailboxindex];
		
		list.add(card);
		if (sharecard[mailboxindex] == false) sharecard[mailboxindex] = true;
	}
	
	public static Card getCard(int to){
		Card card = null;
		@SuppressWarnings("unchecked")
		ArrayList<Card> list = (ArrayList<Card>)cardlist[to];
		card = list.remove(0);
		if (list.size() == 0) sharecard[to] = false;
		return card;
	}
	public static void userlist_updated_time_add(){
		System.out.println("userlist changed");
		userlist_updated_time = (userlist_updated_time + 1) % 1000000;
	}
	public void getamailindex(User user) {
		for (int i=0; i<ThreadPool.MAX_THREADS; i++) if (mailhost[i] == null){
			mymailboxindex = i;
			mailhost[i] = new String(user.UserID);
			break;
		}
	}
	public void rebackmailbox() {
		if (mymailboxindex != -1){
			mailhost[mymailboxindex] = null;
			cardlist[mymailboxindex] = null;
			sharecard[mymailboxindex] = false;
		}
	}
}
