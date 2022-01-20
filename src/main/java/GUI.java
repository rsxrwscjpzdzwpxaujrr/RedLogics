import java.util.ArrayList;

/**
 * Created by Gazibalonchik on 22.02.2016.
 */
public interface GUI
{
    ArrayList<Button> buttons = new ArrayList<>();

    void draw(KeyState[] keys, KeyState[] buttons);
}
