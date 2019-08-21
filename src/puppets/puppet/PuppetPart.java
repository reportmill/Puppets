package puppets.puppet;
import snap.gfx.*;
import snap.util.*;
import snap.web.WebURL;

/**
 * A class to represent a part of a puppet.
 */
public class PuppetPart implements Loadable {
    
    // The Puppet
    Puppet        _puppet;

    // The name of the part
    String        _name;
    
    // The location of the part
    double        _x, _y;
    
    // The image
    Image         _img;
    
    // The image source
    Object        _isrc;
    
    // The original part
    PuppetPart    _origPart;
    
    // The mother part, if part was derived from another part
    PuppetPart    _motherPart;
    
/**
 * Creates a PuppetPart.
 */
public PuppetPart()  { }

/**
 * Creates a PuppetPart.
 */
public PuppetPart(String aName, Image anImage, double aX, double aY)
{
    setName(aName); setImage(anImage); _x = aX; _y = aY;
}

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
 * Returns the bounds.
 */
public Rect getBounds()  { return new Rect(_x, _y, getImage().getWidth(), getImage().getHeight()); }

/**
 * Returns the scale.
 */
public double getScale()  { return _origPart!=null? getBounds().width/_origPart.getBounds().width : 1; }

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
    // Get file string as XMLElement
    WebURL url = WebURL.getURL(_isrc);
    if(url==null) System.err.println("PuppetPart.getImage: Source not found: " + _isrc);
    Image img = Image.get(_isrc);
    return img;
}

/**
 * Returns the image name.
 */
public String getImageName()
{
    String name = getName();
    String type = getImage().getType().toLowerCase();
    return name + '.' + type;
}

/**
 * Returns the mother part, if this part was derived from another.
 */
public PuppetPart getMotherPart()  { return _motherPart; }

/**
 * Returns whether resource is loaded.
 */
public boolean isLoaded()  { return getLoadable().isLoaded(); }

/**
 * Adds a callback to be triggered when resources loaded (cleared automatically when loaded).
 */
public void addLoadListener(Runnable aRun)  { getLoadable().addLoadListener(aRun); }

/**
 * Returns the default loadable (the image).
 */
protected Loadable getLoadable()  { return getImage(); }

/**
 * Creates a clone with given image.
 */
public PuppetPart cloneForImage(Image anImage)
{
    // Create new version of image for current size
    Image img = getImage();
    int pw = (int)img.getWidth(), ph = (int)img.getHeight();
    Image img2 = Image.get(pw, ph, true);
    Painter pntr = img2.getPainter(); pntr.drawImage(anImage, 0, 0, img2.getWidth(), img2.getHeight());
    
    // Create new part for image and return
    PuppetPart part2 = new PuppetPart(getName(), img2, getX(), getY());
    return part2;
}

/**
 * Creates a clone with given scale.
 */
public PuppetPart cloneForScale(double aScale)
{
    PuppetPart part1 = _origPart!=null? _origPart : this;
    Image img = part1.getImage();
    int w1 = (int)img.getWidth(), h1 = (int)img.getHeight();
    int w2 = (int)Math.round(w1*aScale), h2 = (int)Math.round(h1*aScale);
    Image img2 = Image.get(w2, h2, true);
    Painter pntr = img2.getPainter(); pntr.drawImage(img, 0, 0, img2.getWidth(), img2.getHeight());
    Rect bnds1 = part1.getBounds();
    double x2 = Math.round(bnds1.x - (img2.getWidth() - bnds1.width)/2);
    double y2 = Math.round(bnds1.y - (img2.getHeight() - bnds1.height)/2);
    
    PuppetPart part2 = new PuppetPart(getName(), img2, x2, y2);
    part2._origPart = _origPart!=null? _origPart : this;
    return part2;
}

/**
 * Tries to create a missing part from an existing/composite part.
 */
static PuppetPart createDerivedPart(Puppet aPuppet, String aName)
{
    if(aName==PuppetSchema.RArmTop || aName==PuppetSchema.RArmBtm)
        return PuppetPart.splitPartAroundJoint(aPuppet, PuppetSchema.RArm, PuppetSchema.RArmMid_Joint, aName);
    if(aName==PuppetSchema.RLegTop || aName==PuppetSchema.RLegBtm)
        return PuppetPart.splitPartAroundJoint(aPuppet, PuppetSchema.RLeg, PuppetSchema.RLegMid_Joint, aName);
    if(aName==PuppetSchema.LArmTop || aName==PuppetSchema.LArmBtm)
        return PuppetPart.splitPartAroundJoint(aPuppet, PuppetSchema.LArm, PuppetSchema.LArmMid_Joint, aName);
    if(aName==PuppetSchema.LLegTop || aName==PuppetSchema.LLegBtm)
        return PuppetPart.splitPartAroundJoint(aPuppet, PuppetSchema.LLeg, PuppetSchema.LLegMid_Joint, aName);
    return null;
}

/**
 * Splits a part around joint - for when given arm/leg as one piece instead of top/bottom.
 */
static PuppetPart splitPartAroundJoint(Puppet aPuppet, String aPartName, String aJointName, String aName2)
{
    boolean isTop = aName2.contains("Top");
    PuppetPart part = aPuppet.getPart(aPartName);
    if(part==null) { System.err.println("ORAPuppet.splitPart: Part not found " + aPartName); return null; }
    PuppetJoint joint = aPuppet.getJoint(aJointName);
    if(joint==null) { System.err.println("ORAPuppet.splitView: Joint not found " + aJointName); return null; }
    
    Rect pbnds = getSplitBoundsForView(part, joint, isTop);
    Rect ibnds = new Rect(pbnds.x - part.getX(), pbnds.y - part.getY(), pbnds.width, pbnds.height);
    
    Image img = part.getImage();
    Image img1 = img.getSubimage(ibnds.x, ibnds.y, ibnds.width, ibnds.height);
    
    // Create/add new parts
    PuppetPart np = new PuppetPart(); np.setName(aName2); np._x = pbnds.x; np._y = pbnds.y; np._img = img1;
    np._motherPart = part;
    return np;
}

/**
 * Returns the partial rect when splitting an arm/leg joint in two around joint for above method.
 */
static Rect getSplitBoundsForView(PuppetPart aPart, PuppetJoint aJoint, boolean doTop)
{
    // Get part and joint bounds
    Rect pbnds = aPart.getBounds(), jbnds = aJoint.getBounds();
    double x = pbnds.x, y = pbnds.y, w = pbnds.width, h = pbnds.height, asp = w/h;
    
    // Handle horizontal arm/let
    if(asp<.3333) {
        if(doTop) h = jbnds.getMaxY() - y;
        else { y = jbnds.y; h = pbnds.getMaxY() - y; }
    }
    
    // Handle diagonal arm/leg
    else if(asp<3) {
        
        // Handle Right arm/leg
        if(aPart.getName().startsWith("R")) {
            if(doTop) { x = jbnds.x; w = pbnds.getMaxX() - x; h = jbnds.getMaxY() - y; }
            else { y = jbnds.y; w = jbnds.getMaxX() - x; h = pbnds.getMaxY() - y; }
        }
        
        // Handle Left arm/leg
        else {
            if(doTop) { w = jbnds.getMaxX() - x; h = jbnds.getMaxY() - y; }
            else { x = jbnds.x; y = jbnds.y; w = pbnds.getMaxX() - x; h = pbnds.getMaxY() - y; }
        }
    }
    
    // Handle vertial arm/leg
    else {
        if(doTop) w = jbnds.getMaxX() - x;
        else { x = jbnds.x; w = pbnds.getMaxX() - x; }
    }
    
    // Return rect
    return new Rect(x, y, w, h);
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
    e.add("X", StringUtils.formatNum("#.##", bnds.x));
    e.add("Y", StringUtils.formatNum("#.##", bnds.y));
    e.add("Width", StringUtils.formatNum("#.##", bnds.width));
    e.add("Height", StringUtils.formatNum("#.##", bnds.height));
    
    // Write ImageName
    String iname = getImageName();
    e.add("Image", iname);
        
    // Return element
    return e;
}

/**
 * XML unarchival.
 */
public PuppetPart fromXML(XMLElement anElement)
{
    // Unarchive name
    String name = anElement.getAttributeValue("Name");
    setName(name);
    
    // Unarchive bounds
    double x = anElement.getAttributeDoubleValue("X");
    double y = anElement.getAttributeDoubleValue("Y");
    double w = anElement.getAttributeDoubleValue("Width");
    double h = anElement.getAttributeDoubleValue("Height");
    _x = x;
    _y = y;
    
    // Unarchive Image
    String iname = anElement.getAttributeValue("Image");
        
    // Return this
    return this;
}

/**
 * Standard toString implementation.
 */
public String toString()  { return "Part: name=" + _name + ", x=" + _x + ", y=" + _y; }

}