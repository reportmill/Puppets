package puppets.puppet;
import java.util.*;
import snap.gfx.*;
import snap.util.SnapUtils;
import snap.util.XMLElement;
import snap.web.WebURL;

/**
 * A class to handle miscellaneous functionality for Puppet.
 */
public class PuppetUtils {
    
    // The ActionFile
    static ActionFile        _actionFile = new ActionFile();

    // The marker image
    static Image             _markerImg, _anchorImage;

/**
 * Returns the ActionFile.
 */
public static ActionFile getActionFile()  { return _actionFile; }

/**
 * Returns the joint/marker image.
 */
public static Image getMarkerImage()
{
    if(_markerImg!=null) return _markerImg; double s = 23;
    Image img = Image.get((int)s, (int)s, true);
    Painter pntr = img.getPainter(); Shape arc = new Arc(0,0,s,s,0,360);
    Color fill = new Color("#E49956D0"); // Yellow: #EFD969E0, Orange: #E49956E0
    pntr.setColor(fill); pntr.fill(arc);
    pntr.setColor(fill.darker().darker()); pntr.setStroke(Stroke.getStroke(.5)); pntr.draw(arc);
    pntr.setColor(Color.BLACK); pntr.setStroke(Stroke.Stroke2);
    pntr.drawLine(5,s/2,s-5,s/2); pntr.drawLine(s/2,5,s/2,s-5);
    return _markerImg = img;
}

/**
 * Returns the anchor image.
 */
public static Image getAnchorImage()
{
    if(_anchorImage!=null) return _anchorImage; double s = 90;
    Image img = Image.get((int)s, (int)s, true);
    Painter pntr = img.getPainter(); pntr.setStroke(Stroke.getStroke(4));
    Color red = new Color("#D54438"), blue = new Color("#4B77B8");
    pntr.setColor(red); pntr.drawLine(5,s/2,s-5,s/2);
    pntr.setColor(blue); pntr.drawLine(s/2,5,s/2,s-5);
    return _anchorImage = img;
}

/**
 * A class to manage list of Puppet Actions.
 */
public static class ActionFile {

    // A List of Poses
    List <PuppetAction>  _actions;
    
    // The file path
    String               _filePath = puppets.app.AppPane.ROOT + "chars/HumanActions.xml";
    
    /** Returns the list of actions. */
    public List <PuppetAction> getActions()  { return _actions!=null? _actions : (_actions = loadActions()); }
    
    /** Returns the number of actions. */
    public int getActionCount()  { return getActions().size(); }
    
    /** Returns the individual actions at given index. */
    public PuppetAction getAction(int anIndex)  { return getActions().get(anIndex); }
    
    /** Returns the action with given name. */
    public PuppetAction getAction(String aName)
    {
        for(PuppetAction act : getActions())
            if(act.getName().equals(aName))
                return act;
        return null;
    }
    
    /** Adds an action. */
    public void addAction(PuppetAction anAction)  { addAction(anAction, getActionCount()); }
    
    /** Adds an action. */
    public void addAction(PuppetAction anAction, int anIndex)
    {
        getActions().add(anIndex, anAction);
        saveActions();
    }
    
    /** Removes an action. */
    public PuppetAction removeAction(int anIndex)
    {
        PuppetAction action = getActions().remove(anIndex);
        saveActions();
        return action;
    }
    
    /** Loads actions from file. */
    protected List <PuppetAction> loadActions()
    {
        // Get file string as XMLElement
        WebURL url = WebURL.getURL(_filePath);
        String fileStr = url.getText();
        if(fileStr==null) return new ArrayList();
        XMLElement actionsXML = XMLElement.getElement(url);
        
        // Iterate over actions
        List <PuppetAction> actions = new ArrayList();
        for(XMLElement actionXML : actionsXML.getElements()) {
            PuppetAction action = new PuppetAction().fromXML(null, actionXML);
            actions.add(action);
        }
       
        // Return actions
        return actions;
    }
    
    /** Saves actions to file. */
    public void saveActions()
    {
        if(SnapUtils.isTeaVM) return;
        
        // Create element for actions and iterate over actions and add each
        XMLElement actionsXML = new XMLElement("Actions");
        for(PuppetAction action : getActions()) {
            XMLElement actionXML = action.toXML(null);
            actionsXML.add(actionXML);
        }
        
        // Get as bytes and write to file
        byte bytes[] = actionsXML.getBytes();
        SnapUtils.writeBytes(bytes, _filePath);
    }
}

}