/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Balanceador;

import Server.SocketInformationServer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

/**
 *
 * @author afons
 */

//SwingWorker do Balanceador
public class ServiceBalanceador extends SwingWorker<Object, Object>{
    
    private ArrayList<ServerInformation> maquinas;//guarda todos os servidores que estão a escuta
    private JTextArea log;//textArea onde se mostra os logs
    private int porta;//porta do servidor a escuta
    private boolean stop;//caso seja necessário para o balanceador
    
    public ServiceBalanceador(JTextArea log, int porta){
        this.log = log;
        this.maquinas = new ArrayList();
        this.porta = porta;
        this.stop = false;
    }
    
    //retorna todos os servidores, a escuta
    public String showMaquinas(){
        String maqs = "";
        synchronized(maquinas){
            for (int i = 0; i < maquinas.size(); i++) {
                 maqs += maquinas.get(i).toString();
                 if(i != maquinas.size() - 1)maqs += "\n";
            }
        }
        return maqs;
    }
    
    //remove todos os servidres a escuta
    public void removeMaquinas(){
        String maqs = "";
        synchronized(maquinas){
            for (int i = 0; i < maquinas.size(); i++) {
                try {
                    Socket sock = new Socket(maquinas.get(i).ip, maquinas.get(i).porta);//para retirar a escuta do balanceador
                    ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(sock.getInputStream());
                    out.writeObject(new SocketInformationServer(0, 0, 0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, "goodbye", 1));
                    sock.close();
                    out.close();
                    in.close();
                } catch (IOException ex) {
                    escreverLog(ex.getMessage());
                }
            }
        }
        escreverLog("Todas as maquinas foram encerradas");
    }
    
    //para o balanciador
    public void stop(){
        try {
            this.stop = true;
            Socket fecha = new Socket("127.0.0.1", porta);
            fecha.close();
        } catch (IOException ex) {
            escreverLog(ex.getMessage());
        }

    }
    
    public boolean getStop(){
        return this.stop;
    }
    
    //escreve um log
    private void escreverLog(String linhas){
        Date date = new Date();
        Calendar calen = Calendar.getInstance();
        calen.setTime(date);
        String lin = this.log.getText();
        lin += "\n" + calen.get(Calendar.DAY_OF_MONTH) + ":" + (calen.get(Calendar.MONTH) + 1) +":"+calen.get(Calendar.YEAR)+" "+calen.get(Calendar.HOUR_OF_DAY)+":"+calen.get(Calendar.MINUTE)+":"+calen.get(Calendar.SECOND)+" :: " + linhas + "\nBalanceador>";
        this.log.setText(lin);
        this.log.setCaretPosition(this.log.getText().length());
    }
    
    @Override
    //
    protected Object doInBackground() throws Exception {
        ServerSocket server = new ServerSocket(porta);
        escreverLog("Balanceador a ouvir na porta " + porta);
        while(!stop){
            Socket socket = server.accept();//caso receba uma connecção, dispara logo uma thread para poder aguentar o maximo de connecções possiveis em simultanio
            if(!stop)new ThreadServico(socket, maquinas, log).start();
        }
        server.close();
        return null;
    }
    
}
