import java.util.*;
import java.io.*;
import java.net.*;

// it waits for the client to connect,
// after connection it starts a separate thread to handle the request.
public class Webserver {
	private static ServerSocket serverSocket;
	
	
	public static void main(String[] args) throws IOException {
		System.out.println("Server is started");
		serverSocket=new ServerSocket(8088);   // port number at which server is running 
		while (true) {
			try {
				Socket s=serverSocket.accept();  // Wait for a client to connect
				new ClientHandler(s);  // Handle the client in a separate thread
			}
			catch (Exception e) {
				System.out.println(e);
			}
		}
	}
}

// A ClientHandler reads an HTTP request and responds
class ClientHandler extends Thread {
	private Socket socket;  // The accepted socket from the Web server

	// Start the thread in the constructor
	public ClientHandler(Socket socket) {
		this.socket=socket;
		start();
	}

	// Read the HTTP request, respond, and close the connection
	//thread class contains a method run which is call automatically when we start the thread
	
	public void run() {
		try {
			final String root = "C:/Users/dhiraj/Desktop/root/";
			// Open connections to the socket
			BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintStream ps=new PrintStream(new BufferedOutputStream(socket.getOutputStream()));

			// Read filename from first input line "GET /filename.html"
			
			String s=br.readLine();
			

			// Attempt to serve the file.  Catch FileNotFoundException and
			// return an HTTP error "404 Not Found".  Treat invalid requests
			// the same way.
			String filename="";
			StringTokenizer st=new StringTokenizer(s);
			try {

				// Parse the filename from the GET command
				if (st.hasMoreElements() && st.nextToken().equalsIgnoreCase("GET")
						&& st.hasMoreElements())
					filename=st.nextToken();
				else
					throw new FileNotFoundException();  // Bad request or file not found

				// Append trailing "/" with "index.html" (default content)
				if (filename.endsWith("/"))
					filename+="index.html"; // This is default content for server

				
				// Open the file (may throw FileNotFoundException)
				InputStream is=new FileInputStream(root+filename);

				// Determine the file type and print HTTP header
				String type="text/html";
				if (filename.endsWith(".html") || filename.endsWith(".htm"))
					type="text/html";  //response is in html format
				else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg"))
					type="image/jpeg";  //response is in jpeg format
				else if (filename.endsWith(".gif"))
					type="image/gif";  //response is in gif format
				else if (filename.endsWith(".png"))
					type="image/png";	//response is in png format
				else if (filename.endsWith(".txt"))
					type="text/plain";	//response is in text format
				else if (filename.endsWith(".pdf"))
					type="application/pdf";	//response is in pdf format
				else
					type="application/octet-stream";
				//version of http and 200 for status code
				ps.print("HTTP/1.1 200 OK\r\n"+
						"Content-type: "+type+"\r\n\r\n");  //200 means everything is OK
															

				// Send file contents to client, then close the connection
				byte[] b=new byte[4096];
				int i;
				while ((i=is.read(b))>0)  //if less than zero means end of file
					ps.write(b, 0, i);
				ps.close();
				is.close();
			}
			catch (FileNotFoundException e) {
				ps.println("HTTP/1.1 404 Not Found\r\n"+
						"Content-type: text/html\r\n\r\n"+
						"<html><head></head><body>"+filename+" not found</body></html>\n"); //file not found output
				ps.close();
			}
		}
		catch (IOException e) {
			System.out.println(e);
		}
	}
}