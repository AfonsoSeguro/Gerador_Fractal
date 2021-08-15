/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.net.Socket;
import trabalho1_a21086_a21282.SocketInformationClient;

/**
 *
 * @author afons
 */
public class PseudoCliente {
    public static void main(String[] args) throws Exception{
        Socket socket = new Socket("127.0.0.1", 5000);
        
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        
        out.writeObject("Cliente");
        out.flush();
        
        out.writeObject(new SocketInformationClient(0, 0, 0, BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal(200), new BigDecimal(1), "fra", "alg", 1));
        out.flush();
        
        double [][][] fuckYea = (double[][][])in.readObject();
        //String messagem = (String)in.readObject();
        //System.out.println(messagem);
        
        socket.close();
        out.close();
        in.close();
    }
}
