package asteroids;

import javax.swing.*;
import java.awt.*;
import static asteroids.Constants.*;

/**********************************************************************
 * Defines the top-level appearance of an Asteroids game.
 **********************************************************************/
@SuppressWarnings("serial")
public class Display extends JFrame
{
    private Screen screen;		    // The area where the action takes place
    private JLabel levelLabel;		// Level Label
    private JLabel scoreLabel;		// Score label
    private JLabel livesLabel;		// Lives label

    /**********************************************************************
     * Lays out the game and creates the controller
     **********************************************************************/
    public Display (Controller controller)
    {
        // Title at the top
        setTitle(TITLE);

        // Default behavior on closing
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // The main playing area and the controller
        screen = new Screen(controller);
        
        // This panel contains the screen to prevent the screen from being
        // resized
        JPanel screenPanel = new JPanel();
        screenPanel.setLayout(new GridBagLayout());
        screenPanel.add(screen);

        // This panel contains buttons and labels
        JPanel controls = new JPanel();

        // Create a two column layour for button and scores
        controls.setLayout(new GridLayout(1, 2));
        JPanel left = new JPanel();
        JPanel right = new JPanel();
        controls.add(left);
        controls.add(right);
        
        JButton startGame = new JButton(START_LABEL);		// The button that starts the game
        left.add(startGame);
        levelLabel = new JLabel();							// Create and add Level label
        right.add(levelLabel);
        livesLabel = new JLabel();							// Create and add Lives label
        right.add(livesLabel);
        scoreLabel = new JLabel();							// Create and add Score label
        right.add(scoreLabel);
        
        // Organize everything
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(screenPanel, "Center");
        mainPanel.add(controls, "North");
        setContentPane(mainPanel);
        pack();

        // Connect the controller to the start button
        startGame.addActionListener(controller);
    }
    
    /**********************************************************************
     * Called when it is time to update the screen display. This is what 
     * drives the animation.
     **********************************************************************/
    public void refresh ()
    {
        screen.repaint();
    }
    
    /**********************************************************************
     * Sets the level label
     **********************************************************************/
    public void setLevel(int n)
    {
        levelLabel.setText((new StringBuilder("  Level: ")).append(n).toString());
    }

    /**********************************************************************
     * Sets the lives label
     **********************************************************************/
    public void setLives(int n)
    {
        livesLabel.setText((new StringBuilder("  Lives: ")).append(n).toString());
    }

    /**********************************************************************
     * Sets the score label
     **********************************************************************/
    public void setScore(int n)
    {
        scoreLabel.setText((new StringBuilder("  Score: ")).append(n).toString());
    }
    
    /**********************************************************************
     * Sets the large legend
     **********************************************************************/
    public void setLegend (String s)
    {
        screen.setLegend(s);
    }
}
