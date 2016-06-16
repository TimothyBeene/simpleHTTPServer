//Author: Timothy Beene
//02-28-14
//edited: 06-16-16

import java.io.*;
import java.net.*;

//this class is called from ServerBase.java to create threads
public class HTTPThread extends Thread{
	
	private Socket socket = null;

	public HTTPThread(Socket socket) {
		//Allocates a new Thread object with the name 'HTTPThread'.
		super("HTTPThread");

		//set port the client is listening on.
		this.socket = socket;
	}
	private void writeFileToStream(String fileName, DataOutputStream outStream){
		//open file
		FileInputStream file = null;
		try {
			//create inputStream for picture
			//used for breaking the file into smaller pieces
			int size = 1024;
			byte[] data = new byte[size];
			file = new FileInputStream(fileName);
			
			// writes the file to OutputStream
			int read;
			while( (read = file.read(data, 0, size)) != -1) {
				outStream.write(data, 0, read);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			//close file after use
			try {
				file.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	// look for a HTTP request and serve the info requested. else return appropriate error code.
	public void run() {
		BufferedReader inBR = null;
		DataOutputStream outDS = null;
		String msg = "";
		
		try{
			//make reader for input TCP stream
			inBR = new BufferedReader(
					new InputStreamReader(this.socket.getInputStream()));
			outDS = new DataOutputStream(this.socket.getOutputStream());
	
			//get first line of the HTTP GET request
			String request = inBR.readLine();
	
			if (request != null) {
				//parse the first line for which file to return
				String[] parsed = request.split(" ");
		
				//if file does not exist send 404 code
				if (parsed[0].equals("GET") && !parsed[1].equals("/index.html") && !parsed[1].equals("/pic.jpg")) {
					//make 404 message header
					msg = "HTTP/1.0 404 Not Found\r\n";
					msg += "\r\n";
					outDS.writeBytes(msg);
					
					//writes not_found.html to outDS
					writeFileToStream("not_found.html", outDS);
				}
				//serve the requested file.
				else if (parsed[0].equals("GET")) {
					
					//return index.html to client
					if (parsed[1].equals("/index.html")) {
						// make index.html page
						msg = "HTTP/1.0 200 OK\r\n";
						msg += "\r\n";
						outDS.writeBytes(msg);
						//writes index.html to outDS
						writeFileToStream("index.html", outDS);
					}
					
					//return pic.jpg to client
					else {
						//create response
						msg = "HTTP/1.0 200 OK\r\n";
						msg += "Content-Type: image/jpg;\r\n";
						msg += "\r\n";
						outDS.writeBytes(msg);

						//writes pic.jpg to outDS
						writeFileToStream("pic.jpg", outDS);
					}
				}
				//if request does not meet get format rules send 400 code
				else {
					//create 400 response
					msg = "HTTP/1.0 400 Bad Request\r\n";
					msg += "\r\n";
					outDS.writeBytes(msg);
					
					//writes bad_request.html to outDS
					writeFileToStream("bad_request.html", outDS);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}finally {
			try {
				//release the resources.
				if (inBR != null)
					inBR.close();
				if (outDS != null)
					outDS.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
