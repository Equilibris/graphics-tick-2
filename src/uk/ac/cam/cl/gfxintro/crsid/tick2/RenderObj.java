package uk.ac.cam.cl.gfxintro.crsid.tick2;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public abstract class RenderObj {
    public SkyBox skybox;

    private ArrayList<RenderObj> children = new ArrayList<>();

    protected void add_child(RenderObj obj) {
        children.add(obj);
    }

    public abstract Matrix4f positional_model_matrix(float deltaTime, long elapsedTime);
    public abstract Matrix4f structure_model_matrix(float deltaTime, long elapsedTime);

    protected Matrix4f render_children(Camera camera, Matrix4f parent_translate, float deltaTime, long elapsedTime) {
        var pos_mat = positional_model_matrix(deltaTime, elapsedTime);

        var remat = new Matrix4f(parent_translate);
        remat.mul(pos_mat);

        for (var i : children) {
            i.skybox = skybox;
            i.render(camera, remat, deltaTime, elapsedTime);
        }

        return remat;
    }
    public void render(Camera camera, Matrix4f parent_translate, float deltaTime, long elapsedTime) {
        render_children(camera, parent_translate, deltaTime, elapsedTime);
    }
}
