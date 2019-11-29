precision mediump float;			// Precisión media

uniform vec4 u_Color;				// in: color del objeto
uniform vec3 u_LightPos0;           // in: luz 1
uniform vec3 u_LightPos1;           // in: luz 2
uniform sampler2D u_TextureUnit;	// in: Unidad de textura

varying vec2 v_UV;                  // in: Coordenada UV de la textura
varying vec3 v_P;                   // in: Posición del vértice
varying vec3 v_N;                   // in: Normal del vértice

void main()
{
	float ambient  = 0.2;									// intensidad ambiente
	vec4  specularColor = vec4(1, 1, 1, 1);					// Color especular (brillos blancos)

	vec3 P = v_P;                       					// Posición del vértice
	vec3 N = v_N;                                       	// Normal del vértice

	// Primera Luz
	float d = length(P - u_LightPos0);						// distancia
	vec3  L = normalize(P - u_LightPos0);						// Vector Luz
	vec3  V = normalize(P);	  								// Vector Visión (Eye)
	vec3  R = normalize(reflect(-L, N));					// Vector reflejado R=2N(N.L)-L

	float attenuation = 1.0/(0.3+(0.1*d)+(0.01*d*d)); 		// Cálculo de la atenuación

	float diffuse  = max(dot(N, L), 0.0);					// Cálculo de la intensidad difusa
	float specular = pow(max(dot(V, R), 0.0), 100.0);		// Exponente de Phong

	gl_FragColor = attenuation*(u_Color*diffuse + specularColor*specular);

	// Segunda Luz
	d = length(P - u_LightPos1);								// distancia
	L = normalize(P - u_LightPos1);							// Vector Luz
	V = normalize(P);	  									// Vector Visión (Eye)
	R = normalize(reflect(-L, N));							// Vector reflejado R=2N(N.L)-L

	attenuation = 1.0/(0.3+(0.1*d)+(0.01*d*d)); 			// Cálculo de la atenuación

	diffuse  = max(dot(N, L), 0.0);							// Cálculo de la intensidad difusa
	specular = pow(max(dot(V, R), 0.0), 100.0);				// Exponente de Phong

	gl_FragColor += attenuation*(u_Color*diffuse + specularColor*specular);

	// Añadir color y luz ambiente
	gl_FragColor += u_Color * ambient;

	gl_FragColor *= (texture2D(u_TextureUnit, v_UV));
}