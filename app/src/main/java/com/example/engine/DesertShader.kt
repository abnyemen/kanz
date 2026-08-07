package com.example.engine

import android.opengl.GLES20
import android.util.Log

class DesertShader {

    private val vertexShaderCode = """
        uniform mat4 uMVPMatrix;
        uniform mat4 uModelMatrix;
        uniform vec3 uSunDir;
        uniform vec3 uSunColor;
        uniform vec3 uAmbientColor;
        uniform vec3 uViewPos;
        uniform float uAlpha;
        uniform float uTime;
        uniform float uSpecularPower;
        uniform float uHeatHazeIntensity;
        
        attribute vec3 aPosition;
        attribute vec3 aNormal;
        attribute vec4 aColor;
        
        varying vec4 vColor;
        varying float vFogFactor;
        varying vec3 vWorldNormal;
        varying vec3 vViewDir;
        
        uniform float uFogDensity;

        void main() {
            vec3 pos = aPosition;
            // Desert heat haze distortion wave animation for ULTRA quality
            if (uHeatHazeIntensity > 0.0) {
                float wave = sin(pos.x * 2.5 + pos.z * 2.0 + uTime * 4.0) * 0.03 * uHeatHazeIntensity;
                pos.y += wave;
            }

            vec4 worldPos = uModelMatrix * vec4(pos, 1.0);
            gl_Position = uMVPMatrix * vec4(pos, 1.0);
            
            vec3 worldNormal = normalize(mat3(uModelMatrix) * aNormal);
            vWorldNormal = worldNormal;
            
            vec3 L = normalize(uSunDir);
            float diff = max(dot(worldNormal, L), 0.12);
            
            // Blinn-Phong Specular & Rim Lighting for Gold / Sand Shine
            vec3 V = normalize(uViewPos - worldPos.xyz);
            vViewDir = V;
            vec3 H = normalize(L + V);
            float spec = pow(max(dot(worldNormal, H), 0.0), uSpecularPower);
            
            // Desert Gold Specular Color & Rim Glow
            vec3 specColor = vec3(1.0, 0.9, 0.6) * spec * 0.4;
            float rim = 1.0 - max(dot(V, worldNormal), 0.0);
            vec3 rimGlow = vec3(0.9, 0.7, 0.3) * pow(rim, 3.0) * 0.2;
            
            vec3 finalLight = uAmbientColor + (uSunColor * diff) + specColor + rimGlow;
            vColor = vec4(aColor.rgb * finalLight, aColor.a * uAlpha);
            
            // Distance Fog calculation for sandstorms & atmospheric depth
            float dist = length(gl_Position.xyz);
            vFogFactor = exp(-dist * uFogDensity);
            vFogFactor = clamp(vFogFactor, 0.0, 1.0);
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        precision mediump float;
        
        varying vec4 vColor;
        varying float vFogFactor;
        uniform vec3 uFogColor;

        void main() {
            vec3 colorWithFog = mix(uFogColor, vColor.rgb, vFogFactor);
            gl_FragColor = vec4(colorWithFog, vColor.a);
        }
    """.trimIndent()

    var program: Int = 0
    var positionHandle: Int = -1
    var normalHandle: Int = -1
    var colorHandle: Int = -1
    var mvpMatrixHandle: Int = -1
    var modelMatrixHandle: Int = -1
    var sunDirHandle: Int = -1
    var sunColorHandle: Int = -1
    var ambientColorHandle: Int = -1
    var fogColorHandle: Int = -1
    var fogDensityHandle: Int = -1
    var alphaHandle: Int = -1
    var viewPosHandle: Int = -1
    var timeHandle: Int = -1
    var specularPowerHandle: Int = -1
    var heatHazeIntensityHandle: Int = -1

    fun init() {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)

        positionHandle = GLES20.glGetAttribLocation(program, "aPosition")
        normalHandle = GLES20.glGetAttribLocation(program, "aNormal")
        colorHandle = GLES20.glGetAttribLocation(program, "aColor")

        mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        modelMatrixHandle = GLES20.glGetUniformLocation(program, "uModelMatrix")
        sunDirHandle = GLES20.glGetUniformLocation(program, "uSunDir")
        sunColorHandle = GLES20.glGetUniformLocation(program, "uSunColor")
        ambientColorHandle = GLES20.glGetUniformLocation(program, "uAmbientColor")
        fogColorHandle = GLES20.glGetUniformLocation(program, "uFogColor")
        fogDensityHandle = GLES20.glGetUniformLocation(program, "uFogDensity")
        alphaHandle = GLES20.glGetUniformLocation(program, "uAlpha")
        viewPosHandle = GLES20.glGetUniformLocation(program, "uViewPos")
        timeHandle = GLES20.glGetUniformLocation(program, "uTime")
        specularPowerHandle = GLES20.glGetUniformLocation(program, "uSpecularPower")
        heatHazeIntensityHandle = GLES20.glGetUniformLocation(program, "uHeatHazeIntensity")
    }

    fun use() {
        GLES20.glUseProgram(program)
        if (alphaHandle != -1) {
            GLES20.glUniform1f(alphaHandle, 1.0f)
        }
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        val compiled = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)
        if (compiled[0] == 0) {
            Log.e("DesertShader", "Error compiling shader: " + GLES20.glGetShaderInfoLog(shader))
            GLES20.glDeleteShader(shader)
            return 0
        }
        return shader
    }
}
