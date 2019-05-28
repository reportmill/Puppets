package puppets;
import snap.gfx.*;
import snap.view.*;

/**
 * A class to edit puppet.
 */
public class ActionView extends PuppetView {

/**
 * Creates an ActionView.
 */
public ActionView(Puppet aPuppet, double aScale)
{
    _scale = aScale;
    setPuppet(aPuppet);
    setFill(new Color(.95));
    setBorder(Color.GRAY, 1);
    
    // Remove markers
    for(String name : _puppet.getMarkerNames()) {
        View child = getChild(name);
        removeChild(child);
    }
}


}