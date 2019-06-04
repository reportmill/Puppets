package puppets;
import snap.util.SnapUtils;
import snap.view.*;

/**
 * The top level document UI management class for displaying and editing puppets and actions.
 */
public class DocPane extends ViewOwner {
    
    // The Puppet
    Puppet             _puppet;
    
    // The view that holds the document
    BoxView            _docBox;
    
    // The PuppetView
    PuppetPane         _pupPane;
    
    // The ActionPane
    ActionPane         _actionPane;
    
    // Constants
    static String ROOT = "/Temp/ComicLib/";
    
/**
 * Creates a new DocPane.
 */
public DocPane()
{
    if(SnapUtils.isTeaVM) ROOT = "http://reportmill.com/ComicLib/";
}

/**
 * Opens the given source.
 */
public void open(String aSource)
{
    _puppet = new ORAPuppet(aSource);
    
    showDisplayPane();
}

/**
 * Shows the DisplayPane.
 */
public void showDisplayPane()
{
    _docBox.removeChildren();
    
    _pupPane = new PuppetPane(this);
    _docBox.setContent(_pupPane.getUI());
}

/**
 * Shows the ActionPane.
 */
public void showActionPane()
{
    _docBox.removeChildren();
    
    _actionPane = new ActionPane(this);
    _docBox.setContent(_actionPane.getUI());
}

/**
 * Initialize UI.
 */
protected void initUI()
{
    _docBox = getView("DocBox", BoxView.class);
    if(SnapUtils.isTeaVM) getWindow().setMaximized(true);
    
    runLater(() -> open(ROOT + "chars/CTMan"));
}

/**
 * Respond to UI.
 */
protected void respondUI(ViewEvent anEvent)
{
    // Handle DisplayButton, ActionButton
    if(anEvent.equals("DisplayButton")) showDisplayPane();
    if(anEvent.equals("ActionButton")) showActionPane();
        
    // Handle LadyButton
    if(anEvent.equals("LadyButton")) open(ROOT + "chars/CTLady");
    if(anEvent.equals("ManButton")) open(ROOT + "chars/CTMan");
}

}