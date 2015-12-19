package com.nicholasnassar.thespookydungeon.client;

import com.nicholasnassar.thespookydungeon.Creature;
import com.nicholasnassar.thespookydungeon.Item;
import com.nicholasnassar.thespookydungeon.packets.PacketUpdateUI;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the "The Spooky Dungeon" application.
 * "The Spooky Dungeon" is an advanced, graphical based RPG
 * game.
 * <p>
 * This class allows the game to be played with a GUI.
 * Users can respond through the buttons provided in
 * the bottom right of the GUI.
 */
public class GUI extends JFrame {
    private DungeonClient client;
    private final JPanel panel;
    private JTextArea textArea;
    private JTextField textField;
    private JLabel roomPicture;
    private JLabel slashPicture;
    private JPanel buttonPanel, attackPanel, inventoryPanel, statsPanel;
    private JButton north, east, south, west, back, help;
    private Map<String, Clip[]> sounds;
    private boolean dead;

    /**
     * Creates the GUI and initializes its components
     */
    public GUI(Game game) {
        panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        setupCenterPanel();
        setupRightPanel();
        getContentPane().add(panel);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                game.closing();
            }
        });
        setTitle("The Spooky Dungeon: Spooky Maymays");
        setSize(1150, 800);
        //Places the window in the center of the screen
        setLocationRelativeTo(null);
        setupSounds();
        //Causes the game to exit when the X button at the top right of the window is pressed
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        dead = false;
    }

    /**
     * Sets up the center panel of the UI
     */
    private void setupCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(new EmptyBorder(0, 0, 5, 5));
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        centerPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        centerPanel.add(textField = new JTextField(), BorderLayout.SOUTH);
        textField.addActionListener(e -> {
            client.chat(textField.getText());
            textField.setText("");
        });

        JPanel picturePanel = new JPanel();
        LayoutManager overlay = new OverlayLayout(picturePanel);

        picturePanel.setLayout(overlay);
        roomPicture = new JLabel("");
        roomPicture.setAlignmentX(0.5F);
        roomPicture.setAlignmentY(0.5F);
        centerPanel.add(picturePanel, BorderLayout.NORTH);
        slashPicture = new JLabel("");
        slashPicture.setAlignmentX(0.5F);
        slashPicture.setAlignmentY(0.5F);
        try {
            slashPicture.setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("images/slash/slash.png"))));
        } catch (IOException e) {

        }
        slashPicture.setVisible(false);
        picturePanel.add(slashPicture);
        picturePanel.add(roomPicture);
        panel.add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * Sets up the right panel of the UI
     */
    private void setupRightPanel() {
        JPanel rightPanel = new JPanel();
        BoxLayout layout = new BoxLayout(rightPanel, BoxLayout.Y_AXIS);
        rightPanel.setLayout(layout);
        buttonPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        //Lock the size of the button panel to make it look nice
        Dimension size = new Dimension(285, 140);
        buttonPanel.setPreferredSize(size);
        buttonPanel.setMinimumSize(size);
        buttonPanel.setMaximumSize(size);
        buttonPanel.add(new JLabel("Go:"));
        buttonPanel.add(north = new JButton("North"));
        buttonPanel.add(east = new JButton("East"));
        buttonPanel.add(south = new JButton("South"));
        buttonPanel.add(west = new JButton("West"));
        buttonPanel.add(back = new JButton("Back"));
        buttonPanel.add(new JLabel("Actions:"));
        buttonPanel.add(help = new JButton("Help"));
        attackPanel = new JPanel();
        attackPanel.add(new JLabel("Attack:"));
        inventoryPanel = new JPanel();
        layout = new BoxLayout(inventoryPanel, BoxLayout.PAGE_AXIS);
        inventoryPanel.setLayout(layout);
        statsPanel = new JPanel();
        north.addActionListener(e -> client.goRoom("north"));
        east.addActionListener(e -> client.goRoom("east"));
        south.addActionListener(e -> client.goRoom("south"));
        west.addActionListener(e -> client.goRoom("west"));
        back.addActionListener(e -> client.goRoom("back"));
        help.addActionListener(e -> client.askForHelp());
        rightPanel.add(buttonPanel);
        rightPanel.add(attackPanel);
        rightPanel.add(inventoryPanel);
        rightPanel.add(statsPanel);
        panel.add(rightPanel, BorderLayout.EAST);
    }

    public void setClient(DungeonClient client) {
        this.client = client;
    }


    /**
     * Returns a newly constructed clip
     * using the path sounds/sound.wav,
     * sound being the given sound
     * parameter at the specified master
     * gain.
     */
    private Clip getNewClip(String sound, float masterGain) {
        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(getClass().getClassLoader().getResource("sounds/" + sound + ".wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(stream);
            FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            //In case the master gain goes over the maximum, set it to the maximum to avoid an exception
            if (masterGain > control.getMaximum()) {
                masterGain = control.getMaximum();
            }
            control.setValue(masterGain);
            return clip;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Sets up the sound effects used in
     * the game
     */
    private void setupSounds() {
        sounds = new HashMap<>();
        sounds.put("attack", new Clip[]{getNewClip("sword-clash1", -20), getNewClip("sword-clash2", -20), getNewClip("sword-clash3", -20)});
        sounds.put("walk", new Clip[]{getNewClip("walk1", 10)});
        sounds.put("victory", new Clip[]{getNewClip("victory1", -15)});
    }

    /**
     * Plays a clip. If the sound
     * effect is currently going, it
     * will be stopped and restarted at
     * 0:00
     */
    public void playClip(String sound) {
        Clip[] clips = sounds.get(sound);

        Clip clip = clips[(int) (Math.random() * clips.length)];

        clip.setFramePosition(0);

        clip.start();
    }

    /**
     * Returns the image label
     */
    public JLabel getRoomPicture() {
        return roomPicture;
    }

    /**
     * Returns the text area
     */
    public JTextArea getTextArea() {
        return textArea;
    }

    /**
     * Disables the buttons on the UI
     */
    public void disableUI() {
        buttonPanel.setEnabled(false);
        north.setEnabled(false);
        east.setEnabled(false);
        south.setEnabled(false);
        west.setEnabled(false);
        back.setEnabled(false);
        help.setEnabled(false);
        for (Component component : attackPanel.getComponents()) {
            component.setEnabled(false);
        }
        for (Component component : inventoryPanel.getComponents()) {
            component.setEnabled(false);
        }
    }

    /**
     * Updates the game's UI based on
     * the update UI packet received
     */
    public void updateUI(PacketUpdateUI packet) {
        try {
            //Attempts to open the room's image relative to the GUI class. These images are located in the images/rooms/ folder.
            //When the game is turned into a JAR file, the images folder should be packaged inside of it to still allow access.
            InputStream stream = getClass().getClassLoader().getResourceAsStream("images/rooms/" + packet.roomImage + ".png");

            //If the image was not found, we will use an image that we know for sure exists - the error image
            if (stream == null) {
                getRoomPicture().setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("images/rooms/error.png"))));
            } else {
                getRoomPicture().setIcon(new ImageIcon(ImageIO.read(stream)));
            }
        } catch (IOException e) {

        }
        north.setEnabled(packet.northEnabled);
        east.setEnabled(packet.eastEnabled);
        south.setEnabled(packet.southEnabled);
        west.setEnabled(packet.westEnabled);
        back.setEnabled(packet.backEnabled);

        for (Component component : inventoryPanel.getComponents()) {
            inventoryPanel.remove(component);
        }

        Item[] items = packet.roomItems;

        if (items != null) {
            JLabel itemsLabel = new JLabel("Items in Room:");
            itemsLabel.setAlignmentX(0.5F);
            inventoryPanel.add(itemsLabel);
            if (items.length <= 0) {
                JButton noItems = new JButton("No items.");
                noItems.setAlignmentX(0.5F);
                noItems.setEnabled(false);
                inventoryPanel.add(noItems);
            } else {
                for (int i = 0; i < items.length; i++) {
                    Item item = items[i];
                    JButton itemButton = new JButton("Grab " + item.getName());
                    itemButton.setAlignmentX(0.5F);
                    final int finalI = i;
                    itemButton.addActionListener(e -> client.grabItem(finalI));
                    inventoryPanel.add(itemButton);
                }
            }
        } else {
            JButton searchButton = new JButton("Search for items on the ground");
            searchButton.setAlignmentX(0.5F);
            searchButton.addActionListener(e -> client.search());
            inventoryPanel.add(searchButton);
        }
        inventoryPanel.add(Box.createVerticalGlue());
        JLabel inventoryLabel = new JLabel("Inventory:");
        inventoryLabel.setAlignmentX(0.5F);
        inventoryPanel.add(inventoryLabel);
        Item[] inventory = packet.inventory;
        if (inventory == null) {
            JButton noItems = new JButton("No Items");
            noItems.setEnabled(false);
            noItems.setAlignmentX(0.5F);
            inventoryPanel.add(noItems);
        } else {
            for (int i = 0; i < inventory.length; i++) {
                Item item = inventory[i];
                JButton itemButton = new JButton();
                String buttonName = item.getName();
                if (buttonName.contains("Potion")) {
                    buttonName = "Use " + buttonName;
                } else {
                    itemButton.setEnabled(false);
                }
                itemButton.setText(buttonName);
                itemButton.setAlignmentX(0.5F);
                final int finalI = i;
                itemButton.addActionListener(e -> client.useItem(finalI));
                inventoryPanel.add(itemButton);
            }
        }
        inventoryPanel.add(Box.createVerticalGlue());
        inventoryPanel.revalidate();
        inventoryPanel.repaint();
        for (Component component : attackPanel.getComponents()) {
            if (component instanceof JButton) {
                attackPanel.remove(component);
            }
        }
        Creature[] enemies = packet.enemies;
        if (enemies == null) {
            JButton noTarget = new JButton("No Target");
            noTarget.setEnabled(false);
            attackPanel.add(noTarget);
        } else {
            for (int i = 0; i < enemies.length; i++) {
                Creature creature = enemies[i];
                JButton creatureButton = new JButton(creature.getName() + " - " + creature.getHealth() + " HP");
                final int finalI = i;
                creatureButton.addActionListener(e -> client.attack(finalI));
                attackPanel.add(creatureButton);
            }
        }
        if (packet.startSlash) {
            attackUpdate();
        }
        attackPanel.revalidate();
        attackPanel.repaint();
        for (Component component : statsPanel.getComponents()) {
            statsPanel.remove(component);
        }
        statsPanel.add(new JLabel("Stats:"));
        statsPanel.add(new JLabel("Health:"));
        statsPanel.add(new JLabel(packet.health + "/" + packet.maxHealth));
        statsPanel.add(new JLabel("Damage: "));
        statsPanel.add(new JLabel(packet.minDamage + " - " + packet.maxDamage));
        statsPanel.add(new JLabel("Damage Block: "));
        if (packet.minDamageBlock > 0) {
            statsPanel.add(new JLabel(packet.minDamageBlock + " - " + packet.maxDamageBlock));
        } else {
            statsPanel.add(new JLabel("None"));
        }
        statsPanel.revalidate();
        statsPanel.repaint();
        if (packet.health <= 0) {
            disableUI();
            dead = true;
        }
    }

    /**
     * Show slash on top of picture
     * and disables the attack buttons
     * for a second
     */
    public void attackUpdate() {
        for (Component component : attackPanel.getComponents()) {
            if (component instanceof JButton) {
                component.setEnabled(false);
            }
        }
        slashPicture.setVisible(true);
        Timer timer = new Timer(1000, e -> {
            slashPicture.setVisible(false);
            for (Component component : attackPanel.getComponents()) {
                if (component instanceof JButton && !((JButton) component).getText().equals("No Target") && !dead) {
                    component.setEnabled(true);
                }
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
}
