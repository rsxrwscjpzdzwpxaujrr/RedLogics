import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;

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
        double[] tempX = { 0 };
        double[] tempY = { 0 };

        glfwGetCursorPos(Settings.window, tempX, tempY);

        int mouseX = (int) tempX[0];
        int mouseY = (int) tempY[0];

        int[] displayHeight = { 0 };

        glfwGetWindowSize(Settings.window, null, displayHeight);

        mouseY = displayHeight[0] - mouseY;

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
