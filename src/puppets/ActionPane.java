package puppets;
import snap.view.*;
import snap.viewx.DialogBox;

/**
 * A class to manage UI to create and edit puppet animations.
 */
public class ActionPane extends ViewOwner {

    // The DocPane
    DocPane                  _docPane;
    
    // Whether to show markers
    boolean                  _showMarkers;
    
    // The puppet action view
    ActionView               _actView;
    
    // A List of Puppet Actions
    PuppetActions            _actions = new PuppetActions();
    
    // A ListView to show actions
    ListView <PuppetAction>  _actionList;

    // A ListView to show poses
    ListView <PuppetPose>    _poseList;

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
    
    // Set ActionList
    _actionList = getView("ActionList", ListView.class);
    _actionList.setItemTextFunction(action -> { return action.getName(); });
    _actionList.setItems(_actions.getActions());
    if(_actions.getActionCount()>0)
        _actionList.setSelIndex(0);
    
    // Set PoseList
    _poseList = getView("PoseList", ListView.class);
    _poseList.setItemTextFunction(pose -> { return pose.getName(); });
    
    // Intialize PoseList/StepList
    PuppetAction action = _actionList.getSelItem();
    if(action!=null)
        _poseList.setItems(action.getPoses());
    
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
    //String poseInfo = _actView.getPose().getAsString();
    //setViewText("PoseText", poseInfo);
    
    // Update RenamePoseButton, DeletePoseButton
    //setViewEnabled("RenamePoseButton", _poseList.getSelIndex()>=0);
    setViewEnabled("RemoveActionButton", _actionList.getSelIndex()>=0);
    setViewEnabled("RemovePoseButton", _poseList.getSelIndex()>=0);
}

/**
 * Respond to UI.
 */
protected void respondUI(ViewEvent anEvent)
{
    // Handle ShowMarkersCheckBox
    if(anEvent.equals("ShowMarkersCheckBox"))
        setShowMarkers(anEvent.getBoolValue());
        
    // Handle ActionList
    if(anEvent.equals("ActionList")) {
        PuppetAction action = _actionList.getSelItem();
        if(action!=null)
            _poseList.setItems(action.getPoses());
    }
        
    // Handle AddActionButton
    if(anEvent.equals("AddActionButton")) {
        String name = DialogBox.showInputDialog(_docPane.getUI(), "Add Action", "Enter Action Name:", "Untitled");
        if(name==null || name.length()==0) return;
        PuppetAction action = new PuppetAction(name);
        _actions.addAction(action);
        _actionList.setItems(_actions.getActions());
        _actionList.setSelIndex(_actions.getActionCount()-1);
        _poseList.setItems(action.getPoses());
        _poseList.setSelIndex(-1);
    }
    
    // Handle PoseList
    if(anEvent.equals("PoseList"))
        _actView.setPose(_poseList.getSelItem());
        
    // Handle AddPoseButton
    if(anEvent.equals("AddPoseButton")) {
        PuppetAction action = _actionList.getSelItem(); if(action==null) return;
        String name = DialogBox.showInputDialog(_docPane.getUI(), "Add Pose", "Enter Pose Name:", "Untitled");
        if(name==null || name.length()==0) return;
        PuppetPose pose = _actView.getPose();
        pose.setName(name);
        action.addPose(pose);
        _poseList.setItems(action.getPoses());
        _poseList.setSelIndex(action.getPoseCount()-1);
        _actions.saveActions();
    }
    
    // Handle PlayButton
    if(anEvent.equals("PlayButton")) {
        PuppetAction action = _actionList.getSelItem(); if(action==null) return;
        _actView.performAction(action);
    }
    
    // Handle AddStepButton
    if(anEvent.equals("AddStepButton")) {
        PuppetAction action = _actionList.getSelItem(); if(action==null) return;
        String name = DialogBox.showInputDialog(_docPane.getUI(), "Add Step", "Enter Step Name:", null);
        if(name==null || name.length()==0) return;
        PuppetPose pose = action.getPoseForName(name); if(pose==null) return;
        action.addStep(pose, 500);
        _actions.saveActions();
    }
    
    // Handle RenamePoseButton
    /*if(anEvent.equals("RenamePoseButton")) {
        PuppetPose pose = _poseList.getSelItem();
        String name = DialogBox.showInputDialog(_docPane.getUI(), "Rename Pose", "Enter Pose Name:", pose.getName());
        if(name!=null && name.length()>0) pose.setName(name);
        _poseList.updateItems(pose); //_poses.savePoses(); } */

    // Handle DeletePoseButton
    if(anEvent.equals("DeletePoseButton")) {
        //_poses.removePose(_poseList.getSelIndex());
    }
}

}