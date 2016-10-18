package com.github.epilepticz.JavaLivestreamerWrapper;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

/**
 * Created by Philipp on 14.10.2016.
 */
public class QualityChooser extends JDialog {
    JComboBox<String> box = new JComboBox<>();
    JButton button = new JButton("ok");
    public QualityChooser(String[] options,ICallback callback )
    {
        super();
        this.getContentPane().setLayout(new BoxLayout(this.getContentPane(),BoxLayout.Y_AXIS));
        this.getContentPane().add(box);
        this.getContentPane().add(button);
        Arrays.stream(options).forEach((x)->this.box.addItem(x));
        this.pack();
        this.setVisible(true);
        this.button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                callback.onSelectQuality((String) box.getSelectedItem());
                dispose();
            }
        });
    }



}
