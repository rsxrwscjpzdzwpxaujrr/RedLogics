import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Gazibalonchik on 16.02.2016.
 */
public class Model
{
    private FloatBuffer vertices;
    private FloatBuffer uvs;
    private FloatBuffer normals;
    private IntBuffer faces;
    private int verticesCount;
    private int normalsCount;
    private int uvsCount;
    private int facesCount;
    public final int verticesPerFace = 3;
    private int state = -1;

    public Model()
    {
        verticesCount = 0;
        normalsCount = 0;
        uvsCount = 0;
        facesCount = 0;
        state = -1;
        vertices = FloatBuffer.allocate(2048);
        uvs = FloatBuffer.allocate(2048);
        normals = FloatBuffer.allocate(2048);
        faces = IntBuffer.allocate(8192);
    }

    public void readFromObj(String file)
    {
        try(FileReader reader = new FileReader(file))
        {
            int c;
            String word = "";

            while((c = reader.read()) != -1)
            {
                if(c == (char)10 || c == (char)13 || c == ' ')
                {
                    if(word.length() > 0)
                        onWordEnd(word);

                    if(c == (char)10 || c == (char)13)
                        state = -1;

                    word = "";
                }
                else
                    word += (char)c;
            }
        }
        catch(IOException exception)
        {
            System.out.println(exception.getMessage());
        }
    }

    public void draw()
    {
        switch(verticesPerFace)
        {
        case 3:
            glBegin(GL_TRIANGLES);
            break;

        case 4:
            glBegin(GL_QUADS);
            break;
        }

        for(int i = 0; i < facesCount * verticesPerFace; i++)
        {
            glNormal3f( normals.get(faces.get(i * 3 + 2) * 3),
                        normals.get(faces.get(i * 3 + 2) * 3 + 1),
                        normals.get(faces.get(i * 3 + 2) * 3 + 2));

            //glTexCoord2d(   uvs.get(faces.get(i * 3 + 1) * 3),
            //                uvs.get(faces.get(i * 3 + 1) * 3 + 1));

            glVertex3d( vertices.get(faces.get(i * 3) * 3),
                        vertices.get(faces.get(i * 3) * 3 + 1),
                        vertices.get(faces.get(i * 3) * 3 + 2));
        }

        glEnd();
    }

    private void onWordEnd(String word)
    {
        switch(word)
        {
        case "v":
            state = 0;
            break;

        case "vt":
            state = 1;
            break;

        case "vn":
            state = 2;
            break;

        case "f":
            state = 3;
            facesCount++;
            break;

        default:
            switch(state)
            {
            case 0:
                vertices.put(Float.parseFloat(word));
                verticesCount++;
                break;

            case 1:
                uvs.put(Float.parseFloat(word));
                uvsCount++;
                break;

            case 2:
                normals.put(Float.parseFloat(word));
                normalsCount++;
                break;

            case 3:
                String[] splittedVertices = word.split("/");

                for(String i : splittedVertices)
                    faces.put(Integer.parseInt(i) - 1);
            }
        }
    }
}
