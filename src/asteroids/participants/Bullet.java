package asteroids.participants;

import asteroids.Participant;
import asteroids.ParticipantCountdownTimer;
//import asteroids.destroyers.ShipDestroyer;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;

/**********************************************************************
 * Represents a bullet
 **********************************************************************/
public abstract class Bullet extends Participant
{
    private Shape outline;			// Bullet's outline or shape
    
    /**********************************************************************
     * Construct a bullet on (x,y) coord and set its direction
     **********************************************************************/
    public Bullet(double x, double y, double direction)
    {
        setPosition(x, y);
        setVelocity(asteroids.Constants.BULLET_SPEED, direction);
        outline = new Ellipse2D.Double(0.0D, 0.0D, 1.0D, 1.0D);
        new ParticipantCountdownTimer(this, this, asteroids.Constants.BULLET_DURATION);
    }

    /**********************************************************************
     * Returns the shape of the bullet
     **********************************************************************/
    protected Shape getOutline()
    {
        return outline;
    }
    
    /********************************************
     * Expire the bullet after a specified time
     ********************************************/
    public void countdownComplete(Object payload)
    {
        Participant.expire(this);
    }
}