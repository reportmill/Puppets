package puppets;
import snap.gfx.*;
import snap.view.*;

/**
 * A custom class.
 */
public class DocPane extends ViewOwner {
    
    // The Puppet
    Puppet             _puppet;
    
    // The puppet scale
    double             _scale = 1;
    
    // The view that holds the document
    RowView            _docBox;
    
    // The PuppetView
    PuppetView         _pupView;
    
    // The ActionPane
    ActionPane         _actionPane;

/**
 * Initialize UI.
 */
protected void initUI()
{
    _docBox = getView("DocBox", RowView.class);
    _docBox.setFill(Color.WHITE);
    _docBox.setAlign(Pos.CENTER);
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
    
    _pupView = new PuppetView(_puppet, _scale);
    _pupView.setBorder(Color.PINK,1);
    _docBox.addChild(_pupView);
}

/**
 * Shows the ActionPane.
 */
public void showActionPane()
{
    _docBox.removeChildren();
    
    _actionPane = new ActionPane(this);
    _docBox.addChild(_actionPane.getUI());
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