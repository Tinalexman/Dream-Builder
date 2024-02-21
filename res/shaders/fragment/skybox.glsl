#version 330 core

in vec3 textures;
out vec4 outColor;
uniform samplerCube cubeMap;

void main()
{
    outColor = texture(cubeMap, textures);
}