import java.util.ArrayList;


/**
 * This class makes a simulated Tic Tac Toe board.
 * 
 * @author Lin Yizhou
 *
 */
public class board {
	private int[] grids = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	private final Object lock = new Object();
	private int userno=0;
	
	/**
	 * This method sets corresponding grid on the board and return the result.
	 * 
	 * @param no
	 * @param user
	 * @return String
	 */
	public String setNumber(int no,int user) {
		synchronized (lock) { 
				this.grids[no]=user;
				String win= checkwin(user);
				if(win=="win") {
					clearboard();
					return "win";
				}else if(win =="draw") {
					clearboard();
					return "draw";
				}
				return "keepon";
		}
	}
	
	/**
	 * This method checks whether the game is over and what is the result.
	 * 
	 * @param user
	 * @return String
	 */
	public String checkwin(int user) {
		synchronized (lock) {
			ArrayList<Integer> a = new ArrayList<Integer>();
			int movecount=0;
			for (int i = 0; i < 9; i++) {
				if (user == grids[i]) {
					a.add(i);
				}
			}
			for (int i = 0; i < a.size(); i++) {
				for (int j = i + 1; j < a.size(); j++) {
					if (a.contains(2 * a.get(j) - a.get(i))) {
						if(a.get(i)%3==0) {
							if (a.get(j)%3!=2) {
								return "win";
							}
						}else if(a.get(i)%3==1) {
							if (a.get(j)%3==1) {
								return "win";
							}
						}else {
							if (a.get(j)%3!=0) {
								return "win";
							}
						}
					}
				}
			}
			for (int i = 0; i < 9; i++) {
				if (grids[i]==0) {
					movecount++;
				}
			}
			if(movecount==0) {
				return "draw";
			}
			return "keepon";
		}
	}
	
	/**
	 * This method adds a user.
	 */
	public void userenter() {
		synchronized (lock) {
			this.userno++;
		}
	}
	
	/**
	 * This method returns the NO. of users in the game. 
	 * 
	 * @return
	 */
	public int getuserno() {
		synchronized (lock) {
			return this.userno;
		}
	}
	
	/**
	 * This method clears the board and restart the game.
	 */
	public void clearboard() {
		synchronized (lock) {
			for (int i = 0; i < 9; i++) {
				this.grids[i] = 0;
			}
			this.userno=0;
		}
	}
}
