package de.crafty.skylife.util;

import org.joml.Vector3f;

public class VectorUtils {


    public static Vector3f rotateAroundY(Vector3f vec, double angle) {
        float angleCos = (float) Math.cos(angle);
        float angleSin = (float) Math.sin(angle);

        float x = angleCos * vec.x() + angleSin * vec.z();
        float z = -angleSin * vec.x() + angleCos * vec.z();
        return new Vector3f(x, vec.y(), z);
    }

}
