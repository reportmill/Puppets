package puppets.puppet;
import snap.gfx.*;

/**
 * A class to represent a part of a puppet.
 */
public class PuppetPart {

    // The name of the part
    String     _name;
    
    // The location of the part
    double     _x, _y;
    
    // The image
    Image      _img;

/**
 * Returns the name.
 */
public String getName()  { return _name; }

/**
 * Sets the name.
 */
public void setName(String aName)  { _name = aName; }

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
protected Image getImageImpl()  { return null; }

/**
 * Returns the puppet X.
 */
public double getX()  { return _x; }

/**
 * Returns the puppet Y.
 */
public double getY()  { return _y; }

/**
 * Returns the bounds.
 */
public Rect getBounds()  { return new Rect(_x, _y, getImage().getWidth(), getImage().getHeight()); }

/**
 * Returns the images that need to be loaded for this part.
 */
public Image[] getLoadImages()  { return new Image[0]; }

/**
 * Standard toString implementation.
 */
public String toString()  { return "Part: name=" + _name + ", x=" + _x + ", y=" + _y; }

}