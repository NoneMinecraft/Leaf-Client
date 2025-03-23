#version 120

uniform float iTime;
uniform vec2 iResolution;

// 改进的噪声函数 (替换iChannel0依赖)
float hash(float n) {
	return fract(sin(n)*43758.5453);
}

float noise(vec3 x) {
	vec3 p = floor(x);
	vec3 f = fract(x);
	f = f*f*(3.0-2.0*f);
	float n = p.x + p.y*57.0 + 113.0*p.z;

	return mix(
	mix(
	mix(hash(n + 0.0), hash(n + 1.0), f.x
	),
	mix(
	mix(hash(n + 57.0), hash(n + 58.0), f.x
	),
	f.y
	)));
}

// 优化后的场函数
float field(in vec3 p) {
	float strength = 7.0;
	float accum = 0.0;
	float prev = 0.0;

	for(int i = 0; i < 12; ++i) {
		float noiseVal = noise(p*0.25 + vec3(iTime*0.2));
		accum += exp(-strength * abs(noiseVal - prev));
		prev = noiseVal;
		p = abs(p)/dot(p,p)*2.0 - 1.0;
	}
	return accum * 0.5;
}

void mainImage(out vec4 fragColor, in vec2 fragCoord) {
	vec2 uv = (2.0*fragCoord.xy - iResolution.xy)/iResolution.y;

	// 动态参数
	float time = iTime * 0.5;
	vec3 rd = normalize(vec3(uv, 1.0));

	// 生成过程式云层
	vec3 p = vec3(uv*2.0, time*0.5);
	float cloud = field(p);

	// 颜色生成
	vec3 skyColor = mix(vec3(0.5,0.7,1.0), vec3(0.1,0.2,0.4), length(uv));
	vec3 cloudColor = mix(vec3(1.0,0.9,0.8), vec3(0.6,0.6,0.7), cloud);

	// 添加动态星光
	float stars = pow(hash(uv.x*123.4 + hash(uv.y*567.8)), 256.0);
	cloudColor += stars * vec3(1.0);

	// 最终混合
	vec3 col = mix(skyColor, cloudColor, smoothstep(0.3,0.7,cloud));
	fragColor = vec4(col,1.0);
}

void main() {
	mainImage(gl_FragColor, gl_FragCoord.xy);
}