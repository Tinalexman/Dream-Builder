#version 330 core

const float GAMMA = 2.2;
const float INVERSE_GAMMA = 0.454545;

in vec2 passTextures;
out vec4 outColor;

uniform sampler2D sampler;

void main()
{
    vec4 color = texture(sampler, passTextures);
    color.rgb = pow(color.rgb, vec3(INVERSE_GAMMA));
    outColor = color;
}