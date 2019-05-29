package puppets;
import java.util.*;
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
    
    // A List of PoseMaps
    List <Map>         _poseMaps = new ArrayList();
    
    // A ListView to show poses
    ListView <Map>     _poseList;

/**
 * Creates ActionPane.
 */
public ActionPane(DocPane aDP)  { _docPane = aDP; }

/**
 * Returns the puppet.
 */
public Puppet getPuppet()  { return _actView.getPuppet(); }

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
    Puppet puppet = getPuppet();
    for(View child : _actView.getChildren()) {
        if(puppet.isJointOrMarkerName(child.getName()))
            child.setVisible(aValue);
    }
}

/**
 * Called when ActionView gets MouseRelease.
 */
void actViewDidMouseRelease()
{
    _poseMaps.add(_actView.getPoseMap());
    _poseList.setItems(_poseMaps);
    _poseList.setSelIndex(_poseMaps.size()-1);
    resetLater();
}

/**
 * Initialize UI.
 */
protected void initUI()
{
    // Create ActionView
    _actView = new ActionView(_docPane._puppet, _docPane._scale);
    _actView.addEventFilter(e -> actViewDidMouseRelease(), MouseRelease);
    
    // Get PuppetBox and add ActionView
    BoxView pupBox = getView("PuppetBox", BoxView.class);
    pupBox.setContent(_actView);
    
    // Set PoseList
    _poseList = getView("PoseList", ListView.class);
    _poseList.setItemTextFunction(map -> { return String.valueOf(_poseMaps.indexOf(map)); });
    _poseMaps.add(_actView.getPoseMap());
    _poseList.setItems(_poseMaps);
    _poseList.setSelIndex(0);
    
    // Create/start PhysRunner
    _physRunner = new PhysicsRunner(_actView);
    _physRunner.setRunning(true);
}

/**
 * Reset UI.
 */
protected void resetUI()
{
    // Update ShowMarkersCheckBox
    setViewValue("ShowMarkersCheckBox", _showMarkers);
    
    // Update PoseText
    String poseInfo = _actView.getPoseString().replace("\"", "");
    setViewText("PoseText", poseInfo);
}

/**
 * Respond to UI.
 */
protected void respondUI(ViewEvent anEvent)
{
    // Handle ShowMarkersCheckBox
    if(anEvent.equals("ShowMarkersCheckBox"))
        setShowMarkers(anEvent.getBoolValue());
        
    // Handle PoseList
    if(anEvent.equals("PoseList"))
        _actView.setPoseMap(_poseList.getSelItem(), _physRunner);
}

}