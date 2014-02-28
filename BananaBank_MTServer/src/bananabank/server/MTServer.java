//Anjololuwa Olayemi
//CS 283- Assignment 2-Banana Bank
//MTServer - accepts clients and creates service thread
package bananabank.server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class MTServer {
	public static void main(String[] args){
		BananaBank accounts = null;
		ServerSocket ss = null;
		ArrayList<ServerWorkerThread> clients = null;
		try {
			//create new shared bank
			accounts = new BananaBank("accounts.txt");
			ss = new ServerSocket(2000); //listen on port 2000
			clients = new ArrayList<ServerWorkerThread>();  //store active clients
			
			System.out.println("MT Server socket created");
			
				for(;;){//accept and keep track of new clients
					Socket cs = ss.accept();
					ServerWorkerThread client = new ServerWorkerThread(ss,cs,accounts);
					client.start();
					clients.add(client);
				}
			
		}catch(IOException e)
			{
				System.out.println("Server socket closed");
				for (ServerWorkerThread client:clients)
				{
					//let all clients complete except client requesting shutdown
					if(client.closeRequested != true)
					{
						try {
							client.join();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
				for (ServerWorkerThread client:clients)
				{
					client.closeRequested = true;// release client from waiting for other threads
				}

			}

		}

	}
