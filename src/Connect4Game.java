

/**
 *
 * @author Amey
 */
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
//import java.util.concurrent.TimeUnit;

public class Connect4Game 
{

    // This is public class with the main function to Start the game
    
    public static void main(String[] args) 
    {
            Board board = new Board(); // Board object which will be common for palyer and refree
            Refree refree = new Refree(board); // Create Refree Object
            Player player1 = new Player(board, 'R'); // Create Player Object with color R
            Player player2 = new Player(board, 'Y'); // Create Player Object with color Y
            ExecutorService exe = Executors.newCachedThreadPool(); // To Start Threads
            
            exe.execute(refree);
            exe.execute(player1);      
            exe.execute(player2);

            exe.shutdown();
    }   
}