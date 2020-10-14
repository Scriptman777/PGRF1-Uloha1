package rasterize;

import model.Line;
import model.Point;
import model.Polygon;

import java.util.List;

public class PolygonRasterizer{

    private Raster raster;

    public PolygonRasterizer(Raster raster) {
        this.raster = raster;
    }

    public void drawPolygon(Polygon poly) {
        Point first = poly.points.get(0);
        FilledLineRasterizer fll = new FilledLineRasterizer(raster);


        for (int i = 1; i < poly.points.size(); i++) {
            fll.line(poly.points.get(i - 1).x,poly.points.get(i - 1).y,poly.points.get(i).x,poly.points.get(i).y);

        }

        fll.line(poly.points.get(poly.points.size() - 1).x,poly.points.get(poly.points.size() - 1).y,first.x,first.y);

    }


}
