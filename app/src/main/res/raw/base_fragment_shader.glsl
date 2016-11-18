#extension GL_OES_EGL_image_external : require

precision mediump float;
varying vec2 textureCoordinate;
uniform samplerExternalOES s_texture;

uniform vec3 v0;
uniform vec3 v1;
uniform vec3 v2;
uniform vec3 v3;
uniform vec3 v4;

uniform float f0;
uniform float f1;
uniform float f2;
uniform float f3;
uniform float f4;

uniform int eye;  // 1 = left, 2 = right

void main(void) {
    vec4 regular =  texture2D(s_texture, textureCoordinate);
    vec4 inverted =  vec4(1.0, 1.0, 1.0, 2.0) - regular;
    gl_FragColor = mix(regular, inverted, 1.0);
}
