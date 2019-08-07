package puppets.puppet;
import snap.gfx.*;
import puppets.puppet.ORAReader.Layer;
import puppets.puppet.ORAReader.Stack;
import snap.util.FilePathUtils;

/**
 * A Puppet subclass that reads from ORA (OpenRaster) file.
 */
public class ORAPuppet extends Puppet {

    // The stack of layers
    Stack      _stack;
    
    // The stack of body part layers
    Stack      _bodyStack;
    
    // The stack of layers
    Stack      _jointStack;
    
/**
 * Creates a PuppetView.
 */
public ORAPuppet(String aSource)
{
    setSource(aSource);
}

/**
 * Sets the source.
 */
public void setSource(String aPath)
{
    super.setSource(aPath);
    ORAReader rdr = new ORAReader();
    
    // Get name
    String name = FilePathUtils.getFileName(aPath); if(name.startsWith("CT")) name = name.substring(2);
    setName(name);
    
    // Get stack, body stack and joint stack
    _stack = rdr.readFile(aPath);
    _bodyStack = (Stack)getLayer("RL_Image");
    _jointStack = (Stack)getLayer("RL_Bone_Human");
}

/**
 * Returns the part for given name.
 */
protected PuppetPart createPart(String aName)
{
    // Get layer for part name
    String lname = getLayerNameForPuppetName(aName);
    Layer layer = getLayerForPartName(lname);
    if(layer==null) {
    
        if(aName==Puppet.RArmTop || aName==Puppet.RArmBtm)
            return splitPartAroundJoint(Puppet.RArm, Puppet.RArmMid_Joint, aName);
        if(aName==Puppet.RLegTop || aName==Puppet.RLegBtm)
            return splitPartAroundJoint(Puppet.RLeg, Puppet.RLegMid_Joint, aName);
        if(aName==Puppet.LArmTop || aName==Puppet.LArmBtm)
            return splitPartAroundJoint(Puppet.LArm, Puppet.LArmMid_Joint, aName);
        if(aName==Puppet.LLegTop || aName==Puppet.LLegBtm)
            return splitPartAroundJoint(Puppet.LLeg, Puppet.LLegMid_Joint, aName);
            
        System.out.println("ORAPuppet.createPart: Layer not found for part " + aName); return null;
    }
    
    // Create part
    PuppetPart part = new ORAPart(aName, layer);
    return part;
}

/**
 * Returns the joint for given name.
 */
protected PuppetPart createJoint(String aName)
{
    // Get layer for part name
    String lname = getLayerNameForPuppetName(aName);
    Layer layer = getLayerForJointName(lname);
    if(layer==null) { System.out.println("ORAPuppet.createJoint: Layer not found for joint " + aName); return null; }
    
    // Create part
    PuppetPart part = new ORAPart(aName, layer); part._img = aName==Anchor_Marker? getAnchorImage() : getMarkerImage();
    return part;
}

/**
 * Returns layer name for puppet part name.
 */
String getLayerNameForPuppetName(String aName)
{
    switch(aName) {
        
        // Parts
        case Puppet.Torso: return "Hip";
        case Puppet.Head: return "RL_TalkingHead";
        case Puppet.RArm: return "RArm";
        case Puppet.RArmTop: return "RArmTop";
        case Puppet.RArmBtm: return "RArmBtm";
        case Puppet.RHand: return "RHand";
        case Puppet.RLeg: return "RThigh";
        case Puppet.RLegTop: return "RLegTop";
        case Puppet.RLegBtm: return "RLegBtm";
        case Puppet.RFoot: return "RFoot";
        case Puppet.LArm: return "LArm";
        case Puppet.LArmTop: return "LArmTop";
        case Puppet.LArmBtm: return "LArmBtm";
        case Puppet.LHand: return "LHand";
        case Puppet.LLeg: return "LThigh";
        case Puppet.LLegTop: return "LLegTop";
        case Puppet.LLegBtm: return "LLegBtm";
        case Puppet.LFoot: return "LFoot";
        
        // Joints
        case Puppet.Head_Joint: return "Head";
        case Puppet.RArm_Joint: return "RArm";
        case Puppet.RArmMid_Joint: return "RForearm";
        case Puppet.RHand_Joint: return "RHand";
        case Puppet.RLeg_Joint: return "RThigh";
        case Puppet.RLegMid_Joint: return "RShank";
        case Puppet.RFoot_Joint: return "RFoot";
        case Puppet.LArm_Joint: return "LArm";
        case Puppet.LArmMid_Joint: return "LForearm";
        case Puppet.LHand_Joint: return "LHand";
        case Puppet.LLeg_Joint: return "LThigh";
        case Puppet.LLegMid_Joint: return "LShank";
        case Puppet.LFoot_Joint: return "LFoot";
        
        // Landmarks
        case Puppet.Anchor_Marker: return "ObjectPivot";
        case Puppet.HeadTop_Marker: return "Head_Nub";
        case Puppet.RHandEnd_Marker: return "RHand_Nub";
        case Puppet.RFootEnd_Marker: return "RToe";
        case Puppet.LHandEnd_Marker: return "LHand_Nub";
        case Puppet.LFootEnd_Marker: return "LToe";
        
        // Failure
        default: System.err.println("ORAPuppet.getLayerNameForPuppetName: failed for " + aName); return null;
    }
}

/**
 * Returns the layer for given name.
 */
public Layer getLayer(String aName)  { return getLayer(_stack, aName); }

/**
 * Returns the layer for given name.
 */
Layer getLayer(Layer aLayer, String aName)
{
    if(aLayer.name!=null && aLayer.name.equals(aName))
        return aLayer;
    if(aLayer instanceof Stack) { Stack stack = (Stack)aLayer;
        for(Layer l : stack.entries)
            if(getLayer(l, aName)!=null)
                return getLayer(l, aName);
    }
    return null;
}

/**
 * Returns the layer for given part name.
 */
public Layer getLayerForPartName(String aName)
{
    if(aName.equals("RL_TalkingHead"))
        return _stack.getLayer(aName);
    return _bodyStack.getLayer(aName);
}

/**
 * Returns the layer for given joint name.
 */
public Layer getLayerForJointName(String aName)  { return _jointStack.getLayer(aName); }

/**
 * Splits a part around joint - for when given arm/leg as one piece instead of top/bottom.
 */
PuppetPart splitPartAroundJoint(String aPartName, String aJointName, String aName2)
{
    boolean isTop = aName2.contains("Top");
    PuppetPart part = getPart(aPartName);
    if(part==null) { System.err.println("ORAPuppet.splitPart: Part not found " + aPartName); return null; }
    PuppetPart joint = getJoint(aJointName);
    if(joint==null) { System.err.println("ORAPuppet.splitView: Joint not found " + aJointName); return null; }
    
    Rect pbnds = getSplitBoundsForView(part, joint, isTop);
    Rect ibnds = new Rect(pbnds.x - part.getX(), pbnds.y - part.getY(), pbnds.width, pbnds.height);
    
    Image img = part.getImage();
    Image img1 = img.getSubimage(ibnds.x, ibnds.y, ibnds.width, ibnds.height);
    
    // Create/add new parts
    PuppetPart np = new ORAPart(aName2, null); np._x = pbnds.x; np._y = pbnds.y; np._img = img1;
    return np;
}

/**
 * Returns the partial rect when splitting an arm/leg joint in two around joint for above method.
 */
Rect getSplitBoundsForView(PuppetPart aPart, PuppetPart aJoint, boolean doTop)
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
 * A Puppet.Part subclass for ORAPuppet.
 */
private class ORAPart extends PuppetPart {
    
    Layer  _lyr;
    
    /** Creates an ORAPart for given layer. */
    public ORAPart(String aName, Layer aLayer)
    {
        _name = aName; _lyr = aLayer; if(aLayer==null) return;
        _x = aLayer.x; _y = aLayer.y;
    }
        
    /** Returns the image. */
    protected Image getImageImpl()  { return _lyr.getImage(); }
    
    /** Returns the images that need to be loaded for this part. */
    public Image[] getLoadImages()  { return _lyr!=null? _lyr.getLoadImages() : super.getLoadImages(); }
}

}