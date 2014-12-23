package server;
import java.io.*;
import java.net.*;
import java.util.*;

public class Server{
    public static void main(String[] args) throws Exception {
        //Server server = new Server();
    	new Server();
    }    
    Server(){
    	clearUserStatus();
        startServer();
    }
    
    private void clearUserStatus() {
		// clear login status to 0
    	Connectdb conn = new Connectdb();
    	conn.clearLoginStatus();
    	conn.disconnect();	
	}
    
	public void startServer(){
    	//pool for thread about receiving the quest and answering the quest
    	ThreadPool threadpoolforlisten = new ThreadPool();
    	//pool for thread about sending the user list and card
    	ThreadPool threadpoolforsend = new ThreadPool();
        try{
            @SuppressWarnings("resource")
            //create server socket
			ServerSocket serverSocket = new ServerSocket(18800);
            if (serverSocket != null) System.out.println("Server started @ " + new Date() + '\n');
            
            //while -- get connection with client
            while (true){
                Socket socket = serverSocket.accept();
                System.out.println("New connection accepted " +socket.getInetAddress() + ":" +socket.getPort() + "\n");
                
                //create a server thread to solve the connection
                ServerThread serverthread = new ServerThread(socket,threadpoolforsend);
                
                threadpoolforlisten.add(serverthread);
            }
        } catch (IOException ex){
       
            System.err.println(ex);
            System.out.println("Server socket connect shut down...Server shut down...from Server.java");
        }finally{
        	//close serverSocket
        	
        }    
    }

}