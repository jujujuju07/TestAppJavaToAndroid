package com.example.testappjavatoandroid;

import com.example.testappjavatoandroid.model.button.ButtonList;
import com.example.testappjavatoandroid.model.methode.model.Donner;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class Modifier_Button implements Initializable {
    public Button volume;
    public Button volumePlus;
    public Button volumeMoins;
    public boolean aBoolean1 = true;
    public boolean aBoolean2 = true;
    public TextField textFieldText;
    public TextField textFieldImage;
    public Button buttonSave;
    public Label labelInfo;
    public VBox vBoxM;
    public Button execute;
    public Button lancerAPP;
    public Button lancerSon;
    public ImageView buttonVolume;
    public ImageView buttonExecute;
    private Donner donner;
    private List<ButtonList> buttonLists;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Image image = new Image("image/fleche_menu_déroulante.png");
        buttonVolume.setImage(image);
        buttonExecute.setImage(image);
    }

    public void volume(ActionEvent actionEvent) {
        if (aBoolean1){
            volumePlus.setPrefHeight(0);
            volumeMoins.setPrefHeight(0);
            buttonVolume.setRotate(180);
            aBoolean1 = false;
        }else {
            volumePlus.setPrefHeight(30);
            volumeMoins.setPrefHeight(30);
            buttonVolume.setRotate(0);
            aBoolean1 = true;
        }

    }

    public void execute(ActionEvent actionEvent) {
        if (aBoolean2){
            lancerAPP.setPrefHeight(0);
            lancerSon.setPrefHeight(0);
            buttonExecute.setRotate(180);
            aBoolean2 = false;
        }else {
            lancerAPP.setPrefHeight(30);
            lancerSon.setPrefHeight(30);
            buttonExecute.setRotate(0);
            aBoolean2 = true;
        }
    }

    public void donner(Donner donner, List<ButtonList> buttonLists){
        this.donner = donner;
        this.buttonLists = buttonLists;
        mettreDonner();
    }

    private void mettreDonner(){
        textFieldText.setText(donner.getText());
        textFieldImage.setText(donner.getImage());

        for (int i = 0; i < buttonLists.size(); i++) {
            switch (buttonLists.get(i).getButtonType()){
                case "volumePlus":
                    buttonList("volume +","VolumePlus",buttonLists.get(i).getButtonDonner(),"");
                    break;
                case "VolumeMoins":
                    buttonList("volume -","VolumeMois",buttonLists.get(i).getButtonDonner(),"");
                    break;
                case "lancerSon":
                    buttonList("lancer Son","lancerSon",buttonLists.get(i).getButtonDonner(),".wav");
                    break;
                case "lancerAPP":
                    buttonList("lancer application","lancerAPP",buttonLists.get(i).getButtonDonner(),".exe");
                    break;
                default:
            }
        }
    }

    public void buttonSave(ActionEvent actionEvent) {
        save();
    }

    public void save(){
        donner.setText(textFieldText.getText());
        donner.setImage(textFieldImage.getText());
        if (Objects.equals(donner.getText(), textFieldText.getText()) || Objects.equals(donner.getImage(), textFieldImage.getText())){
            LocalTime localTime = LocalTime.now();
            labelInfo.setText("save " + localTime.getHour() + ":" + localTime.getMinute() + ":" + localTime.getSecond());
        }
        buttonLists.clear();
        System.out.println(vBoxM.getChildren().size());
        for (int i = 0; i < vBoxM.getChildren().size(); i++) {
            HBox hBox = (HBox) vBoxM.getChildren().get(i);
            TextField textField = (TextField) hBox.getChildren().get(1);

            ButtonList buttonList = new ButtonList();
            buttonList.setButtonType(hBox.getId());
            buttonList.setButtonDonner(textField.getText());

            buttonLists.add(buttonList);
        }
    }

    public void button_exporateur_fichier_image(ActionEvent actionEvent) {
        String s = Explorateur();
        System.out.println(s);
        if (s != ""){
            textFieldImage.setText(s);
        }
    }

    private String Explorateur(){
        String path = null;
        try {
            path = Modifier_Button.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        String [] s = path.split("/");
        path = "";
        for (int i = 1; i < s.length-1; i++) {
            path += s[i]+"/";
        }
        System.out.println(path);
        File file = new File(path);
        JFileChooser fileChooser = new JFileChooser(file);

        // Afficher la boîte de dialogue de l'explorateur de fichiers
        int returnValue = fileChooser.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            // Récupérer le fichier sélectionné
            java.io.File selectedFile = fileChooser.getSelectedFile();

            // Récupérer le chemin absolu du fichier
            String filePath = selectedFile.getAbsolutePath();
            String s1 = filePath.substring(path.length());
            s1 = s1.replace("\\","/");
            System.out.println(filePath +" " + s1);
            return s1;
        } else {
            return "";
        }

    }

    private void buttonList(String label, String id_HBox, String valueTextField, String explorateur_extention){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("buttonList.fxml"));
            HBox hBox1 = fxmlLoader.load();
            Button_List buttonList = fxmlLoader.getController();
            buttonList.setinfo(vBoxM,label,id_HBox,valueTextField,explorateur_extention);
            vBoxM.getChildren().add(hBox1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void volumePlus(ActionEvent actionEvent) {
        buttonList("volume +","VolumePlus","","");
    }

    public void volumeMoins(ActionEvent actionEvent) {
        buttonList("volume -","VolumeMois","","");
    }

    public void lancerAPP(ActionEvent actionEvent) {
        buttonList("lancer application","lancerAPP","",".exe");
    }

    public void lancerSon(ActionEvent actionEvent) {
        buttonList("lancer Son","lancerSon","",".wav");
    }
}