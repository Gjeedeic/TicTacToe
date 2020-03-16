import java.net.*;
import java.util.Objects;
import java.util.Scanner;
import java.io.*;
public class UDPServer{
 public static void main(String args[]){ 

  int port = 9909;
    //creating board reference so locations on the board are indicated by locations from 0 to 8
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
  try {
   InetAddress addr = InetAddress.getLocalHost();

   // Get IP Address
   byte[] ipAddr = addr.getAddress();

   // Get hostname
   String hostname = addr.getHostName();
   System.out.println("Server Name: " + hostname + "\nServer Port: " + port);


  } catch (UnknownHostException e) {
  }


  DatagramSocket aSocket = null;
  try{
   aSocket = new DatagramSocket(9909); // create socket at agreed port
   System.out.println("Server is Ready");
   byte[] buffer = new byte[100];
   byte[] bufferi = new byte[100];
   while(true){
    System.out.println("Waiting for client to request a game");
    boolean start=false;
    DatagramPacket requesti = new DatagramPacket(bufferi, bufferi.length);
    aSocket.receive(requesti);
    String startreq=new String(requesti.getData());
    
    if(Integer.valueOf(startreq.trim())==99){
     
     Scanner ini = new Scanner(System.in);
     System.out.println("Client requested to play. Do you want to play X O? yes or no?");
     String again = ini.nextLine();
     again=again.trim();
     again=again.toLowerCase();
     if (Objects.equals(new String ("yes"), again)){
      start=true;
      String messagei= new String(Integer.toString(99));
      DatagramPacket sendi = new DatagramPacket(messagei.getBytes(),messagei.length(),requesti.getAddress(),requesti.getPort());
      aSocket.send(sendi);
     }
     else{
      String messagei= new String(Integer.toString(00));
      DatagramPacket sendi = new DatagramPacket(messagei.getBytes(),messagei.length(),requesti.getAddress(),requesti.getPort());
      aSocket.send(sendi);
      
      break;}
        

     


       
     
     
    }

    game=new XO_game();
    



    while(game.game_result()=='-'&& start){
     System.out.println("initiated game");
     System.out.println(game);
     System.out.println("Waiting for opponents move");
     DatagramPacket request = new DatagramPacket(buffer, buffer.length);
     aSocket.receive(request);
     String movec=new String(request.getData());
     
     boolean valid= game.x_move(board[Integer.valueOf(movec.trim())][0], board[Integer.valueOf(movec.trim())][1]);
     
     if(!valid){System.out.println("Opponents move is not valid");} 
     if(game.game_result()!='-'){System.out.println(game);}
     
     while(valid && game.game_result()=='-'){
      
      System.out.println(instrb);
      System.out.println(game); 
      int move;
      Scanner in = new Scanner(System.in);
      System.out.println("Enter your move");
      move = in.nextInt();
      if (move>=0 && move<=8){
       valid=!(game.o_move(board[move][0], board[move][1])); 
      }
      else{ 
       System.out.println("Your move is not Valid");
      }



      // if move valid from server part it would be sent to the client
      if(!valid){

       String message= new String(Integer.toString(move));
       DatagramPacket send = new DatagramPacket(message.getBytes(),message.length(),request.getAddress(),request.getPort());   

       


       aSocket.send(send);                              
       //byte[] buffer1 = new byte[1000];
       //DatagramPacket reply = new DatagramPacket(buffer1, buffer1.length);
      }
     
     }
     } 
    //Decide result of the game
    
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
     for (int i=0; i<buffer.length; i++) buffer[i] = 0; 
   }
   
  }catch (SocketException e){System.out.println("Socket: " + e.getMessage());
  }catch (IOException e) {System.out.println("IO: " + e.getMessage());
  }finally {if(aSocket != null) aSocket.close();}
 }
}
