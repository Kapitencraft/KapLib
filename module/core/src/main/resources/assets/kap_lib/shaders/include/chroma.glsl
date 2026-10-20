const float DEFAULT_COLOR_WIDTH = 10.0;

float chromaPos(float chromaType, vec2 stage, float spacing) {
    int type = int(chromaType);
    float l;
    if (type == 0) {
        l = length(stage);
    } else if (type == 1) {
        l = (stage.x + stage.y);
    } else {
        l = max(stage.x, stage.y);
    }
    l *= DEFAULT_COLOR_WIDTH * spacing;
    return l;
}

float chromaPos(float chromaType, vec3 stage, float spacing) {
    int type = int(chromaType);
    float l;
    if (type == 0) {
        l = length(stage);
    } else if (type == 1) {
        l = (stage.x + stage.y + stage.z);
    } else {
        l = max(max(stage.x, stage.y), stage.z);
    }
    l *= DEFAULT_COLOR_WIDTH * spacing;
    return l;
}