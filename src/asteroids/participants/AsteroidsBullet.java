package asteroids.participants;

import asteroids.Participant;
import asteroids.destroyers.*;

/**********************************************************************
 * Implements the state of the bullets that destroy asteroids
 **********************************************************************/
public class AsteroidsBullet extends Bullet
    implements ShipDestroyer, AsteroidDestroyer
{

	/**********************************************************************
	 * Customize or call the base bullet constructor for asteroid debris
	 **********************************************************************/
    public AsteroidsBullet(double x, double y, double direction)
    {
        super(x, y, direction);
    }

    /**********************************************************************
     * If bullet collides with asteroid, destroy it
     **********************************************************************/
    public void collidedWith(Participant p)
    {
        if(p instanceof AsteroidBulletDestroyer)
            Participant.expire(this);
    }
}