package puppets;
import java.util.*;
import snap.gfx.*;
import snap.view.*;

/**
 * A class to manage display of Puppet.
 */
public class DesignPane extends ViewOwner {

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
 * Creates a DesignPane.
 */
public DesignPane(DocPane aDP)
{
    _docPane = aDP;
}

/**
 * Returns the puppet.
 */
public Puppet getPuppet()  { return _pupView.getPuppet(); }

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
    
    // Update PartsList selection
    _partsList.setSelItem(aName);
}

/**
 * Sets the selected layer for given name.
 */
public void setSelPart(String aName)
{
    setSelName(aName);
}

/**
 * Returns the selected view.
 */
View getSelView()  { return _selName!=null? _pupView.getChild(_selName) : null; }

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
    _pupView = new PuppetView(_docPane._puppet);
    _pupView.setBorder(Color.LIGHTGRAY, 1);
    
    // Create PuppetBox
    BoxView pupBox = new BoxView(_pupView); pupBox.setGrowWidth(true);
    pupBox.setFill(Color.WHITE); pupBox.setBorder(Color.BLACK, 1);
    
    // Create Parts Label
    Label partsLabel = new Label("Puppet Parts:"); partsLabel.setPadding(4,4,4,4);
    partsLabel.setBorder(Border.createLoweredBevelBorder());
    partsLabel.setFont(new Font("Arial Bold", 20));
    
    // Create/configure PartsList ListView
    _partsList = new ListView(); _partsList.setName("PartsList"); _partsList.setGrowHeight(true);
    
    // Create ToolsColView to hold puppet inspector UI
    ColView toolsColView = new ColView(); toolsColView.setPadding(0,8,8,8);
    toolsColView.setSpacing(5); toolsColView.setFillWidth(true); toolsColView.setPrefWidth(300);
    toolsColView.addChild(partsLabel);
    toolsColView.addChild(_partsList);
    
    RowView mainRowView = new RowView(); mainRowView.setGrowWidth(true); mainRowView.setFillHeight(true);
    mainRowView.addChild(toolsColView);
    mainRowView.addChild(pupBox);
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
    Collections.addAll(partNames, puppet.getMarkerNames());
    _partsList.setItems(partNames);
    
    // Enable PupView drag events
    enableEvents(_pupView, DragEvents);
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
        
    // Handle drop
    if(anEvent.isDragDrop()) {
        Clipboard cb = anEvent.getClipboard();
        if(!cb.hasFiles()) return;
        anEvent.acceptDrag();
        ClipboardData cdata = cb.getFiles().get(0);
        dropFile(cdata);
    }
}

/**
 * Called to handle a file drop on the editor.
 */
private void dropFile(ClipboardData aFile)
{
    // If file not loaded, come back when it is
    if(!aFile.isLoaded()) { aFile.addLoadListener(f -> dropFile(aFile)); return; }

    // Get path and extension (set to empty string if null)
    String ext = aFile.getExtension(); if(ext==null) return; ext = ext.toLowerCase();
    if(!Image.canRead(ext)) return;

    // Get image
    Object imgSrc = aFile.getSourceURL()!=null? aFile.getSourceURL() : aFile.getBytes();
    Image img = Image.get(imgSrc);
    
    // Set new image for puppet part
    setPuppetPartImage(getSelName(), img);
}
        
/**
 * Sets a puppet part image.
 */
void setPuppetPartImage(String aName, Image anImage)
{
    PuppetPart part = getPuppet().getPart(aName);
    int w = part.getImage().getPixWidth(), h = part.getImage().getPixHeight();
    Image img2 = Image.get(w, h, true);
    Painter pntr = img2.getPainter();
    pntr.drawImage(anImage, 0, 0, w, h);
    part._img = img2;
    _pupView.rebuildChildren();
}

}