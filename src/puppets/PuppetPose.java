package puppets;
import java.util.*;
import snap.gfx.Point;
import snap.util.*;

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
public PuppetPose()  { _markers = new LinkedHashMap(); }

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
public Map <String,Point> getMarkers()  { return _markers; }

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

/**
 * Returns a blended pose with this pose and another pose at given ratio.
 */
public PuppetPose getBlendPose(PuppetPose aPose, double aRatio)
{
    PuppetPose pose = new PuppetPose();
    for(String name : _markers.keySet()) {
        Point p0 = getMarkerPoint(name);
        Point p1 = aPose.getMarkerPoint(name);
        double x = p0.x + (p1.x-p0.x)*aRatio;
        double y = p0.y + (p1.y-p0.y)*aRatio;
        pose._markers.put(name, new Point(x,y));
    }
    return pose;
}

/**
 * XML Archival.
 */
public XMLElement toXML(XMLArchiver anArchiver)
{
    // Get new element with name
    XMLElement e = new XMLElement("Pose");
    e.add("Name", getName());
    
    // Iterate over markers and set
    for(String key : getMarkers().keySet()) { Point pnt = getMarkerPoint(key);
        String val = StringUtils.formatNum("#.##", pnt.x) + ' ' + StringUtils.formatNum("#.##", pnt.y);
        e.add(key,val);
    }
    
    // Return element
    return e;
}

/**
 * XML unarchival.
 */
public PuppetPose fromXML(XMLArchiver anArchiver, XMLElement anElement)
{
    // Unarchive name
    String name = anElement.getAttributeValue("Name");
    setName(name);
    
    // Iterate over markers and set
    Map <String,Point> markers = new LinkedHashMap();
    for(XMLAttribute attr : anElement.getAttributes()) {
        String key = attr.getName(), valStr = attr.getValue(); if(key.equals("Name")) continue;
        String valStrs[] = valStr.split("\\s");
        Double val0 = Double.valueOf(valStrs[0]), val1 = Double.valueOf(valStrs[1]);
        Point pnt = new Point(val0, val1);
        _markers.put(key, pnt);
    }

    // Return this
    return this;
}

}