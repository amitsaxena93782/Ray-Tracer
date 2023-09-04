package raytracer.core.def;

import java.util.ArrayList;
import java.util.List;

import raytracer.core.Hit;
import raytracer.core.Obj;
import raytracer.geom.BBox;
import raytracer.geom.Primitive;
import raytracer.math.Point;
import raytracer.math.Ray;
import raytracer.math.Vec3;

/**
 * Represents a bounding volume hierarchy acceleration structure
 */
public class BVH extends BVHBase {
    private List<Obj> objects; // List of objects in the BVH
    private BBox boundingBox; // Additionally created by me

    public BVH() {

        objects = new ArrayList<>();
        boundingBox = BBox.EMPTY;
    }

    @Override
    public BBox bbox() {

        return boundingBox;
    }

    /**
     * Adds an object to the acceleration structure
     *
     * @param prim
     *             The object to add
     */
    @Override
    public void add(final Obj prim) {

        objects.add(prim);

        if (boundingBox == null) {
            boundingBox = prim.bbox();
        } else {
            boundingBox = BBox.surround(boundingBox, prim.bbox());
        }
    }

    /**
     * Builds the actual bounding volume hierarchy
     */
    @Override
    public void buildBVH() {

        if (objects.size() <= THRESHOLD) {
            return; // Stop further subdivision
        }

        Point maxOfMinPoints = calculateMaxOfMinPoints();
        Vec3 extent = boundingBox.getMax().sub(boundingBox.getMin());

        int splitDim = calculateSplitDimension(extent);
        float splitPos = (boundingBox.getMin().get(splitDim) + maxOfMinPoints.get(splitDim)) * 0.5f;

        BVHBase a = new BVH();
        BVHBase b = new BVH();

        distributeObjects(a, b, splitDim, splitPos);

        a.buildBVH();
        b.buildBVH();

        boundingBox = BBox.surround(a.bbox(), b.bbox());
    }

    @Override
    public Point calculateMaxOfMinPoints() {

        Point maxOfMinPoints = null;

        for (Primitive obj : objects) {
            BBox bbox = obj.bbox();
            if (bbox != null) {
                Point min = bbox.getMin();
                if (maxOfMinPoints == null) {
                    maxOfMinPoints = min;
                } else {
                    maxOfMinPoints = min.max(maxOfMinPoints);
                }
            }
        }
        // Add a default value here if no objects were added
        if (maxOfMinPoints == null) {
            maxOfMinPoints = new Point(0, 0, 0);
        }

        return maxOfMinPoints;
    }

    @Override
    public int calculateSplitDimension(final Vec3 extent) {

        int splitDim = 0;
        float maxExtent = 0;

        for (int i = 0; i < 3; i++) {
        float diff = extent.get(i);
        if (diff > maxExtent) {
        maxExtent = diff;
        splitDim = i;
        }
        }

        return splitDim;
    }

    @Override
    public void distributeObjects(final BVHBase a, final BVHBase b,
            final int splitDim, final float splitPos) {

        for (Obj obj : objects) {
            BBox objBBox = obj.bbox();
            float objMinPos = objBBox.getMin().get(splitDim);

            if (objMinPos <= splitPos) {
                a.add(obj);
            } else {
                b.add(obj);
            }
        }
    }

    @Override
    public Hit hit(final Ray ray, final Obj obj, final float tMin, final float tMax) {

        if (boundingBox != null && boundingBox.hit(ray, tMin, tMax) != Hit.No.get()) {
            Hit firstHit = null;
            float closestT = tMax;

            for (Obj child : objects) {
                Hit hit = child.hit(ray, this, tMin, tMax);
                if (hit != Hit.No.get()) {
                    closestT = hit.getParameter();
                    firstHit = hit;
                }
            }

            return firstHit;
        }

        return Hit.No.get();
    }

    @Override
    public List<Obj> getObjects() {

        return objects;
    }
}
