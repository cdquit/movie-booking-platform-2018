/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handin;

/**
 *
 * @author jmm4115
 */
public abstract class SeatReservation {
    private char row;
    private int col;
    protected boolean complementary;
    
    public SeatReservation(char row, int col)
    {
        this.row = row;
        this.col = col;
        complementary = false;
    }
    
    public abstract float getTicketPrice();
    
    public void setComplementary(boolean complementary)
    {
        this.complementary = complementary;
    }
    
    public char getRow()
    {
        return row;
    }
    
    public int getCol()
    {
        return col;
    }
}
