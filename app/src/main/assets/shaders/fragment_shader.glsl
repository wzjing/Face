precision highp float;
uniform sampler2D iChannel0;
uniform float iGlobalTime;
uniform vec2 iResolution;
void main()
{
    float time = iGlobalTime;
    // flip in x and transpose xy
    vec2 uv = (iResolution.yx - gl_FragCoord.yx) / iResolution.yx;

    //uv.x = uv.x + sin(iGlobalTime)*sin(20.0*uv.y)*.02;
    //uv.y = uv.y + sin(iGlobalTime)*sin(20.0*uv.x)*.02;

    vec4 color = texture2D(iChannel0, uv.xy);
    gl_FragColor = color;
}