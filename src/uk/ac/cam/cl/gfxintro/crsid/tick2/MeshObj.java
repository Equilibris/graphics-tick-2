package uk.ac.cam.cl.gfxintro.crsid.tick2;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class MeshObj extends RenderObj {

    private ShaderProgram program;
    private Texture tex;
    private Mesh mesh;

    protected static ShaderProgram shader_for_mesh(Mesh mesh, String VSHADER_FN, String FSHADER_FN) {
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

    public MeshObj(Texture tex, Mesh mesh, String VSHADER_FN, String FSHADER_FN) {
        this.tex = tex;
        this.mesh = mesh;

        program = shader_for_mesh(mesh, VSHADER_FN, FSHADER_FN);
    }

    @Override
    public void render(Camera camera, Matrix4f parent_translate, float deltaTime, long elapsedTime) {

        var model_mat = render_children(camera, parent_translate, deltaTime, elapsedTime).mul(structure_model_matrix(deltaTime, elapsedTime));

        renderMesh(camera, mesh, model_mat, program, tex);
    }

    @Override
    public Matrix4f positional_model_matrix(float deltaTime, long elapsedTime) {
        return new Matrix4f();
    }

    @Override
    public Matrix4f structure_model_matrix(float deltaTime, long elapsedTime) {
        return new Matrix4f();
    }

    /**
     * Draw mesh from a camera perspective
     *
     * @param camera      - Camera to be used for rendering
     * @param mesh        - mesh to render
     * @param modelMatrix - model transformation matrix of this mesh
     * @param shader      - shader to colour this mesh
     * @param texture     - texture image to be used by the shader
     */
    protected void renderMesh(Camera camera, Mesh mesh, Matrix4f modelMatrix, ShaderProgram shader, Texture texture) {
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
