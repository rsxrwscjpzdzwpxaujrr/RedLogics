import org.lwjgl.opengl.Display;

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
        for(Button button: this.buttons)
            button.tick(buttons);

        glBegin(GL_QUADS);
        glColor3f((89.0f / 256.0f) * 0.75f, (229.0f / 256.0f) * 0.75f, (191.0f / 256.0f) * 0.75f);
        glVertex2i(0, 0);
        glVertex2i(Display.getWidth(), 0);

        glColor3f(89.0f / 256.0f, 229.0f / 256.0f, 191.0f / 256.0f);
        glVertex2i(Display.getWidth(), Display.getHeight());
        glVertex2i(0, Display.getHeight());
        glEnd();
    }
}
