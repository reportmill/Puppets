package puppets;
import java.util.*;
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
    
    // The selected part name
    String             _selName;

    // Constants
    static Color SELECT_COLOR = Color.get("#039ed3");
    static Effect SELECT_EFFECT = new ShadowEffect(8, SELECT_COLOR, 0, 0);

/**
 * Creates a PuppetPane.
 */
public PuppetPane(DocPane aDP)  { _docPane = aDP; }

/**
 * Returns the selected part.
 */
public String getSelName()  { return _selName; }

/**
 * Sets the selected layer.
 */
public void setSelName(String aName)
{
    if(getSelView()!=null) getSelView().setEffect(null);
    _selName = aName;
    if(getSelView()!=null) getSelView().setEffect(SELECT_EFFECT);
}

/**
 * Sets the selected layer for given name.
 */
public void setSelPart(String aName)
{
    setSelName(aName);
}

View getSelView()
{
    if(_selName==null) return null;
    return _pupView.getChild(_selName);
}

/**
 * Event handling from select tool for super selected shapes.
 */
public void puppetViewMousePressed(ViewEvent anEvent)
{
    Point pnt = anEvent.getPoint();
    View hitView = ViewUtils.getChildAt(_pupView, pnt.x, pnt.y);
    if(hitView!=null)
        setSelPart(hitView.getName());
}

/**
 * Creates the UI.
 */
protected View createUI()
{
    // Create PuppetView
    _pupView = new PuppetView(_docPane._puppet, _docPane._scale);
    _pupView.setBorder(Color.LIGHTGRAY, 1);
    
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
    // Configure PupView
    _pupView.addEventHandler(e -> puppetViewMousePressed(e), MousePress);
    
    // Sets PartsList Items
    Puppet puppet = _pupView.getPuppet();
    List <String> partNames = new ArrayList();
    Collections.addAll(partNames, puppet.getPartNames());
    Collections.addAll(partNames, puppet.getJointNames());
    _partsList.setItems(partNames);
}

/**
 * Updates the UI controls from currently selected page.
 */
public void resetUI()
{
    // Update PartsList selection
    _partsList.setSelItem(getSelName());
}

/**
 * Responds to UI.
 */
public void respondUI(ViewEvent anEvent)
{
    // Handle PartsList
    if(anEvent.equals("PartsList"))
        setSelName(_partsList.getSelItem());
}

}