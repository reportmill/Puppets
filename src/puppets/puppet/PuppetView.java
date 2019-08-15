package puppets.puppet;
import java.util.*;
import snap.gfx.*;
import snap.view.*;

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
    
    // Whether to show markers
    boolean        _showMarkers = true;
    
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
public PuppetView(Puppet aPuppet)  { setPuppet(aPuppet); }

/**
 * Returns the puppet.
 */
public Puppet getPuppet()  { return _puppet; }

/**
 * Sets the puppet.
 */
public void setPuppet(Puppet aPuppet)
{
    // Set puppet
    _puppet = aPuppet;
    
    // Rebuild children
    if(aPuppet.isLoaded()) rebuildChildren();
    else aPuppet.addLoadListener(() -> rebuildChildren());
}

/**
 * Returns the puppet schema.
 */
public PuppetSchema getSchema()  { return _puppet.getSchema(); }

/**
 * Sets the puppet height.
 */
public void setPuppetHeight(double aHeight)  { _pupHeight = aHeight; }

/**
 * Rebuilds children.
 */
public void rebuildChildren()
{
    // Remove children
    removeChildren();
    
    // Iterate over parts
    for(String pname : _puppet.getPartNames()) {
        PuppetPart part = _puppet.getPart(pname);
        PartView partView = new PartView(part);
        addChild(partView);
    }
    
    // Iterate over joints
    for(String jname : _puppet.getJointNames()) {
        PuppetJoint joint = _puppet.getJoint(jname);
        JointView jointView = new JointView(joint, getSchema().isMarkerName(jname));
        addChild(jointView);
    }
    
    // Make Torso really dense
    getChild(PuppetSchema.Torso).getPhysics().setDensity(1000);
    
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
    relayoutParent();
}

/**
 * Returns a Puppet pose for current puppet articulation.
 */
public PuppetPose getPose()
{
    View anchorView = getChild(PuppetSchema.Anchor_Joint);
    Point anchor = anchorView.localToParent(anchorView.getWidth()/2, anchorView.getHeight()/2);
    Map <String,Point> map = new LinkedHashMap();
    
    // Iterate over pose keys and add pose marker and x/y location to map
    for(String pkey : getSchema().getPoseKeys()) { View pview = getChild(pkey);
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
    _physRunner.resolveMouseJoints();
    View anchorView = getChild(PuppetSchema.Anchor_Joint);
    Point anchor = anchorView.localToParent(anchorView.getWidth()/2, anchorView.getHeight()/2);
    
    // Iterate over pose keys and add pose marker and x/y location to map
    for(String pkey : getSchema().getPoseKeys()) {
        View pview = getChild(pkey);
        Point pnt = aPose.getMarkerPoint(pkey);
        double px = pnt.x*_pupHeight/500 + anchor.x, py = anchor.y - pnt.y*_pupHeight/500;
        _physRunner.setJointOrMarkerToViewXY(pkey, px, py);
    }
}

/**
 * Returns whether to show markers.
 */
public boolean isShowMarkers()  { return _showMarkers; }

/**
 * Sets whether to show markers.
 */
public void setShowMarkers(boolean aValue)
{
    // If already set, just return, otherwise set
    if(aValue==_showMarkers) return;
    _showMarkers = aValue;
    
    // Iterate over children and toggle visible if Joint
    for(View child : getChildren()) {
        if(child instanceof JointView)
            child.setVisible(aValue);
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
    // If already set, just return
    if(aValue==isPosable()) return;
    
    // Create/start PhysRunner
    if(aValue) _physRunner = new PhysicsRunner(this); //_physRunner.setRunning(true);
    
    // Stop/clear PhysRunner
    else _physRunner = null; //_physRunner.setRunning(false);
}

/**
 * A view to display puppet parts.
 */
protected static class PartView extends ImageView {
    
    // The PuppetPart
    PuppetPart     _part;
    
    /** Creates a PartView for given PuppetPart. */
    public PartView(PuppetPart aPart)
    {
        _part = aPart; setImage(aPart.getImage()); setName(aPart.getName());
        setXY(aPart.getX(), aPart.getY()); setSize(getPrefSize());
        getPhysics(true).setGroupIndex(-1);
    }
}

/**
 * A view to display puppet joints.
 */
protected static class JointView extends ImageView {
    
    // The PuppetJoint
    PuppetJoint     _joint;
    
    /** Creates a JointView for given PuppetJoint. */
    public JointView(PuppetJoint aJoint, boolean isMarker)
    {
        _joint = aJoint; setImage(aJoint.getImage()); setName(aJoint.getName());
        setXY(aJoint.getX(), aJoint.getY()); setSize(getPrefSize());
        if(!isMarker) getPhysics(true).setJoint(true);
        else getPhysics(true).setGroupIndex(-1);
    }
}

}