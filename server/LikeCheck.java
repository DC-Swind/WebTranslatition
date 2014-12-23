package server;

import message.Like;
import message.User;

public class LikeCheck {

	LikeCheck(Like like,User user){
		Connectdb conn = new Connectdb();
		if (like.provider == Like.BAIDU) conn.likeupdate(user.UserID,like.word,1);
		else if (like.provider == Like.BING) conn.likeupdate(user.UserID, like.word, 3);
		else if (like.provider == Like.YOUDAO) conn.likeupdate(user.UserID, like.word, 2);
		conn.disconnect();
		System.out.println("like update success! from LikeCheck.java");
	}
}
