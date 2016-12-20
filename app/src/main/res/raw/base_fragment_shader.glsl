#extension GL_OES_EGL_image_external : require

// a Rutt-Etra inspired shader:
// white vertical lines with settable number of lines, phase, displacement and fake3d


precision mediump float;
varying vec2 textureCoordinate;

uniform samplerExternalOES tex0; 	// always the last camera capture
uniform sampler2D tex1; 	        // feedback texture

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
    float imageSize = 600.;                     // approximate image size in pixels
    float nrLines = 43.;					    // number of lines
    float startPhase = 0.5;				    // startphase of lines
    float displace = 2.0;					    // luminance displacement in terms of phase
    float baseBrightness = 0.3;
    float luminanceMapping = 0.6;
    float fakeStereo = 0.2;

    vec4 cam =  texture2D(tex0, textureCoordinate);
    cam +=  texture2D(tex0, textureCoordinate + vec2(-1. / imageSize, -1. / imageSize));
    cam +=  texture2D(tex0, textureCoordinate + vec2(-1. / imageSize, 0. / imageSize));
    cam +=  texture2D(tex0, textureCoordinate + vec2(-1. / imageSize, 1. / imageSize));
    cam +=  texture2D(tex0, textureCoordinate + vec2(-1. / imageSize, 2. / imageSize));
    cam +=  texture2D(tex0, textureCoordinate + vec2(0. / imageSize, -1. / imageSize));
    cam +=  texture2D(tex0, textureCoordinate + vec2(0. / imageSize, 1. / imageSize));
    cam +=  texture2D(tex0, textureCoordinate + vec2(0. / imageSize, 2. / imageSize));
    cam +=  texture2D(tex0, textureCoordinate + vec2(1. / imageSize, -1. / imageSize));
    cam +=  texture2D(tex0, textureCoordinate + vec2(1. / imageSize, 0. / imageSize));
    cam +=  texture2D(tex0, textureCoordinate + vec2(1. / imageSize, 1. / imageSize));
    cam +=  texture2D(tex0, textureCoordinate + vec2(1. / imageSize, 2. / imageSize));
    cam +=  texture2D(tex0, textureCoordinate + vec2(2. / imageSize, -1. / imageSize));
    cam +=  texture2D(tex0, textureCoordinate + vec2(2. / imageSize, 0. / imageSize));
    cam +=  texture2D(tex0, textureCoordinate + vec2(2. / imageSize, 1. / imageSize));
    cam +=  texture2D(tex0, textureCoordinate + vec2(2. / imageSize, 2. / imageSize));
    cam = cam / 16.0;

    if (eye == 2.) {
        displace = displace * (1. - fakeStereo);
    }
    float luminance =  0.21 * cam.r + 0.72 * cam.g + 0.07 * cam.b;
    float phase = fract(textureCoordinate.x * nrLines + startPhase + (luminance - 0.5) * displace);
    float maxBrightness = baseBrightness + luminanceMapping * luminance;
    float brightness = clamp(maxBrightness * (sin(phase * 6.2832) / 2. + 0.5), 0., 1.);
    // gl_FragColor = vec4(brightness * 0.1, brightness, brightness * 0.3, 1.); 	 // green lines
    gl_FragColor = vec4(brightness, brightness, brightness, 1.); 			// white lines
}

