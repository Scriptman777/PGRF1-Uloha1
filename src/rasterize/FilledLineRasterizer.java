package rasterize;

public class FilledLineRasterizer extends LineRasterizer {

    public FilledLineRasterizer(Raster rst) {
        super(rst);
    }

    /*
    Implementace algoritmu "Midpoint"

    Výhody:
    - Nezáleží na směru úsečky ani pozicích počátečního a koncového bodu, algorimus je vždy stejný
    - Rekurzivní kód je snadno čitelný a pochopitelný

    Nevýhody:
    - Rekurzivní algoritmy zpravidla vyzžívají více paměti
    - Může být pomalejší oproti jiným algoritmům

     */

    public void line(int x1, int y1, int x2, int y2){
        int midx, midy;
        midx=(x1+x2)/2;
        midy=(y1+y2)/2;

        raster.setPixel(midx,midy,0xffff00);

        if ((Math.abs(x1-midx)>1) || (Math.abs(y1-midy)>1))
        {
            line(x1,y1,midx,midy);
        }

        if ((Math.abs(x2-midx)>1) || (Math.abs(y2-midy)>1))
        {
            line(midx,midy,x2,y2);
        }



    }



}
