precision mediump float;			// Precisión media

uniform sampler2D u_TextureUnit;	// in: Unidad de Textura
varying vec2 v_TexCoordinate;       // Coordenada recibida desde el vertex shader

void main()
{
	gl_FragColor = (texture2D(u_TextureUnit, v_TexCoordinate));
}