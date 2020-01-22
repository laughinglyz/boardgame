import java.io.*;
import java.net.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;


/**
 * This class makes a simple Tic Tac Toe Cient side application.
 * 
 * @author Lin Yizhou
 *
 */
public class GameClient {

	private Socket socket;
	private Scanner in;
	private PrintWriter out;
	
	private int user=2;
	private boolean[] used= {false,false,false,false,false,false,false,false,false};
	private boolean canmove = false;

	JMenuBar menuBar = new JMenuBar();
	JMenu control = new JMenu("Control");
	JMenu Help = new JMenu("Help");
	JMenuItem help = new JMenuItem("Instruction");
	JMenuItem menuItem = new JMenuItem("Exit");

	JPanel mainPanel = new JPanel();
	JPanel inforPanel = new JPanel();
	JPanel GamePanel = new JPanel();
	JPanel SubmitPanel = new JPanel();

	JButton[] buttons = new JButton[9];

	JLabel label_info = new JLabel("Enter your player name...");
	JTextField txt_inputbet = new JTextField(25);
	JButton submit = new JButton("Submit");
	
	JFrame frame = new JFrame();

	public static void main(String[] args) throws IOException {
		GameClient g = new GameClient();
		g.go();
	}
	
	/**
	 * This method initializes the client side application.
	 * 
	 */
	public void go() {

		try {
			this.socket = new Socket("127.0.0.1", 5050);
			this.in = new Scanner(socket.getInputStream());
			this.out = new PrintWriter(socket.getOutputStream(), true);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		BorderLayout fl = new BorderLayout();
		inforPanel.setLayout(fl);
		inforPanel.add(label_info, BorderLayout.WEST);

		GamePanel.setLayout(new GridLayout(3, 3));

		for (int i = 0; i < 9; i++) {
			buttons[i] = new JButton();
			buttons[i].setEnabled(false);
			buttons[i].setBackground(Color.WHITE);
			GamePanel.add(buttons[i]);
			Font f= new Font("TimesRoman",Font.BOLD,25);
			buttons[i].setFont(f);
			buttons[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					int index=-1;
					for (int i=0;i<9;i++) {
						if(buttons[i]==event.getSource()) {
							index=i;
						}
					}
					if (!used[index] && canmove  ) {
						out.println(index+":");
					}
				}
			});
		}

		SubmitPanel.add(txt_inputbet);
		SubmitPanel.add(submit);

		control.add(menuItem);
		menuBar.add(control);
		Help.add(help);
		menuBar.add(Help);

		frame.setJMenuBar(menuBar);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(GamePanel, BorderLayout.CENTER);
		frame.add(inforPanel, BorderLayout.NORTH);
		frame.add(SubmitPanel, BorderLayout.SOUTH);
		frame.setTitle("Tic Tac Toe");
		frame.setSize(370, 380);
		frame.setVisible(true);

		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String a = txt_inputbet.getText();
				if (a != "") {
					out.println("myname");
					submit.setEnabled(false);
					txt_inputbet.setEnabled(false);
					frame.setTitle("Tic Tac Toe-Player: " + a);
					label_info.setText("WELCOME " + a);
				}
			}
		});

		help.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String b = "Some information about the game:\r\n" + "Criteria for a valid move:\r\n"
						+ "-The move is not occupied by any mark.\r\n" + "-The move is made in the player's turn.\r\n"
						+ "-The move is made within the 3 x 3 board.\r\n"
						+ "The game would continue and switch among the opposite player until it reaches either one of the following conditions:\r\n"
						+ "-Player 1 wins.\r\n" + "-Player 2 wins.";
				JOptionPane.showMessageDialog(frame, b, "Message", JOptionPane.INFORMATION_MESSAGE);
			}
		});

		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		Thread handler = new ClientHandler(socket);
		handler.start();
	}
	
	/**
	 * This class makes a separated thread to handle server input.
	 * 
	 * @author Lin Yizhou
	 *
	 */
	class ClientHandler extends Thread {
		private Socket socket;

		public ClientHandler(Socket socket) {
			this.socket = socket;
		}

		@Override
		/**
		 * This method calls readFromServer method.
		 * 
		 */
		public void run() {
			try {
				readFromServer();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * This method reads input from server.
		 * 
		 * @throws Exception
		 */
		public void readFromServer() throws Exception {
			try {
				while (in.hasNextLine()) {
					var command = in.nextLine();
					if(command.startsWith("left")) {
						String b="Game Ends. One of the players left.";
						JOptionPane.showMessageDialog(frame, b, "Message", JOptionPane.INFORMATION_MESSAGE);
						System.exit(0);
					}else if(command.startsWith("gamestart")) {
						for (int i = 0; i < 9; i++) {
							buttons[i].setEnabled(true);
						}
					}else if(command.startsWith("User1")) {
						user=1;
						canmove=true;
					}else {
						String[] arrofstr= command.split(":");
						int block=Integer.parseInt(arrofstr[2]);
						used[block]=true;
						if(arrofstr[0].equals("1")) {
							buttons[block].setText("X");
							buttons[block].setForeground(Color.green);
						}else {
							buttons[block].setText("O");
							buttons[block].setForeground(Color.red);}
						if (arrofstr[1].equals("win")) {
							if (user==Integer.parseInt(arrofstr[0])) {
								String b="Congratulations. You Win";
								JOptionPane.showMessageDialog(frame, b, "Message", JOptionPane.INFORMATION_MESSAGE);
								System.exit(0);
							}else {
								String b="You lose.";
								JOptionPane.showMessageDialog(frame, b, "Message", JOptionPane.INFORMATION_MESSAGE);
								System.exit(0);}
						}else if (arrofstr[1].equals("draw")) {
							String b="Draw.";
							JOptionPane.showMessageDialog(frame, b, "Message", JOptionPane.INFORMATION_MESSAGE);
							System.exit(0);
						}else {
							if(arrofstr[0].equals("1")) {
								if(user==1) {
									canmove=false;
									label_info.setText("Valid move, wait for your opponent.");
								}else {
									canmove=true;
									label_info.setText("Your opponent has moved, now is your turn.");
								}
							}else {
								if(user==2) {
									canmove=false;
									label_info.setText("Valid move, wait for your opponent.");
								}else {
									canmove=true;
									label_info.setText("Your opponent has moved, now is your turn.");
								}
							}
						}
					}
					out.flush();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				socket.close();
			}
		}
	}
}
