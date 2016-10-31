/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatcondes;

import Paquetes.CIFRAR;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Alumno
 */
public class cChat extends JFrame implements ActionListener {

    String DatoCifrada;
    String ClaveCifrada;
    String Dato;
    String Clave = "133457799BBCDFF1";

    int fact;
    int configKCondition = 0;
    Socket clientSocket;
    Socket socket;
    int puertoReceptor = 6666;
    int puertoEmisor = 5555;
    ServerSocket serverSocket;
    JFrame _ventana;    
    JTextArea _txtChat;    
    Container _cont;

    public cChat() {
        _ventana = new JFrame("Cliente");
        _ventana.setBounds(200, 200, 700, 600);
        _ventana.setLayout(null);
        _ventana.setResizable(false);
        _ventana.setLocationRelativeTo(null);
        _ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        _cont = _ventana.getContentPane();

    }

    public void verVentanaChat() throws IOException {

        _txtChat = new JTextArea();
        _txtChat.setBounds(10, 10, 600, 300);
        _txtChat.disable();
        _txtChat.scrollRectToVisible(_txtChat.getVisibleRect());
        _cont.add(_txtChat);       

        _ventana.setVisible(true);
        IniciarConexion();
        Escuchador();
        EnviarMensaje(1);

    }

    public void IniciarConexion() {
        _txtChat.setText("Clave predeterminada: " + Clave);
        _txtChat.setText(_txtChat.getText() + "\n Estableciendo conexion...");
        _txtChat.setText(_txtChat.getText() + "\n Esperando mensaje...");
    }

    public void Escuchador() throws IOException {
        serverSocket = new ServerSocket(puertoReceptor);
        //el socketServer acpeta el socket cliente

    }

    public static int factorial(int n) {
        int resultado = 1;
        for (int i = 1; i <= n; i++) {
            resultado *= i;
        }
        return resultado;
    }
    
    public boolean RecibirMensaje() throws IOException {
        boolean condicion = true;
        try {
            clientSocket = serverSocket.accept();
            Scanner entrada = new Scanner(clientSocket.getInputStream());
            String msjRecibido = entrada.nextLine();            
            CIFRAR Des = new CIFRAR();
            String MensajeDesCifrado;
            MensajeDesCifrado = Des.CIFRAR(Completar(msjRecibido), Clave, 2);//1 para cifrar, 2 para descifrar  
            fact = factorial(Integer.parseInt(MensajeDesCifrado));
            if (MensajeDesCifrado.equals("ccc")) {
                serverSocket.close();
                clientSocket.close();
                _txtChat.setText(_txtChat.getText() + "\n Conexion Cerrada");
                condicion = false;
            } else {
                condicion = true;
            }
            this._txtChat.setText(_txtChat.getText() + "\n Cliente: \t" + msjRecibido + "\n Descifrado: " + MensajeDesCifrado + "\n factorial: "+ fact);
            System.out.println("Message Recived");
            EnviarMensaje(0);
        } catch (IOException ex) {
            _txtChat.setText(_txtChat.getText() + "\n Cliente: \t" + ex.getMessage());
            condicion = true;
        }
        return condicion;
    }

    public PrintStream ConnecToSend() throws IOException {
        String ipServidor = "127.0.0.1";
        socket = new Socket(ipServidor, puertoEmisor);//abrimos el sockete en el puerto e ip        
        PrintStream salidaServer = new PrintStream(socket.getOutputStream());
        return salidaServer;
    }

    public boolean EnviarMensaje(int i) throws IOException {

        boolean condicion = true;
        try {
            PrintStream salidaServer = ConnecToSend();
            //lo enviamos al servidor
            String msj = "";
            msj = fact +"";

            //CIFRAMOS EL MENSAJE
            CIFRAR Des = new CIFRAR();
            String MensajeCifrado;
            MensajeCifrado = Des.CIFRAR(Completar(msj), Clave, 1);//1 para cifrar, 2 para descifrar            

            switch (msj) {
                case "aacc":
                    _txtChat.setText(_txtChat.getText() + "\n Accesos: \n iicc: informacion \n ccc: cerrar conexion \n cic: configurar clave K");
                    break;
                case "ccc":
                    serverSocket.close();
                    clientSocket.close();
                    _txtChat.setText(_txtChat.getText() + "\n Conexion Cerrada: " + msj);
                    condicion = false;
                    break;
                case "cic":
                    _txtChat.setText(_txtChat.getText() + "\n Ingresa la clave K:");
                    configKCondition = 2;
                    break;
                default:
                    salidaServer.println(MensajeCifrado);
                    condicion = true;
                    break;
            }
            switch (i) {
                case 1:
                    //1 cuando es el 1er mensaje
                    //System.out.println("el servidor dice: " + msjRecibido);
                    _txtChat.setText(_txtChat.getText() + "\n Servidor: Conexion Establecida");
                    _txtChat.setText(_txtChat.getText() + "\n Para cerrar el programa escriba \"ccc\" ");
                    _txtChat.setText(_txtChat.getText() + "\n Esperando respuesta... ");
                    break;                
                default:
                    //System.out.println("el servidor dice: " + msjRecibido);
                    _txtChat.setText(_txtChat.getText() + "\n Servidor: \t" + msj + " Cifrado: " + MensajeCifrado);
                    break;
            }

            if (!condicion) {
                socket.close();
            }
        } catch (IOException ex) {
            _txtChat.setText(_txtChat.getText() + "\n Cliente: \t" + ex.getMessage());
        }
        System.out.println("Message Sended");

        RecibirMensaje();
        return condicion;
    }

    public String Completar(String dato) {
        String finalString;
        int tamaño = dato.length();
        switch (tamaño) {
            case 1:
                finalString = "000000000000000" + dato;
                break;
            case 2:
                finalString = "00000000000000" + dato;
                break;
            case 3:
                finalString = "0000000000000" + dato;
                break;
            case 4:
                finalString = "000000000000" + dato;
                break;
            case 5:
                finalString = "00000000000" + dato;
                break;
            case 6:
                finalString = "0000000000" + dato;
                break;
            case 7:
                finalString = "000000000" + dato;
                break;
            case 8:
                finalString = "00000000" + dato;
                break;
            case 9:
                finalString = "0000000" + dato;
                break;
            case 10:
                finalString = "000000" + dato;
                break;
            case 11:
                finalString = "00000" + dato;
                break;
            case 12:
                finalString = "0000" + dato;
                break;
            case 13:
                finalString = "000" + dato;
                break;
            case 14:
                finalString = "00" + dato;
                break;
            case 15:
                finalString = "0" + dato;
                break;
            case 16:
                finalString = dato;
                break;
            default:
                finalString = "0000000000000000";
                break;
        }
        return finalString;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            EnviarMensaje(configKCondition);
        } catch (IOException ex) {
            Logger.getLogger(cChat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
