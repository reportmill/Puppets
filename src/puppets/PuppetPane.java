package puppets;
import snap.gfx.*;
import snap.view.*;

/**
 * A class to manage display of Puppet.
 */
public class PuppetPane extends ViewOwner {

    // The DocPane
    DocPane         _docPane;

    // The PuppetView
    PuppetView      _pupView;
    
/**
 * Creates a PuppetPane.
 */
public PuppetPane(DocPane aDP)  { _docPane = aDP; }

/**
 * Creates the UI.
 */
protected View createUI()
{
    _pupView = new PuppetView(_docPane._puppet, _docPane._scale);
    _pupView.setBorder(Color.GRAY, 1);
    
    RowView mainRowView = new RowView();
    mainRowView.setAlign(Pos.CENTER);
    mainRowView.addChild(_pupView);
    return mainRowView;
}

}