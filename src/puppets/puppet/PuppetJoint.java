package puppets.puppet;
import snap.gfx.*;

/**
 * A class to represent a location in a puppet that binds parts together or marks a location.
 */
public class PuppetJoint {

    // The name of the part
    String     _name;
    
    // The location of the part
    double     _x, _y;
    
    // The image
    Image      _img;

/**
 * Creates a PuppetJoint.
 */
public PuppetJoint()  { }

/**
 * Creates a PuppetJoint.
 */
public PuppetJoint(String aName, double aX, double aY)  { setName(aName); _x = aX; _y = aY; }

/**
 * Returns the name.
 */
public String getName()  { return _name; }

/**
 * Sets the name.
 */
public void setName(String aName)  { _name = aName; }

/**
 * Returns the puppet X.
 */
public double getX()  { return _x; }

/**
 * Returns the puppet Y.
 */
public double getY()  { return _y; }

/**
 * Returns the image.
 */
public Image getImage()  { return _img!=null? _img : (_img=getImageImpl()); }

/**
 * Sets the image.
 */
public void setImage(Image anImage)  { _img = anImage; }

/**
 * Returns the image.
 */
protected Image getImageImpl()
{
    if(_name==Puppet.Anchor_Marker) return PuppetUtils.getAnchorImage();
    return PuppetUtils.getMarkerImage();
}

/**
 * Returns the bounds.
 */
public Rect getBounds()  { return new Rect(_x, _y, getImage().getWidth(), getImage().getHeight()); }

/**
 * Standard toString implementation.
 */
public String toString()  { return "Joint: name=" + _name + ", x=" + _x + ", y=" + _y; }

}