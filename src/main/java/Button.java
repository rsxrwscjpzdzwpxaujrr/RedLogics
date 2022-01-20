import org.lwjgl.input.Mouse;

/**
 * Created by Gazibalonchik on 22.02.2016.
 */
public abstract class Button
{
    public int[] sizes = new int[4];
    public boolean locked;

    Button(int[] sizes)
    {
        locked = false;
        this.sizes = sizes;
    }

    public void tick(KeyState[] buttons)
    {
        int mouseX = Mouse.getX();
        int mouseY = Mouse.getY();
        boolean mouseOn = mouseX > sizes[0] && mouseX < sizes[2] && mouseY > sizes[1] && mouseY < sizes[3];
        boolean pressed = buttons[0] == KeyState.RELEASED && mouseOn;

        try
        {
            if(pressed)
                onClick();
        }
        catch(NullPointerException exception)
        {
            System.out.println(exception.getMessage());
        }

        draw(pressed, mouseOn);
    }

    public abstract void onClick();

    protected abstract void draw(boolean pressed, boolean mouseOn);
}
