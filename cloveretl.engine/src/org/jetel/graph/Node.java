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
// FILE: c:/projects/jetel/org/jetel/graph/Node.java

package org.jetel.graph;
import java.util.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.jetel.graph.InputPort;
import org.jetel.graph.OutputPort;
import org.jetel.data.DataRecord;
import org.jetel.exception.ComponentNotReadyException;

/**
 *  A class that represents atomic transformation task. It is a base class for
 *  all kinds of transformation components.
 *
 *@author      D.Pavlis
 *@created     January 31, 2003
 *@since       April 2, 2002
 *@see         org.jetel.component
 *@revision    $Revision$
 */
public abstract class Node extends Thread {

	protected TransformationGraph graph;
	protected String id;

	protected TreeMap outPorts;
	protected TreeMap inPorts;

	protected List outPortList;
	protected OutputPort logPort;

	protected volatile boolean runIt = true;

	protected int resultCode;
	protected String resultMsg;
	protected int phase = 0;// default phase is 0

	/**
	 *  Various PORT kinds identifiers
	 *
	 *@since    August 13, 2002
	 */
	public final static char OUTPUT_PORT = 'O';
	/**  Description of the Field */
	public final static char INPUT_PORT = 'I';
	/**  Description of the Field */
	public final static char LOG_PORT = 'L';

	/**
	 *  RESULT CODES DEFINITION
	 *
	 *@since    August 13, 2002
	 */
	public final static int RESULT_RUNNING = 0;
	/**  Description of the Field */
	public final static int RESULT_OK = 1;
	/**  Description of the Field */
	public final static int RESULT_ERROR = 2;
	/**  Description of the Field */
	public final static int RESULT_ABORTED = 3;
	/**  Description of the Field */
	public final static int RESULT_FATAL_ERROR = -1;


	/**
	 *  Description of the Method
	 *
	 *@param  id  Description of Parameter
	 *@since      April 4, 2002
	 */
	public Node(String id) {
		super(id);
		this.id = new String(id);
		outPorts = new TreeMap();
		inPorts = new TreeMap();
		logPort = null;
		setDaemon(true);
		// this thread is daemon - won't live if main thread ends
		phase = 0;
	}


	/**
	 *  Sets the Graph attribute of the Node object
	 *
	 *@param  graph  The new Graph value
	 *@since         April 5, 2002
	 */
	public void setGraph(TransformationGraph graph) {
		this.graph = graph;
	}


	/**
	 *  Sets the EOF attribute of the Node object
	 *
	 *@param  portNum  The new EOF value
	 *@since           April 18, 2002
	 */
	public void setEOF(int portNum) {
		try {
			((OutputPort) outPorts.get(new Integer(portNum))).close();
		} catch (IndexOutOfBoundsException ex) {
			ex.printStackTrace();
		}
	}


	/**
	 *  Returns brief message describing current status
	 *
	 *@return    The Status value
	 *@since     April 2, 2002
	 */
	public String getStatus() {
		switch (resultCode) {
			case RESULT_RUNNING:
				return "RUNNING";
			case RESULT_OK:
				return "OK";
			case RESULT_ERROR:
				return "ERROR";
			case RESULT_FATAL_ERROR:
				return "FATAL_ERROR";
			case RESULT_ABORTED:
				return "ABORTED";
			default:
				return "!invalid status!";
		}
	}



	/**
	 *  Gets the Type attribute of the Node object
	 *
	 *@return    The Type value
	 *@since     April 4, 2002
	 */
	public static String getType() {
		return null;
	}


	/**
	 *  Gets the ID attribute of the Node object
	 *
	 *@return    The ID value
	 *@since     April 2, 2002
	 */
	public String getID() {
		return id;
	}


	/**
	 *  Gets the Leaf attribute of the Node object
	 *
	 *@return    The Leaf value
	 *@since     April 4, 2002
	 */
	public boolean isLeaf() {
		if (outPorts.size() == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isPhaseLeaf(){
		Iterator iterator=getOutPorts().iterator();
		while(iterator.hasNext()){
			if (phase!=((OutputPort)(iterator.next())).getReader().getPhase())
				return true;
		}
		return false;
	}


	/**
	 *  Gets the Root attribute of the Node object
	 *
	 *@return    The Root value
	 *@since     April 4, 2002
	 */
	public boolean isRoot() {
		if (inPorts.size() == 0) {
			return true;
		} else {
			return false;
		}
	}


	/**
	 *  Sets the processing phase of the Node object.<br>
	 *  Default is 0 (ZERO).
	 *
	 *@param  phase  The new phase number
	 */
	public void setPhase(int phase) {
		this.phase = phase;
	}


	/**
	 *  Gets the processing phase of the Node object
	 *
	 *@return    The phase value
	 */
	public int getPhase() {
		return phase;
	}


	/**
	 *  Check the Node configuration - defined input&output ports and if all parameters defined are meaningful.<br>
	 *  
	 *
	 *@return    True if Node configuration is OK, otherwise False
	 */
	public abstract boolean checkConfig();


	/**  Frees all internally allocated resources - such as buffers, etc. */
	public void freeResources(){
	}


	/**
	 *  Gets the OutPorts attribute of the Node object
	 *
	 *@return    Collection of OutPorts
	 *@since     April 18, 2002
	 */
	public Collection getOutPorts() {
		return outPorts.values();
	}


	/**
	 *  Gets the InPorts attribute of the Node object
	 *
	 *@return    Collection of InPorts
	 *@since     April 18, 2002
	 */
	public Collection getInPorts() {
		return inPorts.values();
	}


	/**
	 *  Gets the number of records passed through specified port type and number
	 *
	 *@param  portType  Port type (IN, OUT, LOG)
	 *@param  portNum   port number (0...)
	 *@return           The RecordCount value
	 *@since            May 17, 2002
	 */
	public int getRecordCount(char portType, int portNum) {
		int count;
		Integer port = new Integer(portNum);
		try {
			switch (portType) {
				case OUTPUT_PORT:
					count = ((OutputPort) outPorts.get(port)).getRecordCounter();
					break;
				case INPUT_PORT:
					count = ((InputPort) inPorts.get(port)).getRecordCounter();
					break;
				case LOG_PORT:
					if (logPort != null) {
						count = logPort.getRecordCounter();
					} else {
						count = -1;
					}
					break;
				default:
					count = -1;
			}
		} catch (Exception ex) {
			count = -1;
		}

		return count;
	}


	/**
	 *  Gets the result code of finished Node.<br>
	 *  Zero (0) indicates Success, any other number indicates error.
	 *
	 *@return    The ResultCode value
	 *@since     July 29, 2002
	 */
	public int getResultCode() {
		return resultCode;
	}


	/**
	 *  Gets the ResultMsg of finished Node.<br>
	 *  This message briefly describes what caused and error (if there was any).
	 *
	 *@return    The ResultMsg value
	 *@since     July 29, 2002
	 */
	public String getResultMsg() {
		return resultMsg;
	}



	// Operations
	/**
	 *  main executive method of Node
	 *
	 *@since    April 2, 2002
	 */
	public abstract void run();


	/**
	 *  Initialization of Node
	 *
	 *@exception  ComponentNotReadyException  Error when trying to initialize
	 *      Node/Component
	 *@since                                  April 2, 2002
	 */
	public abstract void init() throws ComponentNotReadyException;


	/**
	 *  Abort execution of Node - brutal force
	 *
	 *@since    April 4, 2002
	 */
	public void abort() {
		if (resultCode==RESULT_RUNNING){
			resultCode = RESULT_ABORTED;
			interrupt();
		}
	}


	/**
	 *  End execution of Node - let Node finish gracefully
	 *
	 *@since    April 4, 2002
	 */
	public void end() {
		runIt = false;
	}


	/**
	 *  An operation that adds port to list of all InputPorts
	 *
	 *@param  port   Port (Input connection) to be added
	 *@since         April 2, 2002
	 *@deprecated    Use the other method which takes 2 arguments (portNum, port)
	 */

	public void addInputPort(InputPort port) {
		Integer portNum;
		int keyVal;
		try {
			portNum = (Integer) inPorts.lastKey();
			keyVal = portNum.intValue() + 1;
		} catch (NoSuchElementException ex) {
			keyVal = 0;
		}
		inPorts.put(new Integer(keyVal), port);
		port.connectReader(this);
	}


	/**
	 *  An operation that adds port to list of all InputPorts
	 *
	 *@param  portNum  Number to be associated with this port
	 *@param  port     Port (Input connection) to be added
	 *@since           April 2, 2002
	 */
	public void addInputPort(int portNum, InputPort port) {
		inPorts.put(new Integer(portNum), port);
		port.connectReader(this);
	}


	/**
	 *  An operation that adds port to list of all OutputPorts
	 *
	 *@param  port   Port (Output connection) to be added
	 *@since         April 4, 2002
	 *@deprecated    Use the other method which takes 2 arguments (portNum, port)
	 */

	public void addOutputPort(OutputPort port) {
		Integer portNum;
		int keyVal;
		try {
			portNum = (Integer) inPorts.lastKey();
			keyVal = portNum.intValue() + 1;
		} catch (NoSuchElementException ex) {
			keyVal = 0;
		}
		outPorts.put(new Integer(keyVal), port);
		port.connectWriter(this);
		outPortList = null;
	}


	/**
	 *  An operation that adds port to list of all OutputPorts
	 *
	 *@param  portNum  Number to be associated with this port
	 *@param  port     The feature to be added to the OutputPort attribute
	 *@since           April 4, 2002
	 */
	public void addOutputPort(int portNum, OutputPort port) {
		outPorts.put(new Integer(portNum), port);
		port.connectWriter(this);
		outPortList = null;
	}


	/**
	 *  Gets the port which has associated the num specified
	 *
	 *@param  portNum  number associated with the port
	 *@return          The outputPort
	 */
	public OutputPort getOutputPort(int portNum) {
		return (OutputPort) outPorts.get(new Integer(portNum));
	}


	/**
	 *  Gets the port which has associated the num specified
	 *
	 *@param  portNum  portNum number associated with the port
	 *@return          The inputPort
	 */
	public InputPort getInputPort(int portNum) {
		return (InputPort) inPorts.get(new Integer(portNum));
	}


	/**
	 *  Adds a feature to the LogPort attribute of the Node object
	 *
	 *@param  port  The feature to be added to the LogPort attribute
	 *@since        April 4, 2002
	 */
	public void addLogPort(OutputPort port) {
		logPort = port;
		port.connectWriter(this);
	}


	/**
	 *  An operation that does removes/unregisteres por<br>
	 *  Not yet implemented.
	 *
	 *@param  _portNum   Description of Parameter
	 *@param  _portType  Description of Parameter
	 *@since             April 2, 2002
	 */
	public void deletePort(int _portNum, char _portType) {
	}


	/**
	 *  An operation that writes one record through specified output port
	 *
	 *@param  _portNum                  Description of Parameter
	 *@param  _record                   Description of Parameter
	 *@exception  IOException           Description of Exception
	 *@exception  InterruptedException  Description of Exception
	 *@since                            April 2, 2002
	 */
	public void writeRecord(int _portNum, DataRecord _record) throws IOException, InterruptedException {
		try {
			((OutputPort) outPorts.get(new Integer(_portNum))).writeRecord(_record);
		} catch (IndexOutOfBoundsException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			throw ex;
		}
	}


	/**
	 *  An operation that reads one record through specified input port
	 *
	 *@param  _portNum                  Description of Parameter
	 *@param  record                    Description of Parameter
	 *@return                           Description of the Returned Value
	 *@exception  IOException           Description of Exception
	 *@exception  InterruptedException  Description of Exception
	 *@since                            April 2, 2002
	 */
	public DataRecord readRecord(int _portNum, DataRecord record) throws IOException, InterruptedException {
		try {
			record = ((InputPort) inPorts.get(new Integer(_portNum))).readRecord(record);
		} catch (IndexOutOfBoundsException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			throw ex;
		}
		return record;
	}


	/**
	 *  An operation that writes record to Log port
	 *
	 *@param  record                    Description of Parameter
	 *@exception  IOException           Description of Exception
	 *@exception  InterruptedException  Description of Exception
	 *@since                            April 2, 2002
	 */
	public void writeLogRecord(DataRecord record) throws IOException, InterruptedException {
		try {
			logPort.writeRecord(record);
		} catch (IndexOutOfBoundsException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			throw ex;
		}
	}


	/**
	 *  An operation that does ...
	 *
	 *@param  record                    Description of Parameter
	 *@exception  IOException           Description of Exception
	 *@exception  InterruptedException  Description of Exception
	 *@since                            April 2, 2002
	 */
	public void writeRecordBroadcast(DataRecord record) throws IOException, InterruptedException {
		if (outPortList == null) {
			outPortList = getPortList(getOutPorts());
		}
		Iterator iterator = outPortList.iterator();
		OutputPort port;

		while (iterator.hasNext()) {
			port = (OutputPort) iterator.next();

			try {
				port.writeRecord(record);
			} catch (IndexOutOfBoundsException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				throw ex;
			}
		}
	}


	/**
	 *  Converts the collection of ports into List (LinkedList)<br>
	 *  This is auxiliary method which "caches" list of ports for faster access
	 *  when we need to go through all ports sequentially. Namely in
	 *  RecordBroadcast situations
	 *
	 *@param  ports  Collection of Ports
	 *@return        List (LinkedList) of ports
	 */
	private List getPortList(Collection ports) {
		List portList = new LinkedList();
		portList.addAll(ports);
		return portList;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  recordBuffer              Description of Parameter
	 *@exception  IOException           Description of Exception
	 *@exception  InterruptedException  Description of Exception
	 *@since                            August 13, 2002
	 */
	public void writeRecordBroadcastDirect(ByteBuffer recordBuffer) throws IOException, InterruptedException {
		if (outPortList == null) {
			outPortList = getPortList(getOutPorts());
		}
		Iterator iterator = outPortList.iterator();
		OutputPortDirect port;

		while (iterator.hasNext()) {
			port = (OutputPortDirect) iterator.next();

			try {
				port.writeRecordDirect(recordBuffer);
				recordBuffer.rewind();
			} catch (IndexOutOfBoundsException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				throw ex;
			}
		}
	}


	/**
	 *  Closes all output ports.
	 *
	 *@since    April 11, 2002
	 */
	public void closeAllOutputPorts() {
		Iterator iterator = getOutPorts().iterator();
		OutputPort port;

		while (iterator.hasNext()) {
			port = (OutputPort) iterator.next();

			try {
				port.close();
			} catch (IndexOutOfBoundsException ex) {
				ex.printStackTrace();
			}
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@since    April 18, 2002
	 */
	public void broadcastEOF() {
		Iterator iterator = getOutPorts().iterator();
		OutputPort port;

		while (iterator.hasNext()) {
			port = (OutputPort) iterator.next();

			try {
				port.close();
			} catch (IndexOutOfBoundsException ex) {
				ex.printStackTrace();
			}
		}
	}


	/**
	 *  Closes specified output port. Will cause IOException in Node connected to
	 *  the other side of port
	 *
	 *@param  portNum  Which port to close
	 *@since           April 11, 2002
	 */
	public void closeOutputPort(int portNum) {
		try {
			((OutputPort) outPorts.get(new Integer(portNum))).close();
		} catch (IndexOutOfBoundsException ex) {
			ex.printStackTrace();
		}
	}


	/**
	 *  Compares this Node to specified Object
	 *
	 *@param  obj  Node to compare with
	 *@return      True if obj represents node with the same ID
	 *@since       April 18, 2002
	 */
	public boolean equals(Object obj) {
		if (this.id.equals(((Node) obj).getID())) {
			return true;
		} else {
			return false;
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Returned Value
	 *@since     May 21, 2002
	 */
	public abstract org.w3c.dom.Node toXML();


	/**
	 *  Description of the Method
	 *
	 *@param  nodeXML  Description of Parameter
	 *@return          Description of the Returned Value
	 *@since           May 21, 2002
	 */
	public static Node fromXML(org.w3c.dom.Node nodeXML) {
		return null;
	}
}
/*
 *  end class Node
 */

