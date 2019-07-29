package puppets.app;

/**
 * A custom class.
 */
public class App {

/**
 * Main method to run panel.
 */
public static void main(String args[])
{
    snaptea.TV.set();
    
    AppPane appPane = new AppPane();
    appPane.setWindowVisible(true);
}

}