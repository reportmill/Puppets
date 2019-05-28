package puppets;
import snap.gfx.*;
import snap.view.*;

/**
 * A class to manage UI to create and edit puppet animations.
 */
public class ActionPane extends ViewOwner {

    // The DocPane
    DocPane            _docPane;
    
    // Whether to show markers
    boolean            _showMarkers;
    
    // The puppet action view
    ActionView         _actView;
    
    // The PhysicsRunner
    PhysicsRunner      _physRunner;

/**
 * Creates ActionPane.
 */
public ActionPane(DocPane aDP)  { _docPane = aDP; }

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
    
    // Iterate over children and toggle visible if Joint Or Marker
    Puppet puppet = _actView.getPuppet();
    for(View child : _actView.getChildren()) {
        if(puppet.isJointOrMarkerName(child.getName()))
            child.setVisible(aValue);
    }
}

/**
 * Creates UI.
 */
protected View createUI()
{
    // Create ActionView
    _actView = new ActionView(_docPane._puppet, _docPane._scale);
    
    // Create PuppetBox
    BoxView pupBox = new BoxView(_actView); pupBox.setGrowWidth(true);
    pupBox.setFill(Color.WHITE); pupBox.setBorder(Color.BLACK, 1);
    
    // Create ShowMarkersCheckBox
    CheckBox smCBox = new CheckBox("Show Markers"); smCBox.setName("ShowMarkersCheckBox");
    
    // Create ToolsColView to hold puppet inspector UI
    ColView toolsColView = new ColView(); toolsColView.setPadding(20,8,8,8);
    toolsColView.setSpacing(5); toolsColView.setFillWidth(true); toolsColView.setPrefWidth(300);
    toolsColView.addChild(smCBox);
    
    // Create MainRowView
    RowView mainRowView = new RowView(); mainRowView.setGrowWidth(true); mainRowView.setFillHeight(true);
    mainRowView.setAlign(Pos.CENTER);
    mainRowView.addChild(pupBox);
    mainRowView.addChild(toolsColView);
    return mainRowView;
}

/**
 * Initialize UI.
 */
protected void initUI()
{
    // Create/start PhysRunner
    _physRunner = new PhysicsRunner(_actView);
    _physRunner.setRunning(true);
}

/**
 * Reset UI.
 */
protected void resetUI()
{
    setViewValue("ShowMarkersCheckBox", _showMarkers);
}

/**
 * Respond to UI.
 */
protected void respondUI(ViewEvent anEvent)
{
    // Handle ShowMarkersCheckBox
    if(anEvent.equals("ShowMarkersCheckBox"))
        setShowMarkers(anEvent.getBoolValue());
}

}