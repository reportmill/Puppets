package puppets.puppet;
import java.util.*;
import snap.util.*;
import snap.web.WebURL;

/**
 * A class to manage list of Puppet Actions.
 */
public class PuppetActions {

    // A List of Poses
    List <PuppetAction>  _actions;
    
    // The file path
    String               _filePath = puppets.app.DocPane.ROOT + "chars/HumanActions.xml";
    
/**
 * Returns the list of actions.
 */
public List <PuppetAction> getActions()
{
    if(_actions!=null) return _actions;
    return _actions = loadActions();
}

/**
 * Returns the number of actions.
 */
public int getActionCount()  { return getActions().size(); }

/**
 * Returns the individual actions at given index.
 */
public PuppetAction getAction(int anIndex)  { return getActions().get(anIndex); }

/**
 * Returns the action with given name.
 */
public PuppetAction getAction(String aName)
{
    for(PuppetAction act : getActions())
        if(act.getName().equals(aName))
            return act;
    return null;
}

/**
 * Adds an action.
 */
public void addAction(PuppetAction anAction)  { addAction(anAction, getActionCount()); }

/**
 * Adds an action.
 */
public void addAction(PuppetAction anAction, int anIndex)
{
    getActions().add(anIndex, anAction);
    saveActions();
}

/**
 * Removes an action.
 */
public PuppetAction removeAction(int anIndex)
{
    PuppetAction action = getActions().remove(anIndex);
    saveActions();
    return action;
}

/**
 * Loads actions from file.
 */
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

/**
 * Saves actions to file.
 */
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