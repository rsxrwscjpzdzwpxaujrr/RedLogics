import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.GLU;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

import java.awt.*;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;

public class Game
{
    private World world;
    private Player player;
    public KeyState[] keys = new KeyState[256];
    public KeyState[] buttons = new KeyState[4];
    public boolean paused = false;
    private double deltaTime;
    private int colorTextureID;
    private int framebufferID;
    private int depthRenderBufferID;
    private boolean vsync;
    private boolean gameStarted;
    private GUI mainMenu;
    private TrueTypeFont font;

    FloatBuffer diffuse;
    FloatBuffer direction;
    FloatBuffer ambient;

    private void display()
    {
        glEnable(GL_LIGHTING);

        glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, framebufferID);

        if(gameStarted)
        {
            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            GLU.gluPerspective(player.camera.fov, (float)Display.getWidth() / (float)Display.getHeight(), 0.1f, 2048.0f);
            glMatrixMode(GL_MODELVIEW);

            glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glLoadIdentity();

            glRotated(player.camera.getPitch() * (180.0 / Math.PI), 1.0, 0.0, 0.0);
            glRotated(player.camera.getYaw() * (180.0 / Math.PI), 0.0, 1.0, 0.0);

            glTranslated(-player.camera.positionX, -player.camera.positionY, -player.camera.positionZ);

            glLight(GL_LIGHT0, GL_DIFFUSE, diffuse);
            glLight(GL_LIGHT0, GL_POSITION, direction);
            glLight(GL_LIGHT0, GL_AMBIENT, ambient);

            world.draw(player.camera);
        }

        glDisable(GL_LIGHTING);
        glEnable(GL_TEXTURE_2D);
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);

        glClearColor (0.0f, 1.0f, 1.0f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glViewport(0, 0, Display.getWidth(), Display.getHeight());
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0.0, Display.getWidth(), 0.0, Display.getHeight(), 1.0, -1.0);
        glMatrixMode(GL_MODELVIEW);

        glBindTexture(GL_TEXTURE_2D, colorTextureID);

        glLoadIdentity();
        font.drawString(400, 400, "dsafadf", Color.red);
        glDisable(GL_TEXTURE_2D);

        if(!gameStarted)
            mainMenu.draw(keys, buttons);

        glEnable(GL_TEXTURE_2D);

        glColor3f(1.0f, 1.0f, 1.0f);
        glBegin(GL_QUADS);
            glTexCoord2f(0.0f, 0.0f); glVertex2i(0, 0);
            glTexCoord2f(1.0f, 0.0f); glVertex2i(Display.getWidth(), 0);
            glTexCoord2f(1.0f, 1.0f); glVertex2i(Display.getWidth(), Display.getHeight());
            glTexCoord2f(0.0f, 1.0f); glVertex2i(0, Display.getHeight());
        glEnd();


        //glDisable(GL_TEXTURE_2D);

        //glFlush ();
    }

    public void tick()
    {
        if(keys[Settings.pauseKey] == KeyState.RELEASED)
            setPaused(!paused);

        if(!paused && gameStarted)
        {
            world.tick(deltaTime);
            player.tick(world, keys, buttons, deltaTime);
        }

        if(Display.wasResized())
            openGLInit();
    }

    private void openGLInit()
    {
        diffuse = BufferUtils.createFloatBuffer(4);
        diffuse.put(1.05f).put(1.05f).put(0.5f).put(0.0f).flip();

        direction = BufferUtils.createFloatBuffer(4);
        direction.put(0.5f).put(1.0f).put(0.2f).put(0.0f).flip();

        ambient = BufferUtils.createFloatBuffer(4);
        ambient.put(0.3f).put(0.4f).put(0.5f).put(0.0f).flip();

        // init OpenGL here
        glEnable(GL_CULL_FACE);
        glEnable(GL_NORMALIZE);
        glCullFace(GL_BACK);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_ALPHA_TEST);
        //glEnable(GL_FOG);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        //glFogf(GL_FOG_DENSITY, 0.001953125f);
        //glHint(GL_FOG_HINT, GL_DONT_CARE);
        //glFogf(GL_FOG_START, 0.0f);
        //glFogf(GL_FOG_END, 1024.0f);
        glEnable(GL_LIGHTING);
        glLightModelf(GL_LIGHT_MODEL_TWO_SIDE, GL_FALSE);
        glEnable(GL_LIGHT0);
        glEnable(GL_COLOR_MATERIAL);
        //glViewport(0, 0, Display.getWidth(), Display.getHeight());
        //glMatrixMode(GL_PROJECTION);
        //glLoadIdentity();
        //glOrtho(0, 800, 0, 600, 1, -1);
        //gluPerspective(player.camera.fov, (float)Display.getWidth() / (float)Display.getHeight(), 0.1f, 2048.0f);
        //glMatrixMode(GL_MODELVIEW);

        if (!GLContext.getCapabilities().GL_EXT_framebuffer_object)
        {
            System.out.println("FBO not supported");
            System.exit(0);
        }
        else
        {
            framebufferID = glGenFramebuffersEXT();
            colorTextureID = glGenTextures();
            depthRenderBufferID = glGenRenderbuffersEXT();

            int displayWidth = Display.getWidth();
            int displayHeight = Display.getHeight();

            glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, framebufferID);

            glBindTexture(GL_TEXTURE_2D, colorTextureID);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, displayWidth, displayHeight, 0, GL_RGBA, GL_INT, (java.nio.ByteBuffer) null);
            glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, colorTextureID, 0);

            glBindRenderbufferEXT(GL_RENDERBUFFER_EXT, depthRenderBufferID);
            glRenderbufferStorageEXT(GL_RENDERBUFFER_EXT, GL14.GL_DEPTH_COMPONENT24, displayWidth, displayHeight);
            glFramebufferRenderbufferEXT(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT_EXT, GL_RENDERBUFFER_EXT, depthRenderBufferID);

            glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
        }
    }

    public void start()
    {
        Settings.mouseSensitivity = 0.002f;
        Settings.upKey =     Keyboard.KEY_W;
        Settings.downKey =   Keyboard.KEY_S;
        Settings.leftKey =   Keyboard.KEY_A;
        Settings.rightKey =  Keyboard.KEY_D;
        Settings.jumpKey =   Keyboard.KEY_SPACE;
        Settings.sneakKey =  Keyboard.KEY_LSHIFT;
        Settings.pauseKey =  Keyboard.KEY_ESCAPE;
        Settings.blockBreakButton = 0;
        Settings.blockPlaceButton = 1;

        for(int i = 0; i < 4; i++)
            buttons[i] = KeyState.DONTPRESSED;

        long startTime;

        try
        {
            Display.setDisplayMode(new DisplayMode(1280, 720));
            Display.create();
            setVsync(false);
            Display.setResizable(true);
        }
        catch (LWJGLException e)
        {
            e.printStackTrace();
            System.exit(0);
        }

        openGLInit();
        font = new TrueTypeFont(new Font("sans", Font.BOLD, 24), true);
        Button[] buttons = new Button[4];

        buttons[0] = new Button(new int[]{64, 352, 320, 480})
        {
            @Override
            public void onClick()
            {
                if(!locked)
                {
                    Thread thread = new Thread()
                    {
                        @Override
                        public void run()
                        {
                            world = new World();
                            player = new Player();
                            gameStarted = true;
                            setPaused(false);
                        }
                    };

                    thread.setPriority(Thread.MAX_PRIORITY);
                    thread.start();

                    locked = true;
                }
            }

            @Override
            protected void draw(boolean pressed, boolean mouseOn)
            {
                if(mouseOn)
                    glColor4f(0.75f, 0.5f, 0.5f, 1.0f);
                else
                    glColor4f(0.0f, 0.5f, 0.5f, 1.0f);

                glBegin(GL_QUADS);
                glVertex2i(sizes[0], sizes[1]);
                glVertex2i(sizes[2], sizes[1]);
                glVertex2i(sizes[2], sizes[3]);
                glVertex2i(sizes[0], sizes[3]);
                glEnd();
            }
        };

        buttons[1] = new Button(new int[]{64, 256, 320, 320})
        {
            @Override
            public void onClick()
            {
            }

            @Override
            protected void draw(boolean pressed, boolean mouseOn)
            {
                if(mouseOn)
                    glColor4f(0.75f, 0.5f, 0.5f, 1.0f);
                else
                    glColor4f(0.0f, 0.5f, 0.5f, 1.0f);

                glBegin(GL_QUADS);
                glVertex2i(sizes[0], sizes[1]);
                glVertex2i(sizes[2], sizes[1]);
                glVertex2i(sizes[2], sizes[3]);
                glVertex2i(sizes[0], sizes[3]);
                glEnd();
            }
        };

        buttons[2] = new Button(new int[]{64, 160, 320, 224})
        {
            @Override
            public void onClick()
            {
            }

            @Override
            protected void draw(boolean pressed, boolean mouseOn)
            {
                if(mouseOn)
                    glColor4f(0.75f, 0.5f, 0.5f, 1.0f);
                else
                    glColor4f(0.0f, 0.5f, 0.5f, 1.0f);

                glBegin(GL_QUADS);
                glVertex2i(sizes[0], sizes[1]);
                glVertex2i(sizes[2], sizes[1]);
                glVertex2i(sizes[2], sizes[3]);
                glVertex2i(sizes[0], sizes[3]);
                glEnd();
            }
        };

        buttons[3] = new Button(new int[]{64, 64, 320, 128})
        {
            @Override
            public void onClick()
            {
                System.exit(0);
            }

            @Override
            protected void draw(boolean pressed, boolean mouseOn)
            {
                if(mouseOn)
                    glColor4f(0.75f, 0.5f, 0.5f, 1.0f);
                else
                    glColor4f(0.0f, 0.5f, 0.5f, 1.0f);

                glBegin(GL_QUADS);
                glVertex2i(sizes[0], sizes[1]);
                glVertex2i(sizes[2], sizes[1]);
                glVertex2i(sizes[2], sizes[3]);
                glVertex2i(sizes[0], sizes[3]);
                glEnd();
            }
        };

        mainMenu = new GUIMainMenu(buttons);

        while (!Display.isCloseRequested())
        {
            startTime = System.nanoTime();

            pollInput();
            tick();
            display();
            Display.update();

            deltaTime = (System.nanoTime() - startTime) / 1000000000.0;
        }

        Display.destroy();
    }

    public void pollInput()
    {
        int x = Mouse.getDX();
        int y = Mouse.getDY();

        if(!paused && gameStarted)
        {
            if(player.camera.getPitch() - y * Settings.mouseSensitivity > -Math.PI / 2 && player.camera.getPitch() - y * Settings.mouseSensitivity < Math.PI / 2)
                player.camera.setPitch(player.camera.getPitch() - y * Settings.mouseSensitivity);

            //player.camera.yaw += (x - halfWidth) * Settings.mouseSensitivity;
            player.camera.setYaw(player.camera.getYaw() + x * Settings.mouseSensitivity);
            //Mouse.setCursorPosition(halfWidth, halfHeight);
        }

        for(int i = 0; i < 256; i++)
        {
            if(keys[i] == KeyState.PRESSEDNOW)
                keys[i] = KeyState.PRESSED;
            else if(keys[i] != KeyState.PRESSED)
                keys[i] = KeyState.DONTPRESSED;
        }

        for(int i = 0; i < 4; i++)
        {
            if(buttons[i] == KeyState.PRESSEDNOW)
                buttons[i] = KeyState.PRESSED;
            else if(buttons[i] == KeyState.RELEASED)
                buttons[i] = KeyState.DONTPRESSED;
            else if(buttons[i] == KeyState.PRESSED && !Mouse.isButtonDown(i))
                buttons[i] = KeyState.RELEASED;

            if(Mouse.isButtonDown(i) && buttons[i] != KeyState.PRESSED)
                buttons[i] = KeyState.PRESSEDNOW;
        }

        while(Keyboard.next())
        {
            if(Keyboard.getEventKeyState())
                keys[Keyboard.getEventKey()] = KeyState.PRESSEDNOW;
            else
                keys[Keyboard.getEventKey()] = KeyState.RELEASED;
        }
    }

    public static void main(String[] args)
    {
        Game displayExample = new Game();

        displayExample.start();
    }

    public boolean getVsync()
    {
        return vsync;
    }

    public void setVsync(boolean vsync)
    {
        this.vsync = vsync;
        Display.setVSyncEnabled(vsync);
    }

    public void toggleVsync()
    {
        setVsync(!getVsync());
    }

    public void setPaused(boolean paused)
    {
        this.paused = paused;
        Mouse.setGrabbed(!paused);
    }
}