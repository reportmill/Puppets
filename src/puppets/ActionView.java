package puppets;
import snap.gfx.*;
import snap.view.*;

/**
 * A class to edit puppet.
 */
public class ActionView extends PuppetView {
    
    // The PuppetAction currently playing
    PuppetAction    _action;
    
    // The currently configured move index
    int             _moveIndex;
    
    // Whether timer loops
    boolean         _loops;
    
    // The ViewTimer
    ViewTimer       _timer;
    
    // The current action time
    int             _actTime;

    // Constants for properties
    public static final String Action_Prop = "Action";
    public static final String ActionTime_Prop = "ActionTime";
    public static final String Move_Prop = "Move";
    
    // Constants
    static int   FRAME_DELAY_MILLIS = 20;
    static float FRAME_DELAY_SECS = 20/1000f;

/**
 * Creates an ActionView.
 */
public ActionView(Puppet aPuppet)
{
    setPuppet(aPuppet);
    setFill(new Color(.95));
    setBorder(Color.GRAY, 1);
    
    // Make markers not visible
    for(String name : _puppet.getMarkerNames()) {
        View child = getChild(name);
        child.setVisible(false);
    }
}

/**
 * Returns the PuppetAction currently playing.
 */
public PuppetAction getAction()  { return _action; }

/**
 * Sets the PuppetAction currently playing.
 */
public void setAction(PuppetAction anAction)
{
    // If already set, just return
    if(anAction==_action) return;
    
    // Cache old, set new
    PuppetAction oldVal = _action; _action = anAction;
    
    // Fire proop change
    firePropChange(Action_Prop, oldVal, _action);
}

/**
 * Returns the current puppet move.
 */
public PuppetMove getMove()  { return _moveIndex>=0? _action.getMove(_moveIndex) : null; }

/**
 * Sets the current puppet move.
 */
public void setMove(PuppetMove aMove)
{
    int ind = _action.getMoves().indexOf(aMove);
    setMoveIndex(ind);
}

/**
 * Returns the move index.
 */
public int getMoveIndex()  { return _moveIndex; }

/**
 * Sets the move index.
 */
protected void setMoveIndex(int anIndex)
{
    // If already set, just return
    if(anIndex==_moveIndex) return;
    
    // Cache old, set new
    int oldVal = _moveIndex; _moveIndex = anIndex;
    
    // Fire prop change
    firePropChange(Move_Prop, oldVal, _moveIndex);
}

/**
 * Returns the action time.
 */
public int getActionTime()  { return _actTime; }

/**
 * Sets the action time.
 */
public void setActionTime(int aTime)
{
    // Cache old, set new time (constrained to Action.MaxTime)
    int oldVal = _actTime; _actTime = Math.min(aTime, _action.getMaxTime());
    
    // Update MoveIndex and Pose
    setMoveForTime(_actTime);
    setPoseForTime(_actTime);
    
    // Fire prop change
    firePropChange(ActionTime_Prop, oldVal, _actTime);
}

/**
 * Sets the action time for given time ratio.
 */
public void setActionTimeForTimeRatio(double aRatio)
{
    int time = _action.getTimeForTimeRatio(aRatio);
    setActionTime(time);
}

/**
 * Sets the move for given time.
 */
protected void setMoveForTime(int aTime)
{
    int moveIndex = _action.getMoveIndexAtTime(aTime);
    setMoveIndex(moveIndex);
}

/**
 * Sets the pose for given time.
 */
protected void setPoseForTime(int aTime)
{
    PuppetPose pose = _action.getPoseForTime(getPuppet(), aTime);
    setPose(pose);
}

/**
 * Returns whether action is playing.
 */
public boolean isPlaying()  { return _timer!=null; }

/**
 * Starts playing the ActionView action with option to loop.
 */
public void playAction(boolean doLoop)
{
    // If already playing or insufficient moves, just return
    if(isPlaying() || _action.getMoveCount()<2) return;
    
    // Create timer and start
    _loops = doLoop;
    _timer = new ViewTimer(FRAME_DELAY_MILLIS, t -> timerFired());
    _timer.start();
}

/**
 * Stops the currently running action.
 */
public void stopAction()
{
    if(_timer!=null) _timer.stop();
    _timer = null;
}

/**
 * Called when timer fires.
 */
void timerFired()
{
    // Get timer time (adjust if looping) and set
    int time = _timer.getTime();
    if(_loops) time = time%_action.getMaxTime();
    setActionTime(time);
    
    // If beyond Action.MaxTime, stop anim
    if(!_loops && time>_action.getMaxTime())
        stopAction();
}

}