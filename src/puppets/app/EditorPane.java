package puppets.app;
import java.util.*;
import puppets.puppet.*;
import snap.gfx.*;
import snap.view.*;

/**
 * A class to manage editing of Puppet.
 */
public class EditorPane extends ViewOwner {

    // The AppPane
    AppPane            _appPane;
    
    // The ListView for Puppets
    ListView <String>  _pupList;

    // The ListView for parts/joints
    ListView <String>  _partsList;
    
    // The PuppetView
    PuppetView         _pupView;
    
    // The selected part name
    String             _selName, _dragName;

    // Constants
    static Color SELECT_COLOR = Color.get("#039ed3");
    static Effect SELECT_EFFECT = new ShadowEffect(8, SELECT_COLOR, 0, 0);
    static String PUPPET_NAMES[] = { "Lady", "Man" };

/**
 * Creates a EditorPane.
 */
public EditorPane(AppPane aAP)
{
    _appPane = aAP;
}

/**
 * Returns the puppet.
 */
public Puppet getPuppet()  { return _pupView.getPuppet(); }

/**
 * Returns the puppet part at X/Y.
 */
public PuppetPart getPuppetPartAtPoint(double aX, double aY)
{
    View hitView = ViewUtils.getChildAt(_pupView, aX, aY);
    String name = hitView!=null? hitView.getName() : null;
    PuppetPart part = name!=null? getPuppet().getPart(name) : null;
    return part;
}

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
View getDragView()  { return _dragName!=null? _pupView.getChild(_dragName) : null; }

/**
 * Returns the drag part.
 */
public String getDragName()  { return _selName; }

/**
 * Sets the selected layer.
 */
void setDragName(String aName)
{
    if(getDragView()!=null) getDragView().setEffect(null);
    _dragName = aName;
    if(getDragView()!=null) getDragView().setEffect(SELECT_EFFECT);
    
    if(_dragName!=null && getSelView()!=null) getSelView().setEffect(null);
    else if(_dragName==null && getSelView()!=null) getSelView().setEffect(SELECT_EFFECT);
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
 * Initialize UI.
 */
protected void initUI()
{
    // Configure PuppetList
    _pupList = getView("PuppetList", ListView.class);
    _pupList.setItems(PUPPET_NAMES);
    
    // Get/configure PupView
    _pupView = new PuppetView(_appPane._puppet);
    _pupView.setBorder(Color.LIGHTGRAY, 1);
    _pupView.addEventHandler(e -> puppetViewMousePressed(e), MousePress);
    
    // Get PuppetBox and add PupView
    BoxView pupBox = getView("PuppetBox", BoxView.class);
    pupBox.setContent(_pupView);
    
    // Get PartNames
    Puppet puppet = _pupView.getPuppet();
    List <String> partNames = new ArrayList();
    Collections.addAll(partNames, puppet.getPartNames());
    Collections.addAll(partNames, puppet.getJointNames());
    Collections.addAll(partNames, puppet.getMarkerNames());
    
    // Get/configure PartsList
    _partsList = getView("PartsList", ListView.class);
    _partsList.setItems(partNames);
    
    // Enable PupView drag events
    enableEvents(_pupView, DragEvents);
}

/**
 * Updates the UI controls from currently selected page.
 */
public void resetUI()
{
    // Update PupList selection
    _pupList.setSelItem(getPuppet().getName());
    
    // Update PartsList selection
    _partsList.setSelItem(getSelName());
}

/**
 * Responds to UI.
 */
public void respondUI(ViewEvent anEvent)
{
    // Handle PupList
    if(anEvent.equals("PuppetList"))
        _appPane.open(anEvent.getStringValue());
        
    // Handle PartsList
    if(anEvent.equals("PartsList"))
        setSelName(_partsList.getSelItem());
        
    // Handle drag over
    if(anEvent.isDragOver()) {
        Clipboard cb = anEvent.getClipboard(); if(!cb.hasFiles()) return;
        anEvent.acceptDrag();
        PuppetPart part = getPuppetPartAtPoint(anEvent.getX(), anEvent.getY());
        setDragName(part!=null? part.getName() : null);
    }
    
    // Handle drop
    if(anEvent.isDragDrop()) {
        Clipboard cb = anEvent.getClipboard(); if(!cb.hasFiles()) return;
        anEvent.acceptDrag();
        ClipboardData cdata = cb.getFiles().get(0);
        dropFile(anEvent, cdata);
        anEvent.dropComplete();
    }
    
    // Handle drop done
    if(anEvent.isDragExit())
        setDragName(null);
}

/**
 * Called to handle a file drop on the editor.
 */
private void dropFile(ViewEvent anEvent, ClipboardData aFile)
{
    // If file not loaded, come back when it is
    if(!aFile.isLoaded()) { aFile.addLoadListener(f -> dropFile(anEvent, aFile)); return; }

    // Get path and extension (set to empty string if null)
    String ext = aFile.getExtension(); if(ext==null) return; ext = ext.toLowerCase();
    if(!Image.canRead(ext)) return;

    // Get image
    Object imgSrc = aFile.getSourceURL()!=null? aFile.getSourceURL() : aFile.getBytes();
    Image img = Image.get(imgSrc);
    
    // Set new image for puppet part
    PuppetPart part = getPuppetPartAtPoint(anEvent.getX(), anEvent.getY());
    if(part!=null)
        setPuppetPartImage(part, img);
}
        
/**
 * Sets a puppet part image.
 */
void setPuppetPartImage(PuppetPart aPart, Image anImage)
{
    int w = aPart.getImage().getPixWidth(), h = aPart.getImage().getPixHeight();
    Image img2 = Image.get(w, h, true);
    Painter pntr = img2.getPainter();
    pntr.drawImage(anImage, 0, 0, w, h);
    aPart.setImage(img2);
    _pupView.rebuildChildren();
}

}