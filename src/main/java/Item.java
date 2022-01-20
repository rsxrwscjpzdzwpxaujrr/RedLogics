/**
 * Created by Gazibalonchik on 30.03.2016.
 */
public class Item implements Cell
{
    private String name;
    private boolean isStackable;

    public Item(String name, boolean isStackable)
    {
        this.name = name;
        this.isStackable = isStackable;
    }

    @Override
    public String getName()
    {
        return name;
    }

    public boolean isStackable()
    {
        return isStackable;
    }
}
