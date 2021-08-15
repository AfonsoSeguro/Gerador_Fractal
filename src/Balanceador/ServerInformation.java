/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Balanceador;

/**
 *
 * @author afons
 */
//guarda informação dos varios servidores ligados ligados ao servidor(ip e porta)
public class ServerInformation {
    public String ip;
    public int porta;
    
    public ServerInformation(String ip, int porta){
        this.ip = ip;
        this.porta = porta;
    }
    
    @Override
    public String toString(){
        return ip + ":" + porta;
    }
}
