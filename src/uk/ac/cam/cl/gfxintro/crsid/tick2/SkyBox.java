package uk.ac.cam.cl.gfxintro.crsid.tick2;

import org.joml.Matrix3dc;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Matrix3x2fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Matrix4x3fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.lang.Math;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;;

public class SkyBox {
	
    // Filenames for vertex and fragment shader source code
    private final static String VSHADER_FN = "resources/skybox_vertex_shader.glsl";
    private final static String FSHADER_FN = "resources/skybox_fragment_shader.glsl";
    

	private Mesh skybox_mesh;				// Mesh of the body
	private ShaderProgram skybox_shader;	// Shader to colour the body mesh
	private Texture skybox_texture;			// Texture image to be used by the body shader
	
/**
 *  Constructor
 *  Initialize all the SkyBox components
 */
	public SkyBox() {
		// Create body node
		
		// Initialise Geometry
		skybox_mesh = new CubeMesh(); 
		
		// Initialise Shader
		skybox_shader = new ShaderProgram(new Shader(GL_VERTEX_SHADER, VSHADER_FN), new Shader(GL_FRAGMENT_SHADER, FSHADER_FN), "colour");
		// Tell vertex shader where it can find vertex positions. 3 is the dimensionality of vertex position
		// The prefix "oc_" means object coordinates
		skybox_shader.bindDataToShader("oc_position", skybox_mesh.vertex_handle, 3);
		
		// Initialise Texturing
		skybox_texture = new Texture(); 
		String filenames[] = {"resources/skybox/right.png",
				"resources/skybox/left.png",
				"resources/skybox/top.png",
				"resources/skybox/bottom.png",
				"resources/skybox/front.png",
				"resources/skybox/back.png"
				};
		skybox_texture.loadCubemap(filenames);
	}
	

	/**
	 * Updates the scene and then renders the CubeRobot
	 * @param camera - Camera to be used for rendering
	 * @param deltaTime		- Time taken to render this frame in seconds (= 0 when the application is paused)
	 * @param elapsedTime	- Time elapsed since the beginning of this program in millisecs
	 */
	public void render(Camera camera, float deltaTime, long elapsedTime) {
		
		// Disable culling of polygon back-faces to draw the inside of the cube
		GL11.glDisable(GL_CULL_FACE);
		// Disable depth writing so that the skybox is always be drawn at the background
		GL11.glDepthMask(false);
		// Skybox cube needs to be scaled a size where it contains the entire scene
		renderMesh(camera, skybox_mesh, new Matrix4f().scale(new Vector3f(10f,10f,10f)), skybox_shader, skybox_texture);
		GL11.glDepthMask(true);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
	}
	
	/**
	 * Draw mesh from a camera perspective
	 * @param camera		- Camera to be used for rendering
	 * @param mesh			- mesh to render
	 * @param modelMatrix	- model transformation matrix of this mesh
	 * @param shader		- shader to colour this mesh
	 * @param texture		- texture image to be used by the shader
	 */
	public void renderMesh(Camera camera, Mesh mesh , Matrix4f modelMatrix, ShaderProgram shader, Texture texture) {
		// If shaders modified on disk, reload them
		shader.reloadIfNeeded(); 
		shader.useProgram();

		// Pass relevant data to the vertex shader
		
		// compute and upload MVP
		// Skybox should not move with camera, only rotate
		Matrix4f view_matrix = camera.getViewMatrix();
		view_matrix.setTranslation(new Vector3f(0,0,0));
		Matrix4f mvp_matrix = new Matrix4f(camera.getProjectionMatrix()).mul(view_matrix).mul(modelMatrix);;
		shader.uploadMatrix4f(mvp_matrix, "mvp_matrix");

		// Draw our VertexArray as triangles
		// Bind Texture
		bindCubemap();
		shader.bindTextureToShader("skybox", 1);
		// draw
		glBindVertexArray(mesh.vertexArrayObj); // Bind the existing VertexArray object
		glDrawElements(GL_TRIANGLES, mesh.no_of_triangles, GL_UNSIGNED_INT, 0); // Draw it as triangles
		glBindVertexArray(0);             // Remove the binding
		
        // Unbind texture
		unBindCubemap();
	}
	
	/**
	 * Binds skybox texture to active texture units
	 * */
	public void bindCubemap() {
		skybox_texture.bindCubemap();
	}
	
	/**
	 * Unbinds skybox texture
	 * */
	public void unBindCubemap() {
		skybox_texture.unBindCubemap();
	}
}
