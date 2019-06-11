package puppets;
import java.util.*;
import snap.util.*;

/**
 * A class to define an animated action.
 */
public class PuppetAction {
    
    // The action name
    String             _name;

    // A list of poses used by this action
    List <PuppetPose>  _poses = new ArrayList();
    
    // A list of moves
    List <PuppetMove>  _moves = new ArrayList();

/**
 * Creates a PuppetAction.
 */
public PuppetAction()  { }

/**
 * Creates a PuppetAction.
 */
public PuppetAction(String aName)  { setName(aName); }

/**
 * Returns the name.
 */
public String getName()  { return _name; }

/**
 * Sets the name.
 */
public void setName(String aName)  { _name = aName; }

/**
 * Returns the list of poses defined for this action.
 */
public List <PuppetPose> getPoses()  { return _poses; }

/**
 * Returns the number of poses.
 */
public int getPoseCount()  { return getPoses().size(); }

/**
 * Returns the individual pose at given index.
 */
public PuppetPose getPose(int anIndex)  { return getPoses().get(anIndex); }

/**
 * Adds a pose.
 */
public void addPose(PuppetPose aPose)
{
    getPoses().add(aPose);
}

/**
 * Removes a pose.
 */
public PuppetPose removePose(int anIndex)
{
    PuppetPose pose = getPoses().remove(anIndex);
    return pose;
}

/**
 * Returns the pose with given name.
 */
public PuppetPose getPoseForName(String aName)
{
    for(PuppetPose pose : getPoses())
        if(pose.getName().equals(aName))
            return pose;
    return null;
}

/**
 * Returns the list of moves defined for this action.
 */
public List <PuppetMove> getMoves()  { return _moves; }

/**
 * Returns the number of moves.
 */
public int getMoveCount()  { return getMoves().size(); }

/**
 * Returns the individual move at given index.
 */
public PuppetMove getMove(int anIndex)  { return getMoves().get(anIndex); }

/**
 * Adds a move for pose and time.
 */
public void addMoveForPoseAndTime(PuppetPose aPose, int aTime)
{
    addMove(new PuppetMove(aPose, aTime));
}

/**
 * Adds a move.
 */
public void addMove(PuppetMove aMove)  { addMove(aMove, getMoveCount()); }

/**
 * Adds a move at given index.
 */
public void addMove(PuppetMove aMove, int anIndex)
{
    // Add to moves list
    getMoves().add(anIndex, aMove);
    
    // If pose not in PoseList, add it
    String poseName = aMove.getPoseName();
    if(getPoseForName(poseName)==null)
        addPose(aMove.getPose());
}

/**
 * Removes a move.
 */
public PuppetMove removeMove(int anIndex)
{
    PuppetMove move = getMoves().remove(anIndex);
    return move;
}

/**
 * Returns the max time for action.
 */
public int getMaxTime()
{
    return getPoseCount()*500 - 500;
}

/**
 * XML Archival.
 */
public XMLElement toXML(XMLArchiver anArchiver)
{
    // Get new element with class name
    XMLElement e = new XMLElement("Action");
    e.add("Name", getName());
    
    // Create element for poses and iterate over poses and add each
    XMLElement posesXML = new XMLElement("Poses"); e.add(posesXML);
    for(PuppetPose pose : getPoses()) {
        XMLElement poseXML = pose.toXML(anArchiver);
        posesXML.add(poseXML);
    }
    
    // Create element for steps and iterate over moves and add each
    XMLElement movesXML = new XMLElement("Moves"); e.add(movesXML);
    for(PuppetMove move : getMoves()) {
        XMLElement moveXML = move.toXML(anArchiver);
        movesXML.add(moveXML);
    }
    
    // Return element
    return e;
}

/**
 * XML unarchival.
 */
public PuppetAction fromXML(XMLArchiver anArchiver, XMLElement anElement)
{
    // Unarchive name
    String name = anElement.getAttributeValue("Name");
    setName(name);
    
    // Iterate over poses element and load
    XMLElement posesXML = anElement.getElement("Poses");
    for(XMLElement poseXML : posesXML.getElements()) {
        PuppetPose pose = new PuppetPose().fromXML(anArchiver, poseXML);
        _poses.add(pose);
    }

    // Iterate over moves element and load
    XMLElement movesXML = anElement.getElement("Moves");
    if(movesXML==null) movesXML = anElement.getElement("Steps"); // Can go soon
    for(XMLElement moveXML : movesXML.getElements()) {
        PuppetMove move = new PuppetMove().fromXML(this, moveXML);
        _moves.add(move);
    }
    
    // Can go soon
    if(getMoveCount()==0)
        for(PuppetPose pose : _poses) addMoveForPoseAndTime(pose, 500);

    // Return this
    return this;
}

}