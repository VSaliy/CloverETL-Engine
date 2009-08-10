
package org.jetel.data.xsd;

import javax.xml.bind.DatatypeConverter;
import org.apache.log4j.Logger;
import org.jetel.exception.DataConversionException;
import org.jetel.metadata.DataFieldMetadata;

/**
 *
 * @author Pavel Pospichal
 */
public class CloverIntegerConvertor implements IGenericConvertor {

    private static Logger logger = Logger.getLogger(CloverIntegerConvertor.class);
    
    static {
    	ConvertorRegistry.registerConvertor(new CloverIntegerConvertor());
    }
    
    public static Integer parseXsdIntToInteger(String value) throws DataConversionException {
    	Integer result = null;
        
		try {
			result = DatatypeConverter.parseInt(value);
		} catch (Exception e) {
			logger.fatal("Unable to parse xsd:int to " + Integer.class.getName() + ".", e);
			throw new DataConversionException("Unable to parse xsd:int to " + Integer.class.getName() + ".", e);
		}
        
        return result;
    }
    
    public static String printIntegerToXsdInt(Integer value) throws DataConversionException {
        String result = null;
        
        try {
			result = DatatypeConverter.printInt(value);
		} catch (Exception e) {
			logger.fatal("Unable to print " + Integer.class.getName() + " to xsd:int.", e);
			throw new DataConversionException("Unable to print " + Integer.class.getName() + " to xsd:int.", e);
        }
        
        return result;
    }

    public Object parse(String input) throws DataConversionException {
        return parseXsdIntToInteger(input);
    }

    public String print(Object obj) throws DataConversionException {
        if (!(obj instanceof Integer)) {
            throw new DataConversionException("Unsupported type by convertion: " + obj.getClass().getName());
        }

        return printIntegerToXsdInt((Integer) obj);
    }

	public boolean supportsCloverType(String cloverDataTypeCriteria) {
		return DataFieldMetadata.INTEGER_TYPE.equals(cloverDataTypeCriteria);
	}
}
