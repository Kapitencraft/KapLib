#version 150

#moj_import <fog.glsl>
#moj_import <kap_lib:extras.glsl>

const float GAME_TIME_SCALE = 200.0;

uniform vec4 ChromaConfig;
uniform vec4 ColorModulator;
uniform vec2 ScreenSize;
uniform float GameTime;

out vec4 fragColor;

void main() {
    vec4 color = ColorModulator;
    if (color.a == 0.0) {
        discard;
    }


    vec2 stage = filterStage(gl_FragCoord.xy / ScreenSize.xy, int(ChromaConfig.r));
    float l = chromaPos(ChromaConfig.a, stage, ChromaConfig.g);

    color = vec4(hsb2rgb(vec3(fract(l + GameTime * ChromaConfig.b * GAME_TIME_SCALE), 1.0, 1.0)), color.a);

    fragColor = color;
}
