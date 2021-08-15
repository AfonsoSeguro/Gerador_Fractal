/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

/**
 *
 * @author afons
 */

//SwingWorker do Servidor
public class ServiceServer extends SwingWorker<Object, Object>{

    JTextArea log;
    JTextField ipBalan, portaBalan, porta;
    AtomicBoolean stop;
    
    public ServiceServer(JTextArea log, JTextField ipBalan, JTextField portaBalan, JTextField porta){
        super();
        this.log = log;
        this.ipBalan = ipBalan;
        this.portaBalan = portaBalan;
        this.porta = porta;
        this.stop = new AtomicBoolean(false);
    }
    
    public void stop(){
        try {
            this.stop.set(true);
            Socket fecha = new Socket("127.0.0.1", Integer.parseInt(porta.getText()));
            fecha.close();
        } catch (IOException ex) {
            escreverLog(ex.getMessage());
        }
        escreverLog("Servidor Finalizado");
    }
    
    public boolean getStop(){
        return this.stop.get();
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
    
    //encontra o ip publico da maquina pingando o google 
    private String findMyPublicIp() throws Exception{
        
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("google.com", 80));
        char [] nums = (socket.getLocalAddress() + "").toCharArray();
        String result = "";
        for (int i = 1; i < nums.length; i++) {
            result += nums[i];
        }
        return result;
    }
    
    //avisa o balanciador que este servidor está pronto para o ouvir
    private boolean hello(){
        try{
            Socket socket = new Socket(this.ipBalan.getText(), Integer.parseInt(this.portaBalan.getText()));

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            out.writeObject("ServerIn");
            out.writeObject(findMyPublicIp());//InetAddress.getLocalHost().getHostAddress());
            out.writeInt(Integer.parseInt(this.porta.getText()));
            out.flush();

            socket.close();
            out.close();
            in.close();
            return true;
        }
        catch(Exception ex){
            escreverLog("Não foi possivel comunicar com o Balanceador");
            this.stop.set(true);
            return false;
        }
    }
    
    //avisa o balanceador que este servidor vai para de estar a escuta
    public boolean goodbye(){
        try{
            Socket socket = new Socket(this.ipBalan.getText(), Integer.parseInt(this.portaBalan.getText()));

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            out.writeObject("ServerOut");
            out.writeObject(findMyPublicIp());//InetAddress.getLocalHost().getHostAddress());
            out.writeInt(Integer.parseInt(this.porta.getText()));
            out.flush();

            socket.close();
            out.close();
            in.close();
            return true;
        }
        catch(Exception ex){
            escreverLog("Não foi possivel comunicar com o Balanceador");
            return false;
        }
    }
    
    @Override
    protected Object doInBackground() throws Exception {
        if(!hello())return null;
        ServerSocket server = new ServerSocket(Integer.parseInt(porta.getText()));
        escreverLog("Servidor a ouvir na porta " + porta.getText());
        while(!stop.get()){
            Socket socket = server.accept();//dispara uma thread por cada connecção para aguentar o maximo de conecções possiveis simultaniamente
            if(!stop.get())new ThreadServicoServer(socket, stop, log, porta).start();
        }
        server.close();
        return null;
    }
    
}
