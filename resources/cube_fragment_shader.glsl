#version 140

const float PI = 3.141597;

in vec3 wc_frag_normal;        	// fragment normal in world coordinates (wc_)
in vec2 frag_texcoord;			// texture UV coordinates
in vec3 wc_frag_pos;			// fragment position in world coordinates

out vec3 color;			        // pixel colour
out vec3 texcoord;			    // pixel colour

uniform sampler2D tex;  		  // 2D texture sampler
uniform samplerCube skybox;		  // Cubemap texture used for reflections
uniform vec3 wc_camera_position;  // Position of the camera in world coordinates

// Combined tone mapping and display encoding
vec3 tonemap(vec3 linearRGB)
{
    float L_white = 0.7; // Controls the brightness of the image

    float inverseGamma = 1./2.2;
    return pow(linearRGB/L_white, vec3(inverseGamma)); // Display encoding - a gamma
}

float magsq(vec3 v) {
    return v.x * v.x + v.y * v.y + v.z * v.z;
}

vec3 norm(vec3 v) {
    return v * inversesqrt(magsq(v));
}

void main()
{
    const vec3 lpos = vec3(-1, 7, -1);
    const vec3 lcol = vec3(0.941, 0.968, 1);
    const vec3 acol = vec3(0.16, 0.1, 0.);
    const float lins = 80;

    const float diffcons = 0.4;
    const float speccons = 0.75;

    const vec3 speccol = vec3(1, 1, 1);
    const float coef = 32;

    vec3 diffcol = texture(tex, frag_texcoord).xyz;

    float distance = sqrt(magsq(lpos - wc_frag_pos));

    vec3 normal = norm(wc_frag_normal);

    vec3 I = lcol * (lins / (PI * pow(distance, 2)));

    vec3 l = norm(lpos);
    vec3 v = norm(wc_camera_position - wc_frag_pos);

    vec3 r = norm(reflect(-l, normal));

    vec3 ambient = acol * diffcol;
    vec3 diffuse =  I * diffcol * diffcons * max(dot(l, normal), 0);
    vec3 specular = I * speccol * speccons * pow(max(dot(r, v), 0), coef);

	vec3 linear_color = ambient + diffuse + specular + 0.1 * texture(skybox, reflect(v, wc_frag_normal)).xyz;

	color = tonemap(linear_color);
}

