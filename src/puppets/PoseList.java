package puppets;
import java.util.*;
import snap.gfx.Point;
import snap.util.*;
import snap.web.WebURL;

/**
 * A class to manage Puppet poses.
 */
public class PoseList {

    // A List of Poses
    List <PuppetPose>   _poses;
    
    // The file path
    String              _filePath = "/Temp/ComicLib/chars/HumanPoses.json";
    
/**
 * Returns the list of poses.
 */
public List <PuppetPose> getPoses()
{
    if(_poses!=null) return _poses;
    return _poses = loadPoses();
}

/**
 * Returns the number of poses.
 */
public int getPoseCount()  { return getPoses().size(); }

/**
 * Returns the individual pose at given index.
 */
public PuppetPose getPose(int anIndex)  { return getPoses().get(anIndex); }

/**
 * Returns the pose with given name.
 */
public PuppetPose getPose(String aName)
{
    for(PuppetPose pose : getPoses())
        if(pose.getName().equals(aName))
            return pose;
    return null;
}

/**
 * Adds a pose.
 */
public void addPose(PuppetPose aPose)
{
    getPoses().add(aPose);
    savePoses();
}

/**
 * Removes a pose.
 */
public PuppetPose removePose(int anIndex)
{
    PuppetPose pose = getPoses().remove(anIndex);
    savePoses();
    return pose;
}

/**
 * Loads pose maps from file.
 */
protected List <PuppetPose> loadPoses()
{
    // Get poses file as string
    WebURL url = WebURL.getURL(_filePath);
    String poseStr = url.getText();
    if(poseStr==null) return new ArrayList();
    
    // Get poses string as JSONNode
    JSONNode posesJSON = new JSONParser().readString(poseStr);
    
    // Iterate over poses
    List <PuppetPose> poses = new ArrayList();
    for(int i=0, iMax=posesJSON.getNodeCount(); i<iMax; i++) {
       String pname = posesJSON.getKey(i);
       JSONNode pjson = posesJSON.getNode(i);
       Map <String,List<Number>> pmap = (Map)pjson.getNative();
       Map <String,Point> pmap2 = convertMapDoublesToPoints(pmap);
       PuppetPose pose = new PuppetPose(pname, pmap2);
       poses.add(pose);
   }
   
   return poses;
}

/**
 * Saves pose maps to file.
 */
protected void savePoses()
{
    // Create top level map of pose names to pose marker maps (with marker map points as list of doubles)
    Map topMap = new LinkedHashMap();
    for(PuppetPose pose : getPoses()) {
        String name = pose.getName();
        Map map = convertMapPointsToDoubles(pose.getMarkers());
        topMap.put(name, map);
    }
    
    // Get top map as string
    JSONNode json = new JSONArchiver().writeObject(topMap);
    String fileStr = json.toString();
    
    // Get string as bytes and write to file
    byte bytes[] = fileStr.getBytes();
    SnapUtils.writeBytes(bytes, _filePath);
}

/** Converts a marker map from points to list of double. */
Map <String,List<Double>> convertMapPointsToDoubles(Map <String,Point> aMap)
{
    Map <String, List<Double>> map = new LinkedHashMap();
    for(String pkey : aMap.keySet()) { Point pnt = aMap.get(pkey);
        map.put(pkey, Arrays.asList(pnt.x, pnt.y)); }
    return map;
}

/** Converts a marker map from doubles list to points. */
Map <String,Point> convertMapDoublesToPoints(Map <String,List<Number>> aMap)
{
    Map <String,Point> map = new LinkedHashMap();
    for(String pkey : aMap.keySet()) { List <Number> vals = aMap.get(pkey);
        map.put(pkey, new Point(vals.get(0).doubleValue(), vals.get(1).doubleValue())); }
    return map;
}

}