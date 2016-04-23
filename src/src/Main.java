import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args){
        // getting the capabilities object of GL2 profile
        final GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);

        MyCanvas canvas = new MyCanvas(600, 600, capabilities);
        // the window frame
        JFrame frame = new JFrame("My solar system example");
        frame.getContentPane().add(canvas, BorderLayout.CENTER);

        frame.setSize(frame.getContentPane().getPreferredSize());
        frame.setResizable(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        canvas.requestFocus();
    }
}
