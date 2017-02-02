package asteroids;

import java.awt.event.*;
import java.util.Iterator;

import javax.swing.*;

import asteroids.participants.AlienShip;
import asteroids.participants.Asteroid;
import asteroids.participants.Ship;
import static asteroids.Constants.*;

/**********************************************************************
 * Controls a game of Asteroids.
 **********************************************************************/
public class Controller implements KeyListener, ActionListener
{
    private ParticipantState pstate;	// The state of all the Participants
    private Ship ship;					// The ship (if one is active) or null (otherwise)
    private AlienShip alienShip;		// The alien ship
    private Timer refreshTimer;			// When this timer goes off, it is time to refresh the animation
    private int lives;					// Number of lives left
    private Display display;		    // The game display
    private int score;					// Score Counter
    private int level;					// Level counter
    private boolean turnLeft;			// Left key pressed notifier
    private boolean turnRight;			// Right key pressed notifier
    private boolean fireBullet;			// Fire bullet or not notifier
    private boolean thrusters;			// Accelerate or not notifier
    
    // The time at which a transition to a new stage of the game should be made.
    // A transition is scheduled a few seconds in the future to give the user
    // time to see what has happened before doing something like going to a new
    // level or resetting the current level.
    private long transitionTime;
    
    
    /**********************************************************************
     * Constructs a controller to coordinate the game and screen
     **********************************************************************/
    public Controller ()
    {
        // Record the game and screen objects
        display = new Display(this);
        display.setVisible(true);        
        
        // Initialize the ParticipantState
        pstate = new ParticipantState();

        // Set up the refresh timer.
        refreshTimer = new Timer(FRAME_INTERVAL, this);

        // Clear the transitionTime
        transitionTime = Long.MAX_VALUE;

        // Bring up the splash screen and start the refresh timer
        splashScreen();
        refreshTimer.start();
    }

    /**********************************************************************
     * Returns the ship, or null if there isn't one
     **********************************************************************/
    public Ship getShip ()
    {
        return ship;
    }

    /**********************************************************************
     * Configures the game screen to display the splash screen
     **********************************************************************/
    private void splashScreen ()
    {
        // Clear the screen, reset the level, and display the legend
        clear();
        level = 1;
        display.setLegend("Asteroids");

        // Place four asteroids near the corners of the screen.
        placeAsteroids();
    }

    /**********************************************************************
     * The game is over. Displays a message to that effect.
     **********************************************************************/
    private void finalScreen ()
    {
        display.setLegend(GAME_OVER);
        display.removeKeyListener(this);
    }

    /************************************************************************
     * Place a new ship in the center of the screen. Remove any existing ship
     * first.
     ************************************************************************/
    private void placeShip ()
    {
        // Place a new ship
        Participant.expire(ship);
        ship = new Ship(SIZE / 2, SIZE / 2, -Math.PI / 2, this);
        addParticipant(ship);
        scheduleTransition(5000);
        display.setLegend("");
    }

    /**********************************************************************
     * Place a new alien ship by checking the current level.
     * Remove any existing ship first
     **********************************************************************/
    private void placeAlienShip()
    {
        Participant.expire(alienShip);		// Remove any existing alien ship
        
        if(level > 1)						// Place an alien ship is level > 1
        {
            int alienShipSize = level != 2 ? 0 : 1;
            
            // Add alien ship, set its postion and velocity
            alienShip = new AlienShip(alienShipSize, this);
            alienShip.setPosition(0.0D, SIZE * Constants.RANDOM.nextDouble());
            alienShip.setVelocity(5 - alienShipSize, (double)Constants.RANDOM.nextInt(2) * Math.PI);
            
            addParticipant(alienShip);		// Add it to list of participants
        }
    }
    
    /**********************************************************************
     * Places four asteroids near the corners of the screen. Gives them 
     * random velocities and rotations.
     **********************************************************************/
    private void placeAsteroids ()
    {
        addParticipant(new Asteroid(0, 2, EDGE_OFFSET, EDGE_OFFSET, 3, this));
        addParticipant(new Asteroid(1, 2, SIZE - EDGE_OFFSET, EDGE_OFFSET, 3, this));
        addParticipant(new Asteroid(2, 2, EDGE_OFFSET, SIZE - EDGE_OFFSET, 3, this));
        addParticipant(new Asteroid(3, 2, SIZE - EDGE_OFFSET, SIZE - EDGE_OFFSET, 3, this));
    }

    /**********************************************************************
     * Returns if whether or not the consecutive bullet
     * fire limit has been reached
     * @return True or false
     **********************************************************************/
    public boolean atBulletLimit(int bulletLimit)
    {
        return pstate.countFiredShipBullets() >= bulletLimit;	// If bullets fired are >= max firing limit
    }
    
    /**********************************************************************
     * Clears the screen so that nothing is displayed
     **********************************************************************/
    private void clear ()
    {
        pstate.clear();				// Clear all participants
        display.setLegend("");
        ship = null;				// No ship
        alienShip = null;			// No alien ship
    }

    /**********************************************************************
     * Sets things up and begins a new game.
     **********************************************************************/
    private void initialScreen ()
    {
        // Clear the screen
        clear();

        // Place four asteroids
        placeAsteroids();

        // Place the ship
        placeShip();

        // Reset statistics
        lives = 3;
        score = 0;
        level = 1;
        
        // Set this attributes to false when called to draw initial screen
        turnLeft = false;
        turnRight = false;
        fireBullet = false;
        thrusters = false;
        
        display.setLives(lives);		// Show lives
        display.setScore(score);		// Show score
        display.setLevel(level);		// Show level
        
        // Start listening to events (but don't listen twice)
        display.removeKeyListener(this);
        display.addKeyListener(this);

        // Give focus to the game screen
        display.requestFocusInWindow();
    }

    /**********************************************************************
     * Prepares for next screen after level up
     **********************************************************************/
    private void nextScreen()
    {
        clear();					// Clear everything
        placeAsteroids();			// Place asteroids
        placeShip();				// Place the ship
        level++;					// Level up
        display.setLevel(level);	// Show level
    }
    
    /**********************************************************************
     * Adds a new Participant
     **********************************************************************/
    public void addParticipant (Participant p)
    {
        pstate.addParticipant(p);
    }

    /**********************************************************************
     * The ship has been destroyed
     **********************************************************************/
    public void shipDestroyed ()
    {
        ship = null;	  				// Null out the ship
        lives--;						// Decrement lives
        display.setLives(lives);		// Show lives remaining
        scheduleTransition(END_DELAY);	// Since the ship was destroyed, schedule a transition
    }

    /**********************************************************************
     * An asteroid of the given size has been destroyed
     **********************************************************************/
    public void asteroidDestroyed (int size)
    {
    	addToScore(Constants.ASTEROID_SCORE[size]);
        if (pstate.countAsteroids() == 0)			// If all the asteroids are gone, schedule a transition
        {
            scheduleTransition(END_DELAY);
        }
    }
    
    /**********************************************************************
     * An alien ship of the given size has been destroyed
     **********************************************************************/
    public void alienShipDestroyed(int size)
    {
        addToScore(Constants.ALIENSHIP_SCORE[size]);	// Add its score to total score counter
        alienShip = null;								// Remove the ship
        if(ship != null)
            scheduleTransition(ALIEN_DELAY);
    }

    /**********************************************************************
     * Add the passed score to the total score counter
     **********************************************************************/
    public void addToScore(int delta)
    {
        score += delta;				// Add to total score
        display.setScore(score);	// Show that score
    }

    /**********************************************************************
     * Returns the current level
     **********************************************************************/
    public int getLevel()
    {
        return level;
    }

    /**********************************************************************
     * Schedules a transition m msecs in the future
     **********************************************************************/
    private void scheduleTransition (int m)
    {
        transitionTime = System.currentTimeMillis() + m;
    }

    /*************************************************************************
     * This method will be invoked because of button presses and timer events.
     *************************************************************************/
    @Override
    public void actionPerformed (ActionEvent e)
    {
        // The start button has been pressed. Stop whatever we're doing
        // and bring up the initial screen
        if (e.getSource() instanceof JButton)
        {
            initialScreen();
        }

        // Time to refresh the screen and deal with keyboard input
        else if (e.getSource() == refreshTimer)
        {
            // It may be time to make a game transition
            performTransition();

            // Move the participants to their new locations
            pstate.moveParticipants();

            // SHip movements and actions
            if(turnLeft && ship != null)
                ship.turnLeft();
            if(turnRight && ship != null)
                ship.turnRight();
            if(thrusters && ship != null)
                ship.accelerate();
            if(fireBullet && ship != null)
            {
                ship.shoot();
                fireBullet = false;
            }
            
            // Refresh screen
            display.refresh();
        }
    }

    /**********************************************************************
     * Returns an iterator over the active participants
     **********************************************************************/
    public Iterator<Participant> getParticipants ()
    {
        return pstate.getParticipants();
    }

    /**********************************************************************
     * If the transition time has been reached, transition to a new state
     **********************************************************************/
    private void performTransition ()
    {
        // Do something only if the time has been reached
        if (transitionTime <= System.currentTimeMillis())
        {
            // Clear the transition time
            transitionTime = Long.MAX_VALUE;

            // If there are no lives left, the game is over. Show the final
            // screen.
            if (lives <= 0)
                finalScreen();
            else if (ship == null)					// If the ship was destroyed, place a new one and continue
                placeShip();
            else if(pstate.countAsteroids() == 0)	// If no asteroids remain, draw next level screen
                    nextScreen();
            else if(ship == null)					// If ship is destroyed, place a new one
                    placeShip();
            else if(alienShip == null)				// If an alien ship is destroyed, place a new one
                    placeAlienShip();
        }
    }

    /**********************************************************************
     * If a key of interest is pressed, record that it is down.
     **********************************************************************/
    @Override
    public void keyPressed (KeyEvent e)
    {
        if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A)
            turnLeft = true;
        
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D)
            turnRight = true;
        
        else if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W)
            thrusters = true;
        
        else if(e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_S)
            fireBullet = true;
    }

    /**********************************************************************
     * Ignore these events.
     **********************************************************************/
    @Override
    public void keyTyped (KeyEvent e)
    {
    }

    /**********************************************************************
     * If the pressed key is released, notify accordingly
     **********************************************************************/
    @Override
    public void keyReleased (KeyEvent e)
    {      
    	if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A)
            turnLeft = false;
        
    	else if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D)
            turnRight = false;
        
    	else if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W)
            thrusters = false;
        
    	else if(e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_S)
            fireBullet = false;
    }
}
