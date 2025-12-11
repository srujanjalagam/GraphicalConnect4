import java.util.Scanner;
import java.util.Stack;
import java.util.HashMap;
import javax.swing.*;
import java.awt.Image;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Main {
    private static JButton[] buttons;  // all the JButtons for selection 
    static int SCALE = 1; // used to vary the graphics size based on how big the screen is
    static int DIFFICULTY = 10000; // difficulty of the AI, higher numbers indicate more difficulty
/**
    This is where everything begins! Calls the main method where the main screen is. 
    I made a sepereate method, becasue I need to called the game method from a different class. 
*/
    public static void main(String[] args) {
        game(); 
    }
    
/** 
------HOW GRAPHICS WORK--------

------IMAGES------
First a JFrame is created which is essantially an empty window where we can add various elements. We can set the size of the window, and set the layout of the window.
To add an image, we first use ImageIcon to read the image, convert the ImageIcon objcet to an Image object to add more constraints (size), and then the Image 
Object is convered to a JLabel Object. The JLabel object can be used to set where the image will be added in the JFrame itself. 

----BUTTONS----
Fisrt we create a JButton Object, then we can add poisitonal and size restraints to the button. We can the overlay the button with an image, to created a "grapical" button."
Then an action listener is added to the button, which is an object that can be used to perform actions when the button is clicked. It always runs it background, so whenever a buttons is 
clicked it runs the code inside the action listener. 

This is the basic conecpt of how all the graphical elements are crated. 


*/

/**
This method is used to display the main menu that conntains the start button to go the seection screen, and the about button to go to the about screen
A JFrame is first created to host all the graphical elements and a background image is overlaid with 3 image-buttons (start, aboout, and back). All the 
graphical elements are constrained to certain positions and dimensions. After the ceration of the buttons, an actionLisener is added to each button 
to make them functional. The buttons are given some more constraints and JFrame commands are given. 
*/
    public static void game() {
        // Creating the main JFrame with background image and constraints.
        JFrame homeFrame = new JFrame("Connect 4 - Home Screen");
        homeFrame.setSize(SCALE*820, SCALE*820);
        homeFrame.setLayout(null);
        ImageIcon back = new ImageIcon("Connect4.png");
        Image img = back.getImage().getScaledInstance(SCALE*800, SCALE*800, Image.SCALE_SMOOTH);
        back = new ImageIcon(img);
        JLabel backLabel = new JLabel(back);
        backLabel.setBounds(SCALE*0, SCALE*0, SCALE*800, SCALE*800);
        homeFrame.add(backLabel);
        //-------------------------------------
        // Creating the 3 buttons with constraints and image overlays. 
        String[] imageFileNames = {"start.png", "about.png", "back.png"};
        for (int i = 0; i < imageFileNames.length; i++) {
            JButton button = new JButton();
            if (i == 2) {
                button.setBounds(350, SCALE*725, SCALE*125, SCALE*125);
                ImageIcon buttonIcon = new ImageIcon(imageFileNames[i]);
                Image scaledImage = buttonIcon.getImage().getScaledInstance(SCALE*125, SCALE*125, Image.SCALE_SMOOTH);
                buttonIcon = new ImageIcon(scaledImage);
                button.setIcon(buttonIcon);
                button.setBorder(BorderFactory.createEmptyBorder());
                button.setContentAreaFilled(false);
                homeFrame.add(button);
            } else {
                button.setBounds(SCALE*15 + (SCALE*i*590), SCALE*360, SCALE*200, SCALE*200);
                ImageIcon buttonIcon = new ImageIcon(imageFileNames[i]);
                Image scaledImage = buttonIcon.getImage().getScaledInstance(SCALE*150, SCALE*150, Image.SCALE_SMOOTH);
                buttonIcon = new ImageIcon(scaledImage);
                button.setIcon(buttonIcon);
                button.setBorder(BorderFactory.createEmptyBorder());
                button.setContentAreaFilled(false);            
                homeFrame.add(button);
            }
            // creating new actionistener to make the buttons functional. 
            int finalI = i;
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    homeFrame.dispose();
                    if (finalI == 0) { // directions for button 1 
                        selectionsScreen(); // goes to selection screen method 
                    }
                    else if (finalI == 1) { // directions for button 2 
                        AboutScreen newGame = new AboutScreen();
                        newGame.aboutScreen(); // goes to about screen method 
                    } 
                    else if (finalI == 2) { // directions for button 3 
                        JOptionPane.showMessageDialog(null, "Thanks for Playing. Come back again soon! Please!");
                       homeFrame.dispose(); 
                        
                    }
                }

            });
            homeFrame.setComponentZOrder(button, i); // preverts overlaping from other elements 
        }
        // Final JFrame commands to make everyhting visibe. 
        homeFrame.setComponentZOrder(backLabel, 4);
        homeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        homeFrame.setVisible(true); 
    }

    
/**
  This method is used to display the seection screen that allows the player to either choose either player vs. player or player vs. bot.  
  A JFrame is first created to host all the graphical elements and a background image is overlaid with 3 image-buttons (PVP, PvB, and back). All the 
  graphical elements are constrained to certain positions and dimensions. After the ceration of the buttons, an actionLisener is added to each button 
  to make them functional. The buttons are given some more constraints and JFrame commands are given. 
*/
    public static void selectionsScreen() {
        // Creating the main JFrame with background image and constraints.
        JFrame homeFrame = new JFrame("Connect 4 - Selection Screen");
        homeFrame.setSize(SCALE*820, SCALE*820);
        homeFrame.setLayout(null);
        ImageIcon back = new ImageIcon("Connect4.png");
        Image img = back.getImage().getScaledInstance(SCALE*800, SCALE*800, Image.SCALE_SMOOTH);
        back = new ImageIcon(img);
        JLabel backLabel = new JLabel(back);
        backLabel.setBounds(SCALE*0, SCALE*0, SCALE*800, SCALE*800);
        homeFrame.add(backLabel);
        //-----------------------------------
        // Creating the 3 buttons with constraints and image overlays.
        String[] imageFileNames = {"bot0.png", "bot1.png", "back.png"};
        for (int i = 0; i < imageFileNames.length; i++) {
            JButton button = new JButton();
            if (i == 2) {
                button.setBounds(350, SCALE*725, SCALE*125, SCALE*125);
                ImageIcon buttonIcon = new ImageIcon(imageFileNames[i]);
                Image scaledImage = buttonIcon.getImage().getScaledInstance(SCALE*125, SCALE*125, Image.SCALE_SMOOTH);
                buttonIcon = new ImageIcon(scaledImage);
                button.setIcon(buttonIcon);
                button.setBorder(BorderFactory.createEmptyBorder());
                button.setContentAreaFilled(false);
                homeFrame.add(button);
            } else {
                button.setBounds(SCALE*20 + (SCALE*i*584), SCALE*360, SCALE*200, SCALE*200);
                ImageIcon buttonIcon = new ImageIcon(imageFileNames[i]);
                Image scaledImage = buttonIcon.getImage().getScaledInstance(SCALE*200, SCALE*200, Image.SCALE_SMOOTH);
                buttonIcon = new ImageIcon(scaledImage);
                button.setIcon(buttonIcon);
                button.setBorder(BorderFactory.createEmptyBorder());
                button.setContentAreaFilled(false);
                homeFrame.add(button);
            }
            // Creating actionListener to make the buttons functional. 
            int finalI = i;
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    homeFrame.dispose();
                    if (finalI == 0) { // direction for the PvP button
                        // creates a new playerVplayer object to run the PvP mode. 
                        playerVplayer newGame = new playerVplayer(); 
                    }
                    else if (finalI == 1) { // direction for the PvB button
                        // creates a new MTCS object to PvB mode with specifiec difficulty.
                        MCTS newGame = new MCTS(DIFFICULTY);
                        String[][] board = newGame.makeBoard(new String[6][7]);
                        newGame.game(board, 0); // runs the method to start the PvB mode
                    } 
                    else if (finalI == 2) { // direction for the back button
                        game(); // goes back to the main menu
                    }
                }

            });
            homeFrame.setComponentZOrder(button, i);
        }
        // Fina JFrame commands to make everything visibe.
        homeFrame.setComponentZOrder(backLabel, 4);
        homeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        homeFrame.setVisible(true);
    }
}