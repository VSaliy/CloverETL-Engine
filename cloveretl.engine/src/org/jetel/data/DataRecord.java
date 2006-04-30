/*
 *  jETeL/Clover - Java based ETL application framework.
 *  Copyright (C) 2002-04  David Pavlis <david_pavlis@hotmail.com>
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
// FILE: c:/projects/jetel/org/jetel/data/DataRecord.java

package org.jetel.data;
import java.io.Serializable;
import java.nio.ByteBuffer;

import org.jetel.metadata.DataFieldMetadata;
import org.jetel.metadata.DataRecordMetadata;

/**
 *  A class that represents one data record Fields are not deleted & created all
 *  the time. Instead, they are created only once and updated when it is needed
 *  When we need to send record through the EDGE, we just serialize it (it isn't
 *  standard version of serializing)
 *
 * @author      D.Pavlis
 * @since       March 26, 2002
 * @revision    $Revision$
 * @created     18. kv�ten 2003
 * @see         org.jetel.metadata.DataRecordMetadata
 */
public class DataRecord implements Serializable, Comparable {

	/**
	 * @since
	 */
	private transient String codeClassName;

	/**
	 * @since
	 */
	private DataField fields[];

	// Associations
	/**
	 * @since
	 */
	private transient DataRecordMetadata metadata;


	/**
	 * Create new instance of DataRecord based on specified metadata (
	 * how many fields, what field types, etc.)
	 * 
	 * @param _metadata
	 */
	public DataRecord(DataRecordMetadata _metadata) {
		this.metadata = _metadata;
		fields = new DataField[metadata.getNumFields()];
	}

	/**
	 * Private constructor used when clonning/copying DataRecord objects.
	 * 
	 * @param _metadata metadata describing this record
	 * @param numFields number of fields this record should contain
	 */
	private DataRecord(DataRecordMetadata _metadata, int numFields){
	    this.metadata = _metadata;
	    fields = new DataField[numFields];
	}

	
	
	/**
	 * Creates deep copy of existing record (field by field).
	 * 
	 * @return new DataRecord
	 */
	public DataRecord duplicate(){
	    DataRecord newRec=new DataRecord(metadata,fields.length);
	    for (int i=0;i<fields.length;i++){
	        newRec.fields[i]=fields[i].duplicate();
	    }
	    return newRec;
	}
	
	/**
	 * Set fields by copying the fields from the record passed as argument.
	 * Does assume that both records have the same structure - i.e. metadata.
	 * @param fromRecord DataRecord from which to get fields' values
	 */
	public void copyFrom(DataRecord fromRecord){
	    for (int i=0;i<fields.length;i++){
	        this.fields[i].copyFrom(fromRecord.fields[i]);
	    } 
	}
	
	
	/**
	 *  Set fields by copying the fields from the record passed as argument.
	 * Can handle situation when records are not exactly the same.
	 *
	 * @param  _record  Record from which fields are copied
	 * @since
	 */

	public void copyFieldsByPosition(DataRecord _record) {
		DataRecordMetadata sourceMetadata = _record.getMetadata();
		DataField sourceField;
		DataField targetField;
		//DataFieldMetadata fieldMetadata;
		int sourceLength = sourceMetadata.getNumFields();
		int targetLength = this.metadata.getNumFields();
		int copyLength;
		if (sourceLength < targetLength) {
			copyLength = sourceLength;
		} else {
			copyLength = targetLength;
		}

		for (int i = 0; i < copyLength; i++) {

			//fieldMetadata = metadata.getField(i);
			sourceField = _record.getField(i);
			targetField = this.getField(i);
			if (sourceField.getType() == targetField.getType()) {
				targetField.setValue(sourceField.getValue());
			} else {
				targetField.setToDefaultValue();
			}
		}
	}




	/**
	 *  Deletes/removes specified field. The field's internal reference
	 * is set to NULL, so it can be garbage collected.
	 *
	 * @param  _fieldNum  Description of Parameter
	 * @since
	 */
	public void delField(int _fieldNum) {
		try {
			fields[_fieldNum] = null;
		} catch (IndexOutOfBoundsException e) {
		}
	}


	/**
	 *  Refreshes this record's content from ByteBuffer. 
	 *
	 * @param  buffer  ByteBuffer from which this record's fields should be read
	 * @since          April 23, 2002
	 */
	public void deserialize(ByteBuffer buffer) {
		for (int i = 0; i < fields.length; i++) {
			fields[i].deserialize(buffer);
		}
	}


	/**
	 *  Test two DataRecords for equality. Records must have the same metadata (be
	 * created using the same metadata object) and their field values must be equal.
	 *
	 * @param  obj  DataRecord to compare with
	 * @return      True if they equals, false otherwise
	 * @since       April 23, 2002
	 */
	public boolean equals(Object obj) {
		if (this==obj) return true;
	    
	    /*
         * first test that both records have the same structure i.e. point to
         * the same metadata
         */
        if (obj instanceof DataRecord) {
            if (metadata != ((DataRecord) obj).getMetadata()) {
                return false;
            }
            // check field by field that they are the same
            for (int i = 0; i < fields.length; i++) {
                if (!fields[i].equals(((DataRecord) obj).getField(i))) {
                    return false;
                }
            }
            return true;
        }else{
            return false;
        }
    }

	
	/**
	 * Compares two DataRecords. Records must have the same metadata (be
	 * created using the same metadata object). Their field values are compare one by one,
	 * the first non-equal pair of fields denotes the overall comparison result.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object obj){
	    if (this==obj) return 0;
	    
	    if (obj instanceof DataRecord) {
            if (metadata != ((DataRecord) obj).getMetadata()) {
                throw new RuntimeException("Can't compare - records have different metadata objects assigned!");
            }
            int cmp;
            // check field by field that they are the same
            for (int i = 0; i < fields.length; i++) {
                cmp=fields[i].compareTo(((DataRecord) obj).getField(i));
                if (cmp!=0) {
                    return cmp;
                }
            }
            return 0;
        }else{
            throw new ClassCastException("Can't compare DataRecord with "+obj.getClass().getName());
        }
	}

	/**
	 *  Gets the codeClassName attribute of the DataRecord object
	 *
	 * @return    The codeClassName value
	 */
	public String getCodeClassName() {
		return codeClassName;
	}


	/**
	 *  An operation that does ...
	 *
	 * @param  _fieldNum  Description of Parameter
	 * @return            The Field value
	 * @since
	 */
	public DataField getField(int _fieldNum) {
		try {
			return fields[_fieldNum];
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}


	/**
	 *  An operation that does ...
	 *
	 * @param  _name  Description of Parameter
	 * @return        The Field value
	 * @since
	 */
	public DataField getField(String _name) {
		try {
			return fields[metadata.getFieldPosition(_name)];
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}


	/**
	 *  An attribute that represents ... An operation that does ...
	 *
	 * @return    The Metadata value
	 * @since
	 */
	public DataRecordMetadata getMetadata() {
		return metadata;
	}


	/**
	 *  An operation that does ...
	 *
	 * @return    The NumFields value
	 * @since
	 */
	public int getNumFields() {
		return metadata.getNumFields();
	}


	/**
	 *  Description of the Method
	 *
	 * @since    April 5, 2002
	 */
	public void init() {
		DataFieldMetadata fieldMetadata;
		// create appropriate data fields based on metadata supplied
		for (int i = 0; i < metadata.getNumFields(); i++) {
			fieldMetadata = metadata.getField(i);
			fields[i] =
					DataFieldFactory.createDataField(
					fieldMetadata.getType(),
					fieldMetadata);
		}
	}


	/**
	 *  Serializes this record's content into ByteBuffer.
	 *
	 * @param  buffer  ByteBuffer into which the individual fields of this record should be put
	 * @since          April 23, 2002
	 */
	public void serialize(ByteBuffer buffer) {
		for (int i = 0; i < fields.length; i++) {
			fields[i].serialize(buffer);
		}
	}


	/**
	 *  Sets the codeClassName attribute of the DataRecord object
	 *
	 * @param  codeClassName  The new codeClassName value
	 */
	public void setCodeClassName(String codeClassName) {
		this.codeClassName = codeClassName;
	}


	/**
	 *  Assigns new metadata to this DataRecord. If the new
	 * metadata is not equal to the current metadata, the record's
	 * content is recreated from scratch. After calling this
	 * method, record is uninitialized and init() method should
	 * be called prior any attempt to manipulate this record's content.
	 *
	 * @param  metadata  The new Metadata value
	 * @since            April 5, 2002
	 */
	public void setMetadata(DataRecordMetadata metadata) {
		if (this.metadata != metadata){
		    this.metadata=metadata;
		    fields = new DataField[metadata.getNumFields()];
		}
	}


	/**
	 *  An operation that sets value of all data fields to their default value.
	 */
	public void setToDefaultValue() {
		for (int i = 0; i < fields.length; i++) {
			fields[i].setToDefaultValue();
		}
	}


	/**
	 *  An operation that sets value of the selected data field to its default
	 *  value.
	 *
	 * @param  _fieldNum  Ordinal number of the field which should be set to default
	 */
	public void setToDefaultValue(int _fieldNum) {
		fields[_fieldNum].setToDefaultValue();
	}


	/**
	 *  Creates textual representation of record's content based on values of individual
	 *  fields
	 *
	 * @return    Description of the Return Value
	 */
	public String toString() {
		StringBuffer str = new StringBuffer(80);
		for (int i = 0; i < fields.length; i++) {
			str.append("#").append(i).append("|");
			str.append(fields[i].getMetadata().getName()).append("|");
			str.append(fields[i].getType());
			str.append("->");
			str.append(fields[i].toString());
			str.append("\n");
		}
		return str.toString();
	}


	/**
	 *  Gets the actual size of record (in bytes).<br>
	 *  <i>How many bytes are required for record serialization</i>
	 *
	 * @return    The size value
	 */
	public int getSizeSerialized() {
		int size=0;
		for (int i = 0; i < fields.length; size+=fields[i++].getSizeSerialized());
		return size;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode(){
	    int hash=17;
	    for (int i=0;i<fields.length;i++){
	        hash=37*hash+fields[i].hashCode();
	    }
	    return hash;
	}
}
/*
 *  end class DataRecord
 */

