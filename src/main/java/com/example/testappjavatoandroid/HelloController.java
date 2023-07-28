package com.example.testappjavatoandroid;

import com.example.testappjavatoandroid.model.button.ButtonList;
import com.example.testappjavatoandroid.model.button.ListButton;
import com.example.testappjavatoandroid.model.execute.Execute;
import com.example.testappjavatoandroid.model.execute.Volume;
import com.example.testappjavatoandroid.model.methode.Case;
import com.example.testappjavatoandroid.model.methode.DonnerApp;
import com.example.testappjavatoandroid.model.methode.model.Donner;
import com.example.testappjavatoandroid.model.methode.model.ArrayListListDonner;
import com.google.gson.Gson;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class HelloController extends Application implements Initializable{
    public Stage stage;

    public ServeurTCP serveurTCP = new ServeurTCP(this);
    public BufferedWriter bw ;
    public BufferedReader br ;
    public Path pathDonner = Paths.get("donner.txt");
    public Path pathExecute = Paths.get("execute.txt");

    private Path pathValeur = Paths.get("valeur.txt");
    public int selectionlistcase;
    public int selectionlistlistcase;
    public int selectionemplacement;
    public ArrayList<ArrayList<Donner>> donnerListList = new ArrayList<>();
    public ArrayList<ArrayList<Case>> listlistcase = new ArrayList<>();
    public List<List<ButtonList>> execute = new ArrayList<>();
    public DonnerApp donnerApp;

    public Label EtatServeur;
    public TextField largeur;
    public TextField longeur;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void lance() throws IOException {
        new Thread(() -> new Serveur_Image().start()).start();
        serveurTCP.serveurMulticast();
        serveurTCP.lancer();
        selectionlistlistcase = -1;
        selectionlistcase = -1;
        String[] ip = String.valueOf(InetAddress.getLocalHost()).split("/");

        if (Files.exists(pathDonner)){
            try {
                Gson gson = new Gson();
                br = Files.newBufferedReader(pathDonner);
                String line = br.readLine();
                if (!Objects.equals(line, "")){
                    try {
                        ArrayListListDonner arrayListListDonner = gson.fromJson(line, ArrayListListDonner.class);
                        donnerListList = arrayListListDonner.getArrayListDonner();
                    }catch (Exception e){
                        creationButton(ip);
                        System.out.println(e.getMessage());
                    }
                }else {
                    creationButton(ip);
                }
                br.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }else {
            creationButton(ip);
        }

        if (Files.exists(pathExecute)){
            try {
                Gson gson = new Gson();
                br = Files.newBufferedReader(pathExecute);
                String line = br.readLine();
                if (!Objects.equals(line,"")){
                    try {
                        ListButton listButton = gson.fromJson(line, ListButton.class);
                        execute = listButton.getButtonButton();
                    }catch (Exception e){
                        creation_Execute();
                        System.out.println(e.getMessage());
                    }
                }else {
                    creation_Execute();
                }
                br.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else {
            creation_Execute();
        }

        for (int i = 0; i < listlistcase.size(); i++) {
            for (int j = 0; j < listlistcase.get(i).size(); j++) {
                if (!Objects.equals(donnerListList.get(i).get(j).getImage(), "")){
                    Image image = new Image("http://"+ ip[1] + ":8080/" + donnerListList.get(i).get(j).getImage());
                    if (image.isError()){
                        image = new Image("image/carre-blanc.jpg");
                    }
                    listlistcase.get(i).get(j).imageView.setImage(image);
                }
                listlistcase.get(i).get(j).label.setText(donnerListList.get(i).get(j).getText());
            }
        }
    }

    private void creation_Execute(){
        for (int i = 0; i < (Integer.parseInt(donnerApp.getLongeur()) * Integer.parseInt(donnerApp.getLargeur())); i++) {
            List<ButtonList> execute = new ArrayList<>();
            this.execute.add(execute);
        }
    }

    private void creationButton(String[] ip) {
        for (int i = 0; i < Integer.parseInt(donnerApp.getLargeur()); i++) {
            donnerListList.add(new ArrayList<Donner>());
            for (int j = 0; j < Integer.parseInt(donnerApp.getLongeur()); j++) {
                Donner donner = new Donner();
                donner.setText("");
                donner.setImage("image/carre-blanc.jpg");
                donnerListList.get(i).add(donner);
            }
        }
    }

    public void fermeture(){
        Gson gson = new Gson();
        ArrayListListDonner donner = new ArrayListListDonner();
        donner.setArrayListDonner(donnerListList);
        ListButton listButton = new ListButton();
        listButton.setButtonButton(execute);
        if (largeur.getText() != ""){
            donnerApp.setLargeur(largeur.getText());
        }
        if (longeur.getText() != ""){
            donnerApp.setLongeur(longeur.getText());
        }
        try {
            bw = Files.newBufferedWriter(pathDonner);
            bw.write(gson.toJson(donner));
            bw.close();
            bw = Files.newBufferedWriter(pathValeur);
            bw.write(gson.toJson(donnerApp));
            bw.close();
            bw = Files.newBufferedWriter(pathExecute);
            bw.write(gson.toJson(listButton));
            bw.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void close(){
        fermeture();
        try {
            serveurTCP.deconexionAll();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.exit(0);
    }

    public void execute(int nombre){
        Volume volume;
        Execute executeLance;

        for (int i = 0; i < execute.get(nombre).size(); i++) {
            ButtonList buttonList = execute.get(nombre).get(i);
            switch (buttonList.getButtonType()){
                case "volumePlus":
                    volume = new Volume();
                    volume.volume_UP(buttonList.getButtonDonner());
                    break;
                case "VolumeMoins":
                    volume = new Volume();
                    volume.volume_DOWN(buttonList.getButtonDonner());
                    break;
                case "lancerSon":
                    executeLance = new Execute();
                    try {
                        executeLance.lancerSon(buttonList.getButtonDonner());
                    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "lancerAPP":
                    executeLance = new Execute();
                    executeLance.lancerAPP(buttonList.getButtonDonner());
                    break;


            }
        }
    }

    public void modif(){
        try {
            start(new Stage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void updateButton() throws UnknownHostException {
        String[] ip = String.valueOf(InetAddress.getLocalHost()).split("/");

        if (!donnerListList.get(selectionlistlistcase).get(selectionlistcase).getImage().equals("")){
            Image image = new Image("http://"+ ip[1] + ":8080/" + donnerListList.get(selectionlistlistcase).get(selectionlistcase).getImage());
            if (image.isError()){
                image = new Image("image/carre-blanc.jpg");
            }
            listlistcase.get(selectionlistlistcase).get(selectionlistcase).imageView.setImage(image);
        }else {
            listlistcase.get(selectionlistlistcase).get(selectionlistcase).imageView.setImage(new Image("image/carre-blanc.jpg"));
        }
        listlistcase.get(selectionlistlistcase).get(selectionlistcase).label.setText(donnerListList.get(selectionlistlistcase).get(selectionlistcase).getText());

        selectionlistlistcase = -1;
        selectionlistcase = -1;
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        stage.setTitle("titre");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("modifie_Button.fxml"));

        Parent root = loader.load();

        ArrayList<List_Button> classArrayList = new ArrayList<>();

        ScrollPane scrollPane = (ScrollPane) root.lookup("#scrollPaneG1");
        Button volumePlus = (Button) scrollPane.getContent().lookup("#volumePlus");
        Button volumeMoins = (Button) scrollPane.getContent().lookup("#volumeMoins");
        Button lancerAPP = (Button) scrollPane.getContent().lookup("#lancerAPP");
        Button lancerSon = (Button) scrollPane.getContent().lookup("#lancerSon");



        VBox vBoxM = (VBox) root.lookup("#vBoxM");

        volumePlus.setOnAction(event -> {
            List_Button listButton = new List_Button();
            HBox hBox = listButton.buttonVolumePlus(vBoxM,"");
            classArrayList.add(listButton);
            vBoxM.getChildren().add(hBox);
        });
        volumeMoins.setOnAction(event -> {
            List_Button listButton = new List_Button();
            HBox hBox = listButton.buttonVolumeMoins(vBoxM,"");
            classArrayList.add(listButton);
            vBoxM.getChildren().add(hBox);
        });
        lancerAPP.setOnAction(event -> {
            List_Button listButton = new List_Button();
            HBox hBox = listButton.buttonLancerAPP(vBoxM,"");
            classArrayList.add(listButton);
            vBoxM.getChildren().add(hBox);
        });
        lancerSon.setOnAction(event -> {
            List_Button listButton = new List_Button();
            HBox hBox = listButton.buttonLancerSon(vBoxM,"");
            classArrayList.add(listButton);
            vBoxM.getChildren().add(hBox);
        });

        Modifier_Button modifier_button = loader.getController();
        modifier_button.donner(donnerListList.get(selectionlistlistcase).get(selectionlistcase), execute.get(selectionemplacement));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();stage.setOnCloseRequest(event -> modifier_button.save());
        updateButton();

    }


    public void mise_a_jour_app(ActionEvent actionEvent) {
        try {
            serveurTCP.mise_a_jour_app();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
