#version 330 core

const float NO_LIGHT = 0.0;
const float DIRECTIONAL_LIGHT = 1.0;
const float POINT_LIGHT = 2.0;
const float SPOT_LIGHT = 3.0;
const float INVERSE_GAMMA = 0.454545;

const float AMBIENCE = 0.5;
const int MAX_LIGHTS = 5;

in vec3 vertexNormal;
in vec2 vertexTexture;
in vec3 fragmentPosition;

out vec4 outColor;

struct Material
{
    vec3 diffuse;
    vec3 specular;
    float reflectance;

    sampler2D diffuseMap;
    sampler2D specularMap;

    float hasDiffuseMap;
    float hasSpecularMap;
};

struct Light
{
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;

    vec3 position;
    vec3 direction;

    float constant;
    float linear;
    float quadratic;

    float cutoff;
    float outerCutoff;

    float type;
};

uniform Light lights[MAX_LIGHTS];
uniform Material material;
uniform vec3 viewPosition;

vec3 directionalLight(Light light, vec3 normal, vec3 viewDirection, vec3 diffuseColor, vec3 specularColor)
{
    vec3 lightDirection = normalize(-light.direction);
    float diff = max(dot(normal, lightDirection), 0.0);
    vec3 halfDirection = normalize(lightDirection + viewDirection);
    float spec = pow(max(dot(viewDirection, halfDirection), 0.0), material.reflectance);

    vec3 ambient = light.ambient * diffuseColor;
    vec3 diffuse = light.diffuse * diff * diffuseColor;
    vec3 specular = light.specular * spec * specularColor;

    return (ambient + diffuse + specular);
}

vec3 pointLight(Light light, vec3 normal, vec3 fragmentPosition, vec3 viewDirection, vec3 diffuseColor, vec3 specularColor)
{
    vec3 lightDirection = normalize(light.position - fragmentPosition);
    float diff = max(dot(normal, lightDirection), 0.0);
    vec3 halfDirection = normalize(lightDirection + viewDirection);
    float spec = pow(max(dot(viewDirection, halfDirection), 0.0), material.reflectance);

    float distance = length(light.position - fragmentPosition);
    float attenuation = 1.0 / (light.constant + (light.linear * distance) + (light.quadratic * distance * distance));

    vec3 ambient = light.ambient * diffuseColor * attenuation;
    vec3 diffuse = light.diffuse * diff * diffuseColor * attenuation;
    vec3 specular = light.specular * spec * attenuation * specularColor;

    return (ambient + diffuse + specular);
}

vec3 spotLight(Light light, vec3 normal, vec3 fragmentPosition, vec3 viewDirection, vec3 diffuseColor, vec3 specularColor)
{
    vec3 lightDirection = normalize(light.position - fragmentPosition);
    float diff = max(dot(normal, lightDirection), 0.0);
    vec3 halfDirection = normalize(lightDirection + viewDirection);
    float spec = pow(max(dot(viewDirection, halfDirection), 0.0), material.reflectance);

    float theta = dot(lightDirection, normalize(-light.direction));
    float epsilon = light.cutoff - light.outerCutoff;
    float intensity = clamp((theta - light.outerCutoff) / epsilon, 0.0, 1.0);

    vec3 ambient = light.ambient * diffuseColor;
    vec3 diffuse = light.diffuse * diff * diffuseColor;
    vec3 specular = light.specular * spec * specularColor;

    diffuse *= intensity;
    specular *= intensity;

    return (ambient + diffuse + specular);
}

void main()
{
    vec4 diffuseColor = vec4(0.0);
    vec4 specularColor = vec4(0.0);

    diffuseColor = (material.hasDiffuseMap == 1.0) ?
        texture(material.diffuseMap, vertexTexture) : vec4(material.diffuse, 1.0);

    specularColor =  (material.hasSpecularMap == 1.0) ?
        texture(material.specularMap, vertexTexture) : vec4(material.specular, 1.0);

    vec3 normal = normalize(vertexNormal);
    vec3 viewDirection = normalize(viewPosition - fragmentPosition);

    vec3 result = vec3(0.0);

    for(int i = 0; i < MAX_LIGHTS; i++)
    {
        Light light = lights[i];
        if(light.type == NO_LIGHT) // If the light is empty
            continue;
        else if(light.type == DIRECTIONAL_LIGHT) // If the light is a directional light
            result += directionalLight(light, normal, viewDirection, diffuseColor.xyz, specularColor.xyz);
        else if(light.type == POINT_LIGHT) // If the light is a point light
            result += pointLight(light, normal, fragmentPosition, viewDirection, diffuseColor.xyz, specularColor.xyz);
        else if(light.type == SPOT_LIGHT) // If the light is a spot light
            result += spotLight(light, normal, fragmentPosition, viewDirection, diffuseColor.xyz, specularColor.xyz);
    }

    if(result == vec3(0.0)) // If no lights are present
        result = vec3(AMBIENCE) * diffuseColor.xyz; // Use ambient lighting


    //result = pow(result, vec3(INVERSE_GAMMA)); // Apply gamma correction


    outColor = vec4(result, 1.0);
}
