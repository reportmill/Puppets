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
    // Create ActionView
    _actView = new ActionView(_docPane._puppet, _docPane._scale);
    
    // Create PuppetBox
    BoxView pupBox = new BoxView(_actView); pupBox.setGrowWidth(true);
    pupBox.setFill(Color.WHITE); pupBox.setBorder(Color.BLACK, 1);
    
    // Create ToolsColView to hold puppet inspector UI
    ColView toolsColView = new ColView(); toolsColView.setPadding(20,8,8,8);
    toolsColView.setSpacing(5); toolsColView.setFillWidth(true); toolsColView.setPrefWidth(300);
    
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

}