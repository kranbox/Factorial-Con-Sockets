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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Alumno
 */
public class Servidor extends JFrame implements ActionListener {

    String DatoCifrada = "";
    String ClaveCifrada = "";
    String Dato;
    String Clave = "133457799BBCDFF1";

    Socket clientSocket;
    Socket socket;
    int puertoReceptor = 5555;
    int puertoEmisor = 6666;
    ServerSocket serverSocket;
    JFrame _ventana;
    JTextField _txtMsj;
    JTextArea _txtChat;
    JButton _btnEnviar;
    Container _cont;

    public Servidor() {
        _ventana = new JFrame("Servidor");
        _ventana.setBounds(200, 200, 700, 600);
        _ventana.setLayout(null);
        _ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        _ventana.getLocation(null);
        _ventana.setLocationRelativeTo(null);
        _ventana.setResizable(false);
        _cont = _ventana.getContentPane();

    }

    public void verVentanaChat() throws IOException {

        _txtChat = new JTextArea();
        _txtChat.setBounds(10, 10, 600, 300);
        _txtChat.scrollRectToVisible(_txtChat.getVisibleRect());
        _txtChat.disable();

        _cont.add(_txtChat);

        _txtMsj = new JTextField(16);
        _txtMsj.setBounds(10, 400, 470, 30);
        _txtMsj.disable();

        _txtMsj.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                int Si_Fue_enter = e.getKeyCode();
                Character caracter = e.getKeyChar();
                if (Si_Fue_enter == KeyEvent.VK_ENTER) {
                } else if (!esValido(caracter)) {
                    String texto = "";
                    for (int i = 0; i < _txtMsj.getText().length(); i++) {
                        if (esValido(_txtMsj.getText().charAt(i))) {
                            texto += _txtMsj.getText().charAt(i);
                        }
                    }
                    _txtMsj.setText(texto);
                    JOptionPane.showMessageDialog(null, "SOLO SE PERMITEN LETRAS ENTRE A-F Y NUMEROS :v,MAXIMO 16 CARACTERES", "ERROR", JOptionPane.ERROR_MESSAGE);
                    getToolkit().beep();
                }

            }

            public boolean esValido(Character caracter) {
                char c = caracter;
                if (_txtMsj.getText().length() > 16) {
                    return false;

                } else {
                    return (c >= '0' && c <= '9');
                }
            }
        });

        _cont.add(_txtMsj);

        _btnEnviar = new JButton();
        _btnEnviar.setBounds(45, 500, 50, 20);
        _btnEnviar.setText("Enviar");
        _btnEnviar.addActionListener(this);
        _btnEnviar.disable();
        _cont.add(_btnEnviar);

        _ventana.setVisible(true);

        IniciarChat();
        IniciarConexion();
        Escuchador();
        if (RecibirMensaje(1)) {
            _txtChat.setText(_txtChat.getText() + "Conexion Establecida");
            _txtChat.setText(_txtChat.getText() + "\n Escribe tu mensaje");
            _txtMsj.enable();
            _btnEnviar.enable();
        }
    }

    public void IniciarChat() {
        _txtChat.setText("Clave predeterminada: " + Clave);
        _txtChat.setText(_txtChat.getText() + "\n Para cerrar el programa escriba \"ccc\" ");
    }

    public void IniciarConexion() {
        _txtChat.setText(_txtChat.getText() + "\n Esperando conexion...");
        _txtMsj.disable();
        _btnEnviar.disable();
    }

    public void Escuchador() throws IOException {
        serverSocket = new ServerSocket(puertoReceptor);
        //el socketServer acpeta el socket cliente

    }

    public boolean RecibirMensaje(int i) throws IOException {
        boolean condicion = true;
        clientSocket = serverSocket.accept();
        try {

            //genero mi entrada de datos como el teclado, pero en flujo de datos del socket
            Scanner entrada = new Scanner(clientSocket.getInputStream());
            String msjRecibido = entrada.nextLine();

            CIFRAR Des = new CIFRAR();
            String MensajeDesCifrado;
            MensajeDesCifrado = Des.CIFRAR(Completar(msjRecibido), Clave, 2);//1 para cifrar, 2 para descifrar  
            //System.out.println("el servidor dice: " + msjRecibido);
            if (MensajeDesCifrado.equals("ccc")) {
                serverSocket.close();
                clientSocket.close();
                _txtChat.setText(_txtChat.getText() + "\n Conexion Cerrada");
                condicion = false;
            } else {
                condicion = true;
            }

            if (i == 1) {//1 cuando es el 1er mensaje
            } else {
                this._txtChat.setText(_txtChat.getText() + "\n Cliente: \t" + msjRecibido + " Factorial: " + MensajeDesCifrado);
            }

            System.out.println("Message Recived");
        } catch (IOException ex) {
            _txtChat.setText(_txtChat.getText() + "\n Servidor: \t" + ex.getMessage());
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

    public boolean EnviarMensaje() throws IOException {
        _txtMsj.enable();
        _btnEnviar.enable();
        boolean condicion = true;
        try {
            PrintStream salidaServer = ConnecToSend();
            //lo enviamos al servidor
            String msj = "";
            msj = _txtMsj.getText();
            _txtMsj.setText("");

            //CIFRAMOS EL MENSAJE
            CIFRAR Des = new CIFRAR();
            String MensajeCifrado;
            MensajeCifrado = Des.CIFRAR(Completar(msj), Clave, 1);//1 para cifrar, 2 para descifrar            

            salidaServer.println(MensajeCifrado);
            condicion = !msj.equals("cerrar");
            if (msj.equals("ccc")) {
                serverSocket.close();
                clientSocket.close();
                _txtChat.setText(_txtChat.getText() + "\n Conexion Cerrada" + msj);
                condicion = false;
            } else {
                condicion = true;
            }
            //System.out.println("el servidor dice: " + msjRecibido);
            _txtChat.setText(_txtChat.getText() + "\n Servidor: \t" + msj + " Cifrado: " + MensajeCifrado);
            if (!condicion) {
                socket.close();
            }
        } catch (IOException ex) {
            _txtChat.setText(_txtChat.getText() + "\n Servidor: \t" + ex.getMessage());
        }
        System.out.println("Message Sended");

        RecibirMensaje(0);

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
            EnviarMensaje();

        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
