package sample;
import gnu.io.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.TooManyListenersException;

public class SerialPortMange {
    public static final ArrayList<String> findPort() {
        // 获得当前所有可用串口
        Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
        ArrayList<String> portNameList = new ArrayList<String>();
        // 将可用串口名添加到List并返回该List
        while (portList.hasMoreElements()) {
            String portName = portList.nextElement().getName();
            portNameList.add(portName);
        }
        return portNameList;
    }
    public static final SerialPort openPort(String portName, int baudrate)
            throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException {
        CommPortIdentifier portIdentifier = CommPortIdentifier
                .getPortIdentifier(portName);
        // 打开端口，并给端口名字和一个timeout（打开操作的超时时间）
        CommPort commPort = portIdentifier.open(portName, 2000);
        SerialPort serialPort=null;
        if (commPort instanceof SerialPort) {
            serialPort = (SerialPort) commPort;
            serialPort.setSerialPortParams(baudrate,
                    SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
        }
        return serialPort;
    }
    public static void closePort(SerialPort serialPort) {
        if (serialPort != null) {
            serialPort.close();
            serialPort = null;
        }
    }
    public static void sendData(SerialPort serialPort,String s){
        if(s==null){
            return;
        }
        byte[] message=s.getBytes();
        OutputStream out=null;
        try {
            out = serialPort.getOutputStream();
            out.write(message);
            out.flush();
        } catch (IOException e) {
            //throw new SendDataToSerialPortFailure();
        } finally {
            try {
                if (out != null) {
                    out.close();
                    out = null;
                }
            } catch (IOException e) {
                //throw new SerialPortOutputStreamCloseFailure();
            }
        }
    }
    public static String readFromPort(SerialPort serialPort) {
        InputStream in = null;
        byte[] bytes = {};
        try {
            in = serialPort.getInputStream();
//            // 缓冲区大小为一个字节
//            byte[] readBuffer = new byte[10];
//
//            int bytesNum = in.read(readBuffer);
//            while (bytesNum > 0) {
//                byte[] newbytes=new byte[]
//                bytesNum = in.read(readBuffer);
//            }
            //尝试
            int bytesNum=in.available();
            bytes=new byte[bytesNum];
            bytesNum=in.read(bytes);
        } catch (IOException e) {
            //new ReadDataFromSerialPortFailure().printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                    in = null;
                }
            } catch (IOException e) {
                //new SerialPortInputStreamCloseFailure().printStackTrace();
            }
        }
        return new String(bytes);
    }
    public static void addListener(SerialPort port, SerialPortEventListener listener) throws TooManyListenersException {
        port.addEventListener(listener);
        //设置当有数据到达时唤醒监听接收线程
        port.notifyOnDataAvailable(true);
        //设置当通信中断时唤醒中断线程
        port.notifyOnBreakInterrupt(true);
    }

}

