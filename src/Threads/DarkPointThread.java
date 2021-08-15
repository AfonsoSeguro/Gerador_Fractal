/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Threads;

import Fractais.ComplexB;
import Fractais.Fractal;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author afons
 */
public class DarkPointThread extends Thread {
    
    AtomicInteger p;
    BigDecimal xC,yC, zs;
    double itera;
    int tamH, tamV;
    MathContext mc;
    double [] pontos;

    public DarkPointThread(AtomicInteger p, BigDecimal xC, BigDecimal yC, BigDecimal zs, double itera, int tamH, int tamV, MathContext mc, double [] pontos) {
        this.p = p;
        this.xC = xC;
        this.yC = yC;
        this.zs = zs;
        this.itera = itera;//número de iterações
        this.tamH = tamH;//tamanho horizontal da janela
        this.tamV = tamV;//tamanho vertical da janela
        this.mc = mc;
        this.pontos = pontos;
    }
    
    @Override
    public void run(){
        for (int i = p.getAndIncrement(); i < tamH; i = p.incrementAndGet()) {
            double color = Fractal.MandelBrothBD(xC.add(zs.multiply(new BigDecimal(i), mc), mc), yC.add(zs.multiply(new BigDecimal((tamH * i)/tamV), mc), mc), itera, mc);
            pontos[i] = (itera - color) / itera;
        }
    }

}
