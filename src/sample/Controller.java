package sample;

import gnu.io.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;

public class Controller {

    private Timeline timer;
    private SerialPort serialPort = null;
    @FXML
    private ChoiceBox choiceBoxPort;
    @FXML
    private CheckBox checkBoxTime;
    @FXML
    private ChoiceBox<Integer> choiceBoxBT;
    //    @FXML
//    private Button buttonOpen;
    @FXML
    private TextField textFieldTime;
    @FXML
    private TextArea textAreaInput;
    @FXML
    private TextArea textAreaOutput;

    @FXML
    private void buttonOpenHandle() {
        //未完善
        try {
            if (choiceBoxBT.getValue() != null)
                serialPort = SerialPortMange.openPort(choiceBoxPort.getValue().toString(), choiceBoxBT.getValue());
            //test
            //System.out.println(choiceBoxPort.getValue());
        } catch (NoSuchPortException e) {
            e.printStackTrace();
        } catch (PortInUseException e) {
            e.printStackTrace();
        } catch (UnsupportedCommOperationException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void buttonCloseHandle() {
        SerialPortMange.closePort(serialPort);
    }

    @FXML
    private void buttonSendHandle(){
        SerialPortMange.sendData(serialPort,textAreaOutput.getText());
    }
    @FXML
    private void checkBoxTimeHandle(){
        if(checkBoxTime.isSelected()){
            timer.setDelay(Duration.millis(Integer.parseInt(textFieldTime.getText())));
            timer.play();
        }else {
            timer.stop();
        }
    }


    @FXML
    private void initialize() {
        choiceBoxPort.setItems(FXCollections.observableArrayList(SerialPortMange.findPort()));
        choiceBoxBT.setItems(FXCollections.observableArrayList(1200, 2400, 4800, 9600, 14400, 19200));
        choiceBoxBT.setValue(9600);
        timer=new Timeline(new KeyFrame(Duration.millis(1500), e-> SerialPortMange.sendData(serialPort,textAreaOutput.getText())
                //System.out.println("hfg"+textAreaOutput.getText())
        ));
        timer.setCycleCount(Timeline.INDEFINITE);

        //test
        //System.out.println("init");
    }


    public void setMessage(String message) {
        this.textAreaInput.setText(textAreaInput.getText() + "/n" + message);
    }

    private class SerialListener implements SerialPortEventListener {
        /**
         * 处理监控到的串口事件
         */
        public void serialEvent(SerialPortEvent serialPortEvent) {

            switch (serialPortEvent.getEventType()) {

                case SerialPortEvent.BI: // 10 通讯中断
                   // ShowUtils.errorMessage("与串口设备通讯中断");
                    break;

                case SerialPortEvent.OE: // 7 溢位（溢出）错误

                case SerialPortEvent.FE: // 9 帧错误

                case SerialPortEvent.PE: // 8 奇偶校验错误

                case SerialPortEvent.CD: // 6 载波检测

                case SerialPortEvent.CTS: // 3 清除待发送数据

                case SerialPortEvent.DSR: // 4 待发送数据准备好了

                case SerialPortEvent.RI: // 5 振铃指示

                case SerialPortEvent.OUTPUT_BUFFER_EMPTY: // 2 输出缓冲区已清空
                    break;

                case SerialPortEvent.DATA_AVAILABLE: // 1 串口存在可用数据
                    String data = null;
                    try {
                        if (serialPort == null) {
                            //ShowUtils.errorMessage("串口对象为空！监听失败！");
                        } else {
                            // 读取串口数据
                            data = SerialPortMange.readFromPort(serialPort);
                            setMessage(data);
                        }
                    } catch (Exception e) {
                        //ShowUtils.errorMessage(e.toString());
                        // 发生读取错误时显示错误信息后退出系统
                        System.exit(0);
                    }
                    break;
            }
        }
    }
}
