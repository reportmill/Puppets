package puppets.puppet;
import java.util.*;
import snap.gfx.*;
import snap.util.*;

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
 * Sets a part.
 */
public void setPart(PuppetPart aPart)
{
    _parts.put(aPart.getName(), aPart);
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
protected Loadable getLoadable()
{
    String names[] = { PuppetSchema.RArm, PuppetSchema.RHand, PuppetSchema.RLeg, PuppetSchema.RFoot, PuppetSchema.Torso,
        PuppetSchema.Head, PuppetSchema.LLeg, PuppetSchema.LFoot, PuppetSchema.LArm, PuppetSchema.LHand };
    PuppetPart parts[] = new PuppetPart[names.length];
    for(int i=0;i<names.length;i++) parts[i] = getPart(names[i]);
    return Loadable.getAsLoadable(parts);
}

/**
 * Returns a puppet for given source.
 */
public static Puppet getPuppetForSource(Object aSource)
{
    if(aSource instanceof String) { String src = (String)aSource;
        if(src.equals("Man")) src = PuppetUtils.ROOT + "chars/CTMan";
        if(src.equals("Lady")) src = PuppetUtils.ROOT + "chars/CTLady";
        return new ORAPuppet(src);
    }
    
    // Handle unknown source
    System.err.println("Puppet.getPuppetForSource: Unknown source " + aSource);
    return null;
}

}