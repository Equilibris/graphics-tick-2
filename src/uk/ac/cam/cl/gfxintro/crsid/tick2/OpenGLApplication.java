package uk.ac.cam.cl.gfxintro.crsid.tick2;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.system.MemoryUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;


/***
 * Class for an OpenGL Window with rendering loop and meshes to draw
 *
 */
public class OpenGLApplication {

	private static final float FOV_Y = (float) Math.toRadians(50);
	protected static int WIDTH = 800, HEIGHT = 600;
	private Camera camera;
	private long window;
	
	private long currentTime;
	private long startTime;
	private long elapsedTime;
	private boolean pause = false;

	// Callbacks for input handling
	private GLFWCursorPosCallback cursor_cb;
	private GLFWScrollCallback scroll_cb;
	private GLFWKeyCallback key_cb;

	
	private CubeRobot cubeRobot;
	private SkyBox skybox;

	/***
	 * Initialise OpenGL and the scene
	 * @throws Exception
	 */
	public void initialize() throws Exception {

		if (glfwInit() != true)
			throw new RuntimeException("Unable to initialize the graphics runtime.");

		// Uncomment the line below to debug issues with OpenGL
		// glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);

		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		
		glfwWindowHint(GLFW_SAMPLES, 4); // Multi sample buffer for MSAA

		// Ensure that the right version of OpenGL is used (at least 3.2)
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE); // Use CORE OpenGL profile without depreciated functions
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE); // Make it forward compatible

		window = glfwCreateWindow(WIDTH, HEIGHT, "Tick 2", MemoryUtil.NULL, MemoryUtil.NULL);
		if (window == NULL)
			throw new RuntimeException("Failed to create the application window.");

		GLFWVidMode mode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(window, (mode.width() - WIDTH) / 2, (mode.height() - HEIGHT) / 2);
		glfwMakeContextCurrent(window);
		createCapabilities();

		// Enable v-sync
		glfwSwapInterval(1);

		// Cull back-faces of polygons
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
		
		// Enable MSAA
		glEnable(GL_MULTISAMPLE);  

		// Do depth comparisons when rendering
		glEnable(GL_DEPTH_TEST);

		// Create camera, and setup input handlers
		camera = new Camera((double) WIDTH / HEIGHT, FOV_Y);
		initializeInputs();
		
		// Create a skybox/enivornment map
		skybox = new SkyBox();
		
		// This is where we are creating the meshes
		cubeRobot = new CubeRobot();
		cubeRobot.skybox = skybox;
		

		startTime = System.currentTimeMillis();
		currentTime = System.currentTimeMillis();
	}

	private void initializeInputs() {

		// Callback for: when dragging the mouse, rotate the camera
		cursor_cb = new GLFWCursorPosCallback() {
			private double prevMouseX, prevMouseY;

			public void invoke(long window, double mouseX, double mouseY) {
				boolean dragging = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS;
				if (dragging) {
					camera.rotate(mouseX - prevMouseX, mouseY - prevMouseY);
				}
				prevMouseX = mouseX;
				prevMouseY = mouseY;
			}
		};

		// Callback for: when scrolling, zoom the camera
		scroll_cb = new GLFWScrollCallback() {
			public void invoke(long window, double dx, double dy) {
				camera.zoom(dy > 0);
			}
		};

		// Callback for keyboard controls: "W" - wireframe, "P" - points, "S" - take screenshot, "V" - capture frames for video, "Space" - Pause animation 
		key_cb = new GLFWKeyCallback() {
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if (key == GLFW_KEY_W && action == GLFW_PRESS) {
					glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
					glDisable(GL_CULL_FACE);
				} else if (key == GLFW_KEY_P && action == GLFW_PRESS) {
					glPolygonMode(GL_FRONT_AND_BACK, GL_POINT);
				} else if (key == GLFW_KEY_S && action == GLFW_RELEASE) {
					takeScreenshot("screenshot.png");
				} else if (key == GLFW_KEY_V && action == GLFW_RELEASE) {
					captureVideoFrames("video_frames", 48);
				} else if (key == GLFW_KEY_SPACE && action ==  GLFW_RELEASE) {
					pause = !pause;
				}
				else if (action == GLFW_RELEASE) {
					glEnable(GL_CULL_FACE);
					glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
				}
			}
		};

		GLFWFramebufferSizeCallback fbs_cb = new GLFWFramebufferSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				glViewport( 0, 0, width, height );
				camera.setAspectRatio( width * 1.f/height );
			}
		};

		// Set callbacks on the window
		glfwSetCursorPosCallback(window, cursor_cb);
		glfwSetScrollCallback(window, scroll_cb);
		glfwSetKeyCallback(window, key_cb);
		glfwSetFramebufferSizeCallback(window, fbs_cb);
	}

	/***
	 * Run loop
	 * @throws Exception
	 */
	public void run() throws Exception {

		initialize();

		while (glfwWindowShouldClose(window) != true) render();
	}
	
	/***
	 * Draw the scene
	 */
	public void render() {
		// render the scene
		
		// Step 1: Clear the buffer
		glClearColor(1.0f, 1.0f, 1.0f, 1.0f); // Set the background colour to white
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);	
		
		// Step 2: Pass relevant data to the vertex shader (done in CubeRobot.render())
		// Step 3: Draw our VertexArray as triangles (done in CubeRobot.render())
		long newTime = System.currentTimeMillis();
		float deltaTime = (newTime - currentTime) / 1000.f; // Time taken to render this frame in seconds (= 0 when the application is paused)

		
		// A hack to detect application freezing issue on Windows
		if(deltaTime > 0.2f) // User is clicking the title bar
			deltaTime = 0f; // pause the game logic update

		if(!pause) {
			elapsedTime += deltaTime * 1000.0f; // Time elapsed since the beginning of this program in millisecs

			skybox.render(camera, deltaTime, elapsedTime);
			cubeRobot.render(camera, deltaTime, elapsedTime);
			
		} else {
			skybox.render(camera, 0f, elapsedTime);
			cubeRobot.render(camera, 0f, elapsedTime);
		}
		
		currentTime = newTime;
		
		
		checkError();
		
		// Step 4: Swap the draw and back buffers to display the rendered image
		glfwSwapBuffers(window);
		glfwPollEvents();
		checkError();
	}

	public void takeScreenshot(String output_path) {
		int bpp = 4;

		// Take screenshot of the fixed size irrespective of the window resolution
		int screenshot_width = 800;
		int screenshot_height = 600;

		int fbo = glGenFramebuffers();
		glBindFramebuffer( GL_FRAMEBUFFER, fbo );

		int rgb_rb = glGenRenderbuffers();
		glBindRenderbuffer( GL_RENDERBUFFER, rgb_rb );
		glRenderbufferStorage( GL_RENDERBUFFER, GL_RGBA, screenshot_width, screenshot_height );
		glFramebufferRenderbuffer( GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_RENDERBUFFER, rgb_rb );

		int depth_rb = glGenRenderbuffers();
		glBindRenderbuffer( GL_RENDERBUFFER, depth_rb );
		glRenderbufferStorage( GL_RENDERBUFFER, GL_DEPTH_COMPONENT, screenshot_width, screenshot_height );
		glFramebufferRenderbuffer( GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depth_rb );
		checkError();

		float old_ar = camera.getAspectRatio();
		camera.setAspectRatio( (float)screenshot_width  / screenshot_height );
		glViewport(0,0, screenshot_width, screenshot_height );

		render();
		
		camera.setAspectRatio( old_ar );

		glReadBuffer(GL_COLOR_ATTACHMENT0);
		ByteBuffer buffer = BufferUtils.createByteBuffer(screenshot_width * screenshot_height * bpp);
		glReadPixels(0, 0, screenshot_width, screenshot_height, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		checkError();

		glBindFramebuffer( GL_FRAMEBUFFER, 0 );
		glDeleteRenderbuffers( rgb_rb );
		glDeleteRenderbuffers( depth_rb );
		glDeleteFramebuffers( fbo );
		checkError();

		BufferedImage image = new BufferedImage(screenshot_width, screenshot_height, BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < screenshot_width; ++i) {
			for (int j = 0; j < screenshot_height; ++j) {
				int index = (i + screenshot_width * (screenshot_height - j - 1)) * bpp;
				int r = buffer.get(index + 0) & 0xFF;
				int g = buffer.get(index + 1) & 0xFF;
				int b = buffer.get(index + 2) & 0xFF;
				image.setRGB(i, j, 0xFF << 24 | r << 16 | g << 8 | b);
			}
		}
		try {
			ImageIO.write(image, "png", new File(output_path));
		} catch (IOException e) {
			throw new RuntimeException("failed to write output file - ask for a demonstrator");
		}
	}
	
	// Take screenshots of multiple frames
	public void captureVideoFrames(String dir_path, int num_frames) {
		File directory = new File(dir_path);
		if(!directory.exists()) {
			directory.mkdir();
		}
		for(int i = 0; i< num_frames; i++)
			takeScreenshot(dir_path + "/frame_" + i + ".png");
	}

	public void stop() {
		glfwDestroyWindow(window);
		glfwTerminate();
	}

	private void checkError() {
		int error = glGetError();
		if (error != GL_NO_ERROR)
			throw new RuntimeException("OpenGL produced an error (code " + error + ") - ask for a demonstrator");
	}
}
