import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;
import javax.media.Manager;
import javax.media.Player;
import javax.media.Time;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MiniMP3Player extends JFrame implements ActionListener, MouseMotionListener, ChangeListener {

    private Player lecteur = null;
    private File fichier = null;
    private JFileChooser fc = null;
    private Timer play_time = new Timer(1, this);
    private JLabel dure = new JLabel("00:00 - 00:00", JLabel.CENTER);
    private JLabel name = new JLabel(" --- ", JLabel.CENTER);
    private boolean enPause = false;
    private JButton play = new JButton("Play");
    private JButton pause = new JButton("Pause");
    private JButton stop = new JButton("Stop");
    private JButton open = new JButton("Open");
    private JSlider deplacement = new JSlider(0, 100, 0);

    public MiniMP3Player() {

        this.setTitle(" __MiniMP3Player__ ");
        this.setSize(760, 150);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        this.setLayout(new FlowLayout(FlowLayout.CENTER));

        this.getContentPane().add(dure);
        this.getContentPane().add(name);
        this.getContentPane().add(deplacement);

        this.getContentPane().add(play);
        this.getContentPane().add(pause);
        this.getContentPane().add(stop);

        this.getContentPane().add(open);

        play.addActionListener(this);
        pause.addActionListener(this);
        stop.addActionListener(this);
        open.addActionListener(this);

        deplacement.addChangeListener(this);
        deplacement.addMouseMotionListener(this);

        dure.setFont(new Font("Arial", Font.CENTER_BASELINE, 20));
        dure.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        dure.setPreferredSize(new Dimension(140, 35));
        name.setFont(new Font("Arial", Font.CENTER_BASELINE, 20));
        name.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        name.setPreferredSize(new Dimension(590, 35));

        deplacement.setPreferredSize(new Dimension(730, 30));
    }

    public String toString(Time t) {
        if (t != null) {
            int min = ((int) t.getSeconds()) / 60;
            int sec = ((int) t.getSeconds()) % 60;
            return (min < 10 ? "0" + min : min) + ":" + (sec < 10 ? "0" + sec : sec);
        }
        return "00:00";
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == play_time) {
            dure.setText(toString(lecteur.getMediaTime()) + " - " + toString(lecteur.getDuration()));

            
            deplacement.setValue((int) (lecteur.getMediaTime().getSeconds() / lecteur.getDuration().getSeconds() * 100));

            if (lecteur.getMediaTime().getSeconds() == lecteur.getDuration().getSeconds()) {
                lecteur.stop();
                lecteur.close();
                lecteur = null;

                deplacement.setValue(0);
                dure.setText("00:00 - " + toString(lecteur.getDuration()));
                play_time.stop();
            }
        }

        if (e.getSource() == open) {
            fc = new JFileChooser();
            fc.setAcceptAllFileFilterUsed(false);
            fc.showOpenDialog(this);

            if (fc.getDialogType() == JFileChooser.APPROVE_OPTION) {
                fichier = fc.getSelectedFile();
                name.setText(fichier.getName().substring(0, (int) fichier.getName().length() - 4));
            }
        }

        if (e.getSource() == play) {
            try {
                if (fichier != null) {
                    if (lecteur == null) {
                        lecteur = Manager.createPlayer(fichier.toURL());
                        lecteur.start();
                        play_time.start();
                    }

                    if (enPause == true) {
                        lecteur.start();
                        play_time.start();
                        enPause = false;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (e.getSource() == pause) {
            if (enPause == false && lecteur != null) {
                lecteur.stop();
                play_time.stop();
                enPause = true;
            }
        }

        if (e.getSource() == stop) {
            if (enPause == false && lecteur != null) {
                lecteur.stop();
                lecteur.close();
                lecteur = null;

                play_time.stop();
                dure.setText("00:00 - 00:00");
                deplacement.setValue(0);
            }
        }
    }

    public void stateChanged(ChangeEvent e) {
        if (lecteur != null) {
            if (e.getSource() == deplacement) {
                deplacement.setToolTipText(toString(lecteur.getMediaTime()));
            }
        }
    }

    public void mouseDragged(MouseEvent e) {
        if (lecteur != null) {
            if (e.getSource() == deplacement) {
                if (e.getModifiers() == MouseEvent.BUTTON1_MASK) {
                    lecteur.setMediaTime(new Time((double) (deplacement.getValue() * lecteur.getDuration().getSeconds() / 100)));
                }
            }
        }
    }

    public void mouseMoved(MouseEvent e) {
    }

    public static void main(String[] args) {
        new MiniMP3Player();
    }
}