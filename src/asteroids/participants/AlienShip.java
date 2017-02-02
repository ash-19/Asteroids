package asteroids.participants;

import asteroids.*;
import asteroids.destroyers.*;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

/**********************************************************************
 * Represents an alien ship
 * @author Snehashish Mishra
 **********************************************************************/
public class AlienShip extends Participant
    implements AsteroidDestroyer, ShipBulletDestroyer, ShipDestroyer
{
    private Shape outline;			// The outline of alien ship
    private int size;				// Its size
    private Controller controller;	// Game controller
    boolean changeDirection;		// Should it change direction?
	
    /**********************************************************************
     * Creates an alien ship of specified size
     **********************************************************************/
    public AlienShip(int size, Controller controller)
    {
        changeDirection = false;		// Initialize to not change the direction of movement
        
        if(size < 0 || size > 1)		// If invalid size passed, throw an exception
        {
            throw new IllegalArgumentException((new StringBuilder("Invalid alien ship size ")).append(size).toString());
        } 
        else							// Else, construct an alien ship
        {
            this.size = size;
            this.controller = controller;
            
            // Construct alien ship
            Path2D.Double poly = new Path2D.Double();
            poly.moveTo(20D, 0.0D);
            poly.lineTo(9D, 9D);
            poly.lineTo(-9D, 9D);
            poly.lineTo(-20D, 0.0D);
            poly.lineTo(20D, 0.0D);
            poly.lineTo(-20D, 0.0D);
            poly.lineTo(-9D, -9D);
            poly.lineTo(9D, -9D);
            poly.lineTo(-9D, -9D);
            poly.lineTo(-5D, -17D);
            poly.lineTo(5D, -17D);
            poly.lineTo(9D, -9D);
            poly.closePath();
            
            outline = poly;				// Set the constructed shape as outline
            
            double scale = Constants.ALIENSHIP_SCALE[size];
            poly.transform(AffineTransform.getScaleInstance(scale, scale));
            new ParticipantCountdownTimer(this, "shoot", 1500);			// Shoot bullet after 1.5 sec
            new ParticipantCountdownTimer(this, "change", 1000);		// Change direction after 1 sec
            return;
        }
    }

    /**********************************************************************
     * Returns the outline of the alien ship
     **********************************************************************/
    protected Shape getOutline()
    {
        return outline;
    }

    /**********************************************************************
     * Returns the size of the alien ship
     **********************************************************************/
    public int getSize()
    {
        return size;
    }

    /**********************************************************************
     * Is invoked to change direction or shoot 
     * after the count down is completed
     **********************************************************************/
    public void countdownComplete(Object payload)
    {
    	// If it is to shoot...
        if("shoot".equals(payload))
        {
            Ship ship = controller.getShip();						// Get the ship
            if(ship != null)										// If it is alive
            {
                fireBullet();										// Fire a bullet
                new ParticipantCountdownTimer(this, "shoot", 1500);	// Begin count down
            }
        } 
        else if("change".equals(payload))		// If it is to change direction...
            changeDirection = true;
    }

    /**********************************************************************
     * Customize the base move method
     **********************************************************************/
    public void move()
    {
        super.move();
        
        // If the alien ship needs to change direction...
        if(changeDirection)
        {
            changeDirection = false;								// Set it to false since just did
            
            // Change direction
            if(Math.cos(getDirection()) > 0.0D)
                setDirection(Constants.RANDOM.nextInt(3) - 1);
            else
                setDirection((Math.PI + (double)Constants.RANDOM.nextInt(3)) - 1.0D);
            
            new ParticipantCountdownTimer(this, "change", 1000);	// Begin count down for next change
        }
    }

    /**********************************************************************
     * Shoot a bullet towards the player ship
     **********************************************************************/
    public void fireBullet()
    {
        AsteroidsBullet b = new AsteroidsBullet(getX(), getY(), getShootingDirectionToShip());
        b.setSpeed(asteroids.Constants.BULLET_SPEED);
        controller.addParticipant(b);
    }

    /**********************************************************************
     * Returns the direction to the player ship
     **********************************************************************/
    public double getShootingDirectionToShip()
    {
        if(size == 1)
        {
            return Constants.RANDOM.nextDouble() * 2 * Math.PI;
        } 
        else
        {
            Ship ship = controller.getShip();
            double deltaX = ship.getX() - getX();								// Get ship's X coord
            double deltaY = ship.getY() - getY();								// Get ship's Y coord
            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);		// Calculate the dist bet. alien and ship
            double direction = Math.acos(deltaX / distance);					// Calculate direction
            return deltaY <= 0.0 ? -direction : direction;						// Return the position
        }
    }

    /**********************************************************************
     * When an alien ship collides with an AlienShipDestroyer, it expires
     **********************************************************************/
    public void collidedWith(Participant p)
    {
        if(p instanceof AlienShipDestroyer)
        {
            Participant.expire(this);		// Expire the alien ship form the game
            
            // Draw the destroyed debris
            controller.addParticipant(new DestructionLine(getX(), getY(), 10 * (size + 1)));
            controller.addParticipant(new DestructionLine(getX(), getY(), 10 * (size + 1)));
            controller.addParticipant(new DestructionLine(getX(), getY(), 10 * (size + 1)));
            controller.addParticipant(new DestructionLine(getX(), getY(), 10 * (size + 1)));
            controller.addParticipant(new DestructionLine(getX(), getY(), 5 * (size + 1)));
            controller.addParticipant(new DestructionLine(getX(), getY(), 5 * (size + 1)));
            
            controller.alienShipDestroyed(size);
        }
    }
}