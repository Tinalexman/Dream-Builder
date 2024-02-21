#version 330 core

in vec2 passTextures;
out vec4 outColor;

uniform sampler2D sampler;

void main()
{
  vec4 result = texture(sampler, passTextures);
  if (result.a < 0.1)
    discard;
  outColor = result;
}