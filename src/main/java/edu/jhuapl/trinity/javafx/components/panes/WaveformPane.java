package edu.jhuapl.trinity.javafx.components.panes;

/*-
 * #%L
 * trinity
 * %%
 * Copyright (C) 2021 - 2023 The Johns Hopkins University Applied Physics Laboratory LLC
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import edu.jhuapl.trinity.data.audio.flac.FlacConverter;
import edu.jhuapl.trinity.data.audio.flac.FlacDecoder;
import edu.jhuapl.trinity.javafx.events.AudioEvent;
import edu.jhuapl.trinity.utils.ResourceUtils;
import edu.jhuapl.trinity.utils.fun.GlitchUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * @author Sean Phillips
 */
public class WaveformPane extends LitPathPane {
    public static Color transDarkCyan = Color.DARKCYAN.deriveColor(1, 1, 1, 0.1);
    Background transBackground = new Background(new BackgroundFill(
        transDarkCyan,CornerRadii.EMPTY, Insets.EMPTY));
    static double iconFitWidth = 30.0;
    BorderPane bp;
    WaveformCanvasOverlayPane waveformCanvas;
    ColorPicker waveColorPicker;
    ColorPicker backgroundColorPicker;
            
    public static int PANE_WIDTH = 700;

    private static BorderPane createContent() {
        BorderPane bpOilSpill = new BorderPane();

        // SET THE PLAY, RESET, PAUSE, AND BACKWARDS PLAY BUTTON
        ImageView playForwardImageView = ResourceUtils.loadIcon("forward", iconFitWidth);
        ImageView pauseImageView = ResourceUtils.loadIcon("pause", iconFitWidth);
        ImageView resetImageView = ResourceUtils.loadIcon("reset", iconFitWidth);
        ImageView fftImageView = ResourceUtils.loadIcon("hypersurface", iconFitWidth);
                
        HBox playImageHBox = new HBox(playForwardImageView);
        playImageHBox.setAlignment(Pos.CENTER);
        HBox pauseImageHBox = new HBox( pauseImageView);
        pauseImageHBox.setAlignment(Pos.CENTER);
        HBox resetImageHBox = new HBox( resetImageView);
        resetImageHBox.setAlignment(Pos.CENTER);
        HBox fftImageHBox = new HBox(fftImageView);
        fftImageHBox.setAlignment(Pos.CENTER);
        
        //create effects
        DropShadow pauseIndicatorGlow = new DropShadow();
        pauseIndicatorGlow.setBlurType(BlurType.GAUSSIAN);
        pauseIndicatorGlow.setOffsetX(0f);
        pauseIndicatorGlow.setOffsetY(0f);
        pauseIndicatorGlow.setRadius(10);
        pauseIndicatorGlow.setSpread(0.5);
        pauseIndicatorGlow.colorProperty().set(Color.ALICEBLUE);

        DropShadow resetIndicatorGlow = new DropShadow();
        resetIndicatorGlow.setBlurType(BlurType.GAUSSIAN);
        resetIndicatorGlow.setOffsetX(0f);
        resetIndicatorGlow.setOffsetY(0f);
        resetIndicatorGlow.setRadius(10);
        resetIndicatorGlow.setSpread(0.5);
        resetIndicatorGlow.colorProperty().set(Color.CYAN.deriveColor(1, 1, 1, 0.5));

        DropShadow playIndicatorGlow = new DropShadow();
        playIndicatorGlow.setBlurType(BlurType.GAUSSIAN);
        playIndicatorGlow.setOffsetX(0f);
        playIndicatorGlow.setOffsetY(0f);
        playIndicatorGlow.setRadius(10);
        playIndicatorGlow.setSpread(0.5);
        playIndicatorGlow.colorProperty().set(Color.CYAN.deriveColor(1, 1, 1, 0.5));

        DropShadow fftIndicatorGlow = new DropShadow();
        fftIndicatorGlow.setBlurType(BlurType.GAUSSIAN);
        fftIndicatorGlow.setOffsetX(0f);
        fftIndicatorGlow.setOffsetY(0f);
        fftIndicatorGlow.setRadius(10);
        fftIndicatorGlow.setSpread(0.5);
        fftIndicatorGlow.colorProperty().set(Color.CYAN.deriveColor(1, 1, 1, 0.5));
        
        // SET THE INDICATOR GLOW EFFECT
        playImageHBox.setEffect(playIndicatorGlow);
        pauseImageHBox.setEffect(pauseIndicatorGlow);
        resetImageHBox.setEffect(resetIndicatorGlow);
        fftImageHBox.setEffect(resetIndicatorGlow);
        
//        // SET EFFECT ON THE RESET BUTTON
//        centerResetImageHBox.setOnMouseEntered(e -> {
//            Platform.runLater(() -> {
//                resetIndicatorGlow.setColor(Color.ALICEBLUE);
//            });
//        });
//        centerResetImageHBox.setOnMouseExited(e -> {
//            Platform.runLater(() -> {
//                resetIndicatorGlow.setColor(Color.CYAN.deriveColor(1, 1, 1, 0.5));
//            });
//        });     
//        // SET EFFECT ON THE PAUSE BUTTON
//        centerPauseImageHBox.setOnMouseClicked(e -> {
//            Platform.runLater(() -> {
//                playBackOn = false;
//                playOn = false;
//                pauseOn = true;
//                playBackIndicatorGlow.setColor((Color.CYAN.deriveColor(1, 1, 1, 0.5)));
//                playIndicatorGlow.setColor((Color.CYAN.deriveColor(1, 1, 1, 0.5)));
//                pauseIndicatorGlow.setColor(Color.ALICEBLUE);
//                centerPauseImageHBox.getScene().getRoot().fireEvent(
//                    new TimelineEvent(TimelineEvent.TIMELINE_PAUSE));
//            });
//        });
//        centerPauseImageHBox.setOnMouseEntered(e -> {
//            Platform.runLater(() -> {
//                if (!pauseOn) {
//                    pauseIndicatorGlow.setColor(Color.ALICEBLUE);
//                }
//            });
//        });
//        centerPauseImageHBox.setOnMouseExited(e -> {
//            Platform.runLater(() -> {
//                if (!pauseOn) {
//                    pauseIndicatorGlow.setColor(Color.CYAN.deriveColor(1, 1, 1, 0.5));
//                }
//            });
//        });        
//        centerForwardImageHBox.setOnMouseEntered(e -> {
//            Platform.runLater(() -> {
//                if (!playOn) {
//                    playIndicatorGlow.setColor(Color.ALICEBLUE);
//                }
//            });
//        });
//        centerForwardImageHBox.setOnMouseExited(e -> {
//            Platform.runLater(() -> {
//                if (!playOn) {
//                    playIndicatorGlow.setColor(Color.CYAN.deriveColor(1, 1, 1, 0.5));
//                }
//            });
//        });
        
        ColorPicker waveColorPicker = new ColorPicker(Color.ORANGERED);
        ColorPicker backgroundColorPicker = new ColorPicker(transDarkCyan);
        
        HBox toolbarHBox = new HBox(25, 
            resetImageHBox, pauseImageHBox, playImageHBox,
            fftImageHBox, waveColorPicker, backgroundColorPicker
        );
        toolbarHBox.setAlignment(Pos.CENTER_LEFT);
        toolbarHBox.setMinHeight(iconFitWidth * 1.5);
        WaveformCanvasOverlayPane waveformCanvas = new WaveformCanvasOverlayPane(true, true);
       

        bpOilSpill.setTop(toolbarHBox);
        bpOilSpill.setCenter(waveformCanvas);

        return bpOilSpill;
    }

    public WaveformPane(Scene scene, Pane parent) {
        super(scene, parent, PANE_WIDTH, 350, createContent(),
            "Waveform Display", "", 300.0, 400.0);
        this.scene = scene;
        bp = (BorderPane) this.contentPane;

        waveformCanvas = (WaveformCanvasOverlayPane) bp.getCenter();
        this.scene.addEventHandler(AudioEvent.NEW_AUDIO_FILE, e -> {
            if(null != e.object) {
                File audioFile = (File)e.object;
                setWaveform(audioFile);
            }
        });
        HBox topHBox = (HBox)bp.getTop();
        HBox resetHBox = (HBox)topHBox.getChildren().get(0);
        HBox pauseHBox = (HBox)topHBox.getChildren().get(1);
        HBox playHBox = (HBox)topHBox.getChildren().get(2);
        HBox fftHBox = (HBox)topHBox.getChildren().get(3);
        waveColorPicker = (ColorPicker) topHBox.getChildren().get(4);
        backgroundColorPicker = (ColorPicker) topHBox.getChildren().get(5);
        
        resetHBox.setOnMouseClicked(eh -> waveformCanvas.resetMedia());
        pauseHBox.setOnMouseClicked(eh -> waveformCanvas.pauseMedia());
        playHBox.setOnMouseClicked(eh -> waveformCanvas.playMedia());
        fftHBox.setOnMouseClicked(eh -> waveformCanvas.fftOnMedia());
        
        waveColorPicker.valueProperty().addListener(cl -> {
            waveformCanvas.setForegroundColor(waveColorPicker.getValue());
            waveformCanvas.paintWaveform();
        });
        backgroundColorPicker.valueProperty().addListener(cl -> {
            waveformCanvas.setBackgroundColor(backgroundColorPicker.getValue());
            waveformCanvas.paintWaveform();
        });
        this.addEventHandler(MouseEvent.MOUSE_CLICKED, e-> {
            GlitchUtils.gitchNode(this, 
            Duration.millis(250), Duration.ZERO, 2);
        });
        
    }

    public void setWaveform(File audioFile) {
        System.out.println("@TODO SMP... process audio file");
        int indexOfPeriod = audioFile.getPath().lastIndexOf(".");
        if(indexOfPeriod > 0) {
            String ext = audioFile.getPath().substring(indexOfPeriod+1);
            if(ext.toLowerCase().contentEquals("wav") ||
                ext.toLowerCase().contentEquals("flac")) {
                
                if(ext.toLowerCase().contentEquals("flac")) {
//                    try {
//                        //convert to wav
//                        FlacDecoder dec = new FlacDecoder(audioFile);
//			// Handle metadata header blocks
//			while (dec.readAndHandleMetadataBlock() != null);
//			if (dec.streamInfo.sampleDepth % 8 != 0) {
//                            System.out.println("Only whole-byte sample depth supported");
//                            return;
//                        }
//			// Decode every block
//			int[][] samples = new int[dec.streamInfo.numChannels]
//                                [(int)dec.streamInfo.numSamples];
//			for (int off = 0; ;) {
//                            int len = dec.readAudioBlock(samples, off);
//                            if (len == 0)
//                                break;
//                            off += len;
//			}
//                        FlacConverter converter = new FlacConverter(dec.streamInfo);
//                        float [] monoWavBytes = converter.convertToWavBytes(samples);
//                        System.out.println("Converted Flac to mono Wav format.");
//                    } catch (IOException ex) {
//                        Logger.getLogger(WaveformPane.class.getName()).log(Level.SEVERE, null, ex);
//                    }
                    //create temporary wav file
                                        
                    try {
                        Path tempFile = Files.createTempFile("flacToWav-"+audioFile.getName(), ".wav");
                        FlacConverter.decodeFlacToWav(audioFile, tempFile.toFile());
                        waveformCanvas.startVisualization(tempFile.toFile());
                    } catch (IOException ex) {
                        Logger.getLogger(WaveformPane.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    waveformCanvas.startVisualization(audioFile);
                }
            } else {
                System.out.println("Could not load this file type.\nTypes supported: .wav | .flac");
            }
        } else {
            System.out.println("Could not determine audio file type.\nTypes supported: .wav | .flac");
        }
    }

 
}
