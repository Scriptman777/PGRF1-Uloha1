package rasterize;

public class DashedLineRasterizer extends LineRasterizer {

    public DashedLineRasterizer(Raster rst) {
        super(rst);
    }

    public void line(int x1, int y1, int x2, int y2){
       drawNormalLine(x1, y1, x2, y2);
       makeItLookDashed(x1, y1, x2, y2);
       /*
       Vykreslit čáru přerušovanou čáru pomocí algoritmu Midpoint je prakticky nemožné, nicméně jsem se o to pokusil.
       Čára nikdy nezačíná ani nekončí mezerou, způsob vykresolování je však VELMI neefektivní.
        */


    }

    private void drawNormalLine(int x1, int y1, int x2, int y2) {
        int midx, midy;
        midx = (x1 + x2) / 2;
        midy = (y1 + y2) / 2;

        raster.setPixel(midx, midy, 0xffff00);

        if ((Math.abs(x1 - midx) > 1) || (Math.abs(y1 - midy) > 1)) {
            drawNormalLine(x1, y1, midx, midy);
        }

        if ((Math.abs(x2 - midx) > 1) || (Math.abs(y2 - midy) > 1)) {
            drawNormalLine(midx, midy, x2, y2);
        }

    }

    private void makeItLookDashed(int x1, int y1, int x2, int y2) {
        int midx, midy;
        midx = (x1 + x2) / 2;
        midy = (y1 + y2) / 2;

        raster.setPixel(midx, midy, 0x000000);

        if ((Math.abs(x1 - midx) > 5) || (Math.abs(y1 - midy) > 5)) {
            makeItLookDashed(x1, y1, midx, midy);
        }

        if ((Math.abs(x2 - midx) > 5) || (Math.abs(y2 - midy) > 5)) {
            makeItLookDashed(midx, midy, x2, y2);
        }

    }


}
