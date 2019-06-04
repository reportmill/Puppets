package puppets;
import java.util.*;
import snap.gfx.*;
import snap.util.*;
import snap.view.ViewUtils;

/**
 * A class to hold information providing image parts of a graphic of a human.
 */
public class Puppet {
    
    // The source of puppet
    Object              _source;
    
    // Cached parts
    Map <String,Part>   _parts = new HashMap();
    
    // Cached joints
    Map <String,Part>   _joints = new HashMap();
    
    // The bounds
    Rect                _bounds;
    
    // Whether the image is loaded
    Boolean             _loaded;
     
    // PropertyChangeSupport
    PropChangeSupport   _loadLsnrs;

    // Constants for human parts
    public static final String Torso = "Torso";
    public static final String Head = "Head";
    public static final String RArm = "RArm";
    public static final String RArmTop = "RArmTop";
    public static final String RArmBtm = "RArmBtm";
    public static final String RHand = "RHand";
    public static final String RLeg = "RLeg";
    public static final String RLegTop = "RLegTop";
    public static final String RLegBtm = "RLegBtm";
    public static final String RFoot = "RFoot";
    public static final String LArm = "LArm";
    public static final String LArmTop = "LArmTop";
    public static final String LArmBtm = "LArmBtm";
    public static final String LHand = "LHand";
    public static final String LLeg = "LLeg";
    public static final String LLegTop = "LLegTop";
    public static final String LLegBtm = "LLegBtm";
    public static final String LFoot = "LFoot";
    
    // Constants for human joints
    public static final String Head_Joint = "HeadJoint";
    public static final String RArm_Joint = "RArmJoint";
    public static final String RArmMid_Joint = "RArmMidJoint";
    public static final String RHand_Joint = "RHandJoint";
    public static final String RLeg_Joint = "RLegJoint";
    public static final String RLegMid_Joint = "RLegMidJoint";
    public static final String RFoot_Joint = "RFoot_Joint";
    public static final String LArm_Joint = "LArmJoint";
    public static final String LArmMid_Joint = "LArmMidJoint";
    public static final String LHand_Joint = "LHandJoint";
    public static final String LLeg_Joint = "LLegJoint";
    public static final String LLegMid_Joint = "LLegMidJoint";
    public static final String LFoot_Joint = "LFoot_Joint";
    
    // Constants for markers
    public static final String Anchor_Marker = "AnchorMarker";
    public static final String HeadTop_Marker = "HeadTopMarker";
    public static final String RHandEnd_Marker = "RHandEndMarker";
    public static final String RFootEnd_Marker = "RFootEndMarker";
    public static final String LHandEnd_Marker = "LHandEndMarker";
    public static final String LFootEnd_Marker = "LFootEndMarker";

    // Constants for properties
    public static final String Loaded_Prop = "Loaded";

/**
 * Returns the source.
 */
public Object getSource()  { return _source; }

/**
 * Sets the source.
 */
public void setSource(Object aSource)  { _source = aSource; }

/**
 * Returns the part for given name.
 */
public Part getPart(String aName)
{
    Part part = _parts.get(aName);
    if(part==null) _parts.put(aName, part = createPart(aName));
    return part;
}

/**
 * Returns the part for given name.
 */
protected Part createPart(String aName)  { return null; }

/**
 * Returns the joint for given name.
 */
public Part getJoint(String aName)
{
    Part joint = _joints.get(aName);
    if(joint==null) _joints.put(aName, joint = createJoint(aName));
    return joint;
}

/**
 * Returns the joint for given name.
 */
protected Part createJoint(String aName)  { return null; }

/**
 * Returns the puppet part names in paint order.
 */
public String[] getPartNames()
{
    return new String[] {
        RArmTop, RArmBtm, RHand, RLegTop, RLegBtm, RFoot, Torso, Head,
        LLegTop, LLegBtm, LFoot, LArmTop, LArmBtm, LHand };
}

/**
 * Returns the puppet joint names.
 */
public String[] getJointNames()
{
    return new String[] { Head_Joint,
        RArm_Joint, RArmMid_Joint, RHand_Joint, RLeg_Joint, RLegMid_Joint, RFoot_Joint,
        LArm_Joint, LArmMid_Joint, LHand_Joint, LLeg_Joint, LLegMid_Joint, LFoot_Joint };
}

/**
 * Returns the puppet marker names.
 */
public String[] getMarkerNames()
{
    return new String[] { Anchor_Marker, HeadTop_Marker,
        RHandEnd_Marker, RFootEnd_Marker, LHandEnd_Marker, LFootEnd_Marker };
}

/**
 * Returns the puppet joint and marker names that define a pose for puppet.
 */
public String[] getPoseKeys()
{
    return new String[] { Puppet.HeadTop_Marker, Puppet.Head_Joint,
        Puppet.RArm_Joint, Puppet.RArmMid_Joint, Puppet.RHand_Joint, Puppet.RHandEnd_Marker,
        Puppet.RLeg_Joint, Puppet.RLegMid_Joint, Puppet.RFoot_Joint, Puppet.RFootEnd_Marker,
        Puppet.LArm_Joint, Puppet.LArmMid_Joint, Puppet.LHand_Joint, Puppet.LHandEnd_Marker,
        Puppet.LLeg_Joint, Puppet.LLegMid_Joint, Puppet.LFoot_Joint, Puppet.LFootEnd_Marker };
}

/**
 * Returns names of parts linked to given joint/marker name.
 */
public String[] getLinkNamesForJointOrMarker(String aName)
{
    switch(aName) {
        
        // Joints
        case Head_Joint: return new String[] { Head, Torso };
        case RArm_Joint: return new String[] { Torso, RArmTop };
        case RArmMid_Joint: return new String[] { RArmTop, RArmBtm };
        case RHand_Joint: return new String[] { RArmBtm, RHand };
        case RLeg_Joint: return new String[] { Torso, RLegTop };
        case RLegMid_Joint: return new String[] { RLegTop, RLegBtm };
        case RFoot_Joint: return new String[] { RLegBtm, RFoot };
        case LArm_Joint: return new String[] { Torso, LArmTop };
        case LArmMid_Joint: return new String[] { LArmTop, LArmBtm };
        case LHand_Joint: return new String[] { LArmBtm, LHand };
        case LLeg_Joint: return new String[] { Torso, LLegTop };
        case LLegMid_Joint: return new String[] { LLegTop, LLegBtm };
        case LFoot_Joint: return new String[] { LLegBtm, LFoot };
        
        // Markers
        case HeadTop_Marker: return new String[] { Head };
        case RHandEnd_Marker: return new String[] { RHand };
        case RFootEnd_Marker: return new String[] { RFoot };
        case LHandEnd_Marker: return new String[] { LHand };
        case LFootEnd_Marker: return new String[] { LFoot };
        default: return new String[0];
    }
}

/**
 * Returns names of parts linked to given joint/marker name.
 */
public String[] getOuterJointNamesForPartName(String aName)
{
    switch(aName) {
        case RArmTop: return new String[] { RArmMid_Joint, RHand_Joint };
        case RArmBtm: return new String[] { RHand_Joint };
        case RLegTop: return new String[] { RLegMid_Joint, RFoot_Joint };
        case RLegBtm: return new String[] { RFoot_Joint };
        case LArmTop: return new String[] { LArmMid_Joint, LHand_Joint };
        case LArmBtm: return new String[] { LHand_Joint };
        case LLegTop: return new String[] { LLegMid_Joint, LFoot_Joint };
        case LLegBtm: return new String[] { LFoot_Joint };
        default: return new String[0];
    }
}

/**
 * Returns whether given name is joint name.
 */
public boolean isJointName(String aName)  { return ArrayUtils.contains(getJointNames(), aName); }

/**
 * Returns whether given name is marker name.
 */
public boolean isMarkerName(String aName)  { return ArrayUtils.contains(getMarkerNames(), aName); }

/**
 * Returns whether given name is marker name.
 */
public boolean isJointOrMarkerName(String aName)  { return isJointName(aName) || isMarkerName(aName); }

/**
 * Returns the bounds.
 */
public Rect getBounds()
{
    // If already set, just return
    if(_bounds!=null) return _bounds;
    
    // Iterate over parts and expand bounds
    double x = Float.MAX_VALUE, y = x, mx = -Float.MAX_VALUE, my = mx;
    for(String pname : getPartNames()) {
        Part part = getPart(pname);
        x = Math.min(x, part.x);
        y = Math.min(y, part.y);
        mx = Math.max(mx, part.x + part.getImage().getWidth());
        my = Math.max(my, part.y + part.getImage().getHeight());
    }
    
    // Return rect
    return _bounds = new Rect(x, y, mx - x, my - y);
}

/**
 * Returns whether puppet is loaded.
 */
public boolean isLoaded()
{
    if(_loaded==null) _loaded = isLoadedDefault();
    return _loaded;
}

/**
 * Sets whether image is loaded.
 */
protected void setLoaded(boolean aValue)
{
    if(aValue==_loaded) return;
    _loaded = aValue;
    if(aValue && _loadLsnrs!=null) {
        _loadLsnrs.firePropChange(new PropChange(this, Loaded_Prop, false, true)); _loadLsnrs = null; }
}

/**
 * Adds a load listener. This is cleared automatically when image is loaded.
 */
public void addLoadListener(PropChangeListener aLoadLsnr)
{
    if(isLoaded()) { aLoadLsnr.propertyChange(new PropChange(this, Loaded_Prop, false, true)); return; }
    if(_loadLsnrs==null) _loadLsnrs = new PropChangeSupport(this);
    _loadLsnrs.addPropChangeListener(aLoadLsnr);
}

/**
 * Returns whether puppet is loaded.
 */
boolean isLoadedDefault()
{
    Image images[] = getLoadImages();
    ImageLoader imgLdr = new ImageLoader(images);
    if(imgLdr.isLoaded()) return true;
    imgLdr.addLoadListener(pc -> ViewUtils.runLater(() -> setLoaded(true)));
    return false;
}

/**
 * Returns the images that need to load.
 */
protected Image[] getLoadImages()
{
    List <Image> images = new ArrayList();
    for(String name : getJointNames()) Collections.addAll(images, getJoint(name).getLoadImages());
    for(String name : getMarkerNames()) Collections.addAll(images, getJoint(name).getLoadImages());
    for(String name : getLoadPartNames()) Collections.addAll(images, getPart(name).getLoadImages());
    return images.toArray(new Image[images.size()]);
}

/**
 * Returns the puppet part names in paint order.
 */
String[] getLoadPartNames()
{
    return new String[] { RArm, RHand, RLeg, RFoot, Torso, Head, LLeg, LFoot, LArm, LHand };
}

/**
 * A class representing a part of the puppet.
 */
public static class Part {
    
    // The name of the part
    public String     name;
    
    // The location of the part
    public double     x, y;
    
    // The image
    Image      _img;
    
    /** Returns the image. */
    public Image getImage()  { return _img!=null? _img : (_img=getImageImpl()); }
    
    /** Returns the image. */
    protected Image getImageImpl()  { return null; }
    
    /** Returns the bounds. */
    public Rect getBounds()  { return new Rect(x, y, getImage().getWidth(), getImage().getHeight()); }
    
    /** Returns the images that need to be loaded for this part. */
    public Image[] getLoadImages()  { return new Image[0]; }
    
    /** Standard toString implementation. */
    public String toString()
    {
        return "Part: name=" + name + ", x=" + x + ", y=" + y;
    }
}

}