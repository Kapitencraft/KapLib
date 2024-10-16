#version 150

#moj_import <fog.glsl>
#moj_import <kap_lib:extras.glsl>

const float DEFAULT_COLOR_WIDTH = 10.0;
const float GAME_TIME_SCALE = 200.0;

uniform sampler2D Sampler0;

uniform vec4 ChromaConfig;
uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform vec2 ScreenSize;
uniform float GameTime;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;
    if (color.a < 0.1) {
        discard;
    }


    float l;
    vec2 stage = filterStage(gl_FragCoord.xy / ScreenSize.xy, int(ChromaConfig.r));
    int type = int(ChromaConfig.a);
    if (type == 0) {
        l = length(stage);
    } else if (type == 1) {
        l = (stage.x + stage.y);
    } else {
        l = max(stage.x, stage.y);
    }
    l *= DEFAULT_COLOR_WIDTH * ChromaConfig.g;
        color = vec4(hsb2rgb(vec3(fract(l + GameTime * ChromaConfig.b * GAME_TIME_SCALE), 1.0, 1.0)), color.a);

    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
