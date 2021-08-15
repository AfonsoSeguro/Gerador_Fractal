/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalho1_a21086_a21282;

import Fractais.ComplexB;
import java.math.BigDecimal;

/**
 *
 * @author afons
 */
public class VideoPoint {
    public ComplexB fin;
    public BigDecimal zoomInit;
    public BigDecimal zoomFin;

    public VideoPoint(ComplexB fin, BigDecimal zoomInit, BigDecimal zoomFin) {
        this.fin = fin;
        this.zoomInit = zoomInit;
        this.zoomFin = zoomFin;
    }
    public VideoPoint() {
        this.fin = null;
        this.zoomInit = null;
        this.zoomFin = null;
    }
    
    public void setZoomInit(BigDecimal zoom){
        this.zoomInit = zoom;
    }
    
    public void setZoomFin(BigDecimal zoom){
        this.zoomFin = zoom;
    }
    
    public void setCenterPoint(ComplexB pt){
        this.fin = pt;
    }
    
    public boolean ready(){
        return (fin != null && zoomInit != null && zoomFin != null);
    }

}
