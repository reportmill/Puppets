package puppets;
import snap.gfx.*;
import snap.view.*;

/**
 * A class to manage display of Puppet.
 */
public class PuppetPane extends ViewOwner {

    // The DocPane
    DocPane            _docPane;

    // The PuppetView
    PuppetView         _pupView;
    
    // The ListView for parts/joints
    ListView <String>  _partsList;
    
/**
 * Creates a PuppetPane.
 */
public PuppetPane(DocPane aDP)  { _docPane = aDP; }

/**
 * Creates the UI.
 */
protected View createUI()
{
    // Create PuppetView
    _pupView = new PuppetView(_docPane._puppet, _docPane._scale);
    _pupView.setBorder(Color.GRAY, 1);
    
    // Create PuppetBox
    BoxView pupBox = new BoxView(_pupView); pupBox.setGrowWidth(true);
    pupBox.setFill(Color.WHITE); pupBox.setBorder(Color.BLACK, 1);
    
    // Create/configure PartsList ListView
    _partsList = new ListView(); _partsList.setName("PartsList"); _partsList.setGrowHeight(true);
    
    // Create ToolsColView to hold puppet inspector UI
    ColView toolsColView = new ColView(); toolsColView.setPadding(20,8,8,8);
    toolsColView.setSpacing(5); toolsColView.setFillWidth(true); toolsColView.setPrefWidth(300);
    toolsColView.addChild(_partsList);
    
    RowView mainRowView = new RowView(); mainRowView.setGrowWidth(true); mainRowView.setFillHeight(true);
    mainRowView.addChild(pupBox);
    mainRowView.addChild(toolsColView);
    return mainRowView;
}

/**
 * Initialize UI.
 */
protected void initUI()
{
    // Sets PartsList Items
    Puppet puppet = _pupView.getPuppet();
    _partsList.setItems(puppet.getPartNames());
}

/**
 * Updates the UI controls from currently selected page.
 */
public void resetUI()
{
    // Update PartsList selection
    //_partsList.setSelItem(getSelName());
}

/**
 * Responds to UI.
 */
public void respondUI(ViewEvent anEvent)
{
    // Handle PartsList
    //if(anEvent.equals("PartsList"))
        //setSelName(_partsList.getSelItem());
}

}