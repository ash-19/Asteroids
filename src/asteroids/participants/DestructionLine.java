package asteroids.participants;

import asteroids.*;
import java.awt.Shape;
import java.awt.geom.Path2D;

/**********************************************************************
 * Represents the destruction lines of participants when destroyed
 * (line debris)
 **********************************************************************/
public class DestructionLine extends Participant
{
    private Shape outline;		// The shape of the debris/destruction line
    
	/**********************************************************************
	 * Constructs one line debris at (x,y) coord
	 * of passed length
	 **********************************************************************/
    public DestructionLine(double x, double y, double length)
    {
        double noise = Constants.RANDOM.nextDouble() * 10D - 5D;
        Path2D.Double line = new Path2D.Double();
        line.moveTo(0.0D, -length / 2D);
        line.lineTo(0.0D, length / 2D);
        setRotation(2* Math.PI * Constants.RANDOM.nextDouble());
        setPosition(x + noise, y + noise);
        setVelocity(Constants.RANDOM.nextDouble(), Constants.RANDOM.nextDouble() * 2 * Math.PI);
        outline = line;
        new ParticipantCountdownTimer(this, this, 1500 + (int)(Constants.RANDOM.nextDouble() * 500D));
    }

    /**********************************************************************
     * Returns the shape of the line debris
     **********************************************************************/
    protected Shape getOutline()
    {
        return outline;
    }

    /**********************************************************************
     * Expires the line after a certain amount of time
     **********************************************************************/
    public void countdownComplete(Object payload)
    {
        Participant.expire(this);
    }

    /**********************************************************************
     * Ignore
     **********************************************************************/
    public void collidedWith(Participant participant)
    {
    }
}