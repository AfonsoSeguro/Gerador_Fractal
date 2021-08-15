/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Balanceador;

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
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JTextArea;
import trabalho1_a21086_a21282.SocketInformationClient;

/**
 *
 * @author afons
 */

//Quando há uma comunicação, passa para esta classe para o balanceador poder receber o máximo de conexão possível
public class ThreadServico extends Thread{
    
    private Socket clientSocket;
    private ArrayList<ServerInformation> maquinas;
    private JTextArea log;
    
    
    public ThreadServico(Socket socket, ArrayList<ServerInformation> maquinas, JTextArea log){
        this.clientSocket = socket;
        this.maquinas = maquinas;
        this.log = log;
    }
    
    private void escreverLog(String linhas){
        Date date = new Date();
        Calendar calen = Calendar.getInstance();
        calen.setTime(date);
        String lin = this.log.getText();
        lin += "\n" + calen.get(Calendar.DAY_OF_MONTH)+":"+(calen.get(Calendar.MONTH) + 1)+":"+calen.get(Calendar.YEAR)+" "+calen.get(Calendar.HOUR_OF_DAY)+":"+calen.get(Calendar.MINUTE)+":"+calen.get(Calendar.SECOND)+" :: " + linhas + "\nBalanceador>";
        this.log.setText(lin);
        this.log.setCaretPosition(this.log.getText().length());
    }
    
    private int calcMath(BigDecimal num) {
        int casas = num.stripTrailingZeros().precision();
        return casas > 5 ? casas : 5;
    }
    
    //lança uma thread por cada servidor para calcular as imagens fractais
    private byte [][] connectServers(SocketInformationClient info) throws Exception{
        MathContext mc = new MathContext(calcMath(info.centroX));
        ArrayList<BigDecimal> zs = new ArrayList();
        BigDecimal zoom = info.zoomOut;
        BigDecimal half = new BigDecimal(0.9);//
        while(zoom.compareTo(info.zoomIn) >= 0){//calcula todos os zoomz
            zs.add(zoom);
            zoom = zoom.multiply(half, mc);
        }
        BigDecimal [] zooms = new BigDecimal[zs.size()];
        for (int i = 0; i < zooms.length; i++)zooms[i] = zs.get(i);
        byte [][] frames = new byte[zooms.length][];
        
        AtomicInteger frame = new AtomicInteger(0);//para saber qual a frame que está a ser calculada por cada servidor
        
        ServerInformation [] maqs;
        synchronized(this.maquinas){
            maqs = new ServerInformation[this.maquinas.size()];
            for (int i = 0; i < maqs.length; i++)maqs[i] = this.maquinas.get(i);
        }
        ExecutorService exe = Executors.newFixedThreadPool(maqs.length);
        for (int i = 0; i < maqs.length; i++) {
            exe.execute(new ThreadCalcFrame(frame, frames, info.clone(),zooms, maqs[i], log, maquinas));//lança uma thread por cada servidor
        }
        exe.shutdown();
        exe.awaitTermination(1, TimeUnit.DAYS);
        return frames;
    }
    
    @Override
    public void run(){
        try{
            ObjectInputStream in = new ObjectInputStream(this.clientSocket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(this.clientSocket.getOutputStream());

            String acao = (String)in.readObject();
            if(acao.equals("ServerIn")){//se a primeira informaçao do servidor for "ServerIn" quer dizer que é um servidor que está a escuta e preparado para ser calculado
                String ip = (String)in.readObject();
                int pt = in.readInt();
                boolean add = true;
                synchronized(this.maquinas){
                    for (int i = 0; i < this.maquinas.size(); i++) {
                        if(this.maquinas.get(i).toString().equals(ip+":"+pt)){
                            escreverLog("máquina " + ip + ":" + pt + " já existe");
                            add = false;
                            break;
                        }
                    }
                    if(add){
                        this.maquinas.add(new ServerInformation(ip, pt));
                        escreverLog("Foi adicionada a máquina " + ip + ":" + pt);
                    }
                }
            }
            else if(acao.equals("ServerOut")){//se a primeira informaçao do servidor for "ServerOut" quer dizer que é um servidor que o servidor já nao está a escuta
                String ip = (String)in.readObject();
                int pt = in.readInt();
                ip += ":"+pt;
                synchronized(this.maquinas){
                    for (int i = 0; i < this.maquinas.size(); i++) {
                        if(this.maquinas.get(i).toString().equals(ip)){
                            this.maquinas.remove(i);
                            escreverLog("Foi removida a máquina " + ip);
                            break;
                        }
                    }
                }
            }
            else if(acao.equals("ClientePC")){//quer dizer que é um cliente a pedir para calcular de um Pc
                SocketInformationClient info = (SocketInformationClient)in.readObject();
                escreverLog("Calculo Fractal para computador " + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort() + " Iniciado");
                out.writeObject(connectServers(info));
                escreverLog("Calculo Fractal para computador " + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort() + " Finalizado");
            }
            else if(acao.equals("ClienteAndroid")){//quer dizer que é um cliente a pedir para calcular de um Android
                SocketInformationClient info = new SocketInformationClient(((String)in.readObject()).split("_"));
                escreverLog("Calculo Fractal para android " + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort() + " Iniciado");
                out.writeObject(connectServers(info));
                escreverLog("Calculo Fractal para android " + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort() + " Finalizado");
            }
            else {
                escreverLog("Cliente não identificado tentou realizar um calculo Fractal");
            }
            
            this.clientSocket.close();
            in.close();
            out.close();
        }
        catch(Exception ex){
            escreverLog(ex.getMessage());
        }
    }
}
