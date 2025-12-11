import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.HashMap;
import javax.swing.*;
import java.awt.Image;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.concurrent.TimeUnit;


public class MCTS {
    private int simulations; // used to run varying about of MTCS simutions 
    private JButton[] buttons; // all the JButtons for selection 
    int SCALE = 1; // used to vary the graphics size based on how big the screen is
/**
    * This is the constructor for the MCTS class. It initializes possible moves and the number of simulations to be used for the rest of the game.
    */
    public MCTS (int s){
        this.simulations = s;
    }

/**
    This method is used to setup all basics to run the PvB mode. It creates and setsups the various data structures and calls the main method to display the board graphics. 
*/
    public void game(String[][] board, int whichPlayer){
        HashMap<String, String> imageCoordinates = new HashMap<String, String>();
        Stack<String> playerMoves = new Stack<String>();
        addCoordinates(imageCoordinates);
        JFrame frame = new JFrame("Connect 4");
        board = makeBoard(new String[6][7]);
        String player = "X";
        displaySetUp(frame, board,imageCoordinates, whichPlayer, playerMoves);
    }
    
/**
    This method is called when a win is deteced. It display a message of which player won, and the buttons are disabled so no more moves are played.
    A back button is displayed, so the user can go back to the main menu, and play another game is desired. 

    @param frame The main JFrame where all graphical elements are added 
    @param whichPlayer used to determine which player won, and display the appropriate message
*/
    public void goToWin(int whichPlayer, JFrame frame){
        ImageIcon winner; 
        if (whichPlayer%2== 1){
            winner = new ImageIcon("Bot win.png"); 
        } else {
            winner = new ImageIcon("yellowWin.png");
        }
        Image scaledImage = winner.getImage().getScaledInstance(SCALE*700, SCALE*125, Image.SCALE_SMOOTH);
        winner = new ImageIcon(scaledImage);
        JLabel imageLabel = new JLabel(winner); 
        imageLabel.setBounds(SCALE*50, SCALE*675, winner.getIconWidth(), winner.getIconHeight()); 
        frame.add(imageLabel);
        for (JButton button : buttons) {
            button.setEnabled(false);
            frame.remove(button);
        }
        JButton back = new JButton(); 
        back.setBounds(SCALE*400, SCALE*790, SCALE*100, SCALE*70); 
        back.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e) {
                Main.selectionsScreen(); 
                frame.dispose();
            }
        });
        ImageIcon buttonImage = new ImageIcon("back.png");
        Image img = buttonImage.getImage().getScaledInstance(SCALE*100, SCALE*70, Image.SCALE_SMOOTH);
        ImageIcon scaledButtonImage = new ImageIcon(img);
        back.setIcon(scaledButtonImage);
        back.setBorder(BorderFactory.createEmptyBorder());
        back.setContentAreaFilled(false);        
        frame.add(back); 
        back.setVisible(true); 
        frame.revalidate(); 
        frame.repaint(); 
    }
    
/**
    This is the main method to set up all the graphical elements including the images, and buttons. 
    After displaying a board, it creates 7 new buttons and sets their size and positions. 
    Then, we assign a new ActionListener to each button, which will be used to determine which button was pressed. 
    The buttons are then added to the frame with an image overlay. 

    @param frame The main JFrame where all graphical elements are added 
    @param Connect4Board The 2D array of Strings representing the Connect4 board. Used to check for all win conditions, and bot move generation 
    @param imageCoordinates A HashMap containing the coordinates of the images in the Connect4 board. Used to add a image to a specific loccation 
           If there is a "coin" in the 2D array at [3,4], the hashmap would return the coordinates, so that a image addded will be in the same 
           cooresponding image as the piece in the 2D array.
    @param whichPlayer The player whose turn it is. Used to determine which image to add to the board (red or blue)
    @param playerMoves A stack of the moves made by the player. Alternative way to determine which player's turn it is 
*/
    public void displaySetUp(final JFrame frame, final String[][] Connect4Board, final HashMap<String, String> imageCoordinates, final int whichPlayer, final Stack<String> playerMoves) {
        //-----DISPLAYING BOARD---------
        ImageIcon board = new ImageIcon("thumb-Connect_Four-a-removebg-preview.png");
        Image scaledImage = board.getImage().getScaledInstance(SCALE*800, SCALE*800, Image.SCALE_SMOOTH); 
        board = new ImageIcon(scaledImage);
        JLabel boardL = new JLabel(board);
        frame.setLayout(null);
        boardL.setBounds(SCALE*0, SCALE*-40, board.getIconWidth(), board.getIconHeight());
        frame.add(boardL);
        buttons = new JButton[7];  // 7 buttons for the 7 columns 
        for (int i = 0; i < buttons.length; i++) {
            // setting size and position constraints to the buttons 
            final int column = i;
            buttons[i] = new JButton();
            buttons[i].setBounds(SCALE*44 + (SCALE*i * 102), SCALE*690, SCALE*100, SCALE*60);
            // creating a actionListener for every buttons with specific commands for each buttons 
            buttons[i].addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                    String isValid = userPlaceValidation(column + 1, Connect4Board);
                    if (isValid.equals("inrange")) { // checks to see if the colmn chosen by the user is valid 
                        handleButtonClick(frame, column, Connect4Board, imageCoordinates, e, playerMoves, whichPlayer);
                    } else if (isValid.equals("columnFull")) { // error message is invalid selection 
                        JOptionPane.showMessageDialog(null, "Column is full. Choose another column.");
                    }
                }
            });
            // adding image overlay to the buttons 
            ImageIcon buttonImage = new ImageIcon((i+1) +".png");
            Image img = buttonImage.getImage().getScaledInstance(SCALE*60, SCALE*60, Image.SCALE_SMOOTH);
            ImageIcon scaledButtonImage = new ImageIcon(img);
            buttons[i].setIcon(scaledButtonImage);
            buttons[i].setBorder(BorderFactory.createEmptyBorder());
            buttons[i].setContentAreaFilled(false);
            frame.add(buttons[i]); // adding button to the JFrame 
        }
        // making everything visible 
        frame.setSize(board.getIconWidth(), board.getIconHeight() + SCALE*100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

/**
    This method is used to handle the button click. This method is called by the actionListeners given to the column selection buttons. The method first 
    gets the coordinates of where to place the image, and then places the image in the correct location. After adding, we have a time delay to so a user doesn't click twice 
    and accidenaltty place 2 piece. The buttons are disabled, and then the botsMove method is called to determine the bots move. 

    @param frame The main JFrame where all graphical elements are added
    @param column The column that the user selected (by using the buttons)
    @param Connect4Board The 2D array of Strings representing the Connect4 board. Used to check for all win conditions, and bot move generation 
    @param imageCoordinates A HashMap containing the coordinates of the images in the Connect4 board. Used to add a image to a specific loccation 
           If there is a "coin" in the 2D array at [3,4], the hashmap would return the coordinates, so that a image addded will be in the same 
           cooresponding image as the piece in the 2D array.
    @param e The ActionEvent that is triggered when a button is clicked
    @param whichPlayer The player whose turn it is. Used to determine which image to add to the board (red or blue)
    @param playerMoves A stack of the moves made by the player. Alternative way to determine which player's turn it is 
*/
    public void handleButtonClick(JFrame frame, int column, String[][] board, HashMap<String, String> imageCoordinates, ActionEvent e, final Stack<String> playerMoves, int whichPlayer) {
        String cords = getCoordinatesAndAddPieces(column + 1, board, "X", playerMoves, imageCoordinates); // get cords 
        if (cords != null) {
            displayBoard(frame, playerMoves.size() - 1, imageCoordinates.get(cords), board, imageCoordinates, playerMoves); // Update display after player's move
            printBoard(board); // Debugging purpose
            
            // Delay before letting bot make its move
            new Thread(() -> {
                try {
                    TimeUnit.MILLISECONDS.sleep(5); // small delay to avoid accidental placement of 2 pieces
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                // Let bot make its move after the delay
                for (JButton button : buttons){
                    button.setEnabled(false); // disable buttons 
                }
                botsMove(frame, board, imageCoordinates, whichPlayer, playerMoves); // bot's move 
            }).start();
        } 
    }

/**
    This method is used to determine the bots move. It first analyzes the board, to come to with the best move using a simplifed version on a Monte Carlo Tree Search Method. 
    Next, the method gets the coordinates (bot's move) of where to place the image, and then places the image in the correct location. The buttons are enabled. 

    @param  frame The main JFrame where all graphical elements are added 
    @param  board The 2D array of Strings representing the Connect4 board. Used to check for all win conditions, and bot move generation 
    @param  imageCoordinates A HashMap containing the coordinates of the images in the Connect4 board. Used to add a image to a specific loccation 
            If there is a "coin" in the 2D array at [3,4], the hashmap would return the coordinates, so that a image addded will be in the same 
            cooresponding image as the piece in the 2D array.
    @param whichPlayer The player whose turn it is. Used to determine which image to add to the board (red or blue)
    @param playerMoves A stack of the moves made by the player. Alternative way to determine which player's turn it is 
    
*/ 
    public void botsMove(JFrame frame, String[][] board, HashMap<String, String> imageCoordinates, int whichPlayer, final Stack<String> playerMoves){
        int column = findBestMove(whichPlayer, board); // find best move 
        String cords = getCoordinatesAndAddPieces(column + 1, board, "O", playerMoves, imageCoordinates); // gets new cords 
        displayBoard(frame, playerMoves.size() - 1, imageCoordinates.get(cords), board, imageCoordinates, playerMoves); // add piece 
        printBoard(board); 
        
        for (JButton button : buttons){
            button.setEnabled(true); // enabled buttons 
        }

        frame.revalidate(); 
        frame.repaint(); 
        frame.setVisible(true);
    }

/** 
    This method is used to place the either the red or blue token on the actual JFrame. The X and Y coordinates and substringed from the string "cords". 
    A new image is created, and added to the JFrame.

    @param frame The main JFrame where all graphical elements are added 
    @param whichPlayer The player whose turn it is. Used to determine which image to add to the board (red or blue)
    @param cords A string containg the combined X and Y coordinates of where to place the image. 
    @param board The 2D array of Strings representing the Connect4 board. Used to check for all win conditions, and bot move generation 
    @param imageCoordinates A HashMap containing the coordinates of the images in the Connect4 board. Used to add a image to a specific loccation 
           If there is a "coin" in the 2D array at [3,4], the hashmap would return the coordinates, so that a image addded will be in the same 
           cooresponding image as the piece in the 2D array.
    @param playerMoves A stack of the moves made by the player. Alternative way to determine which player's turn it is 
*/ 
    public void displayBoard(JFrame frame, int whichPlayer, String cords, String[][] board, HashMap<String, String> imageCoordinates, final Stack<String> playerMoves) {
        // gets cords of where to place the image
        int x = Integer.parseInt(cords.substring(0, cords.indexOf(",")));
        int y = Integer.parseInt(cords.substring(cords.indexOf(",") + 1, cords.length()));
        // getting image 
        ImageIcon imgX;
        if (whichPlayer % 2 == 0) {
            imgX = new ImageIcon("yellow.png");
        } else {
            imgX = new ImageIcon("red.png");
        }
        JLabel xL = new JLabel(imgX);
        xL.setBounds(x, y-SCALE*40, SCALE*100, SCALE*100);
        frame.add(xL); // addedd image 
        frame.revalidate();
        frame.repaint();
        frame.setVisible(true);
        checkAllConditions(frame, board);  // checking for a win after a move has been made 
    }

/**
    This method is used to check all the win condtions. 

    @param frame The main JFrame where all graphical elements are added 
    @param board The 2D array of Strings representing the Connect4 board. Used to check for all win conditions, and bot move generation 
*/
    public void checkAllConditions(JFrame frame, String[][] board) {
        
        if (checkWin(board, "X")){
            System.out.println("X Win");
             goToWin(2, frame); // human win 
         } else if (checkWin(board, "O")){
            System.out.println("O Win");
             goToWin(1, frame); // bot win 
         } else if (isBoardFull(board)){
            goToDraw(frame);
        }
    }
    
/**
    This method is called when a draw is deteced. It display a draw message, and the buttons are disabled so no more moves are played.
    A back button is displayed, so the user can go back to the main menu, and play another game is desired. 
    
    @param frame The main JFrame where all graphical elements are added 
*/
    public void goToDraw(JFrame frame){
        // draw image is added 
        ImageIcon winner = new ImageIcon("draw.png");
        Image scaledImage = winner.getImage().getScaledInstance(SCALE*700, SCALE*125, Image.SCALE_SMOOTH);
        winner = new ImageIcon(scaledImage);
        JLabel imageLabel = new JLabel(winner); 
        imageLabel.setBounds(SCALE*50,SCALE*675, winner.getIconWidth(), winner.getIconHeight()); 
        frame.add(imageLabel);
        for (JButton button : buttons) {
            button.setEnabled(false);
            frame.remove(button);
        }
        // back buttons in created 
        JButton back = new JButton(); 
        back.setBounds(SCALE*400, SCALE*790, SCALE*100, SCALE*70); 
        back.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e) {
                Main.selectionsScreen(); 
                frame.dispose();
            }
        });
        // button-image-overlay
        ImageIcon buttonImage = new ImageIcon("back.png");
        Image img = buttonImage.getImage().getScaledInstance(SCALE*100, SCALE*70, Image.SCALE_SMOOTH);
        ImageIcon scaledButtonImage = new ImageIcon(img);
        back.setIcon(scaledButtonImage);
        back.setBorder(BorderFactory.createEmptyBorder());
        back.setContentAreaFilled(false);        
        frame.add(back); 
        
        back.setVisible(true); 
        frame.revalidate(); 
        frame.repaint(); 
    }

//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
    * This method creates the board at the beginning of each game, however, each board is initialized the same. 
    * 
    * @param  board  The 2D array meant to represent the Connect 4 board.
    * @return  The 2D array of the board.
    */
    public String[][] makeBoard(String[][] board) {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                board[i][j] = "-";
            }
        }
        return board;
    }
    /**
    * This method checks if a player has acheived a four-in-a-row vertically. This method checks all possible
    * vertical combinations. 
    *
    * @param  board  The 2D array containing the current state of the board.
    * @param  player  A string denoting the player to be checked for a win
    * @return  True if a player has achieved four-in-a-row vertically, false otherwise.
    */
    public boolean checkVertical(String[][] board, String player) {
        for (int col = 0; col < board[0].length; col++) {
            for (int row = 0; row < board.length - 3; row++) {
                if (!board[row][col].equals("-")) {
                    if (board[row][col].equals(player) &&
                            board[row+1][col].equals(player) &&
                            board[row+2][col].equals(player) &&
                            board[row+3][col].equals(player)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    /**
    * This method checks if a player has acheived a four-in-a-row horizontally. This method checks through all 
    * possible horizontal combinations of four pieces in a row.
    *
    * @param  board  The 2D array containing the current state of the board.
    * @param  player  A string denoting the player to be checked for a win
    * @return  True if a player has acheived a four-in-a-row horizontally, false otherwise
    */
    public boolean checkHorizontal(String[][] board, String player) {
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length - 3; col++) {
                if (!board[row][col].equals("-")) {
                    if (board[row][col].equals(player) &&
                            board[row][col+1].equals(player) &&
                            board[row][col+2].equals(player) &&
                            board[row][col+3].equals(player)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    /**
    * This method checks to see if a player acheived a four-in-a-row through a diagonal. This method considers
    * diagonals that go in both ways. 
    *
    * @param  board  The 2D array containing the current state of the board.
    * @param  player  A string denoting the player to be checked for a win
    * @return  True if a player has achieved four-in-a-row diagonally, false otherwise.
    */
    public boolean checkDiagonals(String[][] board, String player) {
        for (int i = 3; i < board.length; i++) {
            for (int j = 0; j < board[0].length - 3; j++) {
                if (!board[i][j].equals("-")) {
                    if (board[i][j].equals(player) &&
                            board[i-1][j+1].equals(player) &&
                            board[i-2][j+2].equals(player) &&
                            board[i-3][j+3].equals(player)) {
                        return true;
                    }
                }
            }
        }
        for (int i = 3; i < board.length; i++) {
            for (int j = 3; j < board[0].length; j++) {
                if (!board[i][j].equals("-")) {
                    if (board[i][j].equals(player) &&
                            board[i-1][j-1].equals(player) &&
                            board[i-2][j-2].equals(player) &&
                            board[i-3][j-3].equals(player)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    /**
    * This method calls all the other win conditions and returns true if any of them are true.
    *
    * @param  board  The 2D array containing the current state of the game
    * @param  player  A string denoting which player is being checked for a win
    * @return  A boolean denoting whether the player has won the game
    */
    public boolean checkWin(String[][] board, String player){
        return checkDiagonals(board, player) || checkHorizontal(board, player) || checkVertical(board, player);
    }
    /**
    * This method makes a move based on the column given to this method and which player's turn it is. 
    *
    * @param  board  The 2D array containing the current state of the game
    * @param  whichPlayer  An integer denoting which player's turn it is
    * @param  c  The column to make a move on
    */
    public void makeMove(String[][] board, int whichPlayer, int c){
        for (int i = board.length - 1; i >= 0; i--){
            if (board[i][c].equals("-")){
                board[i][c] = (whichPlayer % 2 == 0) ? "X" : "O";
                break;
            }
        }
    }
    /**
    * This method gets all of the possible moves that can be made in the current state of the game, adding all 
    * possibilities to a HashSet. 
    *
    * @param  board  The 2D array containing the current state of the game
    * @return  A HashSet containing all of the possible moves that can be made in the current state of the game
    */
    public HashSet<Integer> addPossibleMoves(String[][] board){
        HashSet<Integer> possibleMoves = new HashSet<Integer>();
        for (int col = 0; col < board[0].length; col++){
            if (board[0][col].equals("-")){
                possibleMoves.add(col);
            }
        }
        return possibleMoves;
    }
    /**
    * This method gets a random move from a HashSet of possible moves. The HashSet is converted to an Object array, 
    * a random index is generated, and the corresponding column is returned. Finally, the integer version of the 
    * column is returned. 
    * 
    * @param  board  The 2D array containing the current state of the game
    * @return  A random valid column number
    */ 
    public int getRandomMove(String[][] board){
        Set<Integer> possibleMoves1 = new HashSet<Integer>();
        for (int col = 0; col < board[0].length; col++){
            if (board[0][col].equals("-")){
                possibleMoves1.add(col);
            }
        }
        Object[] possibleMovesArray = possibleMoves1.toArray();
        int randomNum = (int) (Math.random() * possibleMoves1.size());
        Object randomMove = possibleMovesArray[randomNum];
        return Integer.parseInt(randomMove.toString());
    }
    /**
    * This method is used to create a copy of the original board. This method is called only in the simulateGame 
    * method, so that a temporary board can be made without modifying the original. 
    *
    * @param  board  The original board to copy
    * @return  A new temporary board
    */
    public String[][] copyBoard(String[][] board){
        String[][] tempBoard = new String[board.length][board[0].length];
        for (int i = 0; i < board.length; i++){
            for (int j = 0; j < board[0].length; j++){
                tempBoard[i][j] = board[i][j];
            }
        }
        return tempBoard;
    }
    /**
    * This method simulates one singular game, by creating a temporary board. Afterwards, this method makes moves on the temporary board,
    * by taking a random move from a HashSet of possible moves. The method continues making moves, until a player wins. Then, an integer
    * is returned based on if the player or the bot or neither won.
    *
    * @param  board  The 2D array containing the current state of the game.
    * @param  whichPlayer  An integer denoting which player's turn it is.
    */
    public int simulateGame(String[][] board, int whichPlayer){
        String[][] tempBoard = copyBoard(board);
        String player = ""; 
        while((!checkWin(tempBoard, "X") && !checkWin(tempBoard, "O")) && !isBoardFull(tempBoard)){
            if (whichPlayer % 2 == 1){
                player = "O";
            } else {
                player = "X";
            }
            makeMove(tempBoard, whichPlayer, getRandomMove(board));
            whichPlayer++;
        }
        if (checkWin(tempBoard, "O")){
            return 1;
        } else if (checkWin(tempBoard, "X")){
            return -1;
        }
        return 0;
    }
    /**
    * This method is used to calculate a value to be used to find the best move for the bot. This method runs simulations on 
    * random games, and tallies how many times the bot won those simulated games. Two formulas are used to determine a score
    * for the best move, with higher scores denoting a better move. 
    *
    * @param  board  The 2D array containing the current state of the game.
    * @param  whichPlayer  An integer denoting which player's turn it is. 
    * @return  A double that represents a score based on the number of wins the bot got through a number of simulated games.
    */
    public double ucb1(String[][]board, int whichPlayer){
        int totalWins = 0;
        for (int i = 0; i < this.simulations; i++){
            int result = simulateGame(board, whichPlayer);
            if (result == 1){
                totalWins++;
            }
        }
        double exploitation = (double) totalWins / this.simulations;
        double exploration = Math.sqrt(2 * Math.log(this.simulations) / this.simulations);
        return exploitation + exploration;
    }
    /**
    * A small and simplified version of the undo method. This method just turns a piece into an empty spot, 
    * by checking the top of a column and going downwards.
    *
    * @param  col  The column to undo a move in.
    * @param  board  The 2D array containing the current state of the game.
    */
    public void undoMove(int col, String[][] board){
        for (int i = 0; i < board.length; i++){
            if (!board[i][col].equals("-")){
                board[i][col] = "-";
                return;
            }
        }
    }
    /**
    * This method prints out the current state of the board. This method is no longer needed, but was used in
    * early stages of developement. This method was also used for testing purposes, to see if the graphics matched
    * the 2D array.
    *
    * @param  board  The 2D array containing the current state of the board.
    */
    public void printBoard(String[][] board){
        System.out.println();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                System.out.print("[" + board[i][j] + "]" + " ");
            }
            System.out.println("\n");
        }
        System.out.println();
    }
    /**
    * This method is used by the bot to find the best move. The first option that is considered is if there is a winning move for the bot.
    * If so, the bot will make that move. The next option that is considered is if there is a winning move for the player. If so, the bot
    * will block that winning move. Lastly, the bot will make a move based on a number of simulations that use a formula to determine a 
    * strategic location. 
    *
    * @param  whichPlayer  An integer that denotes which player's turn it is.
    * @param  board  The 2D array containing the current state of the board.
    * @return  An integer that denotes the column of the best move.
    */
    public int findBestMove (int whichPlayer, String[][] board){
        Set<Integer> possibleMoves = addPossibleMoves(board);
        String[][] tempBoard = copyBoard(board);
        int bestMove = -1;

        whichPlayer++;

        for (int move : possibleMoves) {
            makeMove(tempBoard, whichPlayer, move);
            if (checkWin(tempBoard, "O")) {
                undoMove(move, tempBoard);
                return move;
            }
            undoMove(move, tempBoard);
        }
        
        whichPlayer--;

        for (int move : possibleMoves) {
            makeMove(tempBoard, whichPlayer, move);
            if (checkWin(tempBoard, "X")) {
                undoMove(move, tempBoard);
                return move;
            }
            undoMove(move, tempBoard);
        }

        whichPlayer++;
        
        double bestScore = Double.MIN_VALUE;
        for (int move : possibleMoves){
            double score = ucb1(tempBoard, whichPlayer);
            if (score > bestScore){
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }
   
    /**
    * This method is used to check if the board is full.
    *
    * @param  board  The 2D array containing the current state of the board.
    * @return  True if the board is full, false otherwise.
    */
    public boolean isBoardFull(String[][] board) {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                if (board[i][j].equals("-")) {
                    return false;
                }
            }
        }
        return true;
    }
    /**
    * This method checks to make sure that the user's choice is valid to prevent any errors from occuring. 
    * 
    * @param  playerChoice  The column that the user chose.
    * @param  board  The 2D array containing the current state of the board.
    * @return  A string that contains a message telling other parts of the code what the user decided
    */
    public String userPlaceValidation(int playerChoice, String[][] board) {
        if (playerChoice == 8) {
            return "undo";
        }
        if (!board[0][playerChoice - 1].equals("-")) {
            return "columnFull";
        }
        for (int i = 0; i < board.length; i++) {
            if (board[i][playerChoice - 1].equals("-")) {
                return "inrange";
            }
        }
        return "false"; 
    }
    /**
    * This method gets all of the possible coordinates for the pieces that can be placed on the visual board. 
    * This is used to easily get the coordinates for the visual pieces to be added to the board. 
    *
    * @param  imageCoordinates  This is a HashMap that stores the coordinates for the visual pieces.
    */
    public void addCoordinates(HashMap<String, String> imageCoordinates) {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 6; j++) {
                imageCoordinates.put(String.valueOf(j) + "," + String.valueOf(i), String.valueOf(SCALE*46 + (SCALE*i * 100)) + "," + String.valueOf(SCALE*100 + (j * SCALE*100)));
            }
        }
    }
    /**
    * This method gets the coordinates of the piece that was just placed and adds it to the visual pieces stack. In case of an invalid input, 
    * a null value is returned to signify that a piece shouldn't be added. 
    *
    * @param  playerChoice  The column that the player chose to place their piece in.
    * @param  board  The 2D array containing the current state of the board.
    * @param  whichPlayer  The string that determines which player's turn it is
    * @param  playerPrevMoves  The stack that stores the coordinates of the previous moves made by the player
    * @param  imageCoordinates  The hashmap containing all possible coordinates
    * @return  The coordinates of the piece that was just placed
    */
    public String getCoordinatesAndAddPieces(int playerChoice, String[][] board, String whichPlayer, Stack<String> playerPrevMoves, HashMap<String, String> imageCoordinates) {
        // Check if the top row in the specified column is empty
        if (board[0][playerChoice - 1].equals("-")) {
            // Find the first empty spot from the bottom
            for (int temp = 5; temp >= 0; temp--) {
                if (board[temp][playerChoice - 1].equals("-")) {
                    board[temp][playerChoice - 1] = whichPlayer;
                    String cord = temp + "," + (playerChoice - 1);
                    playerPrevMoves.push(cord);
                    return cord;
                }
            }
        }
        return null; // No available spot found
    }
}