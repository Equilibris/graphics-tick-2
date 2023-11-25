package uk.ac.cam.cl.gfxintro.crsid.tick2;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

;

public class CubeRobot {
	
    // Filenames for vertex and fragment shader source code
    private final static String VSHADER_FN = "resources/cube_vertex_shader.glsl";
    private final static String FSHADER_FN = "resources/cube_fragment_shader.glsl";
    
    // Reference to the skybox of the scene
    public SkyBox skybox;

	private static float B_HEIGHT = 1.25F;


	private Texture default_tex;
	private Texture head_tex;

	private Mesh bmesh;
	private ShaderProgram bshader;

	private Mesh 		  ramesh;
	private ShaderProgram rashader;

	private Mesh 		  lamesh;
	private ShaderProgram lashader;

	private Mesh 		  rlmesh;
	private ShaderProgram rlshader;

	private Mesh 		  llmesh;
	private ShaderProgram llshader;

	private Mesh 		  hmesh;
	private ShaderProgram hshader;

	// Complete rest of the robot

	private static ShaderProgram shader_for_mesh(Mesh mesh) {
		var shader = new ShaderProgram(new Shader(GL_VERTEX_SHADER, VSHADER_FN), new Shader(GL_FRAGMENT_SHADER, FSHADER_FN), "colour");

		// Tell vertex shader where it can find vertex positions. 3 is the dimensionality of vertex position
		// The prefix "oc_" means object coordinates
		shader.bindDataToShader("oc_position", mesh.vertex_handle, 3);
		// Tell vertex shader where it can find vertex normals. 3 is the dimensionality of vertex normals
		shader.bindDataToShader("oc_normal", mesh.normal_handle, 3);
		// Tell vertex shader where it can find texture coordinates. 2 is the dimensionality of texture coordinates
		shader.bindDataToShader("texcoord", mesh.tex_handle, 2);

		return shader;
	}
	
/**
 *  Constructor
 *  Initialize all the CubeRobot components
 */
	public CubeRobot() {
		// Create body node

		default_tex = new Texture();
		default_tex.load("resources/cubemap.png");


		head_tex = new Texture();
		head_tex.load("resources/cubemap_head.png");

		// Initialise Geometry
		bmesh 	= new CubeMesh();
		bshader = shader_for_mesh(bmesh);

		ramesh	 = new CubeMesh();
		rashader = shader_for_mesh(ramesh);

		lamesh	 = new CubeMesh();
		lashader = shader_for_mesh(lamesh);

		rlmesh 	 = new CubeMesh();
		rlshader = shader_for_mesh(rlmesh);

		llmesh 	 = new CubeMesh();
		llshader = shader_for_mesh(llmesh);

		hmesh 	 = new CubeMesh();
		hshader = shader_for_mesh(hmesh);
	}


	private static void arm_common (Matrix4f transform, long elapsed_time) {
		float height = 1.25F;
		float width = .25F;

		transform.translate(.75F, B_HEIGHT, .125F);
		transform.rotateZ((float)(.5 + 0.5 * Math.cos(elapsed_time/500F)));
		transform.scale(width, height, width);
		transform.translate(1F,-1F,-.5F);
	}

	private static Matrix4f rarm_transform(long elapsed_time) {
		var transform = new Matrix4f();

		arm_common(transform, elapsed_time);

		return transform;
	}
	private static Matrix4f larm_transform(long elapsed_time) {
		var transform = new Matrix4f();

		transform.rotateY((float)Math.PI);

		arm_common(transform, elapsed_time);

		return transform;
	}



	private static void leg_common(Matrix4f transform) {
		transform.translate(0,-B_HEIGHT,0);
		transform.scale(0.25F,1.25F,  0.25F);
		transform.translate(0,-1,0);
	}

	 private static Matrix4f lleg_transform() {
		var transform = new Matrix4f();

		transform.translate(-.5F, 0, 0);
		leg_common(transform);

		return transform;
	}
	private static Matrix4f rleg_transform() {
		var transform = new Matrix4f();

		transform.translate(.5F, 0, 0);
		leg_common(transform);

		return transform;
	}

	private static Matrix4f head_transform() {
		var transform = new Matrix4f();

		transform.translate(0, B_HEIGHT,  0);
		transform.scale(0.3F);
		transform.translate(0,1,0);

		return transform;
	}

	private static Matrix4f body_internal() {
		var body_transform = new Matrix4f();
		body_transform.scale(.75F,B_HEIGHT,0.75F);

		return body_transform;
	}
	private static Matrix4f body_transform(long elapsedTime) {
		var transform = new Matrix4f();
		transform.rotateY(elapsedTime / 1500F);
		return transform;
	}
	private static Matrix4f mul (Matrix4f a, Matrix4f b) {
		var dest = new Matrix4f();
		a.mul(b,dest);
		return dest;
	}

	/**
	 * Updates the scene and then renders the CubeRobot
	 * @param camera - Camera to be used for rendering
	 * @param deltaTime		- Time taken to render this frame in seconds (= 0 when the application is paused)
	 * @param elapsedTime	- Time elapsed since the beginning of this program in millisecs
	 */
	public void render(Camera camera, float _dt, long elapsedTime) {
		
		// TODO: Animate Body. Translate the body as a function of time

		
		// TODO: Animate Arm. Rotate the left arm around its end as a function of time

		var body = body_transform(elapsedTime);


		renderMesh(camera, bmesh, mul(body, body_internal()), bshader, default_tex);

		renderMesh(camera, ramesh, mul(body, rarm_transform(elapsedTime)), rashader, default_tex);
		renderMesh(camera, lamesh, mul(body, larm_transform(elapsedTime)), lashader, default_tex);

		renderMesh(camera, rlmesh, mul(body, rleg_transform()), rlshader, default_tex);
		renderMesh(camera, llmesh, mul(body, lleg_transform()), llshader, default_tex);

		renderMesh(camera, hmesh, mul(body, head_transform()), hshader, head_tex);

		// TODO: Chain transformation matrices of the arm and body (Scene Graph)
		// TODO: Render Arm.

		//TODO: Render rest of the robot
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
		// If shaders modified on the disc, reload them
		shader.reloadIfNeeded(); 
		shader.useProgram();

		// Step 2: Pass relevant data to the vertex shader
		
		// compute and upload MVP
		Matrix4f mvp_matrix = new Matrix4f(camera.getProjectionMatrix()).mul(camera.getViewMatrix()).mul(modelMatrix);
		shader.uploadMatrix4f(mvp_matrix, "mvp_matrix");
		
		// Upload Model Matrix and Camera Location to the shader for Phong Illumination
		shader.uploadMatrix4f(modelMatrix, "m_matrix");
		shader.uploadVector3f(camera.getCameraPosition(), "wc_camera_position");
		
		// Transformation by a nonorthogonal matrix does not preserve angles
		// Thus we need a separate transformation matrix for normals
		Matrix3f normal_matrix = (new Matrix3f(modelMatrix)).invert().transpose();
		//TODO: Calculate normal transformation matrix

		shader.uploadMatrix3f(normal_matrix, "normal_matrix");
		
		// Step 3: Draw our VertexArray as triangles
		// Bind Textures
		texture.bindTexture();
		shader.bindTextureToShader("tex", 0);
		skybox.bindCubemap();
		shader.bindTextureToShader("skybox", 1);
		// draw
		glBindVertexArray(mesh.vertexArrayObj); // Bind the existing VertexArray object
		glDrawElements(GL_TRIANGLES, mesh.no_of_triangles, GL_UNSIGNED_INT, 0); // Draw it as triangles
		glBindVertexArray(0);             // Remove the binding
		
        // Unbind texture
		texture.unBindTexture();
		skybox.unBindCubemap();
	}
}
