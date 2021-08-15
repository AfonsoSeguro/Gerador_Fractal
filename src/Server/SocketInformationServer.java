/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author afons
 */
public class SocketInformationServer implements Serializable{
    //descrição das variáveis mais pormenorizada no construtor
    public int tamanhoH, tamanhoV;
    public double iteracoes;
    public BigDecimal centroX, centroY, zoom;
    public String fra;
    public float palete;

    public SocketInformationServer(int tamanhoH, int tamanhoV, double iteracoes, BigDecimal centroX, BigDecimal centroY, BigDecimal zoom, String fra, float palete) {
        this.tamanhoH = tamanhoH;//tamanho horizontal da janela
        this.tamanhoV = tamanhoV;//tamanho vertical da janela
        this.iteracoes = iteracoes;//número de iterações
        this.centroX = centroX;//coordenada real do centro fractal
        this.centroY = centroY;//coordenada imaginária do centro fractal
        this.zoom = zoom;//valor do zoom do fractal
        this.fra = fra;//algoritmo fractal(MandelBroth,...)
        this.palete = palete;
    }
    
    @Override
    public String toString(){
        return zoom + " " + iteracoes + " " + centroX.toString() + " " + centroY.toString() + " " + tamanhoH + " " + tamanhoV;
    }
}
