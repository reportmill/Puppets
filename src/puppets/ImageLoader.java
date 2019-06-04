package puppets;
import snap.gfx.Image;
import snap.util.*;
import snap.view.ViewUtils;

/**
 * A custom class.
 */
public class ImageLoader {
    
    // The list of images
    Image             _images[];
    
    // Whether images are loaded
    boolean           _loaded;

    // PropertyChangeSupport
    PropChangeSupport  _loadLsnrs;

    // Constants for properties
    public static final String Loaded_Prop = Image.Loaded_Prop;

/**
 * Creates an ImageLoader.
 */
public ImageLoader(Image[] theImages)
{
    _images = theImages;
    checkLoaded(null);
}

/**
 * Checks whether images are loaded.
 */
void checkLoaded(Image anImg)
{
    // Get last unloaded image in list - if not found, setLoaded and return
    Image img = getUnloadedImage();
    if(img==null) {
        setLoaded(true); return; }
    
    // Add load listener for next unloaded image
    img.addLoadListener(pc -> ViewUtils.runLater(() -> checkLoaded(img)));
}

/**
 * Returns last unloaded image in list.
 */
Image getUnloadedImage()
{
    for(int i=_images.length-1; i>=0; i--) { Image img = _images[i];
        if(!img.isLoaded())
            return img; }
    return null;
}

/**
 * Returns an unloaded image.
 */
int getUnloadedCount()
{
    int c = 0;
    for(int i=_images.length-1; i>=0; i--) { Image img = _images[i]; if(!img.isLoaded()) c++; }
    return c;
}

/**
 * Returns whether puppet is loaded.
 */
public boolean isLoaded()  { return _loaded; }

/**
 * Sets whether image is loaded.
 */
protected void setLoaded(boolean aValue)
{
    if(aValue==_loaded) return;
    _loaded = aValue;
    if(aValue && _loadLsnrs!=null) {
        _loadLsnrs.firePropChange(new PropChange(this, Loaded_Prop, false, true)); _loadLsnrs = null; }
}

/**
 * Adds a load listener. This is cleared automatically when image is loaded.
 */
public void addLoadListener(PropChangeListener aLoadLsnr)
{
    if(isLoaded()) { aLoadLsnr.propertyChange(new PropChange(this, Loaded_Prop, false, true)); return; }
    if(_loadLsnrs==null) _loadLsnrs = new PropChangeSupport(this);
    _loadLsnrs.addPropChangeListener(aLoadLsnr);
}

}