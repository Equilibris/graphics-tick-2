package uk.ac.cam.cl.gfxintro.crsid.tick2;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform3f;

import org.joml.Vector3f;

/**
 * Class inheriting from Mesh class
 * Defines a Cube mesh by overloading Mesh's 3D position, UV texture coordiantes and normals
 *
 */
public class CubeMesh extends Mesh {
    
	public CubeMesh() {
		super();
		initialize();
	}

	@Override
	float[] initializeVertexPositions() {
		float[] vertPositions = new float[] {
				-1,  1, -1, -1,  1,  1,  1,  1, -1,
				-1,  1,  1,  1,  1,  1,  1,  1, -1,
				-1, -1,  1, -1, -1, -1,  1, -1,  1,
				-1, -1, -1,  1, -1, -1,  1, -1,  1,
				-1,  1,  1, -1, -1,  1,  1,  1,  1,
				-1, -1,  1,  1, -1,  1,  1,  1,  1,
				-1, -1, -1, -1,  1, -1,  1, -1, -1,
				-1,  1, -1,  1,  1, -1,  1, -1, -1,
				-1, -1, -1, -1, -1,  1, -1,  1, -1,
				-1, -1,  1, -1,  1,  1, -1,  1, -1,
				1,  1, -1,  1,  1,  1,  1, -1, -1,
				1,  1,  1,  1, -1,  1,  1, -1, -1
		};
		return vertPositions;
	}

	@Override
	int[] initializeVertexIndices() {

		int[] indices = new int[] {
				0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11,
				12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23,
				24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35
		};
		return indices;
	}

	@Override
	float[] initializeVertexNormals() {

		float[] vertNormals = new float[] {
				0,  1,  0,  0,  1,  0,  0,  1,  0,
				0,  1,  0,  0,  1,  0,  0,  1,  0,
				0, -1,  0,  0, -1,  0,  0, -1,  0,
				0, -1,  0,  0, -1,  0,  0, -1,  0,
				0,  0,  1,  0,  0,  1,  0,  0,  1,
				0,  0,  1,  0,  0,  1,  0,  0,  1,
				0,  0, -1,  0,  0, -1,  0,  0, -1,
				0,  0, -1,  0,  0, -1,  0,  0, -1,
				-1,  0,  0, -1,  0,  0, -1,  0,  0,
				-1,  0,  0, -1,  0,  0, -1,  0,  0,
				1,  0,  0,  1,  0,  0,  1,  0,  0,
				1,  0,  0,  1,  0,  0,  1,  0,  0
		};
		return vertNormals;
	}

	@Override
	float[] initializeTextureCoordinates() {
		float[] texCoors = new float[] {
				1/4f,0, 	1/4f,1/3f, 	1/2f,0,
				1/4f,1/3f,	1/2f,1/3f,	1/2f,0,
				1/4f,2/3f,	1/4f,1,		1/2f,2/3f,
				1/4f,1,		1/2f,1,		1/2f,2/3f,
				1/4f,1/3f, 	1/4f,2/3f,	1/2f,1/3f,
				1/4f,2/3f,	1/2f,2/3f,	1/2f,1/3f,
				1,2/3f,		1,1/3f, 	3/4f,2/3f,
				1,1/3f,		3/4f,1/3f,	3/4f,2/3f,
				0,2/3f,		1/4f,2/3f,	0f,1/3f,
				1/4f,2/3f,	1/4f,1/3f,	0f,1/3f,
				3/4f,1/3f,	1/2f,1/3f,	3/4f,2/3f,
				1/2f,1/3f,	1/2f,2/3f,	3/4f,2/3f
		};
		return texCoors;
	}
}
