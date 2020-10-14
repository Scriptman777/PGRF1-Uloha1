package rasterize;

public class DottedLineRasterizer extends LineRasterizer {

    public DottedLineRasterizer(Raster rst) {
        super(rst);
    }


    public void line(int x1, int y1, int x2, int y2) {

        int midx, midy;
        midx = (x1 + x2) / 2;
        midy = (y1 + y2) / 2;

        raster.setPixel(midx, midy, 0xffff00);

        if ((Math.abs(x1 - midx) > 5) || (Math.abs(y1 - midy) > 5)) {
            line(x1, y1, midx, midy);
        }

        if ((Math.abs(x2 - midx) > 5) || (Math.abs(y2 - midy) > 5)) {
            line(midx, midy, x2, y2);
        }
    }

}
