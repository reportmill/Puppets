package puppets.puppet;
import java.util.*;
import snap.gfx.*;
import snap.util.*;
import snap.web.PathUtils;
import snap.web.WebURL;

/**
 * A class to hold information providing image parts of a graphic of a human.
 */
public class Puppet {
    
    // The source of puppet
    Object                   _source;
    
    // The puppet name
    String                   _name;
    
    // The puppet path
    String                   _path;
    
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
 * Returns the puppet path.
 */
public String getPath()
{
    if(_path!=null) return _path;
    _path = "chars/" + _name + '/' + _name + ".pup";
    return _path;
}

/**
 * Sets the puppet path.
 */
public void setPath(String aPath)  { _path = aPath; }

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
    setPart(part);
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
 * Sets a part.
 */
public void setPart(PuppetPart aPart)
{
    _parts.put(aPart.getName(), aPart);
    if(aPart._puppet==null) aPart._puppet = this;
}

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
    setJoint(joint);
    return joint;
}

/**
 * Returns the joint for given name.
 */
protected PuppetJoint createJoint(String aName)  { return null; }

/**
 * Sets a joint.
 */
public void setJoint(PuppetJoint aJoint)
{
    _joints.put(aJoint.getName(), aJoint);
    if(aJoint._puppet==null) aJoint._puppet = this;
}

/**
 * Returns the puppet part names in paint order.
 */
public String[] getPartNames()  { return _schema.getPartNames(); }

/**
 * Returns the puppet joint names.
 */
public String[] getJointNames()  { return _schema.getJointNames(); }

/**
 * Returns the parts.
 */
public PuppetPart[] getParts()
{
    String names[] = getSchema().getPartNamesNaturalOrder();
    PuppetPart parts[] = new PuppetPart[names.length];
    for(int i=0;i<names.length;i++) parts[i] = getPart(names[i]);
    return parts;
}

/**
 * Returns the joints.
 */
public PuppetJoint[] getJoints()
{
    String names[] = getSchema().getJointNamesNaturalOrder();
    PuppetJoint joints[] = new PuppetJoint[names.length];
    for(int i=0;i<names.length;i++) joints[i] = getJoint(names[i]);
    return joints;
}

/**
 * Returns the mother parts.
 */
public PuppetPart[] getMotherParts()
{
    PuppetPart parts[] = getParts();
    return null;
}

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
    //String names[] = { PuppetSchema.RArm, PuppetSchema.RHand, PuppetSchema.RLeg, PuppetSchema.RFoot, PuppetSchema.Torso,
    //    PuppetSchema.Head, PuppetSchema.LLeg, PuppetSchema.LFoot, PuppetSchema.LArm, PuppetSchema.LHand };
    //PuppetPart parts[] = new PuppetPart[names.length];
    //for(int i=0;i<names.length;i++) parts[i] = getPart(names[i]);
    PuppetPart parts[] = getParts();
    return Loadable.getAsLoadable(parts);
}

/**
 * Reads the puppet.
 */
public void readSource(String aPath)
{
    // Get file string as XMLElement
    WebURL url = WebURL.getURL(aPath);
    String fileStr = url.getText(); if(fileStr==null) System.err.println("Puppet.readSource: File not found: " + aPath);
    XMLElement puppetXML = XMLElement.getElement(url);
        
    // Read puppet
    fromXML(puppetXML);
    
    // Set source for parts
    String dirPath = PathUtils.getParent(aPath);
    for(PuppetPart part : getParts())
        part._isrc = dirPath + '/' + part.getName() + ".png";
}

/**
 * Saves the puppet.
 */
public void save()
{
    if(SnapUtils.isTeaVM) return;
    
    // Create dir
    String path = PuppetUtils.ROOT + getPath();
    String dirPath = PathUtils.getParent(path);
    java.io.File dir = FileUtils.getDirectoryForSource(dirPath, true);
    if(dir==null) { System.err.println("Puppet.save: Failed"); return; }
    
    // Create element for puppet, get as bytes and write to file
    XMLElement puppetXML = toXML(null);
    byte bytes[] = puppetXML.getBytes();
    SnapUtils.writeBytes(bytes, path);
    
    // Write images
    for(PuppetPart part : getParts()) {
        Image img = part.getImage();
        String iname = part.getImageName();
        String ipath = PathUtils.getChild(dirPath, iname);
        byte ibytes[] = img.getBytesPNG();
        SnapUtils.writeBytes(ibytes, ipath);
    }
}

/**
 * XML Archival.
 */
public XMLElement toXML(XMLArchiver anArchiver)
{
    // Get new element with puppet Name, Path
    XMLElement e = new XMLElement("Puppet");
    e.add("Name", getName());
    e.add("Path", getPath());
    
    // Create element for parts and iterate over poses and add each
    XMLElement partsXML = new XMLElement("Parts"); e.add(partsXML);
    for(PuppetPart part : getParts()) {
        XMLElement partXML = part.toXML(anArchiver);
        partsXML.add(partXML);
    }
    
    // Create element for joints and iterate over joints and add each
    XMLElement jointsXML = new XMLElement("Joints"); e.add(jointsXML);
    for(PuppetJoint joint : getJoints()) {
        XMLElement jointXML = joint.toXML(anArchiver);
        jointsXML.add(jointXML);
    }
    
    // Return element
    return e;
}

/**
 * XML unarchival.
 */
public Puppet fromXML(XMLElement anElement)
{
    // Unarchive Name, Path
    String name = anElement.getAttributeValue("Name");
    setName(name);
    String path = anElement.getAttributeValue("Path");
    setPath(path);
    
    // Iterate over parts element and load
    XMLElement partsXML = anElement.getElement("Parts");
    for(XMLElement partXML : partsXML.getElements()) {
        PuppetPart part = new PuppetPart().fromXML(partXML);
        setPart(part);
    }

    // Iterate over joints element and load
    XMLElement jointsXML = anElement.getElement("Joints");
    for(XMLElement jointXML : jointsXML.getElements()) {
        PuppetJoint joint = new PuppetJoint().fromXML(jointXML);
        setJoint(joint);
    }

    // Return this
    return this;
}

/**
 * Returns a puppet for given source.
 */
public static Puppet getPuppetForSource(Object aSource)
{
    // Handle String
    if(aSource instanceof String) { String src = (String)aSource;
    
        // Handle old ORA puppets
        if(src.equals("Man") || src.equals("Lady")) {
            src = PuppetUtils.ROOT + "chars/CT" + src;
            return new ORAPuppet(src);
        }
        
        Puppet puppet = new Puppet();
        puppet.readSource(src);
        return puppet;
    }
    
    // Handle unknown source
    System.err.println("Puppet.getPuppetForSource: Unknown source " + aSource);
    return null;
}

}