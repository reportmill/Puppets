package puppets;
import java.util.*;
import snap.gfx.*;
import snap.util.*;
import snap.view.*;
import puppets.Puppet.Part;

/**
 * A View to display a puppet.
 */
public class PuppetView extends ParentView {
    
    // The puppet
    Puppet     _puppet;
    
    // The scale
    double     _scale = 1;

/**
 * Creates a PuppetView.
 */
public PuppetView()  { }

/**
 * Creates a PuppetView.
 */
public PuppetView(String aSource, double aScale)
{
    _scale = aScale;
    Puppet puppet = new ORAPuppet(aSource);
    setPuppet(puppet);
}

/**
 * Creates a PuppetView.
 */
public PuppetView(Puppet aPuppet, double aScale)
{
    _scale = aScale;
    setPuppet(aPuppet);
}

/**
 * Returns the puppet.
 */
public Puppet getPuppet()  { return _puppet; }

/**
 * Sets the puppet.
 */
protected void setPuppet(Puppet aPuppet)
{
    // Set puppet
    _puppet = aPuppet;
    
    // Add parts/joints
    addAllParts();
    
    // Set pref size to puppet size plus margin
    Rect bnds = _puppet.getBounds();
    double pw = bnds.getX()*2 + bnds.getWidth(); pw *= _scale;
    double ph = bnds.getY()*2 + bnds.getHeight(); ph *= _scale;
    setSize(pw, ph);
    setPrefSize(pw, ph);
}

/**
 * Adds all parts.
 */
void addAllParts()
{
    // Remove children
    removeChildren();
    
    // Iterate over parts
    for(String pname : _puppet.getPartNames()) {
        Part part = _puppet.getPart(pname);
        addImageViewForBodyPart(part);
    }
    
    // Iterate over joints
    for(String jname : _puppet.getJointNames()) {
        Part joint = _puppet.getJoint(jname);
        addImageViewForJoint(joint);
    }
    
    // Iterate over markers
    for(String pname : _puppet.getMarkerNames()) {
        Part part = _puppet.getJoint(pname);
        addImageViewForBodyPart(part);
    }
    
    // Make Torso really dense
    getChild(Puppet.Torso).getPhysics().setDensity(1000);
    
    // Resize children
    for(View c : getChildren())
        c.setBounds(c.getX()*_scale, c.getY()*_scale, c.getWidth()*_scale, c.getHeight()*_scale);
}

/**
 * Adds an image shape for given layer.
 */
ImageView addImageViewForPart(Part aPart)
{
    ImageView iview = new ImageView(aPart.getImage()); iview.setName(aPart.name);
    iview.setXY(aPart.x, aPart.y);
    iview.setSize(iview.getPrefSize());
    addChild(iview);
    return iview;
}

/**
 * Adds an image shape for body part.
 */
ImageView addImageViewForBodyPart(Part aPart)
{
    ImageView iview = addImageViewForPart(aPart);
    iview.getPhysics(true).setGroupIndex(-1);
    return iview;
}

/**
 * Adds an image shape for given joint.
 */
ImageView addImageViewForJoint(Part aPart)
{
    ImageView iview = addImageViewForPart(aPart);
    iview.getPhysics(true).setJoint(true);
    return iview;
}

/**
 * Returns a Map of Pose info.
 */
public String getPoseString()
{
    Map poseInfoMap = getPoseMap();
    JSONNode json = new JSONArchiver().writeObject(poseInfoMap);
    return json.toString();
}

/**
 * Returns a Map of Pose info.
 */
public Map <String,Object> getPoseMap()
{
    View anchorView = getChild(Puppet.Anchor_Marker);
    Point anchor = anchorView.localToParent(anchorView.getWidth()/2, anchorView.getHeight()/2);
    Map <String,Object> map = new LinkedHashMap();
    
    // Iterate over pose keys and add pose marker and x/y location to map
    for(String pkey : getPuppet().getPoseKeys()) {
        View pview = getChild(pkey);
        Point pnt = pview.localToParent(pview.getWidth()/2, pview.getHeight()/2);
        pnt.x = pnt.x - anchor.x; pnt.y = anchor.y - pnt.y;
        map.put(pkey, Arrays.asList(StringUtils.formatNum("#.#", pnt.x), StringUtils.formatNum("#.#", pnt.y)));
    }

    // Return map wrapped in map to get Pose { ... }
    return map;
}

/**
 * Sets a pose map.
 */
public void setPoseMap(Map <String,Object> aMap, PhysicsRunner aPR)
{
    View anchorView = getChild(Puppet.Anchor_Marker);
    Point anchor = anchorView.localToParent(anchorView.getWidth()/2, anchorView.getHeight()/2);
    
    // Iterate over pose keys and add pose marker and x/y location to map
    for(String pkey : getPuppet().getPoseKeys()) {
        View pview = getChild(pkey);
        List <String> plist = (List)aMap.get(pkey);
        double px = Double.valueOf(plist.get(0)), py = Double.valueOf(plist.get(1));
        px = px + anchor.x; py = anchor.y - py;
        aPR.setJointOrMarkerToViewXY(pkey, px, py);
    }
}

}