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


}