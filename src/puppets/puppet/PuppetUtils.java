package puppets.puppet;
import snap.gfx.*;

/**
 * A class to handle miscellaneous functionality for Puppet.
 */
public class PuppetUtils {

    // The marker image
    static Image             _markerImg, _anchorImage;

/**
 * Returns the joint/marker image.
 */
public static Image getMarkerImage()
{
    if(_markerImg!=null) return _markerImg; double s = 23;
    Image img = Image.get((int)s, (int)s, true);
    Painter pntr = img.getPainter(); Shape arc = new Arc(0,0,s,s,0,360);
    Color fill = new Color("#E49956D0"); // Yellow: #EFD969E0, Orange: #E49956E0
    pntr.setColor(fill); pntr.fill(arc);
    pntr.setColor(fill.darker().darker()); pntr.setStroke(Stroke.getStroke(.5)); pntr.draw(arc);
    pntr.setColor(Color.BLACK); pntr.setStroke(Stroke.Stroke2);
    pntr.drawLine(5,s/2,s-5,s/2); pntr.drawLine(s/2,5,s/2,s-5);
    return _markerImg = img;
}

/**
 * Returns the anchor image.
 */
public static Image getAnchorImage()
{
    if(_anchorImage!=null) return _anchorImage; double s = 90;
    Image img = Image.get((int)s, (int)s, true);
    Painter pntr = img.getPainter(); pntr.setStroke(Stroke.getStroke(4));
    Color red = new Color("#D54438"), blue = new Color("#4B77B8");
    pntr.setColor(red); pntr.drawLine(5,s/2,s-5,s/2);
    pntr.setColor(blue); pntr.drawLine(s/2,5,s/2,s-5);
    return _anchorImage = img;
}

}