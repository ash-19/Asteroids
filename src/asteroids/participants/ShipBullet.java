package asteroids.participants;

import asteroids.Participant;
import asteroids.destroyers.*;

/**
 * Represents the bullet from ship that destroys
 * asteroids or alien ship
 */
public class ShipBullet extends Bullet
    implements AsteroidDestroyer, AlienShipDestroyer
{

	/**
	 * Customize or call the base bullet constructor
	 */
    public ShipBullet(double x, double y, double direction)
    {
        super(x, y, direction);
    }

    /**
     * If collides with an object that a bullet can destroy,
     * expire the bullet
     */
    public void collidedWith(Participant p)
    {
        if(p instanceof ShipBulletDestroyer)
            Participant.expire(this);
    }
}