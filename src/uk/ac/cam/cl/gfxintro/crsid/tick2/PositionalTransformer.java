package uk.ac.cam.cl.gfxintro.crsid.tick2;

import org.joml.Matrix4f;

public class PositionalTransformer extends RenderObj {
    private Matrix4f mat;

    public static PositionalTransformer with_child(RenderObj child, Matrix4f mat) {
        var obj = new PositionalTransformer(mat);

        obj.add_child(child);

        return obj;
    }

    public PositionalTransformer(Matrix4f mat) {
        this.mat = mat;
    }

    @Override
    public Matrix4f positional_model_matrix(float deltaTime, long elapsedTime) {
        return mat;
    }

    @Override
    public Matrix4f structure_model_matrix(float deltaTime, long elapsedTime) {
        return new Matrix4f();
    }
}
