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

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class ImageFilter
        extends FileFilter {
    public boolean accept( File f ) {
        if ( f.isDirectory() ) {
            return true;
        }
        String extension = Utils.getExtension( f );
        return extension != null && ( extension.equals( Utils.tiff ) ||
                                      extension.equals( Utils.tif ) ||
                                      extension.equals( Utils.gif ) ||
                                      extension.equals( Utils.jpeg ) ||
                                      extension.equals( Utils.jpg ) ||
                                      extension.equals( Utils.png ) );

    }

    public String getDescription() {
        return "Just Images";
    }
}
