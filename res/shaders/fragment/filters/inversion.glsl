#version 330 core

in vec2 passTextures;
out vec4 outColor;

uniform sampler2D sampler;

void main()
{
    outColor = vec4(vec3(1.0 - texture(sampler, passTextures)), 1.0);
}