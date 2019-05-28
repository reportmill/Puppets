package puppets;
import snap.view.*;

/**
 * The top level document UI management class for displaying and editing puppets and actions.
 */
public class DocPane extends ViewOwner {
    
    // The Puppet
    Puppet             _puppet;
    
    // The puppet scale
    double             _scale = 1;
    
    // The view that holds the document
    BoxView            _docBox;
    
    // The PuppetView
    PuppetPane         _pupPane;
    
    // The ActionPane
    ActionPane         _actionPane;

/**
 * Initialize UI.
 */
protected void initUI()
{
    _docBox = getView("DocBox", BoxView.class);
}

/**
 * Opens the given source.
 */
public void open(String aSource, double aScale)
{
    _puppet = new ORAPuppet(aSource);
    _scale = aScale;
    
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
 * Respond to UI.
 */
protected void respondUI(ViewEvent anEvent)
{
    // Handle DisplayButton, ActionButton
    if(anEvent.equals("DisplayButton")) showDisplayPane();
    if(anEvent.equals("ActionButton")) showActionPane();
        
    // Handle LadyButton
    if(anEvent.equals("LadyButton")) open("/Temp/ComicLib/chars/CTLady",.2);
    if(anEvent.equals("ManButton")) open("/Temp/ComicLib/chars/CTMan",.5);
}

}