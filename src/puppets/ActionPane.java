package puppets;
import snap.view.*;
import snap.viewx.DialogBox;

/**
 * A class to manage UI to create and edit puppet animations.
 */
public class ActionPane extends ViewOwner {

    // The DocPane
    DocPane                _docPane;
    
    // Whether to show markers
    boolean                _showMarkers;
    
    // The puppet action view
    ActionView             _actView;
    
    // A List of PoseMaps
    PoseList               _poses = new PoseList();
    
    // A ListView to show poses
    ListView <PuppetPose>  _poseList;

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
    _poseList.setSelIndex(-1);
    resetLater();
}

/**
 * Initialize UI.
 */
protected void initUI()
{
    // Create ActionView
    _actView = new ActionView(_docPane._puppet);
    _actView.addEventFilter(e -> actViewDidMouseRelease(), MouseRelease);
    
    // Get PuppetBox and add ActionView
    BoxView pupBox = getView("PuppetBox", BoxView.class);
    pupBox.setContent(_actView);
    
    // Set PoseList
    _poseList = getView("PoseList", ListView.class);
    _poseList.setItemTextFunction(pose -> { return pose.getName(); });
    _poseList.setItems(_poses.getPoses());
    
    // Make PuppetView interactive
    _actView.setPosable(true);
}

/**
 * Reset UI.
 */
protected void resetUI()
{
    // Update ShowMarkersCheckBox
    setViewValue("ShowMarkersCheckBox", _showMarkers);
    
    // Update PoseText
    String poseInfo = _actView.getPose().getAsString();
    setViewText("PoseText", poseInfo);
    
    // Update RenamePoseButton, DeletePoseButton
    setViewEnabled("RenamePoseButton", _poseList.getSelIndex()>=0);
    setViewEnabled("DeletePoseButton", _poseList.getSelIndex()>=0);
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
        _actView.setPose(_poseList.getSelItem());
        
    // Handle AddPoseButton
    if(anEvent.equals("AddPoseButton")) {
        PuppetPose pose = _actView.getPose();
        pose.setName("Pose " + (_poses.getPoseCount() + 1));
        _poses.addPose(pose);
        _poseList.setItems(_poses.getPoses());
        _poseList.setSelIndex(_poses.getPoseCount()-1);
    }
    
    // Handle RenamePoseButton
    if(anEvent.equals("RenamePoseButton")) {
        PuppetPose pose = _poseList.getSelItem();
        String name = DialogBox.showInputDialog(_docPane.getUI(), "Rename Pose", "Enter Pose Name:", pose.getName());
        if(name!=null && name.length()>0) pose.setName(name);
        _poseList.updateItems(pose);
        _poses.savePoses();
    }

    // Handle DeletePoseButton
    if(anEvent.equals("DeletePoseButton")) {
        _poses.removePose(_poseList.getSelIndex());
    }
}

}