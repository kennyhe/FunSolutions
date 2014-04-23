import java.util.*;
import java.io.*;



public class ChatApp implements Observer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		chat = new ChatApp();
		mm = new MessageManager();
		mm.addObserver(chat);
		port = mm.getServerPort();

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    	
    	String s = "";
		while (true) {
			System.out.println("Input a message, or <connect> <ip> <port>, or an empty line to quit:");
	    	try {
	            s = br.readLine();
	        } catch (IOException ioe) {
	            System.out.println("IO error trying to read your name!");
	        }
	    	
	    	if (s.isEmpty())
	    		break;
	    	
//	    	System.out.println("Read a line: {" + s + "}");
	    	if (s.matches("connect\\s+\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\s+\\d+")) {
	    		Scanner scan = new Scanner(s);
	    		scan.skip("connect\\s");
	    		ip = scan.next();
	    		port = scan.next();
//	    		System.out.println("Get server: " + ip + ":" + port);
	    	} else {
//	    		System.out.println("Send message: {" + s + "}");
	    		mm.sendMsg(ip, port, s);
	    	}

		};
		mm.quitLocalServer();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void update(Observable obj, Object arg) {
        if (arg instanceof Message) {
            Message resp = (Message) arg;
            System.out.println("\nReceived message from [" + resp.client + "]: " + resp.msg + "\n");
        }
    }

	static MessageManager mm;
	static ChatApp chat;
	static String ip = "127.0.0.1";
	static String port = "";
}
