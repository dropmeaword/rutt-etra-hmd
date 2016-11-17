#extension GL_OES_EGL_image_external : require

precision mediump float;
varying vec2 textureCoordinate;
uniform samplerExternalOES s_texture;

void main(void) {
        vec4 regular =  texture2D(s_texture, textureCoordinate);
        vec4 inverted =  vec4(1.0, 1.0, 1.0, 2.0) - regular;
        gl_FragColor = mix(regular, inverted, 1.0);
//  gl_FragColor = texture2D( s_texture, textureCoordinate );
//  gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
}
