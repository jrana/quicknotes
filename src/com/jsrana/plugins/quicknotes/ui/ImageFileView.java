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
    private Icon jpgIcon = QuickNotesIcon.IMAGE;
    private Icon gifIcon = QuickNotesIcon.IMAGE;
    private Icon tiffIcon = QuickNotesIcon.IMAGE;
    private Icon pngIcon = QuickNotesIcon.IMAGE;

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
            switch (extension) {
                case Utils.jpeg:
                case Utils.jpg:
                    type = "JPEG Image";
                    break;
                case Utils.gif:
                    type = "GIF Image";
                    break;
                case Utils.tiff:
                case Utils.tif:
                    type = "TIFF Image";
                    break;
                case Utils.png:
                    type = "PNG Image";
                    break;
            }
        }
        return type;
    }

    public Icon getIcon( File f ) {
        String extension = Utils.getExtension( f );
        Icon icon = null;

        if ( extension != null ) {
            switch (extension) {
                case Utils.jpeg:
                case Utils.jpg:
                    icon = jpgIcon;
                    break;
                case Utils.gif:
                    icon = gifIcon;
                    break;
                case Utils.tiff:
                case Utils.tif:
                    icon = tiffIcon;
                    break;
                case Utils.png:
                    icon = pngIcon;
                    break;
            }
        }
        return icon;
    }
}