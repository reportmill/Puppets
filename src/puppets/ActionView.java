package puppets;
import snap.gfx.*;

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
}


}