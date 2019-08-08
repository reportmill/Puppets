package puppets.puppet;
import java.util.*;
import snap.gfx.*;
import snap.util.*;
import snap.view.ViewUtils;

/**
 * A class to hold information providing image parts of a graphic of a human.
 */
public class Puppet {
    
    // The source of puppet
    Object                   _source;
    
    // The puppet name
    String                   _name;
    
    // The description of puppet parts and joints
    PuppetSchema             _schema = new PuppetSchema();
    
    // Cached parts
    Map <String,PuppetPart>  _parts = new HashMap();
    
    // Cached joints
    Map <String,PuppetJoint> _joints = new HashMap();
    
    // The bounds
    Rect                     _bounds;
    
    // Whether the image is loaded
    Boolean                  _loaded;
     
    // PropertyChangeSupport
    PropChangeSupport        _loadLsnrs;
    
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
 * Returns the puppet name.
 */
public String getName()  { return _name; }

/**
 * Sets the puppet name.
 */
public void setName(String aName)  { _name = aName; }

/**
 * Returns the schema.
 */
public PuppetSchema getSchema()  { return _schema; }

/**
 * Returns the part for given name.
 */
public PuppetPart getPart(String aName)
{
    // Get cached part (just return if found)
    PuppetPart part = _parts.get(aName); if(part!=null) return part;
    
    // Try to create part
    part = createPart(aName);
    if(part==null)
        part = createDerivedPart(aName);
    if(part==null) {
        System.out.println("Puppet.getPart: part not found " + aName); return null; }
    
    // Add part to cache and return
    _parts.put(aName, part);
    return part;
}

/**
 * Returns the part for given name.
 */
protected PuppetPart createPart(String aName)  { return null; }

/**
 * Tries to create a missing part from an existing/composite part.
 */
protected PuppetPart createDerivedPart(String aName)  { return PuppetPart.createDerivedPart(this, aName); }

/**
 * Returns the joint for given name.
 */
public PuppetJoint getJoint(String aName)
{
    // Get cached joint (just return if found)
    PuppetJoint joint = _joints.get(aName); if(joint!=null) return joint;
    
    // Try to create joint
    joint = createJoint(aName);
    if(joint==null) {
        System.out.println("Puppet.getJoint: part not found " + aName); return null; }
    
    // Add joint to cache and return
    _joints.put(aName, joint);
    return joint;
}

/**
 * Returns the joint for given name.
 */
protected PuppetJoint createJoint(String aName)  { return null; }

/**
 * Returns the puppet part names in paint order.
 */
public String[] getPartNames()  { return _schema.getPartNames(); }

/**
 * Returns the puppet joint names.
 */
public String[] getJointNames()  { return _schema.getJointNames(); }

/**
 * Returns the puppet joint names.
 */
public String[] getRootJointNames()  { return _schema.getRootJointNames(); }

/**
 * Returns the puppet marker names.
 */
public String[] getMarkerNames()  { return _schema.getMarkerNames(); }

/**
 * Returns the puppet joint and marker names that define a pose for puppet.
 */
public String[] getPoseKeys()  { return _schema.getPoseKeys(); }

/**
 * Returns names of parts linked to given joint/marker name.
 */
public String[] getLinkNamesForJointOrMarker(String aName)  { return _schema.getLinkNamesForJointOrMarker(aName); }

/**
 * Returns names of parts linked to given joint/marker name.
 */
public String[] getOuterJointNamesForPartName(String aName)  { return _schema.getOuterJointNamesForPartName(aName); }

/**
 * Returns names of parts linked to given joint/marker name.
 */
public String getNextJointNameForName(String aName)  { return _schema.getNextJointNameForName(aName); }

/**
 * Returns whether given name is marker name.
 */
public boolean isMarkerName(String aName)  { return _schema.isMarkerName(aName); }

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
        PuppetPart part = getPart(pname);
        x = Math.min(x, part.getX());
        y = Math.min(y, part.getY());
        mx = Math.max(mx, part.getX() + part.getImage().getWidth());
        my = Math.max(my, part.getY() + part.getImage().getHeight());
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
    String names[] = { RArm, RHand, RLeg, RFoot, Torso, Head, LLeg, LFoot, LArm, LHand };
    for(String name : names) Collections.addAll(images, getPart(name).getLoadImages());
    return images.toArray(new Image[images.size()]);
}

}