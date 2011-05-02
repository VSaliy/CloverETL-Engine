/*
 * jETeL/CloverETL - Java based ETL application framework.
 * Copyright (c) Javlin, a.s. (info@cloveretl.com)
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jetel.data;

import java.io.Serializable;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.List;

import org.jetel.exception.BadDataFormatException;
import org.jetel.exception.NullDataFormatException;
import org.jetel.metadata.DataFieldMetadata;
import org.jetel.util.string.StringUtils;

/**
 *  A class that represents data field (its value).
 *
 * @author      David Pavlis
 * @since       March 26, 2002
 * @revision    $Revision$
 * @see         OtherClasses
 */
@SuppressWarnings("serial")
public abstract class DataField implements Serializable, Comparable<Object> {

	/**
     * Reference to metadata object describing this field
     *  
	 * @since
	 */
	protected transient DataFieldMetadata metadata;

	/**
	 *  Does this field currently contain NULL value ? 
	 *
	 * @since    September 16, 2002
	 */
	protected boolean isNull;


	/**
	 *  Constructor
	 *
	 * @param  _metadata  Metadata describing field
	 * @since
	 */
	public DataField(DataFieldMetadata _metadata) {
		this.metadata = _metadata;
	}

	/**
     * Constructor
     * 
	 * @param _metadata Metadata describing field
	 * @param plain if true,create plain data field - no formatters,etc. 
     * will be created & assigned to field
	 */
	public DataField(DataFieldMetadata _metadata, boolean plain) {
        this.metadata = _metadata;
    }
    
    
	/**
	 * Creates deep copy of existing field. 
	 * 
	 * @return new Field (exact copy of current field)
	 */
	public abstract DataField duplicate();
	
	/**
	 * Sets current field's value from DataField passed as argument
	 * @param fieldFrom DataField from which to get the value
     * @deprecated use setValue(DataField) instead
	 */
	public void copyFrom(DataField fieldFrom) {
	    setValue(fieldFrom.getValue());   
    }
	
	/**
	 *  An operation that sets value of the data field.
     *  The given object is never persisted in this data field.
     *  The method implementation should do deep copy of the given object everytime.
	 *
	 * @param  _value  The new Value value
	 * @since
	 */
	public abstract void setValue(Object _value);
	
	/**
	 * An operation that sets value of the data field from another data field.
	 * 
	 * @param _value data field for getting value
	 */
	public void setValue(DataField fromField){
		if (fromField != null) {
            setValue(fromField.getValue());   
		}else{
			setNull(true);
		}
	}


	/**
	 *  An operation that sets value of the data field to default value.
	 *  If default value is not preset, tries to set field value to null.
	 * @exception  BadDataFormatException  Description of the Exception
	 */
	public void setToDefaultValue() {
		try {
            Object val;
            if((val = metadata.getDefaultValue()) != null) {
                setValue(val);
                return;
            }
			if (metadata.getDefaultValueStr() != null) {
				fromString(StringUtils.stringToSpecChar(metadata.getDefaultValueStr()));
				if (isNull()) {
					throw new NullDataFormatException("Null cannot be a default value (field '" + metadata.getName() + "').");
				}
				metadata.setDefaultValue(getValueDuplicate());
				return;
			}
			if (metadata.isNullable()) {
				setNull(true);
			}else{
				throw new NullDataFormatException(metadata.getName() + 
						" has not dafault value defined and is not nullable!");
			}
		} catch (Exception ex) {
			// here, the only reason to fail is bad DefaultValue
			throw new BadDataFormatException(metadata.getName() + " has incorrect default value", metadata.getDefaultValueStr());
		}
	}

	/**
	 *  Sets the Null value indicator/status. If passed-in value
	 * is True, then the field's internal value is set to NULL. The nullability
	 * of the field is checked and if the NULL value is not permitted, the
	 * BadDataFormatException is thrown.
	 *
	 * @param  isNull  The new Null value
	 * @since          September 16, 2002
	 */
	public void setNull(boolean isNull) {
		if (isNull && !metadata.isNullable()) {
            try {
                setToDefaultValue();
                return;
            } catch(NullDataFormatException e) {
                throw new NullDataFormatException(getMetadata().getName() + " field can not be set to null! (nullable == false)");
            }
        }
		this.isNull = isNull;
	}

	/**
	 * An operation which sets/resets field to its
     * initial value (just after it was created by JVM) - 
     * it varies depending on type of field.<br>
     * Nullable fields are set to NULL, non-nullable are zeroed, if they
     * have default value defined, then default value is assigned.
     * 
	 */
	public abstract void reset();
    
    
	/**
	 *  Returns the internal value of the data field. If field's value is
	 * deemed to be NULL (isNull() return true) then the returned
	 * value is NULL. Otherwise the internal value is converted
	 * to appropriate object representation and returned.<br>
	 *
	 * @return    The Value value
	 * @since
	 */
	public abstract Object getValue();

    /**
     *  Returns the duplicate of the internal value of the data field. If field's value is
     * deemed to be NULL (isNull() return true) then the returned
     * value is NULL. Otherwise the duplicate of the internal value is converted
     * to appropriate object representation and returned.<br>
     *
     * @return    The Value value
     * @since
     */
    public abstract Object getValueDuplicate();
    
	/**
	 *  Returns type of the field (String, Numeric, Date, etc.)
	 *
	 * @return    The Type value
	 * @since
	 */
	public abstract char getType();


	/**
	 *  Returns metadata associated with the data field
	 *
	 * @return    The Metadata value
	 * @since
	 */
	public DataFieldMetadata getMetadata() {
		return metadata;
	}


	/**
	 *  Checks the field value for being NULL.
	 *
	 * @return    True if the data field's internal value is
	 * deemed to be NULL otherwise false;
	 * @since     September 16, 2002
	 */
	public boolean isNull() {
		return isNull;
	}

	/**
	 *  Converts field's value into String representation
	 *
	 * @return    Description of the Returned Value
	 * @since
	 */
	public abstract String toString();


	/**
	 *  Parses field's value from string
	 *
	 * @param	seq	String representation of the value to be set  
	 * @since 11.12.2006
	 */
	public abstract void fromString(CharSequence seq);

	/**
	 *  Get the field's value from ByteBuffer
	 *
	 * @param  decoder                       Charset decoder which could be used to decode characters
	 * @param  dataBuffer                    Description of the Parameter
	 * @exception  CharacterCodingException  Description of the Exception
	 * @since                                October 31, 2002
	 */
	public void fromByteBuffer(ByteBuffer dataBuffer, CharsetDecoder decoder) throws CharacterCodingException {
		fromString(decoder.decode(dataBuffer));
	}
	
	/**
	 * Reads bytes from a provided ByteBuffer,
	 * interprets them as a packed decimal and converts
	 * it to a long.
	 * 
	 * @see http://www.simotime.com/datapk01.htm
	 * 
	 * @param dataBuffer ByteBuffer containing a packed decimal
	 * @return value of the packed decimal
	 */
	protected static long getPackedDecimal(ByteBuffer dataBuffer) {
		long result = 0;
		int aByte = 0;
		int firstNibble = 0;
		int secondNibble = 0;
		while (dataBuffer.hasRemaining()) {
			aByte = dataBuffer.get() & 0xFF; // use mask to erase non-relevant bits
			firstNibble = aByte >> 4;
			secondNibble = aByte & 0x0F;
			if (dataBuffer.hasRemaining()) {
				result = result * 10 + firstNibble;
				result = result * 10 + secondNibble;
			} else { // last byte
				result = result * 10 + firstNibble;
				if (secondNibble == 0x0D || secondNibble == 0x0B) {
					result = -result; // last nibble D denotes negative value
				} 
				// TODO report an error for invalid sign?
			}
		}
		return result;
	}
	
	/**
	 * Puts the value into the ByteBuffer as a packed decimal.
	 * 
	 * @see http://www.simotime.com/datapk01.htm
	 * 
	 * @param dataBuffer
	 * @param value
	 */
	protected static void putPackedDecimal(ByteBuffer dataBuffer, long value) {
		int firstNibble = 0; // upper half byte
		int secondNibble = 0; // lower half byte
		int digit = 0; // a decimal digit
		byte aByte = 0; // a byte (firstNibble and secondNibble combined)
		List<Byte> bytes = new ArrayList<Byte>(10); // 10 should be enough even for a long
		
		int sign = (value < 0) ? 0x0D : 0x0C; // extract the sign nibble
		long remainingDigits = Math.abs(value);
		secondNibble = sign;
		
		digit = (int) (remainingDigits % 10); // extract the last digit
		remainingDigits = remainingDigits / 10;
		firstNibble = digit << 4;
		
		aByte = (byte) ((firstNibble | secondNibble) & 0xFF); // combine to create the last byte
		bytes.add(aByte);
		
		while (remainingDigits != 0) { // extract remaining digits from last to first, two at a time
			digit = (int) (remainingDigits % 10);
			secondNibble = digit;
			remainingDigits = remainingDigits / 10;
			digit = (int) (remainingDigits % 10);
			firstNibble = digit << 4;
			remainingDigits = remainingDigits / 10;
			aByte = (byte) (firstNibble | secondNibble);
			bytes.add(aByte);
		}
		
		// write the bytes in reverse order
		for (int i = bytes.size() - 1; i >= 0; i--) {
			dataBuffer.put(bytes.get(i));
		}
	}
	

	/**
	 *  Encode the field's value into ByteBuffer. The numeric value is encoded as a string representation.
	 *
	 * @param  encoder                       Charset encoder which could be used to encode characters
	 * @param  dataBuffer                    Description of the Parameter
	 * @exception  CharacterCodingException  Description of the Exception
	 * @since                                October 31, 2002
	 */
	public void toByteBuffer(ByteBuffer dataBuffer, CharsetEncoder encoder) throws CharacterCodingException {
		try {
			dataBuffer.put(encoder.encode(CharBuffer.wrap(toString())));
		} catch (BufferOverflowException e) {
			throw new RuntimeException("The size of data buffer is only " + dataBuffer.limit() + ". Set appropriate parameter in defautProperties file.", e);
		}
	}

	/**
	 *  Serializes data field into provided byte buffer
	 *
	 * @param  buffer  Description of Parameter
	 * @since          September 16, 2002
	 */
	public abstract void serialize(ByteBuffer buffer);


	/**
	 *  Deserializes data field from provided byte buffer
	 *
	 * @param  buffer  Description of Parameter
	 * @since          September 16, 2002
	 */
	public abstract void deserialize(ByteBuffer buffer);


	/**
	 *  Checks whether two DataField objects are equal. Both
	 * fiels should be of the same type. If they are not, ClassCastException
	 * is thrown.<br> 
	 * Two datafields where each is flagged as to be NULL (isNull() returns true) are
	 * deemed <b>NOT equal</b>.
	 *
	 * @param  obj  Description of Parameter
	 * @return      Description of the Returned Value
	 * @since       September 16, 2002
	 */
	public abstract boolean equals(Object obj);


	/**
	 *  Compares two fields and returs -1,0,1 depending on result of comparison.
	 * Both fiels should be of the same type. If they are not, ClassCastException
	 * is thrown.<br>
	 * When comparing fields where one or both are NULL (isNull() returns true), the
	 * result of comparison is that if <code>obj</code> is NULL the result is 1,
	 * if <code>this.value</code> (value of this field) is NULL, the result is -1; 
	 *
	 * @param  obj  Description of the Parameter
	 * @return      Description of the Return Value
	 */
	public abstract int compareTo(Object obj);


	/**
	 *  Gets the actual size of field (in bytes).<br>
	 *  <i>How many bytes are required for field serialization</i>
	 * @return    The size in bytes
	 */
	public abstract int getSizeSerialized();
}
