import java.net.*;
import java.util.Objects;
import java.util.Scanner;
import java.io.*;
public class UDPClient{    
 public static void main(String args[]){
  // args give message contents and destination hostname  

  // Check command line
  /*if (args.length != 1) {
   System.err.println("Usage : ");
   System.err.println("java UDPClient <server>");
   System.exit (1);
  }
   */

  DatagramSocket aSocket=null;
  try {
   aSocket = new DatagramSocket();
   //byte[] m = args[0].getBytes();
   System.out.println("getting server IP address");
   InetAddress aHost = InetAddress.getByName(args[0]);
   int serverPort = 9909;
   System.out.println("create request");
   //initialize game on client side and send request to start game 
   XO_game game= new XO_game();
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
   boolean start=false;
   while(true){
   Scanner ini = new Scanner(System.in);
   System.out.println("Do you want to play X O? yes or no?");
   String again = ini.nextLine();
   again=again.trim();
   again=again.toLowerCase();
   if (Objects.equals(new String ("yes"), again)){
    game= new XO_game();
   
    String messagei= new String(Integer.toString(99));
    System.out.println(messagei);
    DatagramPacket requesti = new DatagramPacket(messagei.getBytes(),  messagei.length(), aHost, serverPort);   
    System.out.println("sending request to start game");
    aSocket.send(requesti); 
    
    byte[] bufferi = new byte[1000];
    DatagramPacket replyi = new DatagramPacket(bufferi, bufferi.length);
    aSocket.receive(replyi);
    String replysi=new String(replyi.getData());
    
    if(Integer.valueOf(replysi.trim())!=99){
     System.out.println("Server refused to play");
     break;
    }
    
    
    
   }
   else{break;}
   
   while(game.game_result()=='-'){
    
    System.out.println(instrb);
    System.out.println(game);
    //ask for move
    int move;
    Scanner ins = new Scanner(System.in);
    System.out.println("Enter your move");
    move = ins.nextInt();
    //input move into game
    boolean valid;
    if (move>=0 && move<=8){
     valid= game.x_move(board[move][0], board[move][1]);
    }
    else{ 
     valid=false;
     System.out.println("Your move is not valid");

    }
    if (valid){
     
     String message= new String(Integer.toString(move));
     System.out.println(message);
     DatagramPacket request = new DatagramPacket(message.getBytes(),  message.length(), aHost, serverPort);   
     System.out.println(game);
     System.out.println("sending move");


     aSocket.send(request);
     System.out.println(game);
     
     while (game.game_result()=='-'&& valid){
      byte[] buffer = new byte[1000];
      DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
      //Receiving response from server
      System.out.println("Waiting for other player");
      aSocket.receive(reply);
      String replys=new String(reply.getData());
      
      valid=!(game.o_move(board[Integer.valueOf(replys.trim())][0], board[Integer.valueOf(replys.trim())][1]));
      
      if (valid){System.out.println("Move sent by opponent is not valid");}
      
      if(game.game_result()!='-'){System.out.println(game);}
     }
    }
    else{
     System.out.println("Move is not valid try again");}



   }
   if (game.game_result()=='X'){
    System.out.println(game);
    System.out.println("You Win!");
    
   }
   else if(game.game_result()=='O'){
    
    System.out.println("You lose");}
   else if((game.game_result()=='d')){
    
    System.out.println("It is a draw");
   }
   else{
   System.out.println("Something is wrong");
   }
   }

  }catch (SocketException e){System.out.println("Socket: " + e.getMessage());
  }catch (IOException e) {System.out.println("IO: " + e.getMessage());
  }finally {if(aSocket != null) aSocket.close();} 

 } 
}