/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalho1_a21086_a21282;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author afons
 */
public class SocketInformationClient implements Serializable{
    //descrição das variáveis mais pormenorizada no construtor
    public int tamanhoH, tamanhoV;
    public double iteracoes;
    public BigDecimal centroX, centroY, zoomOut, zoomIn;
    public String fra, alg;
    public float palete;

    public SocketInformationClient(int tamanhoH, int tamanhoV, double iteracoes, BigDecimal centroX, BigDecimal centroY, BigDecimal zoomOut, BigDecimal zoomIn, String fra, String alg, float palete) {
        this.tamanhoH = tamanhoH;//tamanho horizontal da janela
        this.tamanhoV = tamanhoV;//tamanho vertical da janela
        this.iteracoes = iteracoes;//número de iterações
        this.centroX = centroX;//coordenada real do centro fractal
        this.centroY = centroY;//coordenada imaginária do centro fractal
        this.zoomOut = zoomOut;
        this.zoomIn = zoomIn;
        this.fra = fra;//algoritmo fractal(MandelBroth,...)
        this.alg = alg;
        this.palete = palete;
    }
    
    public SocketInformationClient(String ... inf) {
        this.tamanhoH = Integer.parseInt(inf[0]);
        this.tamanhoV = Integer.parseInt(inf[1]);
        this.iteracoes = Double.parseDouble(inf[2]);
        this.centroX = new BigDecimal(inf[3]);
        this.centroY = new BigDecimal(inf[4]);
        this.zoomOut = new BigDecimal(inf[5]);
        this.zoomIn = new BigDecimal(inf[6]);
        this.fra = inf[7];
        this.alg = inf[8];
        this.palete = Float.parseFloat(inf[9]);
    }
    
    @Override
    public SocketInformationClient clone(){
        return new SocketInformationClient(this.tamanhoH, this.tamanhoV, this.iteracoes, new BigDecimal(centroX.toString()), new BigDecimal(centroY.toString()), new BigDecimal(zoomOut.toString()), new BigDecimal(zoomIn.toString()), fra, alg, palete);
    }
}
