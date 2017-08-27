precision mediump float;
uniform sampler2D iChannel;
uniform float iGlobalTime;
uniform vec2 iResolution;
void main()
{
    float time = iGlobalTime;
    vec2 uv = gl_FragCoord.xy / iResolution.xy;
    uv.x = 0.1*uv.y*sin(10.0*time) + uv.x;
    uv.y = 0.1*uv.x*sin(10.0*time) + uv.y;

    vec4 color = texture2D(tex, uv);
    gl_FragColor = color;
}