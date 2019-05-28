package puppets;
import snap.gfx.*;
import snap.view.*;

/**
 * A class to manage UI to create and edit puppet animations.
 */
public class ActionPane extends ViewOwner {

    // The DocPane
    DocPane            _docPane;
    
    // The puppet action view
    ActionView         _actView;
    
    // The PhysicsRunner
    PhysicsRunner      _physRunner;

/**
 * Creates ActionPane.
 */
public ActionPane(DocPane aDP)  { _docPane = aDP; }

/**
 * Creates UI.
 */
protected View createUI()
{
    _actView = new ActionView(_docPane._puppet, _docPane._scale);
    
    RowView mainRowView = new RowView();
    mainRowView.setAlign(Pos.CENTER);
    mainRowView.addChild(_actView);
    return mainRowView;
}

/**
 * Initialize UI.
 */
protected void initUI()
{
    _physRunner = new PhysicsRunner(_actView);
    _physRunner.setRunning(true);
}

}