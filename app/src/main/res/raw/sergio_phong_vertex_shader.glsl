uniform mat4 u_MVPMatrix;   		// in: Matriz Projection*ModelView
uniform mat4 u_MVMatrix;			// in: Matriz ModelView

attribute vec4 a_Position;			// in: Posición de cada vértice
attribute vec3 a_Normal;			// in: Normal de cada vértice
attribute vec2 a_UV;				// in: Coordenadas UV de mapeado de textura

varying vec2 v_UV;                  // out: Coordenadas UV de mapeado de textura para el fragment
varying vec4 v_Color;				// out: Color de salida al fragment shader
varying vec3 v_P;                   // out: Posición del vértice
varying vec3 v_N;                   // out: Normal del vértice

void main()
{
    // Pasar datos al fragment shader
    v_UV = a_UV;
    v_P  = vec3(u_MVMatrix * a_Position);           // Posición del vértice
    v_N  = vec3(u_MVMatrix * vec4(a_Normal, 0.0));  // Normal del vértice

	gl_Position = u_MVPMatrix * a_Position;
}