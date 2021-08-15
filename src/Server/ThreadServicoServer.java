/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Fractais.Complex;
import Fractais.ComplexB;
import Fractais.Fractal;
import Threads.DarkPointThread;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author afons
 */

//Quando há uma comunicação, passa para esta classe para o servidor poder receber o máximo de conexão possível
public class ThreadServicoServer extends Thread{
    
    Socket socket;
    AtomicBoolean stop;
    JTextArea log;
    JTextField porta;

    public ThreadServicoServer(Socket socket, AtomicBoolean stop, JTextArea log, JTextField porta) {
        super();
        this.socket = socket;
        this.stop = stop;
        this.log = log;
        this.porta = porta;
    }    
    
    private void escreverLog(String linhas){
        Date date = new Date();
        Calendar calen = Calendar.getInstance();
        calen.setTime(date);
        String lin = this.log.getText();
        lin += "\n" + calen.get(Calendar.DAY_OF_MONTH)+":"+(calen.get(Calendar.MONTH) + 1)+":"+calen.get(Calendar.YEAR)+" "+calen.get(Calendar.HOUR_OF_DAY)+":"+calen.get(Calendar.MINUTE)+":"+calen.get(Calendar.SECOND)+" :: " + linhas;
        this.log.setText(lin);
        this.log.setCaretPosition(this.log.getText().length());
    }

    //Calcula o número de casas decimais de num, para calcular o mathContext em função do zoom
    private int calcMath(BigDecimal num) {
        int casas = num.stripTrailingZeros().precision();
        return casas > 5 ? casas : 5;
    }
    
    //converte uma imagem para um byte []
    public static byte[] imageToByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        baos.flush();
        return  baos.toByteArray();
    }
    
    //para descobrir qual o ponto mais negro da teoria da pertubação
    private ComplexB pontoMaisNegroBal(BigDecimal xC, BigDecimal yC, BigDecimal zs, double itera, int tamH, int tamV, MathContext mc){
        int numProc = Runtime.getRuntime().availableProcessors();
        double [] pontos = new double[tamH];
        ExecutorService exe = Executors.newFixedThreadPool(numProc);
        AtomicInteger num = new AtomicInteger(0);
        for (int i = 0; i < numProc; i++) {
            exe.execute(new DarkPointThread(num, xC, yC, zs, itera, tamH, tamV, mc, pontos));
        }
        exe.shutdown();
        try {
            exe.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            //Logger.getLogger(CalcularFractal.class.getName()).log(Level.SEVERE, null, ex);
        }
        int maxI = 0;
        double maxV = 0;
        for (int i = 0; i < pontos.length; i++) {
            if(pontos[i] > maxV ){
                maxI = i;
                maxV = pontos[i];
            }
        }
        return new ComplexB(xC.add(zs.multiply(new BigDecimal(maxI), mc), mc), yC.add(zs.multiply(new BigDecimal((tamH * maxI)/tamV), mc), mc));
    }
    
    //determina A ,B, C no calculo de aproximação por series
    private Complex [] serieErro(ArrayList<ComplexB> center, ArrayList<ComplexB> penter, double itera, BigDecimal zoom, MathContext mc){
        Complex AA = new Complex(0,0);
        Complex um = new Complex(1,0);
        Complex dois = new Complex(2,0);
        Complex A0 = penter.get(0).subtract(center.get(0), mc).doubleComplex();
        Complex A02 = A0.multiply(A0);
        Complex A03 = A0.multiply(A02);
        Complex AB = new Complex(0,0);
        Complex An = new Complex(1,0);
        Complex Bn = new Complex(0,0);
        Complex Cn = new Complex(0,0);
        Complex Xn2;
        int i = 0;
        while(i < 3){
            Xn2 = dois.multiply(center.get(i).doubleComplex());
            Complex A = Xn2.multiply(An).sum(um);
            Complex B = Xn2.multiply(Bn).sum(An.multiply(An));
            Complex C = Xn2.multiply(Cn).sum(dois.multiply(An).multiply(Bn));

            An = A;
            Bn = B;
            Cn = C;

            AB = (An.multiply(A0)).sum(Bn.multiply(A02)).sum(Cn.multiply(A03));

            if((AA.re*AA.re)+(AA.im*AA.im) >= 4 && itera < i) break;
            AA = (center.get(i).doubleComplex().multiply(AA).multiply(dois)).sum(AA.multiply(AA)).sum(A0);
            i++;
        }
        Complex [] result = {An, Bn, Cn};
        return result;
    }
    
    private byte [] desenharFractalMovie(SocketInformationServer soc) throws IOException{
        if(soc.zoom.compareTo(new BigDecimal(1.0E-13)) < 0 && soc.fra.contains("MandelBrot"))return desenharFractalMovieFI(soc);
        BufferedImage img = new BufferedImage(soc.tamanhoH, soc.tamanhoV, BufferedImage.TYPE_INT_RGB);
        int mini = soc.tamanhoH < soc.tamanhoV ? soc.tamanhoH : soc.tamanhoV;
        //class que represente um inteiro mas preparado para ser mexido por varias threads
        AtomicInteger colunasI = new AtomicInteger(0);
        //numero threads disponiveis
        int numProc = Runtime.getRuntime().availableProcessors();
        //inicialização de uma pool fixa de threads
        ExecutorService exe = Executors.newFixedThreadPool(numProc);

        for (int i = 0; i < numProc; i++) {
            //inicialização da thread especifica
            exe.execute(new FractalThreadBalServer(colunasI, soc.tamanhoH, soc.tamanhoV, soc.centroX.doubleValue(), soc.centroY.doubleValue(), soc.zoom.doubleValue(), mini, soc.iteracoes, soc.fra, img, soc.palete));
        }
        //indica que mais nenhuma thread vai ser adicionada
        exe.shutdown();
        try {
            //espera pelas threads lançadas até ao maximo de um dia
            exe.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            //Logger.getLogger(CalcularFractal.class.getName()).log(Level.SEVERE, null, ex);
        }
        //imprime no textArea toda a informação relacioda ao fractal 
        return imageToByteArray(img);   
    }
    
    private byte [] desenharFractalMovieFI(SocketInformationServer soc) throws IOException{
        BufferedImage img = new BufferedImage(soc.tamanhoH, soc.tamanhoV, BufferedImage.TYPE_INT_RGB);
        int mini = soc.tamanhoH < soc.tamanhoV ? soc.tamanhoH : soc.tamanhoV;
        
        BigDecimal x, y, tamH = new BigDecimal(soc.tamanhoH), tamV = new BigDecimal(soc.tamanhoV), dois = new BigDecimal(2),miniB = new BigDecimal(mini);
        MathContext mc = new MathContext(calcMath(soc.centroX));

        BigDecimal xC = soc.centroX.subtract(((soc.zoom.multiply(tamH, mc)).divide(new BigDecimal(mini), mc)).divide(dois, mc), mc);
        BigDecimal yC = soc.centroY.subtract(((soc.zoom.multiply(tamV, mc)).divide(new BigDecimal(mini), mc)).divide(dois, mc), mc);
        BigDecimal zs = soc.zoom.divide(new BigDecimal(mini), mc);

        ComplexB cp = pontoMaisNegroBal(xC, yC, zs, soc.iteracoes, soc.tamanhoH, soc.tamanhoV, mc);
        //-1.1876116653582036 -0.3016687738833415
        //ComplexB cp = new ComplexB(new BigDecimal(-0.6834926996410432), new BigDecimal(-0.32792535928503335));
        ComplexB pp = new ComplexB(xC.add(zs.multiply(new BigDecimal(soc.tamanhoH/2), mc), mc), yC.add(zs.multiply(new BigDecimal(soc.tamanhoV/2), mc), mc));
        //ArrayList<ComplexB> center = MandelBrothP.centralPoint(cp.re, cp.im, itera, mc);
        ArrayList<ComplexB> center = Fractal.centralPoint(cp.re, cp.im, soc.iteracoes, mc);
        ArrayList<ComplexB> penter = Fractal.centralPoint(pp.re, pp.im, soc.iteracoes, mc);

        Complex [] ABC = serieErro(center, penter, soc.iteracoes, soc.zoom, mc);

        Complex [] centroDouble = new Complex[center.size()];
        for (int i = 0; i < centroDouble.length; i++)centroDouble[i] = center.get(i).doubleComplex();

        BigDecimal cX = center.get(0).re;
        BigDecimal cY = center.get(0).im;

        AtomicInteger colunasI = new AtomicInteger(0);
        int numProc = Runtime.getRuntime().availableProcessors();
        ExecutorService exe = Executors.newFixedThreadPool(numProc);

        for (int i = 0; i < numProc; i++) {
            exe.execute(new FractalThreadBalFServer(colunasI, soc.tamanhoH, soc.tamanhoV, soc.iteracoes, mc, centroDouble, cX, cY, xC, yC, zs, ABC, img, soc.palete));
        }
        exe.shutdown();
        try {
            exe.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            escreverLog(ex.getMessage());
        }
        return imageToByteArray(img);
    }
    
    @Override
    public void run(){
        try{
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

            SocketInformationServer soc = (SocketInformationServer)in.readObject();
            if(soc.fra.equals("goodbye")){
                socket.close();
                out.close();
                in.close();
                this.stop.set(true);
                Socket fecha = new Socket("127.0.0.1", Integer.parseInt(porta.getText()));
                fecha.close();
                escreverLog("Servidor desconnectado");
                
                return;
            }
            escreverLog("A calcular Imagem " + soc.fra + ":" + soc.zoom.doubleValue());
            out.writeObject(desenharFractalMovie(soc));
            out.flush();

            socket.close();
            out.close();
            in.close();
        }catch(Exception ex){
            escreverLog(ex.getMessage());
        }
    }
}
