package puppets;
import java.util.*;
import snap.gfx.*;
import snap.view.*;
import puppets.Puppet.Part;

/**
 * A View to display a puppet.
 */
public class PuppetView extends ParentView {
    
    // The puppet
    Puppet         _puppet;
    
    // The scale
    double         _scale = 1;
    
    // The puppet height in points
    double         _pupHeight = 500;

    // The PhysicsRunner
    PhysicsRunner  _physRunner;
    
/**
 * Creates a PuppetView.
 */
public PuppetView()  { }

/**
 * Creates a PuppetView.
 */
public PuppetView(String aSource)
{
    Puppet puppet = new ORAPuppet(aSource);
    setPuppet(puppet);
}

/**
 * Creates a PuppetView.
 */
public PuppetView(Puppet aPuppet)
{
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
    rebuildChildren();
}

/**
 * Rebuilds children.
 */
protected void rebuildChildren()
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
    
    // Calculate scale to make puppet 500 pnts tall
    Rect bnds = _puppet.getBounds();
    _scale = _pupHeight/bnds.height;
    
    // Resize children
    for(View c : getChildren())
        c.setBounds(c.getX()*_scale, c.getY()*_scale, c.getWidth()*_scale, c.getHeight()*_scale);
        
    // Resize PuppetView
    double pw = bnds.x*2 + bnds.width; pw *= _scale;
    double ph = bnds.y*2 + bnds.height; ph *= _scale;
    setSize(pw, ph); setPrefSize(pw, ph);
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
 * Returns a Puppet pose for current puppet articulation.
 */
public PuppetPose getPose()
{
    View anchorView = getChild(Puppet.Anchor_Marker);
    Point anchor = anchorView.localToParent(anchorView.getWidth()/2, anchorView.getHeight()/2);
    Map <String,Point> map = new LinkedHashMap();
    
    // Iterate over pose keys and add pose marker and x/y location to map
    for(String pkey : getPuppet().getPoseKeys()) { View pview = getChild(pkey);
        Point pnt = pview.localToParent(pview.getWidth()/2, pview.getHeight()/2);
        pnt.x = pnt.x - anchor.x; pnt.y = anchor.y - pnt.y;
        map.put(pkey, pnt);
    }

    // Return map wrapped in map to get Pose { ... }
    return new PuppetPose("Untitled", map);
}

/**
 * Sets a Puppet pose.
 */
public void setPose(PuppetPose aPose)
{
    View anchorView = getChild(Puppet.Anchor_Marker);
    Point anchor = anchorView.localToParent(anchorView.getWidth()/2, anchorView.getHeight()/2);
    
    // Iterate over pose keys and add pose marker and x/y location to map
    for(String pkey : getPuppet().getPoseKeys()) {
        View pview = getChild(pkey);
        Point pnt = aPose.getMarkerPoint(pkey);
        double px = pnt.x + anchor.x, py = anchor.y - pnt.y;
        _physRunner.setJointOrMarkerToViewXY(pkey, px, py);
    }
}

/**
 * Returns whether puppet is posable via user interaction.
 */
public boolean isPosable()  { return _physRunner!=null; }

/**
 * Sets whether puppet is posable via user interaction.
 */
public void setPosable(boolean aValue)
{
    if(aValue==isPosable()) return;
    
    // Create/start PhysRunner
    if(aValue) {
        _physRunner = new PhysicsRunner(this);
        _physRunner.setRunning(true);
    }
    
    // Stop PhysRunner
    else {
        _physRunner.setRunning(false);
        _physRunner = null;
    }
}

}