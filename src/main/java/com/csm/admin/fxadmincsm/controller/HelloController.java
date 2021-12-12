package com.csm.admin.fxadmincsm.controller;

import com.csm.Message;
import com.csm.SIModel;
import com.csm.admin.fxadmincsm.Client;
import com.csm.admin.fxadmincsm.HelloApplication;
import com.csm.model.OsHWModel;
import com.csm.model.ProcessModel;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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
    public Label CPUtext;
    public Label Display;
    public TabPane tabPane;
    public Tab ProcessPane;
    public TableView<ProcessModel> Processtable;
    public TableColumn<ProcessModel,String> ProcessNameColumn;
    public TableColumn<ProcessModel, Integer> ProcessPIDColumn;
    public TableColumn<ProcessModel, String> ProcessMemoryColumn;
    @FXML
    private Label welcomeText;
    public static ObservableList<String> listClient;
    public static HashMap<String,OsHWModel> OShash;
    public static ObservableList<ProcessModel> listProcess;

    //Take Screen Shot
    @FXML
    protected void onHelloButtonClick() {
        try {
            // write on the output stream
            String input = table.getSelectionModel().getSelectedItem();
            if(input==null){
                return;
            }
            Message object = new Message();
            object.command = Message.TAKE_SCREEN_SHOT;
            object.data = "data";
            object.toId = input;
            Client.dos.writeObject(object);
            try {
                Message msg = (Message) Client.dis.readObject();
                if(msg.data.equals("error"))
                    return;
                BufferedImage image = base64StringToImg(msg.data);
                int h = image.getHeight();
                int w = image.getWidth();
                image = resize(image, h / 2, w / 2);
                ImageIO.write(image, "png", new File("src/main/resources/Shot.png"));
                Image imageShow = new Image("src/main/resources/Shot.png");
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
                InetAddress ip = InetAddress.getByName("localhost");
                // establish the connection
                Client.s = new Socket(ip, Client.ServerPort, ip, 3123);
                // obtaining input and out streams
                Client.dos = new ObjectOutputStream(Client.s.getOutputStream());
                Client.dis = new ObjectInputStream(Client.s.getInputStream());
            } else {
                System.exit(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        pcName.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue()));
        listClient = FXCollections.observableList(Arrays.asList(""));
        table.setItems(listClient);
        OShash = new HashMap<>();
    }

    public void onRefeshAction() {
        try {
            // write on the output stream
            Message object = new Message();
            object.command = Message.GET_LIST_USER;
            object.data = "";
            object.toId = "all";
            Client.dos.writeObject(object);
            Message msg = (Message) Client.dis.readObject();
            if(msg.data.equals("error"))
                return;
            System.out.println(listSIModeltoJson(msg.data).size());
            listClient = FXCollections.observableList(listSIModeltoJson(msg.data));
            table.setItems(listClient);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String listSIModeltoJson(List<String> list){
        Type listType = new TypeToken<List<String>>() {}.getType();
        return new Gson().toJson(list,listType);
    }
    public static List<String> listSIModeltoJson(String json){
        Type listType = new TypeToken<List<String>>() {}.getType();
        return new Gson().fromJson(json,listType);
    }

    private static  BufferedImage resize(BufferedImage src, int h, int w) {
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
        if(index == null||!index.contains("client")){
            return;
        }
        tabPane.getSelectionModel().selectFirst();
        if(OShash.containsKey(index)){
            OsHWModel os = OShash.get(index);
            OsName.setText(os.getOSPreFix());
            CPUtext.setText(os.getProc());
            Display.setText(os.getDisplay());
        }
        try {
            // write on the output stream
            Message object = new Message();
            object.command = Message.GET_OS_INFO;
            object.data = "";
            object.toId = index;
            Client.dos.writeObject(object);
            Message msg = (Message) Client.dis.readObject();
            if(msg.data.equals("error")){
                OsName.setText("");
                CPUtext.setText("");
                Display.setText("");
                return;
            }

            OsHWModel os = new Gson().fromJson(msg.data,OsHWModel.class);
            OsName.setText(os.getOSPreFix());
            CPUtext.setText(os.getProc());
            Display.setText(os.getDisplay());
            OShash.put(index,os);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void logOutUser() {
        String index = table.getSelectionModel().getSelectedItem();
        if(index == null||!index.contains("client")){
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
//        String index = table.getSelectionModel().getSelectedItem();
//        if(index == null||!index.contains("client")){
//            return;
//        }
//        Message object = new Message();
//        object.command = Message.SHUT_DOWN;
//        object.data = "";
//        object.toId = index;
//        try {
//            Client.dos.writeObject(object);
//        } catch (IOException e) {
//            System.err.println("Lỗi ghê á LogOutUser");
//        }
    }

    public void getClipBoardAction() {
        String index = table.getSelectionModel().getSelectedItem();
        if(index == null||!index.contains("client")){
            return;
        }
        Message object = new Message();
        object.command = Message.GET_CLIPBOARD;
        object.data = "";
        object.toId = index;
        try {
            Client.dos.writeObject(object);
            Message msg = (Message) Client.dis.readObject();
            if(msg.data.equals("error")){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("");
                alert.setContentText("Người dùng đã bị ngắt kết nối");
                alert.setTitle("Thông tin trong ClopBoard");
                alert.showAndWait();
                return;
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("");
            alert.setContentText(msg.data);
            alert.setTitle("Thông tin trong ClopBoard");
            alert.showAndWait();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Lỗi ghê á getClipBoard");
        }
    }

    public void getNewProcess() {
        String index = table.getSelectionModel().getSelectedItem();
        if(index == null||!index.contains("client")){
            return;
        }
        Message object = new Message();
        object.command = Message.GET_LIST_PROCESS;
        object.data = "";
        object.toId = index;
        try {
            Client.dos.writeObject(object);
            Message msg = (Message) Client.dis.readObject();
            if(msg.data.equals("error"))
                return;
            listProcess = FXCollections.observableList(getProcessfromJson(msg.data));
            Processtable.setItems(listProcess);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Lỗi ghê á getProcess");
        }
    }

    public void killProcess() {
        String index = table.getSelectionModel().getSelectedItem();
        if(index == null||!index.contains("client")){
            return;
        }
        ProcessModel process = Processtable.getSelectionModel().getSelectedItem();
        if(process==null){
            return;
        }
        Message object = new Message();
        object.command = Message.KILL_PROCESS;
        object.data = ""+process.getPID();
        object.toId = index;
        try {
            Client.dos.writeObject(object);
            getNewProcess();
        } catch (IOException e) {
            System.err.println("Lỗi ghê á getProcess");
        }
    }

    public void onSelectionChanged() {
        if(ProcessPane.isSelected()){
            String index = table.getSelectionModel().getSelectedItem();
            if(index == null||!index.contains("client")){
                return;
            }
            Message object = new Message();
            object.command = Message.GET_LIST_PROCESS;
            object.data = "";
            object.toId = index;
            try {
                Client.dos.writeObject(object);
                Message msg = (Message) Client.dis.readObject();
                System.out.println(msg.command);
                if(msg.data.equals("error"))
                    return;
                listProcess = FXCollections.observableList(getProcessfromJson(msg.data));
                Processtable.setItems(listProcess);
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Lỗi ghê á getProcess");
            }
        }
    }
    public static List<ProcessModel> getProcessfromJson(String json){
            Type listType = new TypeToken<List<ProcessModel>>() {}.getType();
            return new Gson().fromJson(json,listType);
    }
}