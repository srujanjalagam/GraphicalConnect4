import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class AboutScreen {
    private int SCALE = 1; // used to vary the graphics size based on how big the screen is
    /**
    * This method is used to display the about screen, in case the user is confused on how the game or the buttons work. 
    * First, the image containing the instructions is loaded to an appropriate size. Then a back button is added to the screen,
    * to allow the user to go back to the main menu. This button is then configured to go back to the main menu. Finally, this
    * button is made visible. 
    */
    public void aboutScreen() {
        //----Creating 
        JFrame homeFrame = new JFrame("Connect 4 - Home Screen");
        homeFrame.setSize(SCALE*820, SCALE*820);
        homeFrame.setLayout(null);
        ImageIcon back = new ImageIcon("Info.png");
        Image img = back.getImage().getScaledInstance(SCALE*800, SCALE*800, Image.SCALE_SMOOTH);
        back = new ImageIcon(img);
        JLabel backLabel = new JLabel(back);
        backLabel.setBounds(SCALE*0, SCALE*0, SCALE*800, SCALE*800);
        homeFrame.add(backLabel);
    //------------------------------------------------
        String[] imageFileNames = {"back.png"};
        for (int i = 0; i < imageFileNames.length; i++) {
            JButton button = new JButton();
            if (i == 0) {
                button.setBounds(650, SCALE*725, SCALE*125, SCALE*125);
                ImageIcon buttonIcon = new ImageIcon(imageFileNames[i]);
                Image scaledImage = buttonIcon.getImage().getScaledInstance(SCALE*125, SCALE*125, Image.SCALE_SMOOTH);
                buttonIcon = new ImageIcon(scaledImage);
                button.setIcon(buttonIcon);
                button.setBorder(BorderFactory.createEmptyBorder());
                button.setContentAreaFilled(false);
                homeFrame.add(button);
            } 
            int finalI = i;
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    homeFrame.dispose();
                    if (finalI == 0) {
                        Main.game(); 
                    }
                }
            });
            homeFrame.setComponentZOrder(button, i);
        }
        homeFrame.setComponentZOrder(backLabel, 2);
        homeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        homeFrame.setVisible(true);
    } 
}
