/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author jmm4115
 */
public class MovieSession implements Comparable<MovieSession>{
    private String movieName;
    private char rating;
    private Time sessionTime;
    private SeatReservation[][] sessionSeats;
    public static final int NUM_ROWS = 8;
    public static final int NUM_COLS = 6;
    
    public MovieSession(String movieName, char rating, Time sessionTime)
    {
        this.movieName = movieName;
        this.rating = rating;
        this.sessionTime = sessionTime;
        sessionSeats = new SeatReservation[NUM_ROWS][NUM_COLS];
    }
    
    public static int convertRowToIndex(char rowLetter)
    {
        int index = -1;
        switch (rowLetter)
        {
            case 'A': index = 0; break;
            case 'B': index = 1; break;
            case 'C': index = 2; break;
            case 'D': index = 3; break;
            case 'E': index = 4; break;
            case 'F': index = 5; break;
            case 'G': index = 6; break;
            case 'H': index = 7; break;
        }
        return index;
    }
    
    public static char convertIndexToRow(int rowIndex)
    {
        char letter = '\0';
        switch (rowIndex)
        {
            case 0: letter = 'A'; break;
            case 1: letter = 'B'; break;
            case 2: letter = 'C'; break;
            case 3: letter = 'D'; break;
            case 4: letter = 'E'; break;
            case 5: letter = 'F'; break;
            case 6: letter = 'G'; break;
            case 7: letter = 'H'; break;
        }
        return letter;
    }
    
    public char getRating()
    {
        return rating;
    }
    
    public String getMovieName()
    {
        return movieName;
    }
    
    public Time getSessionTime()
    {
        return sessionTime;
    }
    
    public SeatReservation getSeat(char row, int col) //throws IndexOutOfBoundsException
    {
        int rowIndex = convertRowToIndex(row);
        if ((rowIndex >= 0 && rowIndex < NUM_ROWS) && (col >= 0 && col < NUM_COLS))
//                if (!isSeatAvailable(row, col))
                    return sessionSeats[rowIndex][col];
        else
            throw new ArrayIndexOutOfBoundsException(row + "," + col);
//        return null;
    }
    
    public boolean isSeatAvailable(char row, int col)
    {
//        return sessionSeats[convertRowToIndex(row)][col] == null;
        return getSeat(row, col) == null;
    }
    
    public boolean applyBookings(List<SeatReservation> reservations)
    {
        boolean isAdult = false;
        if (rating == 'M')            
            for (int k = 0; k < reservations.size() && isAdult == false; k++)
                if (reservations.get(k) instanceof AdultReservation)
                    isAdult = true;
        
        try
        {
            for (SeatReservation r: reservations)
            {
                char row = r.getRow();
                int col = r.getCol();

                if (isSeatAvailable(row, col))
                {
                    if (r instanceof ChildReservation && rating == 'R')
                        return false;

                    if (r instanceof ChildReservation && rating == 'M')
                        if (!isAdult)
                            return false;
                }
                else
                    return false;
            }

            for (SeatReservation r: reservations)
            {
                char row = r.getRow();
                int col = r.getCol();
                sessionSeats[convertRowToIndex(row)][col] = r;
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {}
        
        return true;
    }
    
    public void printSeats()
    {
        for (int k = 0; k < NUM_ROWS; k++)
        {
            for (int j = 0; j < NUM_COLS; j++)
            {
                System.out.print("|");
                
                if (sessionSeats[k][j] == null)
                    System.out.print("_");
                else if (sessionSeats[k][j] instanceof ChildReservation)
                    System.out.print("C");
                else if (sessionSeats[k][j] instanceof ElderlyReservation)
                    System.out.print("E");
                else if (sessionSeats[k][j] instanceof AdultReservation)
                    System.out.print("A");
                
                System.out.print("|");
            }
            System.out.println();
        }
    }
    
    @Override
    public String toString()
    {
        return movieName + "(" + rating + ") - " + sessionTime;
    }

    @Override
    public int compareTo(MovieSession o)
    {
        if (sessionTime.equals(o.sessionTime))
            return movieName.compareTo(o.movieName);
        return sessionTime.compareTo(o.sessionTime);
    }
    
    public static void main(String[] args)
    {
        ArrayList<MovieSession> movieList = new ArrayList<MovieSession>();
        movieList.add(new MovieSession("Incredible 2", 'G', new Time(14, 30, 0)));
        movieList.add(new MovieSession("The Meg", 'M', new Time(12, 0, 0)));
        movieList.add(new MovieSession("Luis and the Aliens", 'G', new Time(10, 30, 0)));
        movieList.add(new MovieSession("Slenderman", 'M', new Time(10, 0, 0)));
        movieList.add(new MovieSession("The Meg", 'M', new Time(14, 30, 0)));
        movieList.add(new MovieSession("The Happytime Murders", 'R', new Time(10, 0, 0)));
        movieList.add(new MovieSession("The Equalizer 2", 'R', new Time(13, 0, 0)));
        Collections.sort(movieList);
        
        System.out.println("Movies in sorted order:");
        for (MovieSession m : movieList)
            System.out.println(m);
        
        ArrayList<SeatReservation> reservations = new ArrayList<SeatReservation>();
        reservations.add(new ChildReservation('C', 2));
        reservations.add(new ChildReservation('D', 2));
        reservations.add(new ChildReservation('E', 2));
        
        boolean isBooked = false;
        
        //Slenderman - M rating with only children. Seats are NOT booked.
        //3 children
        isBooked = movieList.get(0).applyBookings(reservations);
        System.out.println("\nScenario 1: The seats are NOT booked: M-rating movie with only children");
        System.out.println("bookings: " + isBooked);
        movieList.get(0).printSeats();
        
        //Slenderman - M rating with children with an adult. Seats are booked.
        //2 children, 1 adult
        reservations.set(2, new AdultReservation('E', 2)); //bookings with an adult
        isBooked = movieList.get(0).applyBookings(reservations);
        System.out.println("\nScenario 2: The seats are booked: M-rating movie with an adult");
        System.out.println("bookings: " + isBooked);
        movieList.get(0).printSeats();
        
        //Slenderman - M rating with children with an elderly. Seats are NOT booked as seats were taken.
        //2 children, 1 elderly
        reservations.set(2, new ElderlyReservation('E', 2)); //bookings with an elder and previously booked seats.
        isBooked = movieList.get(0).applyBookings(reservations);
        System.out.println("\nScenario 3: The seats NOT booked: M-rating movie - the seats were taken in scenario 2");
        System.out.println("bookings: " + isBooked);
        movieList.get(0).printSeats();
        
        //Slenderman - M rating with children with an elderly with different seatings. Seats are booked.
        //2 children, 1 elderly
        reservations.set(0, new ChildReservation('C', 3));
        reservations.set(1, new ChildReservation('D', 3));
        reservations.set(2, new ElderlyReservation('E', 3)); //bookings with an elderly
        isBooked = movieList.get(0).applyBookings(reservations);
        System.out.println("\nScenario 4: The seats are booked: M-rating movie with an elderly in different seatings");
        System.out.println("bookings: " + isBooked);
        movieList.get(0).printSeats();
        
        //Slenderman - M rating with children with an elderly with different seatings (horizontal). Seats are booked.
        //1 children, 1 adult, 1 elderly
        reservations.set(0, new ChildReservation('G', 2));
        reservations.set(1, new AdultReservation('G', 3));
        reservations.set(2, new ElderlyReservation('G', 4));
        isBooked = movieList.get(0).applyBookings(reservations);
        System.out.println("\nScenario 5: The seats are booked: M-rating movie with an elderly in different seatings (horizontal)");
        System.out.println("bookings: " + isBooked);
        movieList.get(0).printSeats();
        
        //Slenderman - M rating with children with an elderly (horizontal). Seats are NOT booked.
        //1 children, 1 adult, 1 elderly
        reservations.set(0, new ChildReservation('G', 0));
        reservations.set(1, new AdultReservation('G', 1));
        reservations.set(2, new ElderlyReservation('G', 2)); //previously booked seats.
        isBooked = movieList.get(0).applyBookings(reservations);
        System.out.println("\nScenario 6: The seats are NOT booked: M-rating movie - the seats were taken in scenario 5 (horizontal)");
        System.out.println("bookings: " + isBooked);
        movieList.get(0).printSeats();
        
        //The Happytime Murders - R rating with only children. Seats are NOT booked.
        //3 children
        reservations.set(1, new ChildReservation('G', 1));
        reservations.set(2, new ChildReservation('G', 2));
        isBooked = movieList.get(1).applyBookings(reservations);
        System.out.println("\nScenario 7: The seats are NOT booked: R-rating movie with only children");
        System.out.println("bookings: " + isBooked);
        movieList.get(1).printSeats();
        
        //The Happytime Murders - R rating with children + adult. Seats are NOT booked.
        //2 children, 1 adult
        reservations.set(2, new AdultReservation('G', 2)); //bookings with an adult
        isBooked = movieList.get(1).applyBookings(reservations);
        System.out.println("\nScenario 8: The seats are NOT booked: R-rating movie with children and adult");
        System.out.println("bookings: " + isBooked);
        movieList.get(1).printSeats();
        
        //The Happytime Murders - R rating with only adult. Seats are booked.
        //3 adults
        reservations.set(0, new AdultReservation('G', 0));
        reservations.set(1, new AdultReservation('G', 1));
        isBooked = movieList.get(1).applyBookings(reservations);
        System.out.println("\nScenario 9: The seats are booked: R-rating movie with only adult");
        System.out.println("bookings: " + isBooked);
        movieList.get(1).printSeats();
        
        //The Happytime Murders - R rating with only elderly - random seatings. Seats are booked.
        //3 elderly
        reservations.set(0, new ElderlyReservation('A', 2));
        reservations.set(1, new ElderlyReservation('H', 3));
        reservations.set(2, new ElderlyReservation('D', 4));
        isBooked = movieList.get(1).applyBookings(reservations);
        System.out.println("\nScenario 10: The seats are booked: R-rating movie with only elderly - random seatings");
        System.out.println("bookings: " + isBooked);
        movieList.get(1).printSeats();
        
        //Luis and the Aliens - G rating with only children - random seatings. Seats are booked.
        //3 children
        reservations.set(0, new ChildReservation('A', 2));
        reservations.set(1, new ChildReservation('H', 3));
        reservations.set(2, new ChildReservation('D', 4));
        isBooked = movieList.get(2).applyBookings(reservations);
        System.out.println("\nScenario 11: The seats are booked: G-rating movie with only children - random seatings");
        System.out.println("bookings: " + isBooked);
        movieList.get(2).printSeats();
    }
}
