package uk.ac.cam.cl.gfxintro.crsid.tick2;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

/**
 * Abstract class encapsulating a 3D mesh object
 * Mesh object must have 3D position, UV texture coordiantes and normals
 *
 */
public abstract class Mesh {

	// shape/rendering properties
	public int vertexArrayObj;
	public int no_of_triangles;
	public int vertex_handle;
	public int normal_handle;
	public int tex_handle;

	// abstract methods that all subclasses should implement
	abstract float[]  initializeVertexPositions(); 
	abstract int[]  initializeVertexIndices();
	abstract float[]  initializeVertexNormals();
	abstract float[]  initializeTextureCoordinates();
	
	/**
	 * Create mesh object.
	 */
	public Mesh() {

	}

	/**
	 * Initialise. Make sure this is called before you start using the mesh
	 */
	public void initialize() {

		float vertPositions[] = initializeVertexPositions();
		int indices[] = initializeVertexIndices();
		float vertNormals[] = initializeVertexNormals();
		float textureCoordinates[] = initializeTextureCoordinates();
		no_of_triangles = indices.length;

		loadDataOntoGPU( vertPositions, indices, vertNormals, textureCoordinates );
	}
	
	/**
	 * Move the data from Java arrays to OpenGL buffers (these are most likely on the GPU)
	 * @param vertPositions
	 * @param indices
	 * @param vertNormals
	 * @param textureCoordinates
	 */
	protected void loadDataOntoGPU( float[] vertPositions, int[] indices, float[] vertNormals, float[] textureCoordinates ) {

		vertexArrayObj = glGenVertexArrays(); // Get a OGL "name" for a vertex-array object
		glBindVertexArray(vertexArrayObj); // Create a new vertex-array object with that name

		// ---------------------------------------------------------------
		// LOAD VERTEX POSITIONS
		// ---------------------------------------------------------------

		// Construct the vertex buffer in CPU memory
		FloatBuffer vertex_buffer = BufferUtils.createFloatBuffer(vertPositions.length);
		vertex_buffer.put(vertPositions); // Put the vertex array into the CPU buffer
		vertex_buffer.flip(); // "flip" is used to change the buffer from read to write mode

		vertex_handle = glGenBuffers(); // Get an OGL name for a buffer object
		glBindBuffer(GL_ARRAY_BUFFER, vertex_handle); // Bring that buffer object into existence on GPU
		glBufferData(GL_ARRAY_BUFFER, vertex_buffer, GL_STATIC_DRAW); // Load the GPU buffer object with data

		// ---------------------------------------------------------------
		// LOAD VERTEX NORMALS
		// ---------------------------------------------------------------
		FloatBuffer normal_buffer = BufferUtils.createFloatBuffer(vertNormals.length);
		normal_buffer.put(vertNormals); // Put the normal array into the CPU buffer
		normal_buffer.flip(); // "flip" is used to change the buffer from read to write mode

		normal_handle = glGenBuffers(); // Get an OGL name for a buffer object
		glBindBuffer(GL_ARRAY_BUFFER, normal_handle); // Bring that buffer object into existence on GPU
		glBufferData(GL_ARRAY_BUFFER, normal_buffer, GL_STATIC_DRAW); // Load the GPU buffer object with data


		// ---------------------------------------------------------------
		// LOAD VERTEX INDICES
		// ---------------------------------------------------------------

		IntBuffer index_buffer = BufferUtils.createIntBuffer(indices.length);
		index_buffer.put(indices).flip();
		int index_handle = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, index_handle);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, index_buffer, GL_STATIC_DRAW);

		// ---------------------------------------------------------------
		// LOAD Texture coordinates
		// ---------------------------------------------------------------

		// Put texture coordinate array into a buffer in CPU memory
		FloatBuffer tex_buffer = BufferUtils.createFloatBuffer(textureCoordinates.length);
		tex_buffer.put(textureCoordinates).flip();

		// Create an OpenGL buffer and load it with texture coordinate data
		tex_handle = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, tex_handle);
		glBufferData(GL_ARRAY_BUFFER, tex_buffer, GL_STATIC_DRAW);

	}
}
