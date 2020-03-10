/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author jmm4115
 */
public class MovieBookingGUI extends JPanel{
    private JLabel cinemaName;
    private DefaultListModel model;
    private JList movieList;
    private JScrollPane scroll;
    private JButton[][] seatingButtons;
    private JRadioButton adult, elderly, child;
    private ButtonGroup customerTypeGroup;
    private JCheckBox complementary;
    private JButton exit, cancel, book;
    private Color colour;
    private ArrayList<SeatReservation> currentReservation;
    private Listener listener;
    
    public MovieBookingGUI(ArrayList<MovieSession> sessions)
    {
        super(new BorderLayout());
        listener = new Listener();
        
        //add cinema title
        cinemaName = new JLabel("Movies N Chill", JLabel.CENTER);
        cinemaName.setPreferredSize(new Dimension(750, 40));
        Font font = new Font("Arial", Font.BOLD, 25);
        cinemaName.setFont(font);
        add(cinemaName, BorderLayout.NORTH);
        
        //add movielist
        model = new DefaultListModel();
        for (MovieSession s: sessions)
            model.addElement(s);
        movieList = new JList(model);
        movieList.setLayoutOrientation(JList.VERTICAL);
        movieList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        movieList.addListSelectionListener(listener);
        scroll = new JScrollPane(movieList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setPreferredSize(new Dimension(225, 500));
        add(scroll, BorderLayout.EAST);
        
        //add seatings
        int cinemaHeight = MovieSession.NUM_ROWS;
        int cinemaWidth = MovieSession.NUM_COLS;
        seatingButtons = new JButton[cinemaHeight][cinemaWidth];
        JPanel centerPanel = new JPanel(new GridLayout(cinemaHeight, cinemaWidth));
        centerPanel.setPreferredSize(new Dimension(500, 500));
        createSeatings(cinemaHeight, cinemaWidth, centerPanel);
        add(centerPanel, BorderLayout.CENTER);                
        
        //add radiobuttons, checkboxes, and buttons
        adult = new JRadioButton("Adult");
        elderly = new JRadioButton("Elderly");
        child = new JRadioButton("Child");
        adult.addActionListener(listener);
        elderly.addActionListener(listener);
        child.addActionListener(listener);
        customerTypeGroup = new ButtonGroup();
        customerTypeGroup.add(adult);
        customerTypeGroup.add(elderly);
        customerTypeGroup.add(child);
        complementary = new JCheckBox("Complementary");
        exit = new JButton("Exit");
        cancel = new JButton("Cancel");
        book = new JButton("Book");
        exit.addActionListener(listener);
        cancel.addActionListener(listener);
        book.addActionListener(listener);
        JPanel southPanel = new JPanel();
        southPanel.add(adult);
        southPanel.add(elderly);
        southPanel.add(child);
        southPanel.add(complementary);
        southPanel.add(exit);
        southPanel.add(cancel);
        southPanel.add(book);
        add(southPanel, BorderLayout.SOUTH);
        
        colour = null;
        currentReservation = new ArrayList<SeatReservation>();
    }
    
    private void createSeatings(int cinemaHeight, int cinemaWidth, JPanel centerPanel)
    {
        for (int k = 0; k < cinemaHeight; k++)
        {
            String row = "" + MovieSession.convertIndexToRow(k);
            for (int j = 0; j < cinemaWidth; j++)
            {
                seatingButtons[k][j] = new JButton(row + "," + j);
                seatingButtons[k][j].addActionListener(listener);
                centerPanel.add(seatingButtons[k][j]);
            }
        }
    }
    
    private class Listener implements ListSelectionListener, ActionListener
    {
        @Override
        public void valueChanged(ListSelectionEvent e)
        {
            MovieSession movie = (MovieSession) movieList.getSelectedValue();
            cinemaName.setText(movie.toString());
            reset();
            changeMovie(movie);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            Object source = e.getSource();
            if (source instanceof JButton)
            {
                if (source == exit)
                {
                    System.exit(0);
                }
                else if (source == cancel)
                {
                    reset();
                    JOptionPane.showMessageDialog(null, "Selections cleared!");
                }
                else if (source == book)
                {
                    if (!currentReservation.isEmpty())
                    {
                        MovieSession movie = (MovieSession) movieList.getSelectedValue();
                        boolean isBooked = movie.applyBookings(currentReservation);

                        int ticketNumber = currentReservation.size();
                        String title = ticketNumber + " Ticket" + (ticketNumber > 1 ? "s" : "" );
                        if (isBooked)
                        {
                            float price = 0.0f;
                            for (SeatReservation s: currentReservation)
                            {
                                price += s.getTicketPrice();
                                int rowIndex = MovieSession.convertRowToIndex(s.getRow());
                                if (s instanceof ElderlyReservation)
                                {
                                    seatingButtons[rowIndex][s.getCol()].setBackground(Color.WHITE);
                                }   
                                else if (s instanceof AdultReservation)
                                {
                                    seatingButtons[rowIndex][s.getCol()].setBackground(Color.BLUE);
                                }
                                else if (s instanceof ChildReservation)
                                {
                                    seatingButtons[rowIndex][s.getCol()].setBackground(Color.YELLOW);
                                }

                                seatingButtons[rowIndex][s.getCol()].setEnabled(false);
                            }
                            
                            title += " Booked";
                            JOptionPane.showMessageDialog(null, "Ticket costs : $" + String.format("%.2f", price), title, JOptionPane.INFORMATION_MESSAGE);
                        }
                        else
                        {
                            title += " Not Booked";
                            JOptionPane.showMessageDialog(null, "CANNOT BOOK CHILD IN R MOVIE OR UNSUPERVISED IN M MOVIES", title, JOptionPane.ERROR_MESSAGE);
                        }

                        reset();
                    }
                    else
                        JOptionPane.showMessageDialog(null, "Please make bookings", "ERROR!", JOptionPane.ERROR_MESSAGE);
                }
                else //making reservations
                {
                    if (!movieList.isSelectionEmpty())
                    {
                        if (colour != null)
                        {
                            String text = e.getActionCommand();
                            int rowIndex = MovieSession.convertRowToIndex(text.charAt(0));
                            int col = Integer.parseInt(text.substring(2));
                            seatingButtons[rowIndex][col].setForeground(colour);

                            //check whether the button been pressed before - ie. seatings reservation has been made before for that seats.
                            char rowLetter = text.charAt(0);
                            boolean added = false; 
                            for (int k = 0; k < currentReservation.size() && added == false; k++)
                            {
                                if (currentReservation.get(k).getRow() == rowLetter && currentReservation.get(k).getCol() == col)
                                {
                                    currentReservation.remove(k);
                                    added = true;
                                }
                            }

                            if (colour == Color.BLUE)
                                currentReservation.add(new AdultReservation(rowLetter, col));

                            if (colour == Color.WHITE)
                                currentReservation.add(new ElderlyReservation(rowLetter, col));

                            if (colour == Color.YELLOW)
                                currentReservation.add(new ChildReservation(rowLetter, col));

                            if (complementary.isSelected() && !currentReservation.isEmpty())
                                currentReservation.get(currentReservation.size() - 1).setComplementary(true);
                        }
                        else
                            JOptionPane.showMessageDialog(null, "Please select an age group", "ERROR!", JOptionPane.ERROR_MESSAGE);
                    }
                    else
                        JOptionPane.showMessageDialog(null, "Please select a movie", "ERROR!", JOptionPane.ERROR_MESSAGE);
                }
            }
            
            if (source instanceof JRadioButton)
            {
                if (source == adult)
                    colour = Color.BLUE;
                
                if (source == elderly)
                    colour = Color.WHITE;
                
                if (source == child)
                    colour = Color.YELLOW;                
            }
        }
        
        private void reset()
        {
            colour = null;
            for (SeatReservation s: currentReservation)
                seatingButtons[MovieSession.convertRowToIndex(s.getRow())][s.getCol()].setForeground(colour);
            currentReservation.clear();
            customerTypeGroup.clearSelection();
        }
        
        private void changeMovie(MovieSession movie)
        {
            for (int k = 0; k < MovieSession.NUM_ROWS; k++)
            {
                char rowLetter = MovieSession.convertIndexToRow(k);
                for (int j = 0; j < MovieSession.NUM_COLS; j++)
                {
                    //clearing seating arrangements from previous movie
                    if (seatingButtons[k][j].isBackgroundSet())
                        seatingButtons[k][j].setBackground(null);
                    
                    if (!seatingButtons[k][j].isEnabled())
                        seatingButtons[k][j].setEnabled(true);
                    
                    //adding the booked seatings for this movie
                    try
                    {
                        if (!movie.isSeatAvailable(rowLetter, j))
                        {
                            if (movie.getSeat(rowLetter, j) instanceof ElderlyReservation)
                                seatingButtons[k][j].setBackground(Color.WHITE);
                            else if (movie.getSeat(rowLetter, j) instanceof AdultReservation)
                                seatingButtons[k][j].setBackground(Color.BLUE);
                            else if (movie.getSeat(rowLetter, j) instanceof ChildReservation)
                                seatingButtons[k][j].setBackground(Color.YELLOW);

                            seatingButtons[k][j].setEnabled(false);
                        }
                    }
                    catch (ArrayIndexOutOfBoundsException e) {}
                }
            }
        }
    }
    
    public static void main(String[] args)
    {
        ArrayList<MovieSession> sessions = new ArrayList<MovieSession>();
        sessions.add(new MovieSession("Incredible 2", 'G', new Time(14, 30, 0)));
        sessions.add(new MovieSession("The Meg", 'M', new Time(12, 0, 0)));
        sessions.add(new MovieSession("Luis and the Aliens", 'G', new Time(10, 30, 0)));
        sessions.add(new MovieSession("Slenderman", 'M', new Time(10, 0, 0)));
        sessions.add(new MovieSession("The Meg", 'M', new Time(14, 30, 0)));
        sessions.add(new MovieSession("The Happytime Murders", 'R', new Time(10, 0, 0)));
        sessions.add(new MovieSession("The Equalizer 2", 'R', new Time(13, 0, 0)));
        Collections.sort(sessions);
        
        MovieBookingGUI bookingPanel = new MovieBookingGUI(sessions);
        JFrame frame = new JFrame("Movies N Chill");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(bookingPanel);
        frame.pack();
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        int screenHeight = d.height;
        int screenWidth = d.width;
        frame.setLocation(new Point((screenWidth / 2) - (frame.getWidth() / 2),
                    (screenHeight / 2) - (frame.getHeight() / 2)));
        frame.setVisible(true);
    }
}
