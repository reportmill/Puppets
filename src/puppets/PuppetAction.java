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
    
    // A list of poses + time
    List <PoseStep>    _steps = new ArrayList();

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
 * Returns the list of pose-steps defined for this action.
 */
public List <PoseStep> getSteps()  { return _steps; }

/**
 * Returns the number of steps.
 */
public int getStepCount()  { return getSteps().size(); }

/**
 * Returns the individual pose step at given index.
 */
public PoseStep getStep(int anIndex)  { return getSteps().get(anIndex); }

/**
 * Adds a pose-step.
 */
public void addStep(PuppetPose aPose, int aTime)
{
    addStep(new PoseStep(aPose, aTime));
}

/**
 * Adds a pose-step.
 */
public void addStep(PoseStep aStep)
{
    getSteps().add(aStep);
}

/**
 * Removes a pose-step.
 */
public PoseStep removeStep(int anIndex)
{
    PoseStep step = getSteps().remove(anIndex);
    return step;
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
    
    // Create element for steps and iterate over steps and add each
    XMLElement stepsXML = new XMLElement("Steps"); e.add(stepsXML);
    for(PoseStep step : getSteps()) {
        XMLElement stepXML = step.toXML(anArchiver);
        stepsXML.add(stepXML);
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

    // Iterate over steps element and load
    XMLElement stepsXML = anElement.getElement("Steps");
    for(XMLElement stepXML : stepsXML.getElements()) {
        PoseStep step = new PoseStep().fromXML(anArchiver, stepXML);
        _steps.add(step);
    }

    // Return this
    return this;
}

/**
 * A class to represent a pose over a given time.
 */
public class PoseStep {
    
    // The pose
    PuppetPose     _pose;
    
    // The time span in milliseconds for the change
    int            _time;
    
    /** Creates a PoseChange. */
    public PoseStep()  { }
    
    /** Creates a PoseChange. */
    public PoseStep(PuppetPose aPose, int theTime)  { _pose = aPose; _time = theTime; }
    
    /** Returns the pose. */
    public PuppetPose getPose()  { return _pose; }
    
    /** Returns the time interval. */
    public int getTime()  { return _time; }
    
    /** XML Archival. */
    public XMLElement toXML(XMLArchiver anArchiver)
    {
        XMLElement e = new XMLElement("Step");
        e.add("Pose", getPose().getName());
        e.add("Time", getTime());
        return e;
    }
    
    /** XML unarchival. */
    public PoseStep fromXML(XMLArchiver anArchiver, XMLElement anElement)
    {
        String name = anElement.getAttributeValue("Pose");
        _pose = getPoseForName(name);
        _time = anElement.getAttributeIntValue("Time");
        return this;
    }
}

}