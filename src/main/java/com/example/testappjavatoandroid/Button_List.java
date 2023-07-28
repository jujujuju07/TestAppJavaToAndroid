package com.example.testappjavatoandroid;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.net.URISyntaxException;

public class Button_List {

    public Label label;
    public TextField textField;
    public VBox vBox;
    public HBox hBox;
    public Button explorateur;
    public ImageView explorateur_image;

    private FileNameExtensionFilter filter;




    public void setinfo(VBox vBox,String label, String id_HBox, String valueTextField, String explorateur){
        this.label.setText(" " + label + " ");
        this.vBox = vBox;
        this.hBox.setId(id_HBox);
        this.textField.setText(valueTextField);
        switch (explorateur){
            case ".wav" :
                explorateur_wav();
                break;
            case ".exe" :
                explorateur_exe();
                break;

            default:
                this.explorateur_image.setScaleY(0);
                this.explorateur.setMinWidth(0);
                this.explorateur.setPrefWidth(0);
                break;
        }
    }

    public void explorateur_exe(){
        filter = new FileNameExtensionFilter("exe","exe");
    }

    public void explorateur_wav(){
        filter = new FileNameExtensionFilter("wav","wav");
    }


    public void supprimer(ActionEvent actionEvent) {
        vBox.getChildren().remove(hBox);
    }

    public void buttonUP(ActionEvent actionEvent) {
        int postion = vBox.getChildren().indexOf(hBox);
        if (postion > 0){
            vBox.getChildren().remove(hBox);
            vBox.getChildren().add(postion-1,hBox);
        }
    }

    public void butttonDOWN(ActionEvent actionEvent) {
        int postion = vBox.getChildren().indexOf(hBox);
        if (postion < vBox.getChildren().size()-1){
            vBox.getChildren().remove(hBox);
            vBox.getChildren().add(postion+1,hBox);
        }
    }

    public void explorateur(ActionEvent actionEvent) {
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
        fileChooser.setFileFilter(filter);

        // Afficher la boîte de dialogue de l'explorateur de fichiers
        int returnValue = fileChooser.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            // Récupérer le fichier sélectionné
            java.io.File selectedFile = fileChooser.getSelectedFile();

            // Récupérer le chemin absolu du fichier
            String filePath = selectedFile.getAbsolutePath();
            textField.setText(filePath);
        }

    }
}
