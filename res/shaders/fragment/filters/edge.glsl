#version 330 core

in vec2 passTextures;
out vec4 outColor;

uniform sampler2D sampler;

const float offset = 1.0 / 300.0;
const int kernelSize = 9;

void main()
{
    vec2 offsets[kernelSize] = vec2[]
    (
        vec2(-offset, offset),
        vec2(0.0, offset),
        vec2(offset, offset),

        vec2(-offset, 0.0),
        vec2(0.0, 0.0),
        vec2(offset, 0.0),

        vec2(-offset, -offset),
        vec2(0.0, -offset),
        vec2(offset, -offset)
    );

    float kernels[kernelSize] = float[]
    (
        1, 1, 1,
        1, -8, 1,
        1, 1, 1
    );

    vec3 sampleTextures[kernelSize];
    for(int i = 0; i < kernelSize; i++)
    {
        sampleTextures[i] = vec3(texture(sampler, passTextures.st + offsets[i]));
    }

    vec3 color = vec3(0.0);
    for(int i = 0; i < kernelSize; i++)
    {
        color += sampleTextures[i] * kernels[i];
    }

    outColor = vec4(color, 1.0);
}