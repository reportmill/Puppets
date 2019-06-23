package puppets;
import java.util.*;
import snap.view.*;
import snap.gfx.*;

/**
 * A class to manage UI to generate sprites for puppets.
 */
public class SpritePane extends ViewOwner {

    // The DocPane
    DocPane                  _docPane;
    
    // A List of Puppet Actions
    PuppetActions            _actions;
    
    // The image view
    ImageView                _imgView;
    
    // A ListView to show actions
    ListView <PuppetAction>  _actionList;

/**
 * Creates SpritePane.
 */
public SpritePane(DocPane aDP)
{
    _docPane = aDP;
    _actions = new PuppetActions();
}

/**
 * Initialize UI.
 */
protected void initUI()
{
    // Create ImageView
    _imgView = new ImageView();
    
    // Get PuppetBox and add ActionView
    BoxView pupBox = getView("PuppetBox", BoxView.class);
    pupBox.setContent(_imgView);
    
    // Set ActionList
    _actionList = getView("ActionList", ListView.class);
    _actionList.setItemTextFunction(action -> { return action.getName(); });
    _actionList.setItems(_actions.getActions());
    _actionList.setSelIndex(0);
}

/**
 * Respond to UI.
 */
protected void respondUI(ViewEvent anEvent)
{
    // Handle PlayButton
    if(anEvent.equals("PlayButton"))
        setSpriteImage();
}

/**
 * Sets the sprite image.
 */
protected void setSpriteImage()
{
    Puppet puppet = _docPane._puppet;
    PuppetAction action = _actionList.getSelItem();
    Image img = getImage(puppet, action);
    
    ImageView iview = new ImageView(img);
    BoxView pupBox = getView("PuppetBox", BoxView.class);
    pupBox.setContent(iview);
    
    int time = action.getMaxTime();
    iview.getAnim(time).setValue("Frame", img.getImageSet().getCount()).play();
}

/**
 * Returns an image for given action.
 */
public Image getImage(Puppet aPuppet, PuppetAction anAction)
{
    ActionView actView = new ActionView(aPuppet);
    actView._pupHeight = 200;
    actView.setPuppet(aPuppet);
    actView.setPosable(true);
    actView.setAction(anAction);
    actView.setFill(null);
    actView.setBorder(null);
    
    int FRAME_DELAY_MILLIS = 25, frameCount = anAction.getMaxTime()/25;
    List <Image> images = new ArrayList();
    for(int i=0; i<frameCount; i++) {
        actView.setActionTime(i*25);
        actView._physRunner.resolveMouseJoints();
        Image img = ViewUtils.getImage(actView);
        images.add(img);
    }
    
    ImageSet imgSet = new ImageSet(images);
    Image img = imgSet.getImage(0);
    return img;
}

}