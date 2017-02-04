

import java.security.SecureRandom;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Amey
 */
public class Board 
{
    // This class is careteed to provide the Board manupulation functionality 
    // and object of this class will be shared.
    
    private final int ROWS = 6;
    private final int COLUMN = 7;

    private char[][] board = {  {'_','_','_','_','_','_','_'},   
                                {'_','_','_','_','_','_','_'},
                                {'_','_','_','_','_','_','_'},
                                {'_','_','_','_','_','_','_'},
                                {'_','_','_','_','_','_','_'},
                                {'_','_','_','_','_','_','_'}};
   
    // Declaring Lock and Condition variables for Synchronization
    private final Lock lock = new ReentrantLock();
    
    private final Condition canWrite = lock.newCondition();
    private final Condition canRead = lock.newCondition();
    
    private int checked = 0;
    private int board_full = 0;

    private int check_horizontal_pattern(String input_var)
    {
        // This function is used to match the winning patern 
        // horizontally in the board array
	int i, j;
	String current_line = new String();
        // Scaning pattern row by row.
	for(i = ROWS-1 ; i > -1 ; i--)
        {
		for(j=0; j < 7 ; j++)
		{	  
		  current_line = current_line + board[i][j];
		}
              	
		if(current_line.contains(input_var)) 
		{
			System.out.printf("Horizontal Match Found..\n");
			return(1);
		}
                current_line = "";
               
        }  
	return(0);
    }

    private int check_diagonal_pattern(String input_var)
    {
        // This function is used to match the winning patern 
        // vertically in the board array
	
	int i, j, a, b = 0;
	String current_line = new String();
	
        // Scaning "/" pattern in Board array.
        for(i = 0 ; i < ROWS ; i++)
        {
            a = i;
            j = 0;

            while(a < ROWS  && j < COLUMN)
            {
                    current_line = current_line + board[a][j];
                    a++;
                    j++;
            }
            
            if(current_line.contains(input_var)) 
		{
                    System.out.printf("/ Diagonal Match Found..\n");
                    return(1);
		}
            current_line = "";
        }

	i = 0;
        j = 0;
        a = 0;
        
	for(i = 1; i < COLUMN ; i++)
        {
            a = i;
            j = 0;
            while(j < ROWS && a < COLUMN)
            {
                current_line = current_line + board[j][a];
                a++;
                j++;
            }
            if(current_line.contains(input_var)) 
            {
                System.out.printf("/ Diagonal Match Found..\n");
                return(1);
            }
            current_line = "";
        }

	i = 0;
        j = 0;
        a = 0;
        // Scaning "\" pattern in Board array.
	for(i = 0; i < COLUMN ; i++)
        {
            a = i;
            j = 0;
            while(j < ROWS && a > -1)
            {
                current_line = current_line + board[j][a];
                a--;
                j++;
            }
            
            if(current_line.contains(input_var)) 
            {
                System.out.printf("\\ Diagonal Match Found..\n");
                return(1);
            }
		current_line = "";
        }
	
	i = 0;
        j = 0;
        a = 0;
	for(i = 1; i < ROWS ; i++)
        {
            b = 0;
            a = i;
            j = COLUMN - 1;
            while(a < ROWS && j > -1)
            {
                current_line = current_line + board[a][j];
                b++;
                a++;
                j--;
            }
            
            if(current_line.contains(input_var)) 
            {
                    System.out.printf("\\ Diagonal Match Found..\n");
                    return(1);
            }
            current_line = "";
        }
        return(0);
    }

    private void print_board()
    {
        // This function is used to print board array
	
	int i,j;

	for(i = ROWS-1 ; i > -1 ; i--)
        {
		for(j=0 ; j < COLUMN ; j++)
		{
			System.out.printf("| %c ", board[i][j]);
		}
		System.out.printf("|\n");
        }
	System.out.printf("=============================\n");
	System.out.printf(" (1) (2) (3) (4) (5) (6) (7) \n\n");   
    }

    private void make_the_move(char input_var)
    {
        // This function is used to randomly selecting the cloumn in
        // board array and insering input variable at bottom most available location 
	
	int random_col = 0;
	int insert_location = -1;
        SecureRandom rand = new SecureRandom();
	
	int i;

	while(insert_location == -1)
        {
            
            random_col = rand.nextInt(7);
            try 
            {
                Thread.sleep(1000); // Sleep For 1 Second before inserting
            } 
            catch (InterruptedException ex) 
            {
                Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
            }
            for(i = 0 ; i < ROWS ; i++)
            {
                if(board[i][random_col] == '_') // check if location is empty
                {
                    insert_location = i;
                    break;
                }
            }
        }

	System.out.printf("Selected Column: %d\n", random_col + 1);
	board[insert_location][random_col] = input_var;
	print_board(); // Print Board after insertion
    }

    int check_board_availability()
    {
        // This function is used to calculate is there are still 
        // empty spaces in the board if not fucntion returns 1 i.e. Board Finished.
        
        int i,j;

        for(i = 0 ; i < ROWS ; i++)
        {
            for(j = 0 ; j < COLUMN ; j++)
            {
                if(board[i][j] == '_')
                {
                    return(0);
                }
            }
        }  
        System.out.printf("Board Finished...\n");
        return(1);
    }

    public void monitor_board()
    {
        // This function is called by to Referee object to check if any player 
        // has won the game after his last inserion 
	int ret = 0;
	while(true)
        {
            try
            {
                lock.lock();
                while(checked == 0)
                {
                    canRead.await(); // wait for player to complete his turn
                }

                System.out.printf("Refree is Checking...\n");
                checked = 0;
                // Check if Player R is winning
                if(check_horizontal_pattern("RRRR") == 1 || check_diagonal_pattern("RRRR") == 1)
                {
                    board_full = 1;
                    System.out.printf("\n\n******* Player 'R' Won!!! *******\n\n");
                    canWrite.signalAll();
                    // lock.unlock(); 
                    return;
                }
                // Check if Player Y is winning
                if(check_horizontal_pattern("YYYY") == 1 || check_diagonal_pattern("YYYY") == 1)
                {
                    board_full = 1;
                    System.out.printf("\n\n******** Player 'Y' Won!!! ********\n\n");
                    canWrite.signalAll();
                    // lock.unlock(); 
                    return;
                }

                // Check if Board is still available
                ret = check_board_availability();

                switch(ret)
                {
                    // Continue game..
                    case 0:
                            System.out.printf("Board is Available...\n");
                            canWrite.signalAll();
                            // lock.unlock(); 
                            break;
                    // Declare Tie and end the game.
                    case 1:
                            board_full = 1;
                            System.out.printf("\n\n******* IT IS A TIE ********\n\n");
                            canWrite.signalAll();
                            // lock.unlock(); 
                            return;
                }
            }
            catch(Exception e)
            {
                System.out.printf("Error: %s", e.toString());
            }
            finally
            {
                lock.unlock(); 
            }
        }	     
    }

    public void take_turn(char color)
    {
        // This function is called by to Player object to insert the tile of the 
        // playes color
	while(true)
        {
            try
            {
                lock.lock();   
                while(checked == 1)
                {
                    canWrite.await(); // wait for refree to monitor
                }

                if(board_full == 1)
                {
                    canRead.signalAll();
                    // lock.unlock(); 
                    return;
                }
                System.out.printf("%c's turn.. \n\n", color);
                make_the_move(color);
                checked = 1;
                
                canRead.signalAll();
               // lock.unlock();
            }
            catch(Exception e)
            {
                System.out.printf("Error: %s", e.toString());
            }
            finally
            {
                lock.unlock(); 
            }
        }
    }

}