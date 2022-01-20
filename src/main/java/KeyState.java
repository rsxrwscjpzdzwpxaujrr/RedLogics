/**
 * Created by Gazibalonchik on 12.01.2016.
 */
public enum KeyState
{
    PRESSEDNOW,
    PRESSED,
    RELEASED,
    DONTPRESSED;

    public Boolean pressed()
    {
        return this == PRESSED || this == PRESSEDNOW;
    }

    public Boolean up()
    {
        return this == RELEASED || this == DONTPRESSED;
    }
}
