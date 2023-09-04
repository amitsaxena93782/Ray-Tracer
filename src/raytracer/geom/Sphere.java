package raytracer.geom;

import raytracer.core.Hit;
import raytracer.core.Obj;
import raytracer.core.def.LazyHitTest;
import raytracer.math.Constants;
import raytracer.math.Point;
import raytracer.math.Ray;
import raytracer.math.Vec2;
import raytracer.math.Vec3;

public class Sphere extends BBoxedPrimitive {
    private Point center;
    private float radius;

    public Sphere(Point center, float radius) {
        super(BBox.create(
                new Point(center.x() + radius, center.y() + radius, center.z() + radius),
                new Point(center.x() - radius, center.y() - radius, center.z() - radius)));
        this.center = center;
        this.radius = radius;
    }

    @Override
    public Hit hitTest(final Ray ray, final Obj obj, final float tmin, final float tmax) {
        return new LazyHitTest(obj) {
            private Point point = null;
            private float t;

            @Override
            public float getParameter() {
                return t;
            }

            @Override
            public Point getPoint() {
                if (point == null) {
                    point = ray.eval(t);
                }
                return point;
            }

            @Override
            protected boolean calculateHit() {
                Vec3 oc = ray.base().sub(center);
                float a = ray.dir().dot(ray.dir());
                float b = 2 * oc.dot(ray.dir());
                float c = oc.dot(oc) - radius * radius;
                float discriminant = b * b - 4 * a * c;

                if (discriminant < Constants.EPS) {
                    return false;
                }

                float sqrtDiscriminant = (float) Math.sqrt(discriminant);
                float root1 = (-b + sqrtDiscriminant) / (2 * a);
                float root2 = (-b - sqrtDiscriminant) / (2 * a);
                t = Math.min(root1, root2);

                if (t < Constants.EPS) {
                    return false;
                }

                if (t < tmin || t > tmax) {
                    return false;
                }

                return true;
            }

            @Override
            public Vec2 getUV() {
                Vec3 intersectionPoint = getPoint().sub(center).normalized();
                float u = 0.5f + (float) (Math.atan2(intersectionPoint.z(), intersectionPoint.x()) / (2 * Math.PI));
                float v = 0.5f - (float) (Math.asin(intersectionPoint.y()) / Math.PI);
                return new Vec2(u, v);
            }

            @Override
            public Vec3 getNormal() {
                return getPoint().sub(center).normalized();
            }
        };
    }

    @Override
    public int hashCode() {
        int result = center != null ? center.hashCode() : 0;
        result = 31 * result + Float.floatToIntBits(radius);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Sphere other = (Sphere) obj;
        return Float.compare(other.radius, radius) == 0
                && (center != null ? center.equals(other.center) : other.center == null);
    }

}
