package asteroids.participants;

import java.awt.Shape;
import java.awt.geom.*;

import asteroids.Controller;
import asteroids.Participant;
import asteroids.destroyers.*;
import static asteroids.Constants.*;

/**
 * Represents ships
 */
public class Ship extends Participant implements AsteroidDestroyer
{
    private Shape noflameOutline;		// The outline of the ship without thrusters
    private Shape flameOutline;			// The outline of the ship without thrusters
    private boolean showFlame;			// Should the thrusters be activated?
    private boolean accelerating;		// Is the ship accelerating?
    private Controller controller;		// Game controller
    
    /**
     * Constructs a ship at the specified coordinates 
     * that is pointed in the given direction.
     */
    public Ship (int x, int y, double direction, Controller controller)
    {
        this.controller = controller;
        setPosition(x, y);
        setRotation(direction);

        // Ship without thrusters
        Path2D.Double poly = new Path2D.Double();
        poly.moveTo(20, 0);
        poly.lineTo(-20, 12);
        poly.lineTo(-13, 10);
        poly.lineTo(-13, -10);
        poly.lineTo(-20, -12);
        poly.closePath();
        noflameOutline = poly;
        
        // Ship with thrusters
        Path2D.Double poly1 = new Path2D.Double();
        poly1.moveTo(20D, 0.0D);
        poly1.lineTo(-20D, 12D);
        poly1.lineTo(-13D, 10D);
        poly1.lineTo(-13D, -5D);
        poly1.lineTo(-25D, 0.0D);
        poly1.lineTo(-13D, 5D);
        poly1.lineTo(-13D, -10D);
        poly1.lineTo(-20D, -12D);
        poly1.closePath();
        flameOutline = poly1;
        
    }

    /**
     * Returns the x-coordinate of the point on the screen where the ship's nose
     * is located.
     */
    public double getXNose ()
    {
        Point2D.Double point = new Point2D.Double(20, 0);
        transformPoint(point);
        return point.getX();
    }

    /**
     * Returns the x-coordinate of the point on the screen where the ship's nose
     * is located.
     */
    public double getYNose ()
    {
        Point2D.Double point = new Point2D.Double(20, 0);
        transformPoint(point);
        return point.getY();
    }

    /**
     * This method returns the outline of the ship, i.e., 
     * with or without thrusters
     */
    @Override
    protected Shape getOutline ()
    {
    	// If the ship is accelerating...
    	if(accelerating)
        {
            accelerating = false;		// Set to not accelerating
            showFlame = !showFlame;		// Reverse the use of thrusters
            if(showFlame)				// If thrusters are active, draw ship with thrusters
                return flameOutline;
            else						// Else, draw the ship without it
                return noflameOutline;
        } 
    	else							// If the ship is not accelerating,draw the ship without thrusters 
        {
            return noflameOutline;
        }

    }

    /**
     * Customizes the base move method by imposing friction
     */
    @Override
    public void move ()
    {
        applyFriction(SHIP_FRICTION);
        super.move();
    }

    /**
     * Turns right by Pi/16 radians
     */
    public void turnRight ()
    {
        rotate(Math.PI / 16);
    }

    /**
     * Turns left by Pi/16 radians
     */
    public void turnLeft ()
    {
        rotate(-Math.PI / 16);
    }
    
    /**
     * Accelerates by SHIP_ACCELERATION
     */
    public void accelerate ()
    {
        accelerate(SHIP_ACCELERATION);
        accelerating = true;
    }

    /**
     * Checks whether the bullet limit is reached and if not,
     * draws (shoots) a bullet
     */
    public void shoot()
    {
        if(!controller.atBulletLimit(asteroids.Constants.BULLET_LIMIT))			// If not at bullet limit
        {
            Bullet b = new ShipBullet(getXNose(), getYNose(), getRotation());	// Create a bullet
            b.setVelocity(asteroids.Constants.BULLET_SPEED, getRotation());		// Fire it
            controller.addParticipant(b);							            // Add it to the participants
        }
    }
    
    /**
     * When a Ship collides with a ShipDestroyer, it expires
     */
    @Override
    public void collidedWith (Participant p)
    {
        if (p instanceof ShipDestroyer)
        {
            // Expire the ship from the game
            Participant.expire(this);

            //Call 3 destruction lines
            controller.addParticipant(new DestructionLine(getX(), getY(), 20D));
            controller.addParticipant(new DestructionLine(getX(), getY(), 20D));
            controller.addParticipant(new DestructionLine(getX(), getY(), 5D));
            
            // Tell the controller the ship was destroyed
            controller.shipDestroyed();
        }
    }
    
}
