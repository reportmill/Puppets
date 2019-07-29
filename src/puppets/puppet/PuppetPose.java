package puppets.puppet;
import java.util.*;
import snap.gfx.Point;
import snap.util.*;

/**
 * A class to hold a pose.
 */
public class PuppetPose implements Cloneable {
    
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
public PuppetPose getBlendPose(Puppet aPuppet, PuppetPose aPose, double aRatio)
{
    // Copy this pose
    PuppetPose pose = clone();
    
    // Iterate over puppet root joint names
    for(String name : aPuppet.getRootJointNames()) {
        
        // Set blend marker point for root joint
        setBlendPoseMarker(pose, aPose, name, aRatio);
        
        // If next joint, set blend point for it
        String jointThis = name, jointNext = aPuppet.getNextJointNameForName(name);
        while(jointNext!=null) {
            pose.setBlendPoseMarker(this, aPose, jointThis, jointNext, aRatio);
            jointThis = jointNext; jointNext = aPuppet.getNextJointNameForName(jointNext);
        }
    }
    
    // Return pose
    return pose;
}

/**
 * Returns a blended pose with this pose and another pose at given ratio.
 */
void setBlendPoseMarker(PuppetPose aPose1, PuppetPose aPose2, String aJointName, double aRatio)
{
    // Blend root names
    Point p0 = aPose1.getMarkerPoint(aJointName);
    Point p1 = aPose2.getMarkerPoint(aJointName);
    double x = p0.x + (p1.x-p0.x)*aRatio;
    double y = p0.y + (p1.y-p0.y)*aRatio;
    p0.setXY(x,y);
}

/**
 * Returns a blended pose with this pose and another pose at given ratio.
 */
void setBlendPoseMarker(PuppetPose aPose1, PuppetPose aPose2, String aJointName1, String aJointName2, double aRatio)
{
    // Get start points of start line and end line
    Point p0 = aPose1.getMarkerPoint(aJointName1);
    Point p1 = aPose2.getMarkerPoint(aJointName1);
    
    // Get end points of start line and end line
    Point p2 = aPose1.getMarkerPoint(aJointName2);
    Point p3 = aPose2.getMarkerPoint(aJointName2);
    if(p2.equals(p3)) return;
    
    // Get angle of start line, end line and blend line
    double ang0 = getAngle(p0, p2);
    double ang1 = getAngle(p1, p3);
    double dang = ang1 - ang0; if(Math.abs(dang)>Math.PI) dang = Math.copySign(2*Math.PI - Math.abs(dang), -dang);
    double ang2 = ang0 + dang*aRatio;
    
    // Get distance of start line and calculate end point of blend line and set
    double dist = p1.getDistance(p3) + .2;
    Point p0b = getMarkerPoint(aJointName1);
    double x2 = p0b.x + Math.cos(ang2)*dist;
    double y2 = p0b.y + Math.sin(ang2)*dist;
    Point p1b = getMarkerPoint(aJointName2);
    p1b.setXY(x2, y2);
}

/**
 * Returns the angle for a set of line points.
 */
public static double getAngle(Point p0, Point p1)  { return getAngle(p0.x, p0.y, p1.x, p1.y); }

/**
 * Returns the angle for a set of line point coordinates.
 */
public static double getAngle(double x0, double y0, double x1, double y1)  { return Math.atan2(y1 - y0, x1 - x0); }

/**
 * Standard clone implementation.
 */
public PuppetPose clone()
{
    PuppetPose clone = null; try { clone = (PuppetPose)super.clone(); }
    catch(Exception e) { throw new RuntimeException(e); }
    clone._markers = new LinkedHashMap(_markers.size());
    for(String name : _markers.keySet())
        clone._markers.put(name, _markers.get(name).clone());
    return clone;
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