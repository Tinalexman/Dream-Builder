#version 330 core

layout (location = 0) in vec3 vertices;
layout (location = 1) in vec2 textures;
layout (location = 2) in vec3 normals;

uniform mat3 inverseNormals;

uniform mat4 transformation;
uniform mat4 projection;
uniform mat4 view;

out vec3 vertexNormal;
out vec2 vertexTexture;
out vec3 fragmentPosition;

void main()
{
    vec4 position = transformation * vec4(vertices, 1.0);

    gl_Position = projection * view * position;

    vertexNormal = inverseNormals * normals;
    vertexTexture = textures;
    fragmentPosition = position.xyz;
}