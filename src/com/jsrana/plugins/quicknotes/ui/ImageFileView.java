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

import com.jsrana.plugins.quicknotes.util.Utils;

import javax.swing.*;
import javax.swing.filechooser.FileView;
import java.io.File;

public class ImageFileView
        extends FileView {
    ImageIcon jpgIcon = Utils.createImageIcon( "image.png" );
    ImageIcon gifIcon = Utils.createImageIcon( "image.png" );
    ImageIcon tiffIcon = Utils.createImageIcon( "image.png" );
    ImageIcon pngIcon = Utils.createImageIcon( "image.png" );

    public String getName( File f ) {
        return null; //let the L&F FileView figure this out
    }

    public String getDescription( File f ) {
        return null; //let the L&F FileView figure this out
    }

    public Boolean isTraversable( File f ) {
        return null; //let the L&F FileView figure this out
    }

    public String getTypeDescription( File f ) {
        String extension = Utils.getExtension( f );
        String type = null;

        if ( extension != null ) {
            if ( extension.equals( Utils.jpeg ) ||
                 extension.equals( Utils.jpg ) ) {
                type = "JPEG Image";
            }
            else if ( extension.equals( Utils.gif ) ) {
                type = "GIF Image";
            }
            else if ( extension.equals( Utils.tiff ) ||
                      extension.equals( Utils.tif ) ) {
                type = "TIFF Image";
            }
            else if ( extension.equals( Utils.png ) ) {
                type = "PNG Image";
            }
        }
        return type;
    }

    public Icon getIcon( File f ) {
        String extension = Utils.getExtension( f );
        Icon icon = null;

        if ( extension != null ) {
            if ( extension.equals( Utils.jpeg ) ||
                 extension.equals( Utils.jpg ) ) {
                icon = jpgIcon;
            }
            else if ( extension.equals( Utils.gif ) ) {
                icon = gifIcon;
            }
            else if ( extension.equals( Utils.tiff ) ||
                      extension.equals( Utils.tif ) ) {
                icon = tiffIcon;
            }
            else if ( extension.equals( Utils.png ) ) {
                icon = pngIcon;
            }
        }
        return icon;
    }
}