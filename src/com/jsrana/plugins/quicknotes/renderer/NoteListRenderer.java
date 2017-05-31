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
package com.jsrana.plugins.quicknotes.renderer;

import org.jdom.Element;

import javax.swing.*;
import java.awt.*;

public class NoteListRenderer
        implements ListCellRenderer {
    private static Color COLOR_EVEN = new Color( 135, 206, 255 );
    private static Color COLOR_ODD = new Color( 176, 226, 255 );
    private static Color COLOR_SELECTED = new Color( 58, 95, 205 );

    /**
     * @param list
     * @param value
     * @param index
     * @param isSelected
     * @param cellHasFocus
     * @return
     */
    public Component getListCellRendererComponent( final JList list,
                                                   final Object value,
                                                   final int index,
                                                   final boolean isSelected,
                                                   boolean cellHasFocus ) {
        return new JPanel() {
            public void paintComponent( Graphics g ) {
                super.paintComponent( g );
                Element e = ( Element ) value;
                String title = e.getAttributeValue( "title" );
                String text = e.getText().trim();

                g.setColor( isSelected ? COLOR_SELECTED : index % 2 == 0 ? COLOR_EVEN : COLOR_ODD );
                g.fillRect( 0, 0, getWidth(), getHeight() );

                g.setColor( isSelected ? Color.WHITE : list.getForeground() );
                g.setFont( new Font( Font.SANS_SERIF, Font.BOLD, 12 ) );
                g.drawString( ( index + 1 ) + ". " + title, 5, 16 );
                g.setFont( new Font( Font.SANS_SERIF, Font.PLAIN, 12 ) );

                if ( text.indexOf( "\n" ) != -1 ) {
                    text = text.substring( 0, text.indexOf( "\n" ) );
                }
                g.drawString( text, 20, 32 );
            }

            public Dimension getPreferredSize() {
                return new Dimension( 200, 40 );
            }
        };
    }
}
