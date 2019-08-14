package puppets.puppet;
import snap.gfx.*;

/**
 * A class to represent a part of a puppet.
 */
public class PuppetPart {

    // The name of the part
    String        _name;
    
    // The location of the part
    double        _x, _y;
    
    // The image
    Image         _img;
    
    // The original part
    PuppetPart    _origPart;
    
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
protected Image getImageImpl()  { return null; }

/**
 * Returns the images that need to be loaded for this part.
 */
public Image[] getLoadImages()  { return new Image[0]; }

/**
 * Returns the original part.
 */
PuppetPart getOrigPart()  { PuppetPart p = _origPart; while(p!=null && p._origPart!=null) p = p._origPart; return p; }

/**
 * Creates a clone with given image.
 */
public PuppetPart cloneForImage(Image anImage)
{
    // Create new version of image for current size
    int w = getImage().getPixWidth(), h = getImage().getPixHeight();
    Image img2 = Image.get(w, h, true); Painter pntr = img2.getPainter(); pntr.drawImage(anImage, 0, 0, w, h);
    
    // Create new part for image and return
    PuppetPart part2 = new PuppetPart(getName(), img2, getX(), getY());
    //part2._origPart = _origPart!=null? _origPart : this;
    return part2;
}

/**
 * Creates a clone with given scale.
 */
public PuppetPart cloneForScale(double aScale)
{
    PuppetPart part1 = _origPart!=null? _origPart : this;
    Image img = part1.getImage();
    int w1 = img.getPixWidth(), h1 = img.getPixHeight();
    int w2 = (int)Math.round(w1*aScale), h2 = (int)Math.round(h1*aScale);
    Image img2 = Image.get(w2, h2, true); Painter pntr = img2.getPainter(); pntr.drawImage(img, 0, 0, w2, h2);
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
 * Standard toString implementation.
 */
public String toString()  { return "Part: name=" + _name + ", x=" + _x + ", y=" + _y; }

}