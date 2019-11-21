precision mediump float;			// Precisión media

uniform sampler2D u_TextureUnit;	// in: Unidad de Textura
varying vec2 v_UV;                  // in: Coordenada recibida desde el vertex shader
varying vec4 v_Color;				// in: Color recibido desde el vertex shader

void main()
{
	gl_FragColor = v_Color * (texture2D(u_TextureUnit, v_UV));
}