import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Game {
    private World world;
    private Player player;
    public KeyState[] keys = new KeyState[349];
    public KeyState[] buttons = new KeyState[4];
    public boolean paused = false;
    private double deltaTime;
    private GUI mainMenu;
    private long window;
    private boolean vsync;
    private boolean gameStarted;

    boolean needOpenglInit;

    private int colorTextureID = -1;
    private int framebufferID = -1;
    private int depthRenderBufferID = -1;

    public void start() {
        Settings.mouseSensitivity = 0.002f;
        Settings.upKey =     GLFW_KEY_W;
        Settings.downKey =   GLFW_KEY_S;
        Settings.leftKey =   GLFW_KEY_A;
        Settings.rightKey =  GLFW_KEY_D;
        Settings.jumpKey =   GLFW_KEY_SPACE;
        Settings.sneakKey =  GLFW_KEY_LEFT_SHIFT;
        Settings.pauseKey =  GLFW_KEY_ESCAPE;
        Settings.blockBreakButton = 0;
        Settings.blockPlaceButton = 1;

        for(int i = 0; i < 4; i++)
            buttons[i] = KeyState.DONTPRESSED;

        long startTime;

        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(1280, 720, "RedLogics", NULL, NULL);

        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        Settings.window = window;

        glfwMakeContextCurrent(window);

        glfwShowWindow(window);
        GL.createCapabilities();

        openGLInit();

        glfwSetWindowSizeCallback(window, new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                needOpenglInit = true;
            }
        });

        glfwSetKeyCallback(window, new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if(glfwGetKey(window, key) == GLFW_PRESS)
                    keys[key] = KeyState.PRESSEDNOW;
                else
                    keys[key] = KeyState.RELEASED;
            }
        });

        //font = new TrueTypeFont(new Font("sans", Font.BOLD, 24), true);
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

        while (!glfwWindowShouldClose(window)) {
            startTime = System.nanoTime();

            pollInput();
            glfwPollEvents();
            tick();
            display();

            glfwSwapBuffers(window);

            deltaTime = (System.nanoTime() - startTime) / 1000000000.0;
        }

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void pollInput() {
        if(!paused && gameStarted) {
            double[] tempX = { 0 };
            double[] tempY = { 0 };

            glfwGetCursorPos(window, tempX, tempY);

            double x = tempX[0];
            double y = tempY[0];

            int[] windowWidth = { 0 };
            int[] windowHeight = { 0 };

            glfwGetWindowSize(window, windowWidth, windowHeight);

            float windowHalfWidth = windowWidth[0] / 2.0f;
            float windowHalfHeight = windowHeight[0] / 2.0f;

            Camera camera = player.camera;

            double newPitch = camera.getPitch();
            float  sensitivity = Settings.mouseSensitivity;

            newPitch += (y - windowHalfHeight) * sensitivity;

            if (newPitch > -Math.PI / 2.0f && newPitch < Math.PI / 2.0f)
                camera.setPitch(newPitch);

            camera.setYaw(camera.getYaw() + (x - windowHalfWidth) * sensitivity);

            glfwSetCursorPos(window, windowHalfWidth, windowHalfHeight);
        }

        for(int i = 0; i < 348; i++) {
            if(keys[i] == KeyState.PRESSEDNOW)
                keys[i] = KeyState.PRESSED;
            else if(keys[i] != KeyState.PRESSED)
                keys[i] = KeyState.DONTPRESSED;
        }

        for(int i = 0; i < 4; i++) {
            if(buttons[i] == KeyState.PRESSEDNOW)
                buttons[i] = KeyState.PRESSED;
            else if(buttons[i] == KeyState.RELEASED)
                buttons[i] = KeyState.DONTPRESSED;
            else if(buttons[i] == KeyState.PRESSED && glfwGetMouseButton(window, i) != GLFW_PRESS)
                buttons[i] = KeyState.RELEASED;

            if(glfwGetMouseButton(window, i) == GLFW_PRESS && buttons[i] != KeyState.PRESSED)
                buttons[i] = KeyState.PRESSEDNOW;
        }
    }

    public void tick() {
        if(keys[Settings.pauseKey] == KeyState.RELEASED)
            setPaused(!paused);

        if(!paused && gameStarted) {
            world.tick(deltaTime);
            player.tick(world, keys, buttons, deltaTime);
        }

        if (needOpenglInit)
            openGLInit();
    }

    private void display() {
        glEnable(GL_LIGHTING);

        glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, framebufferID);

        int[] displayWidth = { 0 };
        int[] displayHeight = { 0 };

        glfwGetWindowSize(window, displayWidth, displayHeight);

        if(gameStarted) {
            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();

            double ratio = displayWidth[0] / (double) displayHeight[0];
            double nearDist = 0.1;
            double farDist = 2048.0;

            glFrustum(
                -ratio * nearDist, ratio * nearDist,
                -1.0   * nearDist, 1.0   * nearDist,
                (ratio * nearDist) / Math.tan(player.camera.fov * (Math.PI / 360.0)),
                farDist
            );

            glMatrixMode(GL_MODELVIEW);

            glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glLoadIdentity();

            glRotated(player.camera.getPitch() * (180.0 / Math.PI), 1.0, 0.0, 0.0);
            glRotated(player.camera.getYaw() * (180.0 / Math.PI), 0.0, 1.0, 0.0);

            glTranslated(-player.camera.positionX, -player.camera.positionY, -player.camera.positionZ);

            FloatBuffer diffuse = BufferUtils.createFloatBuffer(4);
            diffuse.put(1.05f).put(1.05f).put(0.5f).put(0.0f).flip();

            FloatBuffer direction = BufferUtils.createFloatBuffer(4);
            direction.put(0.5f).put(1.0f).put(0.2f).put(0.0f).flip();

            FloatBuffer ambient = BufferUtils.createFloatBuffer(4);
            ambient.put(0.3f).put(0.4f).put(0.5f).put(0.0f).flip();

            glLightfv(GL_LIGHT0, GL_DIFFUSE, diffuse);
            glLightfv(GL_LIGHT0, GL_POSITION, direction);
            glLightfv(GL_LIGHT0, GL_AMBIENT, ambient);

            world.draw(player.camera);
        }

        glDisable(GL_LIGHTING);
        glEnable(GL_TEXTURE_2D);
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);

        glClearColor (0.0f, 1.0f, 1.0f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glViewport(0, 0, displayWidth[0], displayHeight[0]);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0.0, displayWidth[0], 0.0, displayHeight[0], 1.0, -1.0);
        glMatrixMode(GL_MODELVIEW);

        glBindTexture(GL_TEXTURE_2D, colorTextureID);

        glLoadIdentity();
//        font.drawString(400, 400, "dsafadf", Color.red);
        glDisable(GL_TEXTURE_2D);

        if(!gameStarted)
            mainMenu.draw(keys, buttons);

        glEnable(GL_TEXTURE_2D);

        glColor3f(1.0f, 1.0f, 1.0f);
        glBegin(GL_QUADS);
        glTexCoord2f(0.0f, 0.0f); glVertex2i(0, 0);
        glTexCoord2f(1.0f, 0.0f); glVertex2i(displayWidth[0], 0);
        glTexCoord2f(1.0f, 1.0f); glVertex2i(displayWidth[0], displayHeight[0]);
        glTexCoord2f(0.0f, 1.0f); glVertex2i(0, displayHeight[0]);
        glEnd();
    }

    private void openGLInit() {
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

        if (!GL.getCapabilities().GL_EXT_framebuffer_object) {
            throw new RuntimeException("FBO not supported");
        } else {
            if (framebufferID != -1)
                glDeleteFramebuffersEXT(framebufferID);

            if (colorTextureID != -1)
                glDeleteTextures(colorTextureID);

            if (depthRenderBufferID != -1)
                glDeleteRenderbuffersEXT(depthRenderBufferID);

            framebufferID = glGenFramebuffersEXT();
            colorTextureID = glGenTextures();
            depthRenderBufferID = glGenRenderbuffersEXT();

            int[] displayWidth = { 0 };
            int[] displayHeight = { 0 };

            glfwGetWindowSize(window, displayWidth, displayHeight);

            glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, framebufferID);

            glBindTexture(GL_TEXTURE_2D, colorTextureID);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, displayWidth[0], displayHeight[0], 0, GL_RGBA, GL_INT, (java.nio.ByteBuffer) null);
            glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, colorTextureID, 0);

            glBindRenderbufferEXT(GL_RENDERBUFFER_EXT, depthRenderBufferID);
            glRenderbufferStorageEXT(GL_RENDERBUFFER_EXT, GL14.GL_DEPTH_COMPONENT24, displayWidth[0], displayHeight[0]);
            glFramebufferRenderbufferEXT(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT_EXT, GL_RENDERBUFFER_EXT, depthRenderBufferID);

            glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
        }
    }

    public static void main(String[] args) {
        new Game().start();
    }

    public boolean getVsync() {
        return vsync;
    }

    public void setVsync(boolean vsync) {
        this.vsync = vsync;
        glfwSwapInterval(vsync ? 1 : 0);
    }

    public void toggleVsync()  {
        setVsync(!getVsync());
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
        glfwSetInputMode(window, GLFW_CURSOR, paused ? GLFW_CURSOR_NORMAL : GLFW_CURSOR_DISABLED);
    }
}
