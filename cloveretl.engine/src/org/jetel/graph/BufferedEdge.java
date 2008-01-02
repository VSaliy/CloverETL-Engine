/*
*    jETeL/Clover - Java based ETL application framework.
*    Copyright (C) 2002-04  David Pavlis <david_pavlis@hotmail.com>
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
*/

// FILE: c:/projects/jetel/org/jetel/graph/Edge.java

package org.jetel.graph;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.jetel.data.DataRecord;
import org.jetel.data.Defaults;
import org.jetel.data.DynamicRecordBuffer;

/**
 * A class that represents Edge - data connection between two NODEs.<br>
 * This EDGE buffers data in-memory and if this buffer is exhausted then
 * on disk to allow unlimited buffering for writer.<br>
 * It internally allocates two buffers (for reading,writing) of 
 * <code>BUFFERED_EDGE_INTERNAL_BUFFER_SIZE</code>.
 *
 * @author     D.Pavlis
 * @see        org.jetel.graph.InputPort
 * @see        org.jetel.graph.OutputPort
 * @revision   $Revision$
 */
public class BufferedEdge extends EdgeBase {

    protected int outputRecordCounter;
    protected int inputRecordCounter;
    protected long byteCounter;
    protected int internalBufferSize;
    protected boolean isEOF;
    
    protected DynamicRecordBuffer recordBuffer;
    
	public BufferedEdge(Edge proxy) {
	    this(proxy,Defaults.Graph.BUFFERED_EDGE_INTERNAL_BUFFER_SIZE);
	}

	/**
	 * Constructor for the Edge object
	 * 
	 * @param proxy Edge proxy
	 * @param internalBufferSize	how much space allocate for internal buffer which is
	 * used for storing/buffering data records
	 */
	public BufferedEdge(Edge proxy,int internalBufferSize) {
	    super(proxy);
	    this.internalBufferSize=internalBufferSize;
	}
	
	
	public int getOutputRecordCounter() {
		return outputRecordCounter;
	}

    public int getInputRecordCounter() {
        return inputRecordCounter;
    }

    public long getOutputByteCounter(){
        return byteCounter;
    }

    public long getInputByteCounter(){
        return byteCounter;
    }

    public int getBufferedRecords(){
        return recordBuffer.getBufferedRecords();
    }
    
	/**
	 *  Description of the Method
	 *
	 * @exception  IOException  Description of Exception
	 * @since                   April 2, 2002
	 */
	public void init() throws IOException {
		// initialize & open the data pipe
		// we are ready to supply data
        recordBuffer=new DynamicRecordBuffer(internalBufferSize);
        recordBuffer.init();
        outputRecordCounter = 0;
        inputRecordCounter = 0;
        byteCounter=0;
        isEOF = false;
	}

	@Override
	public void reset() {
        recordBuffer.clear();
        outputRecordCounter = 0;
        inputRecordCounter = 0;
        byteCounter=0;
        isEOF = false;
	}
	
	// Operations
	/**
	 * An operation that does read one DataRecord from Edge
	 *
	 * @param  record                    Description of Parameter
	 * @return                           Description of the Returned Value
	 * @exception  IOException           Description of Exception
	 * @exception  InterruptedException  Description of Exception
	 * @since                            April 2, 2002
	 */

	public DataRecord readRecord(DataRecord record) throws IOException, InterruptedException {
        DataRecord ret = recordBuffer.readRecord(record);
        if(ret != null) inputRecordCounter++;
        return ret;
    }


	/**
	 *  Description of the Method
	 *
	 * @param  record                    Description of Parameter
	 * @return                           True if success, otherwise false (if no more data)
	 * @exception  IOException           Description of Exception
	 * @exception  InterruptedException  Description of Exception
	 * @since                            August 13, 2002
	 */
	public boolean readRecordDirect(ByteBuffer record) throws IOException, InterruptedException {
        boolean ret = recordBuffer.readRecord(record);
        if(ret) inputRecordCounter++;
        return ret;
    }


	/**
	 * An operation that does send one DataRecord through the Edge/PIPE
	 *
	 * @param  record                    Description of Parameter
	 * @exception  IOException           Description of Exception
	 * @exception  InterruptedException  Description of Exception
	 * @since                            April 2, 2002
	 */
	public void writeRecord(DataRecord record) throws IOException, InterruptedException {
        byteCounter += recordBuffer.writeRecord(record);
        outputRecordCounter++;
	}


	/**
	 *  Description of the Method
	 *
	 * @param  record                    Description of Parameter
	 * @exception  IOException           Description of Exception
	 * @exception  InterruptedException  Description of Exception
	 * @since                            August 13, 2002
	 */
	public void writeRecordDirect(ByteBuffer record) throws IOException, InterruptedException {
	    byteCounter += recordBuffer.writeRecord(record);
        outputRecordCounter++;
    }

	/**
	 *  Description of the Method
	 *
	 * @since    April 2, 2002
	 */
	public void eof() {
        try{
            recordBuffer.setEOF();
            isEOF = true;
        }catch(IOException ex){
            throw new RuntimeException("Error when closing BufferedEdge: "+ex.getMessage(),ex);
        }
	}

    @Override
    public boolean isEOF() {
        return isEOF;
    }
    
    @Override
    public void free() {
        try {
            recordBuffer.close();
        } catch (IOException e) {
            //do nothing
        }
    }
    
	public boolean hasData(){
		return (recordBuffer.hasData());
	}

    /**
     * @param internalBufferSize The internalBufferSize to set.
     */
    public void setInternalBufferSize(int internalBufferSize) {
        if (internalBufferSize>Defaults.Graph.BUFFERED_EDGE_INTERNAL_BUFFER_SIZE)
            this.internalBufferSize = internalBufferSize;
    }
    
}
/*
 *  end class DirectEdge
 */

