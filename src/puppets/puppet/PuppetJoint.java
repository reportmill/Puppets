package puppets.puppet;
import snap.gfx.*;
import snap.util.*;

/**
 * A class to represent a location in a puppet that binds parts together or marks a location.
 */
public class PuppetJoint {

    // The name of the part
    String        _name;
    
    // The location of the part
    double        _x, _y;
    
    // The image
    Image         _img;

    // The Puppet that owns this joint
    Puppet        _puppet;

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
 * Returns the puppet X.
 */
public double getMidX()  { return _x + getImage().getWidth()/2; }

/**
 * Returns the puppet Y.
 */
public double getMidY()  { return _y + getImage().getHeight()/2; }

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
    if(_name==PuppetSchema.Anchor_Joint) return PuppetUtils.getAnchorImage();
    return PuppetUtils.getMarkerImage();
}

/**
 * Returns the bounds.
 */
public Rect getBounds()  { return new Rect(_x, _y, getImage().getWidth(), getImage().getHeight()); }

/**
 * Returns the puppet that owns this joint.
 */
public Puppet getPuppet()  { return _puppet; }

/**
 * Returns the next joint.
 */
public PuppetJoint getNext()
{
    String nextName = getPuppet().getSchema().getNextJointNameForName(getName());
    PuppetJoint next = nextName!=null? getPuppet().getJoint(nextName) : null;
    return next;
}

/**
 * XML Archival.
 */
public XMLElement toXML(XMLArchiver anArchiver)
{
    // Get new element with part name
    XMLElement e = new XMLElement("Part");
    e.add("Name", getName());
    
    // Write bounds
    Rect bnds = getBounds();
    e.add("X", StringUtils.formatNum("#.##", bnds.getMidX()));
    e.add("Y", StringUtils.formatNum("#.##", bnds.getMidY()));
        
    // Return element
    return e;
}

/**
 * XML unarchival.
 */
public PuppetJoint fromXML(XMLElement anElement)
{
    // Unarchive name
    String name = anElement.getAttributeValue("Name");
    setName(name);
    
    // Unarchive bounds
    double x = anElement.getAttributeDoubleValue("X");
    double y = anElement.getAttributeDoubleValue("Y");
    
    // Unarchive Image
    _img = PuppetUtils.getMarkerImage();
    if(name.equals(PuppetSchema.Anchor_Joint)) _img = PuppetUtils.getAnchorImage();
    _x = x - _img.getWidth()/2;
    _y = y - _img.getHeight()/2;
        
    // Return this
    return this;
}

/**
 * Standard toString implementation.
 */
public String toString()  { return "Joint: name=" + _name + ", x=" + _x + ", y=" + _y; }

}