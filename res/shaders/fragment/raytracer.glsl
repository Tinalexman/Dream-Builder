#version 330 core

out vec4 outColor;
uniform vec2 resolution;

void render(inout vec3 col, in vec2 uv)
{
    col.rg += uv;
}

void main()
{
    vec2 uv = (2.0 * gl_FragCoord.xy - resolution.xy) / resolution.y;

    vec3 color;
    render(color, uv);

    outColor = vec4(color, 1.0);
}
