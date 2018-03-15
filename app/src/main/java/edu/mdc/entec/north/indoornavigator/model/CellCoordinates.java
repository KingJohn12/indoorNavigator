package edu.mdc.entec.north.indoornavigator.model;


public class CellCoordinates {
    private int x;
    private int y;
    //private int roomID;


    public CellCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
        //roomID = 0;
    }



    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /*public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }
*/
    @Override
    public String toString() {
        return "Cell{" +
                "x=" + x +
                ", y=" + y +
                //", roomID='" + roomID + '\'' +
                '}';
    }



    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        //result = 31 * result + roomID;
        return result;
    }
}

