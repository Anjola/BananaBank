//Anjololuwa Olayemi
//CS 283- Assignment 2-Banana Bank
//ServerWorkerThread - responsible for most of the work and services clients
package bananabank.server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.StringTokenizer;


public class ServerWorkerThread extends Thread {

	Socket cs;
	ServerSocket ss;
	boolean closeRequested; 
	BananaBank accounts;
	
	public ServerWorkerThread(ServerSocket ss, Socket cs, BananaBank accounts){
		this.cs = cs; 
		this.accounts = accounts; //shared bank
		this.ss = ss; //to close server socket
		closeRequested = false; //to exclude shutdown thread from join
	}
	@Override
	public void run() {
		System.out.println("Worker Thread Starting");
		BufferedReader r;
		PrintStream ps;
		try {
			//set up proper input and output streams
			r = new BufferedReader(new InputStreamReader(cs.getInputStream()));
			ps = new PrintStream(cs.getOutputStream());
			
			String line;
			while((line = r.readLine()) != null)
			{
				if(line.equals("SHUTDOWN"))
				{
					closeRequested = true; //let main know this guy is not getting done
					ss.close();
					while(!closeRequested);//busy wait for server to give go ahead
						Collection<Account> allAccounts = accounts.getAllAccounts();
						int sum = 0;
						 for (Account account : allAccounts) {
						    	sum+= account.getBalance();
						    }
						    accounts.save("accounts.txt"); //save to file
						    ps.println(sum); //send response to client 
					break; //can rest in peace 
				}
					
				StringTokenizer st = new StringTokenizer(line);
				int amount = Integer.parseInt(st.nextToken());
				int srcAccountNumber = Integer.parseInt(st.nextToken());
				int dstAccountNumber = Integer.parseInt(st.nextToken());
				
				Account srcAccount = accounts.getAccount(srcAccountNumber);
				Account dstAccount = accounts.getAccount(dstAccountNumber);
					
				//process client request and write back to client result 
				if(srcAccount == null){
					ps.println("Invalid source account");
				}
				else if(dstAccount == null){
					ps.println("Invalid destination account");
				}
				else if(srcAccount.getBalance() < amount){
					ps.println("Not enough money in source account");
				}
				else if(srcAccountNumber == dstAccountNumber){
					//do nothing
				}
				//enforce proper lock acquiring order 
				else if(srcAccountNumber < dstAccountNumber){
					
					synchronized(srcAccount){
						synchronized(dstAccount){
							srcAccount.transferTo(amount, dstAccount);
						}
					}
					ps.println("$" + amount + " succesfully transfered from " + srcAccount.getAccountNumber() + " to "
					+ dstAccount.getAccountNumber());
						
				}
				else{
					synchronized(dstAccount){
						synchronized(srcAccount){
							srcAccount.transferTo(amount, dstAccount);
						}
					}
					ps.println("$" + amount + " succesfully transfered from " + srcAccount.getAccountNumber() + " to "
					+ dstAccount.getAccountNumber());
				}
				
					
			}
			System.out.println("Client Disconneted");
			r.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Worker thread Exiting");
	}

	
}
