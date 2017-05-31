/**
 * Copyright 2009 Jitendra Rana, jsrana@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jsrana.plugins.quicknotes.ui;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

public class ImagePreview
        extends JComponent
        implements PropertyChangeListener {
    ImageIcon thumbnail = null;
    File file = null;

    public ImagePreview( JFileChooser fc ) {
        setPreferredSize( new Dimension( 100, 50 ) );
        fc.addPropertyChangeListener( this );
    }

    public void loadImage() {
        if ( file == null ) {
            thumbnail = null;
            return;
        }

        //Don't use createImageIcon (which is a wrapper for getResource)
        //because the image we're trying to load is probably not one
        //of this program's own resources.
        ImageIcon tmpIcon = new ImageIcon( file.getPath() );
        if ( tmpIcon != null ) {
            if ( tmpIcon.getIconWidth() > 90 ) {
                thumbnail = new ImageIcon( tmpIcon.getImage().getScaledInstance( 90, -1, Image.SCALE_FAST ) );
            }
            else { //no need to miniaturize
                thumbnail = tmpIcon;
            }
        }
    }

    public void propertyChange( PropertyChangeEvent e ) {
        boolean update = false;
        String prop = e.getPropertyName();

        //If the directory changed, don't show an image.
        if ( JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals( prop ) ) {
            file = null;
            update = true;
        }
        else if ( JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals( prop ) ) {
            file = ( File ) e.getNewValue();
            update = true;
        }

        //Update the preview accordingly.
        if ( update ) {
            thumbnail = null;
            if ( isShowing() ) {
                loadImage();
                repaint();
            }
        }
    }

    protected void paintComponent( Graphics g ) {
        if ( thumbnail == null ) {
            loadImage();
        }
        if ( thumbnail != null ) {
            int x = getWidth() / 2 - thumbnail.getIconWidth() / 2;
            int y = getHeight() / 2 - thumbnail.getIconHeight() / 2;

            if ( y < 0 ) {
                y = 0;
            }

            if ( x < 5 ) {
                x = 5;
            }
            thumbnail.paintIcon( this, g, x, y );
        }
    }
}