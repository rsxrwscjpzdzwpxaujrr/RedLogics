import static org.lwjgl.opengl.GL11.glColor4f;

/**
 * Created by Gazibalonchik on 26.01.2016.
 */
public class Color
{
    public float red;
    public float green;
    public float blue;
    public float alpha;

    Color(float red, float green, float blue, float alpha)
    {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    Color(float red, float green, float blue)
    {
        this(red, green, blue, (byte)127);
    }

    public void apply()
    {
        glColor4f(red, green, blue, alpha);
    }
}
