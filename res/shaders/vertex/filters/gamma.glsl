#version 330 core

layout (location = 0) in vec3 vertices;
layout (location = 1) in vec2 textures;

out vec2 passTextures;

void main()
{
    gl_Position = vec4(vertices.x, vertices.y, 0.0, 1.0);
    passTextures = textures;
}