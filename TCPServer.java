import java.net.*;
import java.util.Objects;
import java.util.Scanner;
import java.io.*;
public class TCPServer {
 public static void main (String args[]) {
  int serverPort = 7896; // the server port
  try {
   InetAddress addr = InetAddress.getLocalHost();

   // Get IP Address
   byte[] ipAddr = addr.getAddress();

   // Get hostname
   String hostname = addr.getHostName();
   System.out.println("Server Name: " + hostname + "\nServer Port: " + serverPort);
  } catch (UnknownHostException e) {
  }

  try{
   ServerSocket listenSocket = new ServerSocket(serverPort);
   System.out.println("Server is Ready");
   while(true) {
    System.out.println("listening to client sockets");
    Socket clientSocket = listenSocket.accept();
    System.out.println("connection found, creating a new connection thread");
    Connection c = new Connection(clientSocket);
   }
  } catch(IOException e) {System.out.println("IOException Listen socket:"+e.getMessage());}
 }
}
class Connection extends Thread {
 DataInputStream in;
 DataOutputStream out;
 Socket clientSocket;
 public Connection (Socket aClientSocket) {
  System.out.println("in new connection thread");
  try {
   clientSocket = aClientSocket;
   in = new DataInputStream(clientSocket.getInputStream());
   out = new DataOutputStream(clientSocket.getOutputStream());
   this.start();
  } catch(IOException e) {System.out.println("Connection:"+e.getMessage());}
 }
 public void run(){
  System.out.println("server thread started");
  try { // an echo server
   XO_game game;
   byte [][] board=new byte [9][2];
   for (int i=0;i<3;i++){
    board[i][0]= (byte) 0;
    board[i][1]=(byte) i;
   }
   int j=0;
   for (int i=3;i<6;i++){
    board[i][0]= (byte) 1;
    board[i][1]=(byte) j;
    j ++;
   }
   j=0;
   for (int i=6;i<9;i++){
    board[i][0]= (byte) 2;
    board[i][1]=(byte) j;
    j ++;
   }
   String instrb="0 1 2\n3 4 5\n6 7 8";
   while(true){

    System.out.println("Waiting for client to request a game");

    boolean start=false;
    String startreq = in.readUTF();


    if(Integer.valueOf(startreq.trim())==99){

     Scanner ini = new Scanner(System.in);
     System.out.println("Client requested to play. Do you want to play X O? yes or no?");
     String again = ini.nextLine();
     again=again.trim();
     again=again.toLowerCase();
     if (Objects.equals(new String ("yes"), again)){
      start=true;
      out.writeUTF("99");
     }
     else{
      out.writeUTF("00");
      break;}








    }

    game=new XO_game();




    while(game.game_result()=='-'&& start){
     System.out.println("initiated game");
     System.out.println(game);
     System.out.println("Waiting for opponents move");
     String movec = in.readUTF();
     

     boolean valid= game.x_move(board[Integer.valueOf(movec.trim())][0], board[Integer.valueOf(movec.trim())][1]);
     
     if(!valid){System.out.println("Opponents move is not valid");} 
     if(game.game_result()!='-'){System.out.println(game);}

     while(valid && game.game_result()=='-'){

      System.out.println(instrb);
      System.out.println(game); 
      int move;
      Scanner ins = new Scanner(System.in);
      System.out.println("Enter your move");
      move = ins.nextInt();
      if (move>=0 && move<=8){
       valid=!(game.o_move(board[move][0], board[move][1])); 
      }
      else{ 
       System.out.println("Your move is not Valid");
      }



      // if move valid from server part it would be sent to the client
      if(!valid){
       
       out.writeUTF(Integer.toString(move));                              
      }

     }
    } 
    
    //Decide result of the gam

    if (game.game_result()=='X'){
     
     System.out.println("You Lose!");
    }
    else if(game.game_result()=='O'){
     
     System.out.println("You Win!");}
    else if((game.game_result()=='d')){
    
     System.out.println("It is a draw");
    }
    else{
    
     System.out.println("Error");
    }

   }

  }catch (EOFException e){System.out.println("EOF:"+e.getMessage());
  } catch(IOException e) {System.out.println("readline:"+e.getMessage());
  } finally{ try {clientSocket.close();}catch (IOException e){/*close failed*/}}


 }
}
