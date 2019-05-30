package puppets;
import java.util.*;
import snap.gfx.Point;
import snap.util.StringUtils;

/**
 * A class to hold a pose.
 */
public class PuppetPose {
    
    // The pose name
    String              _name;
    
    // The pose marker maps
    Map <String,Point>  _markers;
    
/**
 * Creates a new pose for name.
 */
public PuppetPose(String aName)  { _name = aName; _markers = new LinkedHashMap(); }

/**
 * Creates a new pose for name and map or markers.
 */
public PuppetPose(String aName, Map <String,Point> aMap)  { _name = aName; _markers = aMap; }

/**
 * Returns the name.
 */
public String getName()  { return _name; }

/**
 * Sets the name.
 */
public void setName(String aName)  { _name = aName; }

/**
 * Returns the maps.
 */
public Map getMarkers()  { return _markers; }

/**
 * Returns the point for given marker.
 */
public Point getMarkerPoint(String aName)  { return _markers.get(aName); }

/**
 * Sets a point for given marker name.
 */
public void setMarkerPoint(String aName, Point aPoint)  { _markers.put(aName, aPoint); }

/**
 * Returns a full string.
 */
public String getAsString()
{
    StringBuffer sb = new StringBuffer();
    for(String key : _markers.keySet()) { Point pnt = getMarkerPoint(key);
        String x = StringUtils.formatNum("#.#", pnt.x), y = StringUtils.formatNum("#.#", pnt.y);
        String str = String.format("%s: [ %s %s ],\n", key, x, y); sb.append(str);
    }
    return sb.toString();
}

}