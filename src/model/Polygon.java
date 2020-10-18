package model;

import java.util.ArrayList;
import java.util.List;

public class Polygon {

    public List<Point> points;

    public Polygon() {
        points = new ArrayList<Point>();
    }

    public Point getLastPoint()
    {
        if (points.size() > 0)
        {
            return points.get(points.size()-1);
        }
        else
        {
            return null;
        }


    }


}
