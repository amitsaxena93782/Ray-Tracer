package raytracer.geom;

import raytracer.core.Hit;
import raytracer.core.Obj;
import raytracer.core.def.LazyHitTest;
import raytracer.math.Constants;
import raytracer.math.Point;
import raytracer.math.Ray;
import raytracer.math.Vec2;
import raytracer.math.Vec3;

class Plane extends BBoxedPrimitive {
    private Point a;
    private Vec3 normal;
    private float d;

    public Plane(Vec3 normal, Point a) {
        super();
        this.a = a;
        this.normal = normal.normalized();
        d = a.dot(normal);
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
                float denom = ray.dir().normalized().dot(normal);
                if (Math.abs(denom) < Constants.EPS) {
                    return false;
                }

                t = (d - ray.base().dot(normal)) / denom;

                if (t < tmin || t > tmax) {
                    return false;
                }

                return true;
            }

            @Override
            public Vec2 getUV() {
                Point supp = a; // Use the support point of the plane
                if (point == null) {
                    point = ray.eval(t);
                }
                return Util.computePlaneUV(normal, supp, point);
            }

            @Override
            public Vec3 getNormal() {
                return normal;
            }
        };
    }

    @Override
    public int hashCode() {
        int result = a.hashCode();
        result = 31 * result + normal.hashCode();
        result = 31 * result + Float.floatToIntBits(d);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj == null || getClass() != obj.getClass()) return false;

        Plane objPlane = (Plane) obj;
        return Float.compare(objPlane.d, d) == 0 && a.equals(objPlane.a) && normal.equals(objPlane.normal);
    }

}