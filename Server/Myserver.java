import java.io.IOException;
import java.net.ServerSocket;

/**
 * This class sets up the server.
 * 
 * @author Lin Yizhou
 *
 */
public class Myserver {

	public static void main(String[] args) throws IOException {

		try (var listener = new ServerSocket(5050)) {
			Server myServer = new Server(listener);
			myServer.start();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}