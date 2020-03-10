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
public class AdultReservation extends SeatReservation{
    public AdultReservation(char row, int col)
    {
        super(row, col);
    }
    
    @Override
    public float getTicketPrice()
    {
        if (complementary)
            return 0.0f;
        return 12.5f;
    }    
}
