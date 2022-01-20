import java.util.ArrayList;

/**
 * Created by Gazibalonchik on 08.04.2016.
 */
public class GUIInventory implements GUI
{
    private ArrayList<InventoryCell> inventoryCells = new ArrayList<>();

    @Override
    public void draw(KeyState[] keys, KeyState[] buttons)
    {
        for(Button button: this.buttons)
            button.tick(buttons);

        for(int i = 0; i < inventoryCells.size(); i++)
            inventoryCells.get(i).draw(false, false);
    }
}
