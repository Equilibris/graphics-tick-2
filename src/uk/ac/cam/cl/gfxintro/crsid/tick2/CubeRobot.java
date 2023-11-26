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

public class CubeRobot extends MeshObj {
	
    // Filenames for vertex and fragment shader source code
    private final static String VSHADER_FN = "resources/cube_vertex_shader.glsl";
    private final static String FSHADER_FN = "resources/cube_fragment_shader.glsl";
    
	private static final float B_HEIGHT = 1.25F;

	private static class Arm extends MeshObj {
		public Arm() { super(default_tex(), new CubeMesh(), VSHADER_FN, FSHADER_FN); }

		@Override
		public Matrix4f positional_model_matrix(float dt, long et) {
			var transform = new Matrix4f();
			transform.translate(.75F, B_HEIGHT, .125F);
			return transform;
		}

		@Override
		public Matrix4f structure_model_matrix(float deltaTime, long elapsed_time) {
			var transform = new Matrix4f();
			float height = 1.25F;
			float width = .25F;

			transform.rotateZ((float)(.5 + 0.5 * Math.cos(elapsed_time/500F)));
			transform.scale(width, height, width);
			transform.translate(1F,-1F,-.5F);

			return transform;
		}
	}
	private static class Leg extends MeshObj {
		public Leg() { super(default_tex(), new CubeMesh(), VSHADER_FN, FSHADER_FN); }

		@Override
		public Matrix4f positional_model_matrix(float dt, long et) {
			var transform = new Matrix4f();
			transform.translate(0,-B_HEIGHT,0);
			return transform;
		}

		@Override
		public Matrix4f structure_model_matrix(float deltaTime, long elapsed_time) {
			var transform = new Matrix4f();
			transform.scale(0.25F,1.25F,  0.25F);
			transform.translate(0,-1,0);
			return transform;
		}
	}

	// Complete rest of the robot

	public static Texture default_tex() {
		var default_tex = new Texture();
		default_tex.load("resources/cubemap.png");

		return default_tex;
	}

/**
 *  Constructor
 *  Initialize all the CubeRobot components
 */
	public CubeRobot() {
		super(default_tex(), new CubeMesh(), VSHADER_FN, FSHADER_FN);

		var head_tex = new Texture();
		head_tex.load("resources/cubemap_head.png");

		add_child(
			PositionalTransformer.with_child(
				new MeshObj(
					head_tex,
					new CubeMesh(),
					VSHADER_FN,
					FSHADER_FN
				),
				new Matrix4f().translate(0, B_HEIGHT, 0)
						.scale(0.3f)
						.translate(0,1,0)
			)
		);
		add_child(new Arm());
		add_child(
			PositionalTransformer.with_child(
				new Arm(),
				new Matrix4f().rotateY((float)Math.PI)
			)
		);
		add_child(
			PositionalTransformer.with_child(
				new Leg(),
				new Matrix4f().translate(.5F, 0, 0)
			)
		);
		add_child(
			PositionalTransformer.with_child(
				new Leg(),
				new Matrix4f().translate(-.5F, 0, 0)
			)
		);
	}

	@Override
	public Matrix4f structure_model_matrix(float deltaTime, long elapsedTime) {
		var body_transform = new Matrix4f();
		body_transform.scale(.75F,B_HEIGHT,0.75F);

		return body_transform;
	}

	@Override
	public Matrix4f positional_model_matrix(float deltaTime, long elapsedTime) {
		var transform = new Matrix4f();
		transform.rotateY(elapsedTime / 1500F);
		return transform;
	}
}
