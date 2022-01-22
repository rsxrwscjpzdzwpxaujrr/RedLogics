import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Gazibalonchik on 22.02.2016.
 */
public class GUIMainMenu implements GUI
{
    public GUIMainMenu(Button[] buttons)
    {
        for(Button button: buttons)
            this.buttons.add(button);
    }

    @Override
    public void draw(KeyState[] keys, KeyState[] buttons)
    {
        int[] displayWidth = { 0 };
        int[] displayHeight = { 0 };

        glfwGetWindowSize(Settings.window, displayWidth, displayHeight);

        for(Button button: this.buttons)
            button.tick(buttons);

        glBegin(GL_QUADS);
        glColor3f((89.0f / 256.0f) * 0.75f, (229.0f / 256.0f) * 0.75f, (191.0f / 256.0f) * 0.75f);
        glVertex2i(0, 0);
        glVertex2i(displayWidth[0], 0);

        glColor3f(89.0f / 256.0f, 229.0f / 256.0f, 191.0f / 256.0f);
        glVertex2i(displayWidth[0], displayHeight[0]);
        glVertex2i(0, displayHeight[0]);
        glEnd();
    }
}
