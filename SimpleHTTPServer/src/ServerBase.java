//Author: Timothy Beene
//02-28-14
//edited: 06-16-16

import java.io.*;
import java.net.*;

//I took the TCPServer code from the lecture slides
public class ServerBase {

	public static void main(String[] args) throws Exception {
		// set port number to listen on I hard set it to 8080
		int portNumber = 8080;
		// initiates a server socket
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(portNumber);
			//will create a new thread for each client
			while (true) {
				//starts a new thread paired with the client that just connected
				new HTTPThread(serverSocket.accept()).start();
			}
		} catch (IOException e) {
			//catches when thread failed to start
			e.printStackTrace();
			System.exit(-1);
		} finally {
			//closes socket
			serverSocket.close();
		}

		
	}
}