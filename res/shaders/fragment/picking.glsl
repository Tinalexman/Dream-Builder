#version 330 core

const float maxColorRange = 255.0;

uniform float drawIndex;
uniform float objectIndex;

out vec3 outColor;

void main()
{
    outColor = vec3(objectIndex / maxColorRange, drawIndex / maxColorRange, (gl_PrimitiveID + 1) / maxColorRange);
}