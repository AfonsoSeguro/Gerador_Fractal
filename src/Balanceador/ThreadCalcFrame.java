/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Balanceador;

import Server.SocketInformationServer;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
import javax.swing.JTextArea;
import trabalho1_a21086_a21282.SocketInformationClient;

/**
 *
 * @author afons
 */

//Calcula uma imagem fractal em função de um servidor
public class ThreadCalcFrame extends Thread{
    
    private AtomicInteger fr;//saber qual a frame que se se está a calcular
    private byte [][] frames;// video fractal com tdas as frames
    private BigDecimal [] zooms;//todos os zooms das imagens do video
    private SocketInformationClient info;//objecto que foi enviado do cliente com os parametros necessários para o calculo da imagem fractal
    private ServerInformation maq;//ip e porta do servidor que vai calcular a frame
    private JTextArea log;//painel de logs
    private ArrayList<ServerInformation> maquinas;//todos o servers que estão á ecuta do balanciador
    
    public ThreadCalcFrame(AtomicInteger fr, byte [][] frames, SocketInformationClient info, BigDecimal [] zooms, ServerInformation maq, JTextArea log, ArrayList<ServerInformation> maquinas){
        this.fr = fr;
        this.frames = frames;
        this.zooms = zooms;
        this.info = info;
        this.maq = maq;
        this.log = log;
        this.maquinas = maquinas;
    }
    
    //escreve um log na consola
    private void escreverLog(String linhas){
        Date date = new Date();
        Calendar calen = Calendar.getInstance();
        calen.setTime(date);
        String lin = this.log.getText();
        lin += "\n" + calen.get(Calendar.DAY_OF_MONTH)+":"+(calen.get(Calendar.MONTH) + 1)+":"+calen.get(Calendar.YEAR)+" "+calen.get(Calendar.HOUR_OF_DAY)+":"+calen.get(Calendar.MINUTE)+":"+calen.get(Calendar.SECOND)+" :: " + linhas + "\nBalanceador>";
        this.log.setText(lin);
        this.log.setCaretPosition(this.log.getText().length());
    }
    
    //converte uma bufferedimage para um byte []
    public static byte[] imageToByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        baos.flush();
        return  baos.toByteArray();
    }
    
    @Override
    public void run(){
        int i = 0;
        try{
            BigDecimal half = new BigDecimal(0.9);
            for (i = fr.getAndIncrement(); i < frames.length; i = fr.getAndIncrement()){
                Socket socket = new Socket(maq.ip, maq.porta);//abre uma socket com o servidor

                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());//para enviar informação ao servidor
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());//para receber informação do servidor

                SocketInformationServer soc = new SocketInformationServer(info.tamanhoH, info.tamanhoV, info.iteracoes, info.centroX, info.centroY, zooms[i], info.fra, info.palete);//parametros para calcualar a imagem fractal

                out.writeObject(soc);
                out.flush();
                
                frames[i] = (byte [])in.readObject();//recebe um byte [] que contem a imagem fractal
                socket.close();
                out.close();
                in.close();
            }
        }
        catch(Exception ex){
            //caso o servidor, pare inesperadamente, o mesmo é retrado da lista de servidores
            synchronized(this.log){
                escreverLog(ex.getMessage());
            }
            try {frames[i]  = imageToByteArray(new BufferedImage(info.tamanhoH, info.tamanhoV, BufferedImage.TYPE_INT_RGB));}
            catch (IOException ex1) {escreverLog(ex1.getMessage());}
            String ip = maq.ip + ":" + maq.porta;
            synchronized(this.maquinas){
                for (i = 0; i < this.maquinas.size(); i++) {
                    if(this.maquinas.get(i).toString().equals(ip)){
                        this.maquinas.remove(i);
                        escreverLog("Foi removida a máquina " + ip);
                        break;
                    }
                }
            }
        }
    }
}
