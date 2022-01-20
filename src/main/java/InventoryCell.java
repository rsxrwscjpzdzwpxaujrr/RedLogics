import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Gazibalonchik on 02.05.2016.
 */
public class InventoryCell extends Button
{
    Cell cell;

    InventoryCell(int[] sizes)
    {
        super(sizes);
    }

    InventoryCell(int[] sizes, Cell cell)
    {
        super(sizes);
        setCell(cell);
    }

    @Override
    public void onClick()
    {

    }

    @Override
    protected void draw(boolean pressed, boolean mouseOn)
    {
        if(cell != null)
        {
            if(mouseOn)
                glColor4f(0.75f, 0.5f, 0.5f, 1.0f);
            else
                glColor4f(0.0f, 0.5f, 0.5f, 1.0f);
        }
        else
            glColor3f(0.0f, 0.0f, 0.0f);

        glBegin(GL_QUADS);
        glVertex2i(sizes[0], sizes[1]);
        glVertex2i(sizes[2], sizes[1]);
        glVertex2i(sizes[2], sizes[3]);
        glVertex2i(sizes[0], sizes[3]);
        glEnd();
    }

    public Cell getCell()
    {
        return cell;
    }

    public void setCell(Cell cell)
    {
        this.cell = cell;
    }
}
