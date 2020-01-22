import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Executors;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class defines the server operations.
 * 
 * @author Lin Yizhou
 *
 */
public class Server {
	private ServerSocket serverSocket;
	private board myBoard;

	// The set of all the print writers for all the clients, used for broadcast.
	private Set<PrintWriter> writers = new HashSet<>();
	
	/**
	 * This method constructs the method.
	 * 
	 * @param serverSocket
	 */
	public Server(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
		this.myBoard = new board();
	}

	/**
	 * This method starts threads to serve different clients.
	 * 
	 */
	public void start() {
		var pool = Executors.newFixedThreadPool(200);
		int clientCount = 0;
		while (true) {
			while (clientCount <3) {
				try {
					Socket socket = serverSocket.accept();
					clientCount++;
					pool.execute(new Handler(socket,clientCount));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			clientCount=0;
		}
	}
	
	
	/**
	 * This class defines the job of a server thread.
	 * 
	 * @author Lin Yizhou
	 */
	public class Handler implements Runnable {
		private Socket socket;
		private Scanner input;
		private PrintWriter output;
		private int Clientno;
		private boolean gameover= false;
		private boolean submitname = false;

		/**
		 * This method constructs the runnable object.
		 */
		public Handler(Socket socket, int no) {
			this.socket = socket;
			this.Clientno = no;
		}

		/**
		 * This method defines the job of a server thread.
		 */
		@Override
		public void run() {
			try {
				input = new Scanner(socket.getInputStream());
				output = new PrintWriter(socket.getOutputStream(), true);

				// add this client to the broadcast list
				writers.add(output);
				if (Clientno == 1) {
					output.println("User1");
				}

				while (input.hasNextLine()) {
					var command = input.nextLine();					
					if(!submitname) {
						this.submitname=true;
						myBoard.userenter();
						if (myBoard.getuserno()==2) {
							for (PrintWriter writer : writers) {
								writer.println("gamestart");
							}
						}
					}else {
						int num= Integer.parseInt(command.split(":")[0]);
						String result=myBoard.setNumber(num, Clientno);
						if(result.equals("keepon")) {
							for (PrintWriter writer : writers) {
								writer.println(Clientno+":move:"+command);
							}
						}else if(result.equals("win")){
							this.gameover=true;
							for (PrintWriter writer : writers) {
								writer.println(Clientno+":win:"+command);
							}
						}else if(result.equals("draw")){
							this.gameover=true;
							for (PrintWriter writer : writers) {
								writer.println(Clientno+":draw:"+command);
							}
						}
					}
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			} finally {
				writers.remove(output);
				if (!gameover) {
					myBoard.clearboard();
					for (PrintWriter writer : writers) {
						writer.println("left");
					}
				}
			}
		}
	}
}
