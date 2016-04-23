import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by darkg on 4/23/2016.
 */
public class MyCanvas extends GLCanvas implements GLEventListener {
    private static final float SUN_RADIUS = 12f;

    private FPSAnimator animator;
    private GLU glu;
    private Texture earthTexture;
    private Texture solarPanelTexture;
    private ArrayList<Planet> planets;

    private float satelliteAngle = 0;
    private float earthAngle = 0;
    private float systemAngle = 0;
    private Sun sun;

    public MyCanvas(int width, int height, GLCapabilities capabilities) {
        super(capabilities);
        setSize(width, height);
        addGLEventListener(this);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        glu = new GLU();
        planets = new ArrayList<>();
        // Enable z- (depth) buffer for hidden surface removal.
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LEQUAL);

        // Enable smooth shading.
        gl.glShadeModel(GL2.GL_SMOOTH);

        // Define "clear" color.
        gl.glClearColor(0f, 0f, 0f, 0f);

        // We want a nice perspective.
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);

        // Start animator (which should be a field).
        animator = new FPSAnimator(this, 60);
        animator.start();

        String textureFile = "C:\\Users\\darkg\\workspace\\JOGL_Textures\\earthmap1k.jpg";
        earthTexture = getObjectTexture(gl, textureFile);

        textureFile = "C:\\Users\\darkg\\workspace\\JOGL_Textures\\solar_panel_256x32.png";
        solarPanelTexture = getObjectTexture(gl, textureFile);


        textureFile = "C:\\Users\\darkg\\workspace\\JOGL_Textures\\preview_sun.jpg";
        this.sun = new Sun(gl, glu, getObjectTexture(gl, textureFile));

        textureFile = "C:\\Users\\darkg\\workspace\\JOGL_Textures\\mercurymap.jpg";
        Planet mercury = new Planet(gl,glu, getObjectTexture(gl, textureFile), 1.2f, SUN_RADIUS + 2f , 2.56f);

        textureFile = "C:\\Users\\darkg\\workspace\\JOGL_Textures\\venusmap.jpg";
        Planet venus = new Planet(gl,glu, getObjectTexture(gl, textureFile), 0.7f, SUN_RADIUS + 16f , 3.56f);

        textureFile = "C:\\Users\\darkg\\workspace\\JOGL_Textures\\mars_1k_color.jpg";
        Planet mars = new Planet(gl,glu, getObjectTexture(gl, textureFile), 0.4f, SUN_RADIUS + 55f , 5.01f);
        planets.add(mercury);
        planets.add(venus);
        planets.add(mars);


    }
    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {

    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        if (!animator.isAnimating()) {
            return;
        }

        final GL2 gl = glAutoDrawable.getGL().getGL2();
        // Clear screen.
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        // Set camera.
        setCamera(gl, 125);

        setLights(gl);
        sun.display();

        drawEarthAndSatellite(gl);
        for(Planet p : planets){
            p.display();
        }

    }

    private void drawEarthAndSatellite(GL2 gl) {
        gl.glPushMatrix();
        // Compute satellite position.
        systemAngle = (systemAngle + 0.4f) % 360f;

        final float distance = SUN_RADIUS + 30f;
        final float x = (float) Math.sin(Math.toRadians(systemAngle)) * distance;
        final float y = (float) Math.cos(Math.toRadians(systemAngle)) * distance;
        final float z = 0;
        gl.glTranslatef(x, y, z);


        drawEarth(gl);
        drawSatellite(gl);
        gl.glPopMatrix();
    }

    private void drawEarth(GL2 gl) {


        // Set material properties.
        float[] rgba = {1f, 1f, 1f};
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, rgba, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, rgba, 0);
        gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, 0.5f);

        // Apply texture.
        earthTexture.enable(gl);
        earthTexture.bind(gl);

        earthAngle = (earthAngle + 0.1f) % 360f;


        gl.glPushMatrix();
        gl.glRotatef(earthAngle , 0.2f, 0.1f, 0);

        // Draw sphere (possible styles: FILL, LINE, POINT).
        //gl.glColor3f(0.3f, 0.5f, 1f);
        GLUquadric earth = glu.gluNewQuadric();
        glu.gluQuadricTexture(earth, true);
        glu.gluQuadricDrawStyle(earth, GLU.GLU_FILL);
        glu.gluQuadricNormals(earth, GLU.GLU_FLAT);
        glu.gluQuadricOrientation(earth, GLU.GLU_OUTSIDE);
        final float radius = 6.378f;
        final int slices = 16;
        final int stacks = 16;
        glu.gluSphere(earth, radius, slices, stacks);
        glu.gluDeleteQuadric(earth);


        gl.glPopMatrix();
    }

    private void drawSatellite(GL2 gl) {

        // Save old state.
        gl.glPushMatrix();

        // Compute satellite position.
        satelliteAngle = (satelliteAngle + 1f) % 360f;
        final float distance = 10.000f;
        final float x = (float) Math.sin(Math.toRadians(satelliteAngle)) * distance;
        final float y = (float) Math.cos(Math.toRadians(satelliteAngle)) * distance;
        final float z = 0;
        gl.glTranslatef(x, y, z);
        gl.glRotatef(satelliteAngle, 0, 0, -1);
        gl.glRotatef(45f, 0, 1, 0);

        // Set silver color, and disable texturing.
        gl.glDisable(GL.GL_TEXTURE_2D);
        float[] ambiColor = {0.3f, 0.3f, 0.3f, 1f};
        float[] specColor = {0.8f, 0.8f, 0.8f, 1f};
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_AMBIENT, ambiColor, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, specColor, 0);
        gl.glMaterialf(GL.GL_FRONT, GL2.GL_SHININESS, 90f);

        // Draw satellite body.
        final float cylinderRadius = 1f;
        final float cylinderHeight = 2f;
        final int cylinderSlices = 16;
        final int cylinderStacks = 16;
        GLUquadric body = glu.gluNewQuadric();
        glu.gluQuadricTexture(body, false);
        glu.gluQuadricDrawStyle(body, GLU.GLU_FILL);
        glu.gluQuadricNormals(body, GLU.GLU_FLAT);
        glu.gluQuadricOrientation(body, GLU.GLU_OUTSIDE);
        gl.glTranslatef(0, 0, -cylinderHeight / 2);
        glu.gluDisk(body, 0, cylinderRadius, cylinderSlices, 2);
        glu.gluCylinder(body, cylinderRadius, cylinderRadius, cylinderHeight, cylinderSlices, cylinderStacks);
        gl.glTranslatef(0, 0, cylinderHeight);
        glu.gluDisk(body, 0, cylinderRadius, cylinderSlices, 2);
        glu.gluDeleteQuadric(body);
        gl.glTranslatef(0, 0, -cylinderHeight / 2);

        // Set white color, and enable texturing.
        float[] rgba = {1f, 1f, 1f};
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_AMBIENT, rgba, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, rgba, 0);
        gl.glMaterialf(GL.GL_FRONT, GL2.GL_SHININESS, 0f);

        // Draw solar panels.
        gl.glScalef(6f, 0.7f, 0.1f);
        solarPanelTexture.bind(gl);
        gl.glBegin(GL2.GL_QUADS);
        final float[] frontUL = {-1.0f, -1.0f, 1.0f};
        final float[] frontUR = {1.0f, -1.0f, 1.0f};
        final float[] frontLR = {1.0f, 1.0f, 1.0f};
        final float[] frontLL = {-1.0f, 1.0f, 1.0f};
        final float[] backUL = {-1.0f, -1.0f, -1.0f};
        final float[] backLL = {-1.0f, 1.0f, -1.0f};
        final float[] backLR = {1.0f, 1.0f, -1.0f};
        final float[] backUR = {1.0f, -1.0f, -1.0f};
        // Front Face.
        gl.glNormal3f(0.0f, 0.0f, 1.0f);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3fv(frontUR, 0);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3fv(frontUL, 0);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3fv(frontLL, 0);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3fv(frontLR, 0);
        // Back Face.
        gl.glNormal3f(0.0f, 0.0f, -1.0f);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3fv(backUL, 0);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3fv(backUR, 0);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3fv(backLR, 0);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3fv(backLL, 0);
        gl.glEnd();

        // Restore old state.
        gl.glPopMatrix();

    }

    private Texture getObjectTexture(GL2 gl, String fileName){
        InputStream stream = null;
        Texture tex = null;
        String extension = fileName.substring(fileName.lastIndexOf('.'));
        try {
            stream = new FileInputStream(new File(fileName));
            TextureData data = TextureIO.newTextureData(gl.getGLProfile(), stream, false, extension);
            tex = TextureIO.newTexture(data);
        } catch (FileNotFoundException e) {
            System.err.println("Error loading the file!");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("IO Exception!");
            e.printStackTrace();
        }

        return tex;
    }

    private void setLights(GL2 gl) {

        // Prepare light parameters.
        float SHINE_ALL_DIRECTIONS = 1;
        float[] lightPos = {0, 0, 0, SHINE_ALL_DIRECTIONS};
        float[] lightColorAmbient = {0.5f, 0.5f, 0.5f, 1f};
        float[] lightColorSpecular = {0.8f, 0.8f, 0.8f, 1f};

        // Set light parameters.
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, lightPos, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, lightColorAmbient, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, lightColorSpecular, 0);

        // Enable lighting in GL.
        gl.glEnable(GL2.GL_LIGHT1);
        gl.glEnable(GL2.GL_LIGHTING);


    }

    private void setCamera(GL2 gl, float distance) {
        // Change to projection matrix.
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        // Perspective.
        float widthHeightRatio = (float) getWidth() / (float) getHeight();
        glu.gluPerspective(45, widthHeightRatio, 1, 1000);
        glu.gluLookAt(0, 0, distance, 0, 0, 0, 0, 1, 0);

        // Change back to model view matrix.
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL gl = drawable.getGL();
        gl.glViewport(0, 0, width, height);
    }
}
