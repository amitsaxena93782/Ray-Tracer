package raytracer.shade;

import java.util.Collection;

import raytracer.core.Hit;
import raytracer.core.LightSource;
import raytracer.core.Shader;
import raytracer.core.Trace;
import raytracer.math.Color;
import raytracer.math.Vec3;
import raytracer.math.Ray;
import raytracer.core.Scene;
import raytracer.math.Point;

public class Phong implements Shader {
    private Shader inner;
    private Color ambient;
    private float diffuse;
    private float specular;
    private float shininess;

    public Phong(final Shader inner, final Color ambient, final float diffuse, final float specular,
            final float shininess) {
        this.ambient = ambient;
        this.inner = inner;
        this.diffuse = diffuse;
        this.specular = specular;
        this.shininess = shininess;
    }

    @Override
    public Color shade(Hit hit, Trace trace) {
        Color sub = inner.shade(hit, trace);
        Collection<LightSource> lightCollection = trace.getScene().getLightSources();

        Color diffuseColor = Color.BLACK;
        Color speculaColor = Color.BLACK;

        for (LightSource light : lightCollection) {

            Vec3 v = light.getLocation().sub(hit.getPoint()).normalized();

            Ray illuminateRay = trace.spawn(hit.getPoint(), v).getRay();
            Scene scene = trace.getScene();
            Hit blockHit = scene.hit(illuminateRay);

            Point hitPoint = hit.getPoint();
            boolean ignore = false;

            if (blockHit.hits()) {
                Point blockHPoint = blockHit.getPoint();
                Point lightPoint = light.getLocation();

                Vec3 hitToLight = new Vec3(hitPoint.x() - lightPoint.x(), hitPoint.y() - lightPoint.y(),
                        hitPoint.z() - lightPoint.z());

                Vec3 hitToBlockHit = new Vec3(blockHPoint.x() - hitPoint.x(), blockHPoint.y() - hitPoint.y(),
                        blockHPoint.z() - hitPoint.z());

                ignore = hitToLight.norm() < hitToBlockHit.norm();
            }
            if (!blockHit.hits() || ignore) {

                diffuseColor = diffuseColor
                        .add(light.getColor().mul(sub).scale(diffuse).scale(Float.max(hit.getNormal().dot(v), 0)));

                Vec3 viewDir = trace.getRay().dir().normalized();
                Vec3 reflectDir = viewDir.reflect(hit.getNormal()).normalized();

                float lastTerm = (float) Math.pow(Float.max(0, reflectDir.dot(v)), shininess);

                speculaColor = speculaColor.add(light.getColor().scale(specular).scale(lastTerm));
            }
        }
        return ambient.add(speculaColor).add(diffuseColor);
    }
}
