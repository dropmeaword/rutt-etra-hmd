#extension GL_OES_EGL_image_external : require

precision mediump float;
varying vec2 textureCoordinate;

uniform samplerExternalOES tex0; // always the last camera capture
uniform sampler2D tex1; // feedback texture

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

uniform float eye;  //  0 = monocular, 1 = left, 2 = right (apparently 'int' isn't well supported by glsl-es)
uniform mat4 eyetrans;

void main(void) {
    vec4 regular =  texture2D(tex0, textureCoordinate);
    vec4 inverted = regular;
    if (eye > 1.0) {
        inverted =  vec4(1.0, 1.0, 1.0, 0.1) - regular;
    }
    gl_FragColor = mix(regular, inverted, 1.0);
}
