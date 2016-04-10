/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemasdistribuidossocketservidor.Srv;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import sistemasdistribuidossocketcliente.Utilidades.Llamada;

/**
 *
 * @author kryshedz
 */
public class ConeccionSocket {
    public static ServerSocket SOCKET_SERVIDOR;
    public static Socket SOCKET_CLIENTE;
    DataOutputStream envio;
    DataInputStream recibo;
    BufferedInputStream in;
    int tamano;
    float valorCobrar;

    public ConeccionSocket() throws IOException {
        SOCKET_SERVIDOR=new ServerSocket(9999);
        
    }
    public void conectar() throws IOException, ClassNotFoundException{
        while(true){
            System.out.println("conectando");
            SOCKET_CLIENTE=SOCKET_SERVIDOR.accept();
            System.out.println("recibiendo datos");
            recibirDatos();
        }
        
    }
    public String recibirTipoONombre() throws IOException{
        recibo=new DataInputStream(SOCKET_CLIENTE.getInputStream());
        return recibo.readUTF().toString();
    }
    public int recibirTamano() throws IOException{
        recibo=new DataInputStream(SOCKET_CLIENTE.getInputStream());
        return recibo.readInt();
    }
    
    public void recibirDatos() throws IOException, ClassNotFoundException{
        System.out.println("entrada recibir datos");
        String tipo=recibirTipoONombre();
        
        tamano=recibirTamano();
        System.out.println(tamano);
        System.out.println(tipo);
        switch(tipo){
            case("AUDIO"):
                System.out.println("audio");
                String nombreArchivo=recibirTipoONombre();
                FileOutputStream fos = new FileOutputStream("src/" + nombreArchivo);
                BufferedOutputStream out = new BufferedOutputStream(fos);
                in = new BufferedInputStream(SOCKET_CLIENTE.getInputStream());
                byte[] buffer = new byte[tamano];
                for (int i = 0; i < buffer.length; i++) {
                    buffer[i] = (byte) in.read();
                }

                out.write(buffer);
                System.out.println("Fin de envio audio");
                break;
            case("OBJETO"):
                in=new BufferedInputStream(SOCKET_CLIENTE.getInputStream());
                System.out.println("objeto");
                byte[] bufferObj = new byte[tamano];
                for (int i = 0; i < bufferObj.length; i++) {
                    bufferObj[i] = (byte) in.read();
                }
                ByteArrayInputStream bis = new ByteArrayInputStream(bufferObj);
                ObjectInputStream ois = new ObjectInputStream(bis);
                Llamada clase = (Llamada) ois.readObject();
                valorCobrar=clase.cobrarLlamada(tipoLlamada(clase.getNumeroTelefono()));
                System.out.println("Numero: "+clase.getNumeroTelefono());
                System.out.println("Tiempo: "+clase.getHora()+" "+clase.getMinutos()+" "+clase.getSegundos());
                System.out.println(valorCobrar);
                enviarDatos();
                System.out.println("Fin de envio Objeto");
                break;
            default:
                System.out.println("ninguna de las opciones");
                


        }
        
    }
    public void enviarDatos() throws IOException{
        envio=new DataOutputStream(SOCKET_CLIENTE.getOutputStream());
        envio.writeFloat(valorCobrar);
    }
    
    
    private String tipoLlamada(String numero){
        int numeroLng=numero.length();
        String tipoLlamda="";
        switch(numeroLng){
            case(7):
                tipoLlamda="nacionalConvencional";
                break;
            case(10):
                tipoLlamda="celularNacional";
                break;
            case(13):
                tipoLlamda="internacional";
                break;
            default:
                System.out.println("tipo de llamada no existe");

            
        }
        return tipoLlamda;
    }
    
    
    public void cerrarConecionTotal() throws IOException{
        if(recibo != null)
            recibo.close();
        if(envio != null)
            envio.close();
        if(in!=null)
            in.close();
        if(SOCKET_CLIENTE != null)
            SOCKET_CLIENTE.close();
    }
    
    
    
}
