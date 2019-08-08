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