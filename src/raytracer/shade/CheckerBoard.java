package raytracer.shade;

import raytracer.core.Hit;
import raytracer.core.Shader;
import raytracer.core.Trace;
import raytracer.math.Color;
import raytracer.math.Vec2;

public class CheckerBoard implements Shader {
    private final Shader shader1;
    private final Shader shader2;
    private final float size;

    public CheckerBoard(final Shader shader1, final Shader shader2, final float size) {
        this.shader1 = shader1;
        this.shader2 = shader2;
        this.size = size;
    }

    @Override
    public Color shade(final Hit hit, final Trace trace) {
        final Vec2 texCoord = hit.getUV();

        final float u = texCoord.x() / size;
        final float v = texCoord.y() / size;
        final int x = (int) Math.floor(u) + (int) Math.floor(v);

        if (x % 2 == 0)
            return shader1.shade(hit, trace);
        return shader2.shade(hit, trace);
    }
}
