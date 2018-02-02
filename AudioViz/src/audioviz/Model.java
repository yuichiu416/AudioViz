/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package audioviz;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.converter.NumberStringConverter;

/**
 * FXML Controller class
 *
 * @author dale
 * Music: http://www.bensound.com/royalty-free-music
 * http://www.audiocheck.net/testtones_sinesweep20-20k.php
 * http://stackoverflow.com/questions/11994366/how-to-reference-primarystage
 */
public class Model {
    public static ObservableList songs = FXCollections.observableArrayList();
    public static ObservableList lyrics = FXCollections.observableArrayList();
    public Media media;
    public MediaPlayer mediaPlayer;
    public Integer numBands = 40;
    public final Double updateInterval = 0.05;
    public ArrayList<Visualizer> visualizers;
    public Visualizer currentVisualizer;
    public final Integer[] bandsList = {1, 2, 4, 8, 16, 20, 40, 60, 100, 120, 140};
    public double millis;
    public double seconds;
    public double minutes;
    public double hours;
    public final NumberStringConverter converter1 = new NumberStringConverter("0");    
    public final NumberStringConverter converter2 = new NumberStringConverter("00");
    public int backgroundXCoordinate = 0;
    public double opacity = 1;
    public double volume;
    public int songPlaying = 0;
    public int playListSize = 0;
    public List<File> playList;
    public List<File> lyricList;
    public AnchorPane vizPane;
    public MediaView mediaView;
    public Text filePathText;
    public Text lengthText;
    public Text currentText;
    public Text bandsText;
    public Text visualizerNameText;
    public Text errorText;
    public Menu visualizersMenu;
    public Menu bandsMenu;
    public Slider timeSlider;
    public Slider volumeSlider;
    public Text volumeText;
    public ListView<String> playListView;
    public File lyric;
    public List<String> lyricStringList;
    public int lyricLine = 0;
    public long timeMillis;
    public boolean lyricRead = false;
    public boolean lyricPrinted = false;
    public Text lyricText;


    public void setReference(AnchorPane vizPane, 
                            MediaView mediaView, 
                            Text filePathText, 
                            Text lengthText, 
                            Text currentText, 
                            Text bandsText, 
                            Text visualizerNameText, 
                            Text errorText, 
                            Menu visualizersMenu, 
                            Menu bandsMenu, 
                            Slider timeSlider, 
                            Slider volumeSlider, 
                            Text volumeText, 
                            ListView<String> playListView,
                            ArrayList<Visualizer> visualizers,
                            Visualizer currentVisualizer,
                            Text lyricText
                            ){
        this.vizPane = vizPane;
        this.mediaView = mediaView;
        this.filePathText = filePathText;
        this.lengthText = lengthText;
        this.currentText = currentText;
        this.bandsText = bandsText;
        this.visualizerNameText = visualizerNameText;
        this.errorText = errorText;
        this.visualizersMenu = visualizersMenu;
        this.bandsMenu = bandsMenu;
        this.timeSlider = timeSlider;
        this.volumeSlider = volumeSlider;
        this.volumeText = volumeText;
        this.playListView = playListView;
        this.visualizers = visualizers;
        this.currentVisualizer = currentVisualizer;
        this.lyricText = lyricText;
    }    
    public void initialize(URL url, ResourceBundle rb) {
        bandsText.setText(Integer.toString(numBands));
        
        visualizers = new ArrayList<>();
        visualizers.add(new EllipseVisualizer1());
        visualizers.add(new EllipseVisualizer2());
        visualizers.add(new EllipseVisualizer3());
        visualizers.add(new Rkry8EllipseVisualizer());
        
        for (Visualizer visualizer : visualizers) {
            MenuItem menuItem = new MenuItem(visualizer.getName());
            menuItem.setUserData(visualizer);
            menuItem.setOnAction((ActionEvent event) -> {
                selectVisualizer(event);
            });
            visualizersMenu.getItems().add(menuItem);
        }
        currentVisualizer = visualizers.get(0);
        visualizerNameText.setText(currentVisualizer.getName());
        
        for (Integer bands : bandsList) {
            MenuItem menuItem = new MenuItem(Integer.toString(bands));
            menuItem.setUserData(bands);
            menuItem.setOnAction((ActionEvent event) -> {
                selectBands(event);
            });
            bandsMenu.getItems().add(menuItem);
        }
    }
     public void selectVisualizer(ActionEvent event) {
        MenuItem menuItem = (MenuItem)event.getSource();
        Visualizer visualizer = (Visualizer)menuItem.getUserData();
        changeVisualizer(visualizer);
    }
    
    public void selectBands(ActionEvent event) {
        MenuItem menuItem = (MenuItem)event.getSource();
        numBands = (Integer)menuItem.getUserData();
        if (currentVisualizer != null) {
            currentVisualizer.start(numBands, vizPane);
        }
        if (mediaPlayer != null) {
            mediaPlayer.setAudioSpectrumNumBands(numBands);
        }
        bandsText.setText(Integer.toString(numBands));
    }
    
    public void changeVisualizer(Visualizer visualizer) {
        if (currentVisualizer != null) {
            currentVisualizer.end();
        }
        currentVisualizer = visualizer;
        currentVisualizer.start(numBands, vizPane);
        visualizerNameText.setText(currentVisualizer.getName());
    }
    
    public void openMedia(File file) {
        filePathText.setText("");
        errorText.setText("");
        lyricText.setText("");
        if (mediaPlayer != null) {
            mediaPlayer.dispose();
        }
        try {
            media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);
            mediaPlayer.setOnReady(() -> {
                handleReady();
            });
            mediaPlayer.setOnEndOfMedia(() -> {
                handleEndOfMedia();
            });
            mediaPlayer.setAudioSpectrumNumBands(numBands);
            mediaPlayer.setAudioSpectrumInterval(updateInterval);
            mediaPlayer.setAudioSpectrumListener((double timestamp, double duration, float[] magnitudes, float[] phases) -> {
                try {
                    handleUpdate(timestamp, duration, magnitudes, phases);
                } catch (ParseException ex) {
                    Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            mediaPlayer.setAutoPlay(true);
            filePathText.setText(file.getPath());
        } catch (Exception ex) {
            errorText.setText(ex.toString());
        }
    }
    
    public void handleReady() {
        Duration duration = mediaPlayer.getTotalDuration();
        double ms = duration.toMillis();
        millis = ms/10;
        seconds = ms / 1000;
        minutes = seconds / 60;
        hours = minutes / 60;
        millis %= 100;
        seconds %= 60;
        minutes %= 60;
        hours %= 60;
        lengthText.setText(converter2.toString(hours) + ":" + converter2.toString(minutes) + ":" + converter2.toString(seconds) + ":" + converter1.toString(millis));
        Duration ct = mediaPlayer.getCurrentTime();
        currentText.setText(ct.toString());
        currentVisualizer.start(numBands, vizPane);
        timeSlider.setMin(0);
        timeSlider.setMax(duration.toMillis());
        volumeSlider.setValue(50);
        volumeText.setText("50");
        mediaPlayer.setVolume(volumeSlider.getValue());
    }
    
    public void handleEndOfMedia() {
        mediaPlayer.stop();
        mediaPlayer.seek(Duration.ZERO);
        timeSlider.setValue(0);
    }
    
    public void handleUpdate(double timestamp, double duration, float[] magnitudes, float[] phases) throws ParseException {
        Duration ct = mediaPlayer.getCurrentTime();
        double ms = ct.toMillis();
        millis = ms/100;
        seconds = ms / 1000;
        minutes = seconds / 60;
        hours = minutes / 60;
        millis %= 10;
        seconds %= 60;
        minutes %= 60;
        hours %= 60;
        currentText.setText(converter2.toString(hours) + ":" + converter2.toString(minutes) + ":" + converter2.toString(seconds) + ":" + converter1.toString(millis));
        timeSlider.setValue(ms);
        vizPane.setStyle("-fx-background-position: " + backgroundXCoordinate++ + " center");
        currentVisualizer.update(timestamp, duration, magnitudes, phases);
        if(backgroundXCoordinate > 2000){
            backgroundXCoordinate = 0;
        }
        visualizerNameText.setStyle("-fx-opacity: " + opacity + ";");
        opacity -= 0.05;
        if(opacity <= 0){
            opacity = 1;
        }
        if(lyricRead){
            SimpleDateFormat sdf = new SimpleDateFormat("mm:ss.SSS");
            Date date = null;
            try {
                date = sdf.parse(lyricStringList.get(lyricLine).substring(1));
                timeMillis = date.getTime();  
                timeMillis -= 21600000;
               if(timeMillis - timeSlider.getValue() <= 0){  
                   lyricText.setText(lyricStringList.get(lyricLine++).substring(11));
               }
            } catch (ParseException ex) {
            }
        }
    }
    public void handleOpen(Event event) {
        Stage primaryStage = (Stage)vizPane.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new ExtensionFilter("mp3 files", "*.mp3"));
        
        try{ 
            playList = fileChooser.showOpenMultipleDialog(primaryStage);
            if (playList.get(0) != null) {
                openMedia(playList.get(0));
            }
            playListSize = playList.size();
            for(int i = 0; i < playListSize; i++){
                songs.add(playList.get(i).getName());
            }
            playListView.setItems(songs);
        }catch (Exception e){
        }
    }
    public void handleOpenLyrics(Event event) {
        Stage primaryStage = (Stage)vizPane.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new ExtensionFilter("txt files", "*.txt"));
        try {
            lyric = fileChooser.showOpenDialog(primaryStage);
            lyricStringList = Files.readAllLines(lyric.toPath(), StandardCharsets.UTF_8);
            lyricRead = true;
        } catch (IOException ex) {
            System.out.println("failed to open lyric file");
        }
    }
    public void handlePlay(ActionEvent event) {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }
    public void handlePause(ActionEvent event) {
        if (mediaPlayer != null) {
           mediaPlayer.pause(); 
        }
    }
    public void handleStop(ActionEvent event) {
        if (mediaPlayer != null) {
           mediaPlayer.stop(); 
           lyricRead = false;
           lyricText.setText("");
        }
    }
    public void handlePrevious(ActionEvent event){
        mediaPlayer.stop();
        songPlaying--;
        if(songPlaying < 0){
            songPlaying = playListSize - 1;
        }
        openMedia(playList.get(songPlaying));
        mediaPlayer.play();   
        lyricRead = false;
        lyricText.setText("");
        try{
            openMedia(playList.get(songPlaying));
            mediaPlayer.play();   
      }catch (Exception e){
            songPlaying++;
            openMedia(playList.get(songPlaying));
            mediaPlayer.play();  
      }
    } 
    public void handleNext(ActionEvent event){
        mediaPlayer.stop();
        songPlaying++;
        lyricRead = false;
        lyricText.setText("");
      if(songPlaying == playListSize){
          songPlaying = 0;
      }
      try{
            openMedia(playList.get(songPlaying));
            mediaPlayer.play();   
      }catch (Exception e){
            songPlaying--;
            openMedia(playList.get(songPlaying));
            mediaPlayer.play();  
      }
    } 
    public void handleSliderMousePressed(Event event) {
        if (mediaPlayer != null) {
           mediaPlayer.pause(); 
        }  
        lyricText.setText("");
    }
    public void handleSliderMouseReleased(Event event) {
        if (mediaPlayer != null) {
            mediaPlayer.seek(new Duration(timeSlider.getValue()));
            currentVisualizer.start(numBands, vizPane);
            mediaPlayer.play();
            SimpleDateFormat sdf = new SimpleDateFormat("mm:ss.SSS");
            Date date = null;
            lyricLine = 0;
            try {
                while(timeMillis - timeSlider.getValue() <= 0){
                    date = sdf.parse(lyricStringList.get(lyricLine).substring(1));
                    timeMillis = date.getTime();  
                    timeMillis -= 21600000;                        
                    if(timeMillis - timeSlider.getValue() <= 0){  
                        lyricLine++;
                    }
               }
            } catch (ParseException ex) {
            }
            if(lyricLine > 0){                   
                lyricText.setText(lyricStringList.get(--lyricLine).substring(11));
                }
        }  
    }
    public void handleVolumeSliderMousePressed(Event event){
        if(mediaPlayer != null){
            mediaPlayer.setVolume(volumeSlider.getValue() / 100);
            volumeText.setText(converter2.toString(volumeSlider.getValue()));
        }
    }
    public void handleVolumeSliderMouseReleased(Event event) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volumeSlider.getValue() / 100);
            volumeText.setText(converter2.toString(volumeSlider.getValue()));
        }  
    }    
    public void handleSelectMedia(MouseEvent event){
        if(mediaPlayer != null){
            if(event.getButton() == MouseButton.SECONDARY){
                handleDeleteMedia(event);
                return;
            }
            if(event.getClickCount() == 2){
                mediaPlayer.stop(); 
                String message = event.toString();
                String[] messageList;
                int index1, index2;
                index1 = message.indexOf('\"');
                index2 = message.indexOf('3', index1 + 4);
                if(index1 == -1 || index2 == -1){
                    index1 = message.indexOf('\'');
                    index2 = message.indexOf('3', index1 + 4);
                }
                index1++;//index1 was ", rather than the first index of the filename
                index2++;//index2 was p, rather than 3 
                message = message.substring(index1, index2);
                for(int i = 0; i < playListSize; i++){
                    if(playList.get(i).getName().equals(message)){
                        songPlaying = i;
                        break;
                    }
                }
                openMedia(playList.get(songPlaying));
                mediaPlayer.play();   
            }
        }
    }
    public void handleDeleteMedia(MouseEvent event){
        if(mediaPlayer != null){
            String message = event.toString();
            String[] messageList;
            int index1, index2;
            index1 = message.indexOf('\"');
            index2 = message.indexOf('3', index1 + 4);
            if(index1 == -1 || index2 == -1){
                index1 = message.indexOf('\'');
                index2 = message.indexOf('3', index1 + 4);
            }
            index1++;//index1 was ", rather than the first index of the filename
            index2++;//index2 was p, rather than 3 
            message = message.substring(index1, index2);
            int remove = 0;
            for(int i = 0; i < playListSize; i++){
                if(playList.get(i).getName().equals(message)){
                    remove = i;
                    break;
                }
            }
            if(songPlaying == remove){
                mediaPlayer.stop();
                songPlaying--;
                lyricRead = false;
            }
            songs.remove(remove);
            playListSize = playList.size();
            playListView.setItems(songs);
            lyricText.setText("");
        }
    }
}


