import java.util.Scanner;
import java.util.Stack;
import java.util.HashMap;
import javax.swing.*;
import java.awt.Image;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class playerVplayer {
    private JButton[] buttons; // all the JButtons for selection 
    int SCALE = 1; // used to vary the graphics size based on how big the screen is
    
/**
        This method is used to setup all basics to run the PvP mode. It creates and setsups the various data structures and calls the main method to display the board graphics. 
    */
    public playerVplayer() {
        Scanner input = new Scanner(System.in);
        String[][] board = new String[6][7];
        Stack<String> playerMoves = new Stack<String>();
        Stack<JLabel> visualPieces = new Stack<JLabel>();
        HashMap<String, String> imageCoordinates = new HashMap<String, String>();
        addCoordinates(imageCoordinates);
        JFrame frame = new JFrame("Connect 4");
        int whichPlayer = 0;
        makeBoard(board);
        displaySetUp(frame, board, playerMoves, visualPieces, imageCoordinates, whichPlayer);
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
            winner = new ImageIcon("redWin.png"); 
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
        @param  visualPieces  The stack of JLabel objects that contains all moves made by the players.
        @param whichPlayer The player whose turn it is. Used to determine which image to add to the board (red or blue)
        @param playerMoves A stack of the moves made by the player. Alternative way to determine which player's turn it is 
*/
    public void displaySetUp(final JFrame frame, final String[][] Connect4Board, final Stack<String> playerMoves, final Stack<JLabel> visualPieces, 
                             final HashMap<String, String> imageCoordinates, final int whichPlayer) {
        ImageIcon board = new ImageIcon("thumb-Connect_Four-a-removebg-preview.png");
        Image scaledImage = board.getImage().getScaledInstance(SCALE*800, SCALE*800, Image.SCALE_SMOOTH); 
        board = new ImageIcon(scaledImage);
        JLabel boardL = new JLabel(board);
        frame.setLayout(null);
        boardL.setBounds(SCALE*0, SCALE*-40, board.getIconWidth(), board.getIconHeight());
        frame.add(boardL);
        buttons = new JButton[8]; 
        for (int i = 0; i < buttons.length; i++) {
            final int column = i;
            buttons[i] = new JButton();
            buttons[i].setBounds(SCALE*44 + (SCALE*i * 102), SCALE*690, SCALE*100, SCALE*60);
            if (i == 7) {
                buttons[i].setBounds(SCALE*300, SCALE*770, SCALE*200, SCALE*60);
            }
            buttons[i].addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                    String isValid = userPlaceValidation(column + 1, Connect4Board);
                    if (isValid.equals("inrange")) {
                        handleButtonClick(frame, column, Connect4Board, playerMoves, visualPieces, imageCoordinates, e);
                    } else if (isValid.equals("columnFull")) {
                        JOptionPane.showMessageDialog(null, "Column is full. Choose another column.");
                    } else if (isValid.equals("undo")) {
                        undoMoves(Connect4Board, playerMoves, visualPieces, whichPlayer, frame);
                    } 
                }
            });
            ImageIcon buttonImage = new ImageIcon((i+1) +".png");
            Image img = buttonImage.getImage().getScaledInstance(SCALE*60, SCALE*60, Image.SCALE_SMOOTH);
            ImageIcon scaledButtonImage = new ImageIcon(img);
            buttons[i].setIcon(scaledButtonImage);
            buttons[i].setBorder(BorderFactory.createEmptyBorder());
            buttons[i].setContentAreaFilled(false);
            frame.add(buttons[i]);
        }
        frame.setSize(board.getIconWidth(), board.getIconHeight() + SCALE*100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
/**
        This method is used to handle the button click. This method is called by the actionListeners given to the column selection buttons. The method first 
        gets the coordinates of where to place the image, and then places the image in the correct location. After adding, we increment whichplayer so that 
        the next player's turn is achived. 

        @param frame The main JFrame where all graphical elements are added
        @param column The column that the user selected (by using the buttons)
        @param board The 2D array of Strings representing the Connect4 board. Used to check for all win conditions, and bot move generation 
        @param imageCoordinates A HashMap containing the coordinates of the images in the Connect4 board. Used to add a image to a specific loccation 
               If there is a "coin" in the 2D array at [3,4], the hashmap would return the coordinates, so that a image addded will be in the same 
               cooresponding image as the piece in the 2D array.
        @param e The ActionEvent that is triggered when a button is clicked
        @param visualPieces A stack of all the previous pieces. Used to undo moves. 
        @param playerMoves A stack of the moves made by the player. Alternative way to determine which player's turn it is 
    */
    public void handleButtonClick(JFrame frame, int column, String[][] board, Stack<String> playerMoves, Stack<JLabel> visualPieces, HashMap<String, String> imageCoordinates, ActionEvent e) {
        String cords = getCoordinatesAndAddPieces(column + 1, board, playerMoves.size() % 2 == 1 ? "\u001B[1m\u001B[31mO\u001B[0m" : "\u001B[1m\u001B[33mO\u001B[0m", playerMoves, imageCoordinates);
        displayBoard(frame, playerMoves.size() - 1, imageCoordinates.get(cords), visualPieces, board);
        printBoard(board); 
    }
    
/**
    * This method displays the graphical version of the board, including the board itself and all pieces that are placed. 
    * This method also calls respective methods based on if a player wins, or if the two players make a draw. 
    *
    * @param  frame  The JFrame object that is used to display the board.
    * @param  whichPlayer  The integer value that determines which player is currently playing.
    * @param  cords  The string value that contains the coordinates of the piece that was placed.
    * @param  visualPieces  The stack of JLabel objects that contains all moves made by the players.
    * @param  board  The 2D array containing the current state of the board.
    */
    public void displayBoard(JFrame frame, int whichPlayer, String cords, Stack<JLabel> visualPieces, String[][] board) {
        int x = Integer.parseInt(cords.substring(0, cords.indexOf(",")));
        int y = Integer.parseInt(cords.substring(cords.indexOf(",") + 1, cords.length()));
        ImageIcon imgX;
        if (whichPlayer % 2 == 0) {
            imgX = new ImageIcon("yellow.png");
        } else {
            imgX = new ImageIcon("red.png");
        }
        JLabel xL = new JLabel(imgX);
        xL.setBounds(x, y-(SCALE*40), SCALE*100, SCALE*100);
        frame.add(xL);
        visualPieces.push(xL);
        frame.revalidate(); 
        frame.repaint(); 
        frame.setVisible(true);
        if (checkVertical(board) || checkHorizontal(board) || checkDiagonals(board)) {
            goToWin(whichPlayer, frame);
        }
        if(isBoardFull(board)){
            goToDraw(frame); 
        }
    }

/**
    This method is called when a draw is deteced. It display a draw message, and the buttons are disabled so no more moves are played.
    A back button is displayed, so the user can go back to the main menu, and play another game is desired. 
    
    @param frame The main JFrame where all graphical elements are added 
*/
    public void goToDraw(JFrame frame){
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
    * This method creates the board at the beginning of each game, however, each board is initialized the same. 
    * 
    * @param  board  The 2D array meant to represent the Connect 4 board.
    */
    public void makeBoard(String[][] board) {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                board[i][j] = "-";
                System.out.print("[" + board[i][j] + "]" + " ");
            }
            System.out.println("\n");
        }
        System.out.println(" 1   2   3   4   5   6   7");
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
        int temp = 5;
        while (temp >= 0) {
            if (board[temp][playerChoice - 1].equals("-")) {
                board[temp][playerChoice - 1] = whichPlayer;
                String cord = String.valueOf(temp) + "," + String.valueOf(playerChoice - 1);
                playerPrevMoves.push(cord);
                return cord;
            }
            temp--;
        }
        return null;
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
                imageCoordinates.put(String.valueOf(j) + "," + String.valueOf(i), String.valueOf(SCALE*46 + (i * SCALE*100)) + "," + String.valueOf(SCALE*100 + (j * SCALE*100)));
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
    public void printBoard(String[][] board) {
        System.out.println();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                System.out.print("[" + board[i][j] + "]" + " ");
            }
            System.out.println("\n");
        }
        System.out.print(" 1   2   3   4   5   6   7\n");
    }
    /**
    * This method checks if a player has acheived a four-in-a-row vertically. This method checks all possible
    * vertical combinations. 
    *
    * @param  board  The 2D array containing the current state of the board.
    * @return  True if a player has achieved four-in-a-row vertically, false otherwise.
    */
    public boolean checkVertical(String[][] board) {
        for (int col = 0; col < board[0].length; col++) {
            for (int row = 0; row < board.length - 3; row++) {
                if (!board[row][col].equals("-")) {
                    if (board[row][col].equals(board[row + 1][col]) &&
                            board[row][col].equals(board[row + 2][col]) &&
                            board[row][col].equals(board[row + 3][col])) {
                       // System.out.println("Player " + board[row][col] + " wins!");
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
    * @return  True if a player has acheived a four-in-a-row horizontally, false otherwise
    */
    public boolean checkHorizontal(String[][] board) {
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length - 3; col++) {
                if (!board[row][col].equals("-")) {
                    if (board[row][col].equals(board[row][col + 1]) &&
                            board[row][col].equals(board[row][col + 2]) &&
                            board[row][col].equals(board[row][col + 3])) {
                        //System.out.println("Player " + board[row][col] + " wins!");
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
    * @return  True if a player has achieved four-in-a-row diagonally, false otherwise.
    */
    public boolean checkDiagonals(String[][] board) {
        for (int i = 3; i < board.length; i++) {
            for (int j = 0; j < board[0].length - 3; j++) {
                if (!board[i][j].equals("-")) {
                    if (board[i][j].equals(board[i - 1][j + 1]) &&
                            board[i][j].equals(board[i - 2][j + 2]) &&
                            board[i][j].equals(board[i - 3][j + 3])) {
                        //System.out.println("Player " + board[i][j] + " wins!");
                        return true;
                    }
                }
            }
        }
        for (int i = 3; i < board.length; i++) {
            for (int j = 3; j < board[0].length; j++) {
                if (!board[i][j].equals("-")) {
                    if (board[i][j].equals(board[i - 1][j - 1]) &&
                            board[i][j].equals(board[i - 2][j - 2]) &&
                            board[i][j].equals(board[i - 3][j - 3])) {
                        //System.out.println("Player " + board[i][j] + " wins!");
                        return true;
                    }
                }
            }
        }
        return false;
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
    * This method is used to undo the last move made by a player. Using a stack and a 2D array board, this method checks
    * last move that was stored in the stack, and removes that piece from the board. Since the game is graphic based,
    * the visual piece that is meant to be removed, has its visibility set to false. If the stack is empty, then a 
    * message is displayed notifying the user that there are no moves to undo.
    *
    * @param  board  The 2D array containing the current state of the game
    * @param  undo  The stack containing most recent moves at the top
    * @param  visualPieces  The stack containing the visual pieces that were added to the board
    * @param  whichplayer  The int variable that determines which player's turn it is
    * @param  frame  The JFrame that is used to display the board
    *
    * @return An integer representing the previous player's turn 
    */
    public int undoMoves(String[][] board, Stack<String> undo, Stack<JLabel> visualPieces, int whichPlayer, JFrame frame) {
        if (!undo.isEmpty()) {
            String temp = undo.pop();
            JLabel xL = visualPieces.pop();
            xL.setVisible(false);
            String[] coords = temp.split(",");
            int x = Integer.parseInt(coords[0]);
            int y = Integer.parseInt(coords[1]);
            board[x][y] = "-";
            printBoard(board); 
            return whichPlayer - 1;
        } else {
            //System.out.println("No moves to undo.");
            printBoard(board); 
            return whichPlayer;
        }
    }
} 