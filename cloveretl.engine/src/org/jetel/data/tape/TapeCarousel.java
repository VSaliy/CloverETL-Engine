/*
 *    jETeL/Clover - Java based ETL application framework.
 *    Copyright (C) 2002-05  David Pavlis <david_pavlis@hotmail.com> and others.
 *    
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *    
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU    
 *    Lesser General Public License for more details.
 *    
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Created on 3.2.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.jetel.data.tape;

import java.io.IOException;

/**
 * @author david
 * @since  3.2.2005
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TapeCarousel {

    private DataRecordTape[] tapeArray;
    private int currTape;
    
    public TapeCarousel(int numTapes){
        tapeArray=new DataRecordTape[numTapes];
        currTape=0;
    }
    
    public void open() throws IOException{
        for(int i=0;i<tapeArray.length;i++){
            tapeArray[i]=new DataRecordTape();
            tapeArray[i].open();
        }
    }

    public void flush(){
        for (int i=0;i<tapeArray.length;i++){
            try{
                tapeArray[i].flush(true);
            }catch(IOException ex){
                // ignore for now
            }
        }
    }
    
    public void free(){
        for (int i=0;i<tapeArray.length;i++){
           try{
               tapeArray[i].close();
           }catch(IOException ex){
               throw new RuntimeException("IOException when closing tape in carousel: "+ex);
           }
        }
    }
 
    public DataRecordTape getNextTape(){
        if (currTape<tapeArray.length-1){
            return tapeArray[++currTape];
        }else{
            return null;
        }
    }
    
    public DataRecordTape getFirstTape(){
        currTape=0;
        return tapeArray[0];
    }
    
    public DataRecordTape getTape(int order){
            currTape=order;
            return tapeArray[currTape];
    }
    
    public int numTapes(){
        return tapeArray.length;
    }
    
    public void rewind(){
        for(int i=0;i<tapeArray.length;i++){
            tapeArray[i].rewind();
        }
    }
    
    public void clear(){
        for(int i=0;i<tapeArray.length;i++){
            try{
                tapeArray[i].clear();
            }catch(IOException ex){
                throw new RuntimeException("IOException when cleaning tape in carousel: "+ex);
            }
        }
    }
}
