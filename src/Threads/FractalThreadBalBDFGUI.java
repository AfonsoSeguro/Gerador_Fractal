/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Threads;

import Fractais.Complex;
import Fractais.Fractal;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author afons
 */
public class FractalThreadBalBDFGUI extends Thread{
    
    AtomicInteger colunasI;
    int tamanhoH, tamanhoV;
    double maxC, minC, iteracoes;
    String col;
    double [][] graf;
    MathContext mc;
    Complex [] centroDouble;
    BigDecimal cX, cY, xC, yC, zs;
    Complex [] ABC;
    ProgressReporter reporter;
    boolean stop;

    public FractalThreadBalBDFGUI(AtomicInteger colunasI, int tamanhoH, int tamanhoV, double maxC, double minC, double iteracoes, String col, double [][] graf, MathContext mc, Complex[] centroDouble, BigDecimal cX, BigDecimal cY, BigDecimal xC, BigDecimal yC, BigDecimal zs, Complex[] ABC, ProgressReporter reporter) {
        super();
        this.colunasI = colunasI;
        this.tamanhoH = tamanhoH;
        this.tamanhoV = tamanhoV;
        this.maxC = maxC;
        this.minC = minC;
        this.iteracoes = iteracoes;
        this.col = col;
        this.graf = graf;
        this.mc = mc;
        this.centroDouble = centroDouble;
        this.cX = cX;
        this.cY = cY;
        this.xC = xC;
        this.yC = yC;
        this.zs = zs;
        this.ABC = ABC;
        this.reporter = reporter;
        this.stop = false;
    }
    
    @Override
    public void interrupt(){
        this.stop = true;
        super.interrupt();
    }
    
    @Override
    public void run(){
        BigDecimal x,y;
        for (int colunas = colunasI.getAndIncrement(); colunas < tamanhoV; colunas = colunasI.getAndIncrement()) {
            for (int linhas = 0; linhas < tamanhoH; linhas++) {
                x = xC.add(zs.multiply(new BigDecimal(linhas), mc), mc);
                y = yC.add(zs.multiply(new BigDecimal(colunas), mc), mc);

                //calcular a cor
                double color = Fractal.ColorPertubationAproximation(centroDouble,x.subtract(cX, mc).doubleValue(), y.subtract(cY, mc).doubleValue(), ABC, (int)iteracoes, mc)/iteracoes;

                graf[linhas][colunas] = color;
            }
            //sempre que uma linha é calculada a barra de progresso é actualizada
            this.reporter.accumulateProgress(1);
            if(this.stop)return;
        }
    }
    
}