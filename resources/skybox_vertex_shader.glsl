#version 140

in vec3 oc_position;       	// vertex position in object coordinates (oc_)

out vec3 frag_texcoord;    	// fragment texture coordinate in local coordinates (cubemaps are sampled using direction vector)

uniform mat4 mvp_matrix; 	// model-view-projection matrix

void main()
{
    frag_texcoord = oc_position;
    // The position is projected to the screen coordinates using mvp_matrix
    gl_Position = mvp_matrix * vec4(oc_position, 1.0);
}

