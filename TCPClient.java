import java.net.*;
import java.util.Objects;
import java.util.Scanner;
import java.io.*;
public class TCPClient {
	public static void main (String args[]) {
		// arguments supply message and hostname
		// Check command line
		if (args.length < 1) {
			System.err.println("Usage : ");
			System.err.println("java TCPClient <server>");
			System.exit (1);
		}    
		Socket s = null;
		try{
			int serverPort = 7896;
      System.out.println("starting a new client socket");
			s = new Socket(args[0], serverPort);    
			DataInputStream in = new DataInputStream( s.getInputStream());
			DataOutputStream out =new DataOutputStream(s.getOutputStream());
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
			System.out.println(again);
			if (Objects.equals(new String ("yes"), again)){
				game= new XO_game();
			
				out.writeUTF("99");			
				System.out.println("sending request to start game");
					
				
				String replysi = in.readUTF();	
				
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
				Scanner ins1 = new Scanner(System.in);
				System.out.println("Enter your move: type the number location and press enter");
				move = ins1.nextInt();
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
					
					out.writeUTF(Integer.toString(move));
					System.out.println(game);
							                        			
					
					while (game.game_result()=='-'&& valid){
						//Receiving response from server
						System.out.println("Waiting for other player");
						String replys = in.readUTF();
						
						valid=!(game.o_move(board[Integer.valueOf(replys.trim())][0], board[Integer.valueOf(replys.trim())][1]));
						
						if (valid){System.out.println("Move sent by opponent is not valid");}
						
						if(game.game_result()!='-'){System.out.println(game);}
					}
				}
				else{
					System.out.println("Move is not valid try again");}



			}
			if (game.game_result()=='X'){
				
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
		}catch (UnknownHostException e){System.out.println("Socket:"+e.getMessage());
		}catch (EOFException e){System.out.println("EOF:"+e.getMessage());
		}catch (IOException e){System.out.println("readline:"+e.getMessage());
		}finally {if(s!=null) try {s.close();}catch (IOException e){System.out.println("close:"+e.getMessage());}}
  }
}
