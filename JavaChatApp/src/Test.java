import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String s = "";
		ThreadTest st = new ThreadTest();
		st.start();
		while (true) {
			try {
				s = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			st.changeValue(s);

			if (s.isEmpty())
				break;
		}
		System.out.println("Main thread done.");
	}

}


class ThreadTest extends Thread {
	private boolean changed = false;
	private String value;
	
	public void changeValue(String v) {
		value = v;
		changed = true;
	}
	
	public void run () {
		while (true) {
			if (changed) {
				if (value.isEmpty())
					break;
				
				System.out.println(value);
				changed = false;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Thread exit loop.");
		
/*		try {
			this.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/		System.out.println("Thread done.");
	}
}