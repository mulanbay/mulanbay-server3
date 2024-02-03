package cn.mulanbay.pms.persistent.dto.music;

import java.math.BigDecimal;
import java.math.BigInteger;

public class MusicPracticeCompareStat {

    private Object cx;

    private Object cy;

    public MusicPracticeCompareStat(Object cx, Object cy) {
        this.cx = cx;
        this.cy = cy;
    }

    public Object getCx() {
        return cx;
    }

    public void setCx(Object cx) {
        this.cx = cx;
    }

    public Object getCy() {
        return cy;
    }

    public void setCy(Object cy) {
        this.cy = cy;
    }

    public double getCXValue() {
        if (cx instanceof BigDecimal) {
            return ((BigDecimal) cx).doubleValue();
        }
        if (cx instanceof BigInteger) {
            return ((BigInteger) cx).doubleValue();
        }
        return Double.parseDouble(cx.toString());
    }

    public double getCYValue() {
        if (cy instanceof BigDecimal) {
            return ((BigDecimal) cy).doubleValue();
        }
        if (cy instanceof BigInteger) {
            return ((BigInteger) cy).doubleValue();
        }
        return Double.parseDouble(cy.toString());
    }
}
