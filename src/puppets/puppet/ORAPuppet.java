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
    String lname = getLayerNameForPuppetName(aName);
    Layer layer = getLayerForPartName(lname); if(layer==null) return null;
    return new ORAPart(aName, layer);
}

/**
 * Returns the joint for given name.
 */
protected PuppetJoint createJoint(String aName)
{
    String lname = getLayerNameForPuppetName(aName);
    Layer layer = getLayerForJointName(lname); if(layer==null) return null;
    return new PuppetJoint(aName, layer.x, layer.y);
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
 * A PuppetPart subclass for ORAPuppet.
 */
private class ORAPart extends PuppetPart {
    
    Layer  _lyr;
    
    /** Creates an ORAPart for given layer. */
    public ORAPart(String aName, Layer aLayer)  { _name = aName; _lyr = aLayer; _x = aLayer.x; _y = aLayer.y; }
        
    /** Returns the image. */
    protected Image getImageImpl()  { return _lyr.getImage(); }
    
    /** Returns the images that need to be loaded for this part. */
    public Image[] getLoadImages()  { return _lyr!=null? _lyr.getLoadImages() : super.getLoadImages(); }
}

}