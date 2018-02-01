/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package audioviz;

import audioviz.Model;
import audioviz.Visualizer;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.util.converter.NumberStringConverter;

/**
 * FXML Controller class
 *
 * @author yui
 */
public class PlayerController implements Initializable {
    @FXML
    private AnchorPane vizPane;
    @FXML
    private MediaView mediaView;
    @FXML
    private Text filePathText;
    @FXML
    private Text lengthText;
    @FXML
    private Text currentText;
    @FXML
    private Text bandsText;
    @FXML
    private Text visualizerNameText;
    @FXML
    private Text errorText;
    @FXML
    private Menu visualizersMenu;
    @FXML
    private Menu bandsMenu;
    @FXML
    private Slider timeSlider;
    @FXML
    private Slider volumeSlider;
    @FXML
    private Text volumeText;
    @FXML
    private ListView<String> playListView;
    @FXML
    private Text lyricText;
    
    
    public ArrayList<Visualizer> visualizers;
    public Visualizer currentVisualizer;
   
    Model model = new Model();
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        model.setReference(vizPane,
                mediaView,
                filePathText,
                lengthText,
                currentText,
                bandsText,
                visualizerNameText,
                errorText,
                visualizersMenu,
                bandsMenu,
                timeSlider,
                volumeSlider,
                volumeText,
                playListView,
                visualizers,
                currentVisualizer,
                lyricText);
        model.initialize(url, rb);
    }    

    @FXML
    private void handleOpen(ActionEvent event) {
        model.handleOpen(event);
    }
    @FXML
    private void handleOpenLyrics(ActionEvent event) {
        model.handleOpenLyrics(event);
    }

    @FXML
    private void handlePlay(ActionEvent event) {
        model.handlePlay(event);
    }

    @FXML
    private void handlePause(ActionEvent event) {
        model.handlePause(event);
    }

    @FXML
    private void handleStop(ActionEvent event) {
        model.handleStop(event);
    }

    @FXML
    private void handleSliderMouseReleased(MouseEvent event){
        model.handleSliderMouseReleased(event);
    }

    @FXML
    private void handleSliderMousePressed(MouseEvent event) {
        model.handleSliderMousePressed(event);
    }

    @FXML
    private void handlePrevious(ActionEvent event) {
        model.handlePrevious(event);
    }

    @FXML
    private void handleNext(ActionEvent event) {
        model.handleNext(event);
    }

    @FXML
    private void handleVolumeSliderMousePressed(MouseEvent event) {
        model.handleVolumeSliderMousePressed(event);
    }
    @FXML
    private void handleVolumeSliderMouseReleased(Event event){
        model.handleVolumeSliderMouseReleased(event);
    }
    @FXML
    private void handleSelectMedia(MouseEvent event){
        model.handleSelectMedia(event);
    }
}
