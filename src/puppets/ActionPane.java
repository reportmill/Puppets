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

    // A TableView to show moves
    TableView <PuppetMove>   _moveTable;
    
    // The last copied move
    PuppetMove               _copyMove;

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
    _moveTable.setSelIndex(-1);
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
    
    // Set MoveTable
    _moveTable = getView("MoveTable", TableView.class);
    _moveTable.getCol(0).setItemTextFunction(move -> { return move.getPoseName(); });
    _moveTable.getCol(1).setItemTextFunction(move -> { return String.valueOf(move.getTime()); });
    _moveTable.setEditable(true);
    _moveTable.setCellEditEnd(c -> moveTableCellEditEnd(c));
    PuppetAction action = _actionList.getSelItem();
    if(action!=null) {
        _moveTable.setItems(action.getMoves());
        if(action.getMoveCount()>0) {
            _moveTable.setSelIndex(0);
            runLater(() -> _actView.setPose(action.getMove(0).getPose()));
        }
    }
    
    // Make PuppetView interactive
    _actView.setPosable(true);
    
    // Configure TimeSlider
    getView("TimeSlider", Slider.class).setMax(1000);
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
    //setViewEnabled("RemovePoseButton", _poseList.getSelIndex()>=0);
}

/**
 * Respond to UI.
 */
protected void respondUI(ViewEvent anEvent)
{
    // Handle PlayLoopButton
    ToggleButton playLoopButton = getView("PlayLoopButton", ToggleButton.class);
    if(!anEvent.equals("PlayLoopButton") && playLoopButton.isSelected()) {
        _actView.stopAction();
        playLoopButton.setSelected(false);
    }

    // Handle ShowMarkersCheckBox
    if(anEvent.equals("ShowMarkersCheckBox"))
        setShowMarkers(anEvent.getBoolValue());
        
    // Handle ActionList
    if(anEvent.equals("ActionList")) {
        PuppetAction action = _actionList.getSelItem(); if(action==null) return;
        _moveTable.setItems(action.getMoves());
        if(action.getMoveCount()>0) {
            _moveTable.setSelIndex(0);
            _actView.setPose(action.getMove(0).getPose());
        }
    }
        
    // Handle AddActionButton
    if(anEvent.equals("AddActionButton")) {
        String name = DialogBox.showInputDialog(_docPane.getUI(), "Add Action", "Enter Action Name:", "Untitled");
        if(name==null || name.length()==0) return;
        PuppetAction action = new PuppetAction(name);
        _actions.addAction(action);
        _actionList.setItems(_actions.getActions());
        _actionList.setSelIndex(_actions.getActionCount()-1);
        _moveTable.setItems(action.getMoves());
        _moveTable.setSelIndex(-1);
    }
    
    // Handle MoveTable
    if(anEvent.equals("MoveTable"))
        _actView.setPose(_moveTable.getSelItem().getPose());
        
    // Handle AddMoveButton
    if(anEvent.equals("AddMoveButton")) {
        PuppetAction action = _actionList.getSelItem(); if(action==null) return;
        String name = DialogBox.showInputDialog(_docPane.getUI(), "Add Move", "Enter Pose Name:", "Untitled");
        if(name==null || name.length()==0) return;
        PuppetPose pose = action.getPoseForName(name);
        if(pose==null) { pose = _actView.getPose(); pose.setName(name); }
        action.addMoveForPoseAndTime(pose, 500);
        _moveTable.setItems(action.getMoves());
        _moveTable.setSelIndex(action.getMoveCount()-1);
        _actions.saveActions();
    }
    
    // Handle CopyMoveMenu
    if(anEvent.equals("CopyMoveMenu")) {
        PuppetMove move = _moveTable.getSelItem();
        if(move==null) { move = new PuppetMove(_actView.getPose(), 500); move.getPose().setName("Untitled"); }
        _copyMove = move.clone();
    }
    
    // Handle PasteMoveMenu
    if(anEvent.equals("PasteMoveMenu")) {
        PuppetAction action = _actionList.getSelItem(); if(action==null) return;
        PuppetMove move = _copyMove!=null? _copyMove.clone() : null; if(move==null) { beep(); return; }
        int ind = _moveTable.getSelIndex() + 1;
        action.addMove(move, ind);
        _moveTable.setItems(action.getMoves());
        _moveTable.setSelIndex(ind);
        _actions.saveActions();
    }
    
    // Handle PastePoseMenu
    if(anEvent.equals("PastePoseMenu")) {
        PuppetAction action = _actionList.getSelItem(); if(action==null) return;
        PuppetMove srcMove = _copyMove!=null? _copyMove.clone() : null; if(srcMove==null) { beep(); return; }
        PuppetMove dstMove = _moveTable.getSelItem(); if(dstMove==null) { beep(); return; }
        action.replacePose(dstMove.getPoseName(), srcMove.getPose());
        _actions.saveActions();
        _actView.setPose(dstMove.getPose());
    }
    
    // Handle DeleteMoveMenu
    if(anEvent.equals("DeleteMoveMenu")) {
        PuppetAction action = _actionList.getSelItem(); if(action==null) return;
        int ind = _moveTable.getSelIndex(); if(ind<0) { beep(); return; }
        action.removeMove(ind);
        _moveTable.setItems(action.getMoves());
        _moveTable.setSelIndex(ind<action.getMoveCount()? ind : action.getMoveCount()-1);
        _actions.saveActions();
    }
    
    // Handle MoveUpMoveMenu
    if(anEvent.equals("MoveUpMoveMenu")) {
        PuppetAction action = _actionList.getSelItem(); if(action==null) return;
        int ind = _moveTable.getSelIndex(); if(ind<1) { beep(); return; }
        PuppetMove move = action.removeMove(ind);
        action.addMove(move, ind-1);
        _moveTable.setItems(action.getMoves());
        _moveTable.setSelIndex(ind-1);
        _actions.saveActions();
    }
    
    // Handle MoveDownMoveMenu
    if(anEvent.equals("MoveDownMoveMenu")) {
        PuppetAction action = _actionList.getSelItem(); if(action==null) return;
        int ind = _moveTable.getSelIndex(); if(ind+1>=action.getMoveCount()) { beep(); return; }
        PuppetMove move = action.removeMove(ind);
        action.addMove(move, ind+1);
        _moveTable.setItems(action.getMoves());
        _moveTable.setSelIndex(ind+1);
        _actions.saveActions();
    }
    
    // Handle PlayButton
    if(anEvent.equals("PlayButton")) {
        PuppetAction action = _actionList.getSelItem(); if(action==null) return;
        _actView.performAction(action, false);
    }
    
    // Handle PlayLoopButton
    if(anEvent.equals("PlayLoopButton")) {
        PuppetAction action = _actionList.getSelItem(); if(action==null) return;
        if(anEvent.getBoolValue()) _actView.performAction(action, true);
        else {
            _actView.stopAction();
            if(_moveTable.getSelItem()!=null)
                _actView.setPose(_moveTable.getSelItem().getPose());
        }
    }
    
    // Handle RenamePoseButton
    /*if(anEvent.equals("RenamePoseButton")) {
        PuppetPose pose = _poseList.getSelItem();
        String name = DialogBox.showInputDialog(_docPane.getUI(), "Rename Pose", "Enter Pose Name:", pose.getName());
        if(name!=null && name.length()>0) pose.setName(name);
        _poseList.updateItems(pose); //_poses.savePoses(); } */

    // Handle TimeSlider
    if(anEvent.equals("TimeSlider")) {
        PuppetAction action = _actionList.getSelItem(); if(action==null) return;
        double val = anEvent.getFloatValue();
        _actView.setPoseForActionAtRatio(action, val/1000);
    }
}

/**
 * Called when cell stops editing.
 */
void moveTableCellEditEnd(ListCell <PuppetMove> aCell)
{
    // Get row/col and make sure there are series/points to cover it
    PuppetMove move = aCell.getItem();
    String text = aCell.getText();
    int col = aCell.getCol();
    
    // If Time column, set time
    if(col==1) {
        move.setTime(Integer.valueOf(text));
        _moveTable.updateItems(move);
    }
}

}