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

import edu.jhuapl.trinity.utils.ResourceUtils;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;

/**
 * @author Sean Phillips
 */
public class VideoPane extends LitPathPane {
    BorderPane bp;
    public static int PANE_WIDTH = 1000;
    public static double NODE_WIDTH = PANE_WIDTH - 50;
    public static double NODE_HEIGHT = NODE_WIDTH / 2.0;
    Media media;
    MediaView mediaView;
    public MediaPlayer mediaPlayer;
    boolean auto = false;
    ChangeListener endOfMediaListener;
    
    private static BorderPane createContent() {
        BorderPane bpOilSpill = new BorderPane();
        MediaView mediaView = new MediaView();
        bpOilSpill.setCenter(mediaView);
       return bpOilSpill;
    }

    public VideoPane(Scene scene, Pane parent) {
        super(scene, parent, PANE_WIDTH, 750, createContent(),
            "Video Player", "", 300.0, 400.0);
        this.scene = scene;
        bp = (BorderPane) this.contentPane;
        mediaView = (MediaView) bp.getCenter();
        mediaView.setPreserveRatio(true);
        mediaView.setFitWidth(NODE_WIDTH);
        mediaView.setOnMouseClicked(e -> {
            shutdown();
        });
        
        ImageView tv = ResourceUtils.loadIcon("retrowave-tv-2", 72);
        ImageView forward = ResourceUtils.loadIcon("forward", 32);
        ImageView refresh = ResourceUtils.loadIcon("refresh", 32);
        VBox forwardVBox = new VBox(1, forward, new Label("NEXT"));
        forwardVBox.setAlignment(Pos.BOTTOM_CENTER);

        VBox refreshVBox = new VBox(1, refresh, new Label("AUTO"));
        refreshVBox.setAlignment(Pos.BOTTOM_CENTER);
        
        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setOffsetX(4);
        innerShadow.setOffsetY(4);
        innerShadow.setColor(Color.CYAN);
//        Glow glow = new Glow(0.95);
//        glow.setInput(innerShadow);
        forwardVBox.setOnMouseEntered(e -> {
            forward.setEffect(innerShadow);
        });
        forwardVBox.setOnMouseClicked(e -> {
            mediaPlayer.pause();
            setVideo();
        });
        forwardVBox.setOnMouseExited(e -> {
            forward.setEffect(null);
        });
        
        refreshVBox.setOnMouseEntered(e -> {
            refresh.setEffect(innerShadow);
        });
        refreshVBox.setOnMouseClicked(e -> {
            toggleAuto();
            if(auto)
                refreshVBox.setEffect(innerShadow);
            else
                refreshVBox.setEffect(null);
        });
        refreshVBox.setOnMouseExited(e -> {
            refresh.setEffect(null);
        });        
        
        HBox hbox = new HBox(50,  tv, refreshVBox, forwardVBox);
        hbox.setAlignment(Pos.CENTER_RIGHT);
        mainTitleArea.getChildren().add(hbox);
        hbox.prefWidthProperty().bind(mainTitleArea.widthProperty().subtract(10));
    }
    
    @Override
    public void close() {
        super.close();
        mediaPlayer.pause();
    }
    @Override
    public void minimize() {
        super.minimize();
        mediaPlayer.pause();
    }
    
    public void shutdown() {
        close();
        parent.getChildren().remove(this);        
    }
    public void toggleAuto() {
        auto = !auto;
    }
    public void setVideo() {
        try {
            media = ResourceUtils.loadRandomMediaMp4();
            if(null != media) {
                mediaPlayer = new MediaPlayer(media);
                mediaView.setMediaPlayer(mediaPlayer);
                mediaPlayer.setOnEndOfMedia(() -> {
                    if(auto) {
                        setVideo();
                    }                        
                });
                mediaPlayer.play();
            } else {
                shutdown();
            }
        } catch (URISyntaxException | IOException ex) {
            Logger.getLogger(VideoPane.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
